package com.ruyicai.charge.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.ruyicai.charge.domain.Talibatchpay;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;

public class BatchpayAction implements ServletRequestAware, ServletResponseAware {		
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(BatchpayAction.class);
	
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
			String memo = chargeconfigService.getChargeconfig("batchpay.pay.memo");//ConfigUtil.getConfig("charge.properties", "batchpay.pay.memo");
			Map<String, String> map = this.check(ids, memo);
			String detailData = map.get("detailData");
			String batchNum = map.get("batchNum");//付款总笔数
			String batchFee = map.get("batchFee");//付款总金额
			String batchNo = map.get("batchNo");//批次号
			
			logger.info("detailData=" + detailData);
			logger.info("batchNum=" + batchNum);
			logger.info("batchFee=" + batchFee);
			logger.info("batchNo=" + batchNo);
			
			Date date = new Date();			
			batchNo = Talibatchpay.checkBatchNo(batchNo, detailData, batchNum, batchFee, date);
			
			//设置批号
			String url = chargeconfigService.getChargeconfig("batchpaysetbatchno");
			StringBuffer param = new StringBuffer();
			param.append("batchno=").append(batchNo).append("&detaildata=").append(detailData);
			logger.info("支付宝批量付款->设置批次号：url=" + url + ", param=" + param.toString());
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("支付宝批量付款->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!"0".equals(errorCode)) {				
				logger.info("支付宝批量付款->设置批次号出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			}
			
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
	
	private Map<String, String> check(String cashdetailIds, String memo) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String url = chargeconfigService.getChargeconfig("checkcashdetailids");
		StringBuffer param = new StringBuffer();
		param.append("cashdetailids=").append(cashdetailIds).append("&memo=").append(memo);
		logger.info("支付宝批量付款->校验提现：url=" + url + ", param=" + param.toString());
		String result = HttpRequest.doPostRequest(url, param.toString());			
		logger.info("支付宝批量付款->返回 result=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";

		if (!"0".equals(errorCode)) {				
			logger.info("支付宝批量付款->校验提现出现错误 errorCode=" + errorCode);				
			this.printErrorJson(errorCode);
			return null;
		}
		
		Object o = mapResult.containsKey("value")? mapResult.get("value") : null;
		logger.info("支付宝批量付款->校验提现 o=" + o);
		map = (Map<String, String>)o;
		return map;
	}
}
