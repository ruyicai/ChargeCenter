package com.ruyicai.charge.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.alipay.tradequery.AlipayConfig;
import com.ruyicai.charge.consts.CashDetailState;
import com.ruyicai.charge.consts.CashDetailType;
import com.ruyicai.charge.consts.TransactionType;
import com.ruyicai.charge.dao.TaccountDao;
import com.ruyicai.charge.dao.TtransactionDao;
import com.ruyicai.charge.domain.Taccount;
import com.ruyicai.charge.domain.Taccountdetail;
import com.ruyicai.charge.domain.Tcashdetail;
import com.ruyicai.charge.domain.Ttransaction;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ErrorCode;

@Service
@Transactional("transactionManager")
public class FundService {
	private static Logger logger = LoggerFactory.getLogger(FundService.class);
	
	@Autowired
	TtransactionDao ttransactionDao;
	
	@Autowired
	private TaccountDao taccountDao;
	
	@Transactional("transactionManager")
	public BigDecimal deductDrawAmount(String userno, String ttransactionid) {
		Taccount taccount = taccountDao.findTaccount(userno, true);
		if (taccount == null) {
			logger.info("账户记录为空");
			throw new RuyicaiException(ErrorCode.Taccount_NotExists);
		}
		
		Ttransaction ttransaction = ttransactionDao.findTtransaction(ttransactionid);
		if (null == ttransaction) {
			logger.info("ttransaction不存在, transactionId: " + ttransactionid);
			throw new RuyicaiException(ErrorCode.Ttransaction_NotExists);
		}
		if (!TransactionType.yinhangkachongzhi.value().equals(ttransaction.getType())) {
			logger.info("该交易为非银行卡充值, transactionId: " + ttransactionid + ";交易类型type:" + ttransaction.getType().toString());
			//throw new RuyicaiException(ErrorCode.Ttransaction_TypeNotBankCharge);
			return BigDecimal.ZERO;
		}
		
		BigDecimal drawamt = new BigDecimal(ChargeUtil.getDrawamt(ttransaction.getAmt().toString()));
		logger.info("充值金额为：amt=" + ttransaction.getAmt().toString() + ";可提现余额为：drawamt=" + drawamt.toString());
		
		if (taccount.getDrawbalance().compareTo(drawamt) < 0) {
			logger.info("可提现余额变动有误，实际可提现余额为：" + taccount.getDrawbalance().toString());
			throw new RuyicaiException(ErrorCode.Taccount_DrawamtChangeError);
		}
			
		List<Taccountdetail> taccountdetails = Taccountdetail.findByTransactionid(ttransactionid);
		if(taccountdetails.isEmpty()) {
			logger.info("账户明细记录为空");
			throw new RuyicaiException(ErrorCode.Taccountdetail_Empty);
		}		
		
		taccountDao.deductDrawAmount(taccount, "", "", ttransactionid, ttransaction.getType(), drawamt);				
		return drawamt;
	}
	
	@Transactional("transactionManager")
	public void batchpaySetBatchNo(String batchNo, String detailData){
		String[] records = detailData.split(AlipayConfig.BATCHPAY_DELIMITER_1);
		for (String record : records) {
			String[] items = record.split(AlipayConfig.BATCHPAY_DELIMITER_2);
			Tcashdetail tcashdetail = Tcashdetail.findTcashdetail(items[0]);
			if (null == tcashdetail) {
				logger.info("提现记录为空，提现id=" + items[0]);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailNotExist);
			}
//			if (tcashdetail.getState().equals(CashDetailState.Chenggong.value())) {
//				logger.info("该笔提现已成功，提现id=" + items[0]);
//				throw new RuyicaiException(ErrorCode.BatchPay_AlreadySuccess);
//			}
			if (!tcashdetail.getState().equals(CashDetailState.Shenghezhong.value())) {
				logger.info("该提现非已审核状态，提现id=" + items[0]);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailStateNotShenghezhong);
			}
			if (!tcashdetail.getType().equals(CashDetailType.Zhifubao.value())){
				logger.info("该提现类型为非支付宝提现，提现id=" + items[0]);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailTypeNotAlipay);
			}
			//tcashdetail.setState(CashDetailState.TiaoJiao.value());
			tcashdetail.setModifytime(new Date());
			tcashdetail.setBatchno(batchNo);
			tcashdetail.merge();
		}
	}
	
	@Transactional("transactionManager")
	public void batchpaySuccessProcess(String successDetails){
		String[] records = successDetails.split(AlipayConfig.BATCHPAY_DELIMITER_1);
		for (String record : records) {
			String[] items = record.split(AlipayConfig.BATCHPAY_DELIMITER_2);
			Tcashdetail tcashdetail = Tcashdetail.findTcashdetail(items[0]);
			if (null == tcashdetail) {
				logger.info("提现记录为空，提现id=" + items[0]);
				throw new RuyicaiException(ErrorCode.Taccountdetail_Empty);
			}
			if (tcashdetail.getState().equals(CashDetailState.Chenggong.value())) {
				logger.info("该提现已成功，提现id=" + items[0]);
				//throw new RuyicaiException(ErrorCode.BatchPay_AlreadySuccess);
				continue;
			}			
			if (!tcashdetail.getType().equals(CashDetailType.Zhifubao.value())){
				logger.info("该提现类型为非支付宝提现，提现id=" + items[0]);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailTypeNotAlipay);
			}
			if (!tcashdetail.getState().equals(CashDetailState.Shenghezhong.value())) {
				logger.info("该提现非已审核状态，提现id=" + items[0]);
				//throw new RuyicaiException(ErrorCode.BatchPay_cashdetailStateNotShenghezhong);
				continue;
			}
			
			//校验金额
//			BigDecimal amt = new BigDecimal(items[3]);
//			logger.info("items[3]=" + items[3]);
//			amt = amt.multiply(new BigDecimal(100));
//			amt = amt.setScale(0, BigDecimal.ROUND_HALF_UP);
//			logger.info("amt=" + amt);
//			if (!amt.equals(tcashdetail.getAmt())) {
//				logger.info("该提现金额前后不一致，提现id=" + items[0]);
//				throw new RuyicaiException(ErrorCode.BatchPay_amtError);
//			}
			
			tcashdetail.setState(CashDetailState.Chenggong.value());
			tcashdetail.setModifytime(new Date());
			tcashdetail.merge();
		}
	}
}
