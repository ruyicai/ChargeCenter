package com.ruyicai.charge.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.domain.Tcard;
import com.ruyicai.charge.domain.Tcardbat;
import com.ruyicai.charge.domain.Tcardsnstate;
import com.ruyicai.charge.domain.Tchannelcard;
import com.ruyicai.charge.domain.Tusercard;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.StringUtil;


@Service
@Transactional("transactionManager")
public class CardService {
	private static Logger logger = Logger.getLogger(CardService.class);
	private static final String KEY = "0x9f";	
	
	@Transactional("transactionManager")
	public int cardChargeSuccessProcess(String cardno, BigDecimal state, String ttransactionid, String agencyno, String userno) {
		logger.info("如意彩卡充值成功处理开始->cardno=" + cardno + "；state=" + state + "；ttransactionid=" + ttransactionid
				+ "；agencyno=" + agencyno + "；userno=" + userno);
		Tcard tcard = Tcard.findTcard(cardno);
		if (new BigDecimal(3).equals(tcard.getState())) {
			logger.info("充值交易已经成功,ttransactionid=" + ttransactionid);
			throw new RuyicaiException(ErrorCode.CardCharge_AlreadySuccess);
		}
		tcard.setState(state);
		tcard.merge();
		
	    Tusercard tusercard = new Tusercard();
	    tusercard.setTtransactionid(ttransactionid);
	    tusercard.setAgencyno(agencyno);
	    tusercard.setTcardid(cardno);
	    tusercard.setUserno(userno);
	    tusercard.setCardstate(state);
	    tusercard.setGettime(new Date());
	    tusercard.persist();
	    
	    logger.info("如意彩卡充值成功处理结束");
		return 0;
	}
	
	@Transactional("transactionManager")
	public int genCard(int amt, int type, int cardfrom, String agencyno, 
			int beginno, int endno, String endtime, String manager, String channel) {
		logger.info("生成卡开始->amt=" + amt + "；type=" + type + "；cardfrom=" + cardfrom
				+ "；agencyno=" + agencyno + "；beginno=" + beginno
				+ "；endno=" + endno + "；endtime=" + endtime
				+ "；manager=" + manager + "；channel=" + channel);
		int begin = beginno;
		int end = beginno + endno -1;
		String prefix = StringUtil.format(String.valueOf(amt), 5) + String.valueOf(type) + StringUtil.format(String.valueOf(cardfrom), 2);
		Date dateFirstCard = null;
		Date dateLastCard = null;
		BigDecimal batchno = Tcardbat.findMaxId();
		if (null == batchno) {
			batchno = BigDecimal.ONE;			
		} else {
			batchno = batchno.add(BigDecimal.ONE);			
		}
		
		
		for (int i=begin; i<=end; i++) {
			Tcard tcard = new Tcard();
			String tempId = StringUtil.format(String.valueOf(i), 8);
			tcard.setId(prefix + tempId);//id
			tcard.setAmt(new BigDecimal(amt));
			tcard.setType(new BigDecimal(type));
			tcard.setCardfrom(new BigDecimal(cardfrom));
			tcard.setCardsn(String.valueOf(i));//序列号
			tcard.setState(new BigDecimal(1));//状态： 1未激活
			tcard.setSelltime(new Date(0));
			tcard.setDecrptkey(" ");
			
			Date date = new Date();
			tcard.setStarttime(date);//生成日期
			if(i == begin) dateFirstCard = date;
			if(i == end) dateLastCard = date;
			
			Date dateEndtime = StringUtil.getDate(endtime);
			if (null == dateEndtime) {
				logger.info("生成卡->失效日期格式化出错, endtime=" + endtime);
				throw new RuyicaiException(ErrorCode.Charge_DateFormatError);
			}
			tcard.setEndtime(dateEndtime);
			
			String pwd = StringUtil.desPassword("000000000000000" + StringUtil.getSystemTime(), tempId);
			if (null == pwd || "".equals(pwd)) {
				logger.info("生成卡->生成卡密码出错, i=" + i);
				throw new RuyicaiException(ErrorCode.Charge_GenCardPwdError);
			}
			tcard.setPassword(pwd);
			
			tcard.setManager(manager);
			tcard.setBatchno(batchno.toString());
			tcard.setAgencyno(agencyno);
			tcard.setChannel(channel);
			tcard.persist();
		}
		
		Tcardbat cardbat = new Tcardbat();
		cardbat.setId(batchno.toString());
		cardbat.setCardmanager(manager);
		cardbat.setBatchno(" ");//???旧的是lastBatchno
		cardbat.setBeginnumber(begin);
		cardbat.setEndnumber(end);
		cardbat.setStarttime(dateFirstCard);
		cardbat.setEndtime(dateLastCard);
		cardbat.persist();
		
		
		Tcardsnstate cardNoState = new Tcardsnstate();
		BigDecimal cardNoStateId = Tcardsnstate.findMaxId();
		if (null == cardNoStateId) {
			cardNoStateId = new BigDecimal(1);			
		} else {
			cardNoStateId = cardNoStateId.add(new BigDecimal(1));
		}
		cardNoState.setId(cardNoStateId.toString());
		cardNoState.setBeginno(begin);
		cardNoState.setEndno(end);
		cardNoState.setState(new BigDecimal(1));
		cardNoState.persist();		
		
		logger.info("生成卡结束");
		return 0;
	}
	
	@Transactional("transactionManager")
	public int sell(int amt, int type, int cardfrom, String channel, int sellamt, String agencyno, File file) {
		logger.info("销售卡开始->amt=" + amt + "；type=" + type + "；cardfrom=" + cardfrom
				+ "；agencyno=" + agencyno + "；channel=" + channel + "；sellamt=" + sellamt);
		BigDecimal idChannelcard = Tchannelcard.findMaxId();
		if (null == idChannelcard) {
			idChannelcard = BigDecimal.ZERO;			
		}
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file,true));
		} catch (IOException e) {			
			e.printStackTrace();
			logger.info("销售卡->创建writer失败");
			throw new RuyicaiException(ErrorCode.Charge_WriteFileError);
		}
		
		List<Tcard> tcards = Tcard.findTcardsByAmtTypeCardfromChannel(amt, type, cardfrom, channel, sellamt, BigDecimal.ONE);//state=1 未激活的卡
		try {
			for (Tcard tcard : tcards) {
				tcard.setState(new BigDecimal(2));// 修改充值状态为激活
				tcard.merge();

				idChannelcard = idChannelcard.add(BigDecimal.ONE);
				Tchannelcard tchannelcard = new Tchannelcard();
				tchannelcard.setId(idChannelcard.toString());
				tchannelcard.setAgencyno("000100");// ? agencyno?
				tchannelcard.setCardamt(tcard.getAmt());
				tchannelcard.setCardcode(tcard.getId());
				tchannelcard.setGettime(new Date());
				tchannelcard.setCardcount(BigDecimal.ONE);// ? 设置购买数量
				tchannelcard.setDecrptkey(KEY);
				tchannelcard.persist();

				writer.write(tcard.getId() + ":" + tcard.getPassword());
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("销售卡->writer写文件失败");
			throw new RuyicaiException(ErrorCode.Charge_WriteFileError);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.info("销售卡->writer关闭失败");
				throw new RuyicaiException(ErrorCode.Charge_WriteFileError);
			}
		}		
		
		logger.info("销售结束");
		return 0;
	}
	
}
