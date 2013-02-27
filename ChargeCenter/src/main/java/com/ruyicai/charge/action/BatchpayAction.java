package com.ruyicai.charge.action;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.alipay.batchpay.Payment;
import com.ruyicai.charge.alipay.tradequery.AlipayConfig;
import com.ruyicai.charge.consts.CashDetailState;
import com.ruyicai.charge.consts.CashDetailType;
import com.ruyicai.charge.domain.Talibatchpay;
import com.ruyicai.charge.domain.Tcashdetail;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.service.FundService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.StringUtil;

public class BatchpayAction implements ServletRequestAware, ServletResponseAware {		
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(BatchpayAction.class);
	
	@Autowired
	private FundService fundService;
	@Autowired
	ChargeconfigService chargeconfigService;
	
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * 打印错误JSON信息
	 */
	private void printErrorJson(String errorCode) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("errorCode", errorCode);

			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 打印JSON信息
	 */
	private void printJson(String errorCode, String value) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("errorCode", errorCode);
			retMap.put("value", value);

			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：", e);
		}
	}
	
	/**
	 * 打印JSON信息
	 */
	private void printJson(Map<String, String> map) {
		try {
			String json = JsonUtil.toJson(map);
			logger.info("json=" + json);
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(json);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：", e);
		}
	}
	
	private void printJson(String errorCode, String payform, String payUrl) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("errorCode", errorCode);		
			retMap.put("url", payUrl + payform);			
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}
	
	public String alipay() {
		logger.info("支付宝批量付款->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("支付宝批量付款->得到参数：jsonString=" + jsonString);		
				
		String ids = null;//		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			ids = map.containsKey("ids") ? map.get("ids").toString() : "";//提现ids		|分割	
						
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝批量付款->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		try {
			Map<String, String> map = this.check(ids);
			String detailData = map.get("detailData");
			String batchNum = map.get("batchNum");//付款总笔数
			String batchFee = map.get("batchFee");//付款总金额
			String batchNo = map.get("batchNo");//批次号
			
			Date date = new Date();			
			batchNo = Talibatchpay.checkBatchNo(batchNo, detailData, batchNum, batchFee, date);
			
			//设置批号
			fundService.batchpaySetBatchNo(batchNo, detailData);
			
			String notifyUrl = chargeconfigService.getChargeconfig("batchpay.bgreturl");//ConfigUtil.getConfig("charge.properties", "batchpay.bgreturl");
			String accountName = chargeconfigService.getChargeconfig("batchpay.account.name");//ConfigUtil.getConfig("charge.properties", "batchpay.account.name");
			String partner = chargeconfigService.getChargeconfig("batchpay.partner.id");//ConfigUtil.getConfig("charge.properties", "batchpay.partner.id"); 
			String payDate = DateUtil.formatDate(date);
			//batchFee
			String service = AlipayConfig.SERVICE_BATCHPAY;
			String signType = AlipayConfig.SIGN_TYPE;
			//batchNum
			String email = chargeconfigService.getChargeconfig("batchpay.email");//ConfigUtil.getConfig("charge.properties", "batchpay.email");
			//detailData
			
			String payGateway = chargeconfigService.getChargeconfig("batchpay.pay.gateway");//ConfigUtil.getConfig("charge.properties", "batchpay.pay.gateway");
			String payGateway2 = chargeconfigService.getChargeconfig("batchpay.pay.gateway2");//ConfigUtil.getConfig("charge.properties", "batchpay.pay.gateway2");
			
			String key = chargeconfigService.getChargeconfig("batchpay.key");//ConfigUtil.getConfig("charge.properties", "batchpay.key"); 
			 
			String inputCharset = AlipayConfig.INPUT_CHARSET;
			
			String sign = Payment.CreateUrl(payGateway, service, partner, signType, batchNo, accountName, email, payDate, notifyUrl, batchFee.toString(), batchNum.toString(), detailData, key, inputCharset);
			
//			StringBuffer param = new StringBuffer();
//			param.append("&batch_no=").append(batchNo).append("&notify_url=").append(notifyUrl).append("&account_name=").append(accountName)
//			.append("&partner=").append(partner).append("&pay_date=").append(payDate).append("&batch_fee=").append(batchFee)
//			.append("&service=").append(service).append("&sign=").append(sign).append("&sign_type=").append(signType)
//			.append("&batch_num=").append(batchNum).append("&email=").append(email).append("&detail_data=").append(detailData);
//			
//			logger.info("url=" + payGateway2);
//			logger.info("param=" + param.toString());
			//this.printJson(errorCode, param.toString(), payGateway2);
			
			Map<String, String> mapRet = new HashMap<String, String>();
			mapRet.put("errorCode", errorCode);
			mapRet.put("url", payGateway2);
			mapRet.put("batch_no", batchNo);
			mapRet.put("notify_url", notifyUrl);
			mapRet.put("account_name", accountName);
			mapRet.put("partner", partner);
			mapRet.put("pay_date", payDate);
			mapRet.put("batch_fee", batchFee);
			mapRet.put("service", service);
			mapRet.put("sign", sign);
			mapRet.put("sign_type", signType);
			mapRet.put("batch_num", batchNum);
			mapRet.put("email", email);
			mapRet.put("detail_data", detailData);
			this.printJson(mapRet);
			
		} catch (RuyicaiException e) {
			errorCode = e.getErrorCode().value;
			logger.info("RuyicaiException:errorCode=" + errorCode + ";errorMsg=" + e.getErrorCode().memo);
			this.printErrorJson(errorCode);		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝批量付款->出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		logger.info("支付宝批量付款->结束");
		return null;
	}
	
	private Map<String, String> check(String cashdetailIds) {
		Map<String, String> map = new HashMap<String, String>();
		
		if (StringUtil.isEmpty(cashdetailIds)) {
			logger.error("支付宝批量付款->提现Ids为空");
			throw new RuyicaiException(ErrorCode.BatchPay_cashdetailIdsEmpty);
		}
		
		String memo = chargeconfigService.getChargeconfig("batchpay.pay.memo");//ConfigUtil.getConfig("charge.properties", "batchpay.pay.memo");
		String detailData = null;//
		BigDecimal batchNum = BigDecimal.ZERO;//付款总笔数
		BigDecimal batchFee = BigDecimal.ZERO;//付款总金额
		String batchNo = null;
		
		BigDecimal batchFeeTemp = BigDecimal.ZERO;
		StringBuffer sbDetailData = new StringBuffer();
		int i = 0;
		String[] ids = cashdetailIds.split(AlipayConfig.BATCHPAY_DELIMITER_1);
		for (String id : ids) {
			i++;
			
			Tcashdetail tcashdetail = Tcashdetail.findTcashdetail(id);
			if (null == tcashdetail) {
				logger.error("提现记录为空，提现id=" + id);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailNotExist);
			}			
			if (!tcashdetail.getState().equals(CashDetailState.Shenghezhong.value())) {
				logger.info("该提现非已审核状态，提现id=" + id);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailStateNotShenghezhong);
			}
			if (!tcashdetail.getType().equals(CashDetailType.Zhifubao.value())){
				logger.info("该提现类型为非支付宝提现，提现id=" + id);
				throw new RuyicaiException(ErrorCode.BatchPay_cashdetailTypeNotAlipay);
			}
			
			//流水号1^收款方账号1^收款账号姓名1^付款金额1^备注说明1|流水号2^收款方账号2^收款账号姓名2^付款金额2^备注说明2
			if (i > 1) {
				sbDetailData.append(AlipayConfig.BATCHPAY_DELIMITER_3);	
				if (batchNo == null && tcashdetail.getBatchno() == null) {
					//
				} else if (batchNo == null && tcashdetail.getBatchno() != null){
					throw new RuyicaiException(ErrorCode.BatchPay_batchnoDiscordant);
				} else if (batchNo != null && tcashdetail.getBatchno() == null){
					throw new RuyicaiException(ErrorCode.BatchPay_batchnoDiscordant);
				} else {
					if (!batchNo.equals(tcashdetail.getBatchno())) {
						throw new RuyicaiException(ErrorCode.BatchPay_batchnoDiscordant);
					}
				}
			} else {
				batchNo = tcashdetail.getBatchno();
			}
					
			BigDecimal amt = tcashdetail.getAmt().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
			
			sbDetailData.append(id).append(AlipayConfig.BATCHPAY_DELIMITER_4)
			.append(tcashdetail.getBankaccount().trim()).append(AlipayConfig.BATCHPAY_DELIMITER_4)
			.append(tcashdetail.getName().trim()).append(AlipayConfig.BATCHPAY_DELIMITER_4)
			.append(amt.toString()).append(AlipayConfig.BATCHPAY_DELIMITER_4)
			.append(memo);
			
			batchFeeTemp = batchFeeTemp.add(tcashdetail.getAmt());
		}
		
		detailData = sbDetailData.toString();
		batchNum = new BigDecimal(i);
		batchFee = batchFeeTemp.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
		
		map.put("detailData", detailData);
		map.put("batchNum", batchNum.toString());
		map.put("batchFee", batchFee.toString());
		map.put("batchNo", batchNo);
		return map;
	}

	
	public static void main(String[] args) {
		BigDecimal  amt = new BigDecimal(2012);
		BigDecimal  amt2 = amt.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
		System.out.println("amt=" + amt.toString());
		System.out.println("amt2=" + amt2.toString());
		
		BigDecimal  amt3 = new BigDecimal(20.12);
		BigDecimal  amt4 = amt3.multiply(new BigDecimal(100));
		System.out.println("amt3=" + amt3.toString());
		System.out.println("amt4=" + amt4.toString());
		amt4 = amt4.setScale(0, BigDecimal.ROUND_HALF_UP);
		System.out.println("amt3=" + amt3.toString());
		System.out.println("amt4=" + amt4.toString());
		
		String str1 = null;
		String str2 = null;
		boolean ret = str1==null;//str1.equals(str2);
		System.out.println("ret=" + ret);
	}
	
	
	public String bank() {
		logger.info("支付宝批量付款到银行账户->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("支付宝批量付款到银行账户->得到参数：jsonString=" + jsonString);		
				
		String ids = null;//		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			ids = map.containsKey("ids") ? map.get("ids").toString() : "";//提现ids		|分割	
						
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝批量付款到银行账户->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		try {
			Map<String, String> map = this.check(ids);
			String detailData = map.get("detailData");
			String batchNum = map.get("batchNum");//付款总笔数
			String batchFee = map.get("batchFee");//付款总金额
			String batchNo = map.get("batchNo");//批次号
			
			Date date = new Date();			
			batchNo = Talibatchpay.checkBatchNo(batchNo, detailData, batchNum, batchFee, date);
			
			//设置批号
			fundService.batchpaySetBatchNo(batchNo, detailData);
			
			String notifyUrl = ConfigUtil.getConfig("charge.properties", "batchpay.bgreturl");
			String accountName = ConfigUtil.getConfig("charge.properties", "batchpay.account.name");
			String partner = ConfigUtil.getConfig("charge.properties", "batchpay.partner.id"); 
			String payDate = DateUtil.formatDate(date);
			//batchFee
			String service = AlipayConfig.SERVICE_BATCHPAY;
			String signType = AlipayConfig.SIGN_TYPE;
			//batchNum
			String email = ConfigUtil.getConfig("charge.properties", "batchpay.email");
			//detailData
			
			String payGateway = ConfigUtil.getConfig("charge.properties", "batchpay.pay.gateway");
			String payGateway2 = ConfigUtil.getConfig("charge.properties", "batchpay.pay.gateway2");
			
			String key = ConfigUtil.getConfig("charge.properties", "batchpay.key"); 
			 
			String inputCharset = AlipayConfig.INPUT_CHARSET;
			
			String sign = Payment.CreateUrl(payGateway, service, partner, signType, batchNo, accountName, email, payDate, notifyUrl, batchFee.toString(), batchNum.toString(), detailData, key, inputCharset);
			
//			StringBuffer param = new StringBuffer();
//			param.append("&batch_no=").append(batchNo).append("&notify_url=").append(notifyUrl).append("&account_name=").append(accountName)
//			.append("&partner=").append(partner).append("&pay_date=").append(payDate).append("&batch_fee=").append(batchFee)
//			.append("&service=").append(service).append("&sign=").append(sign).append("&sign_type=").append(signType)
//			.append("&batch_num=").append(batchNum).append("&email=").append(email).append("&detail_data=").append(detailData);
//			
//			logger.info("url=" + payGateway2);
//			logger.info("param=" + param.toString());
			//this.printJson(errorCode, param.toString(), payGateway2);
			
			Map<String, String> mapRet = new HashMap<String, String>();
			mapRet.put("errorCode", errorCode);
			mapRet.put("url", payGateway2);
			mapRet.put("batch_no", batchNo);
			mapRet.put("notify_url", notifyUrl);
			mapRet.put("account_name", accountName);
			mapRet.put("partner", partner);
			mapRet.put("pay_date", payDate);
			mapRet.put("batch_fee", batchFee);
			mapRet.put("service", service);
			mapRet.put("sign", sign);
			mapRet.put("sign_type", signType);
			mapRet.put("batch_num", batchNum);
			mapRet.put("email", email);
			mapRet.put("detail_data", detailData);
			this.printJson(mapRet);
			
		} catch (RuyicaiException e) {
			errorCode = e.getErrorCode().value;
			logger.info("RuyicaiException:errorCode=" + errorCode + ";errorMsg=" + e.getErrorCode().memo);
			this.printErrorJson(errorCode);		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝批量付款到银行账户->出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		logger.info("支付宝批量付款到银行账户->结束");
		return null;
	}
}
