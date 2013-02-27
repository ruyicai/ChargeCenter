package com.ruyicai.charge.action;

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

import com.ruyicai.charge.liantonghuajian.LthjUtil;
import com.ruyicai.charge.liantonghuajian.XmlHttpConnection;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.XMLUtil;

public class LiantonghuajianChargeAction implements ServletRequestAware,
		ServletResponseAware {
	@Autowired 
	ChargeconfigService chargeconfigService;
	
	private final static String ORDER_SUBMIT_OK = "0000";
	private final static String KEY_RESPCODE = "respCode";
	private final static int TIME_OUT = 6000;
		
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(LiantonghuajianChargeAction.class);


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
			retMap.put("error_code", errorCode);
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
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
			retMap.put("error_code", errorCode);		
			retMap.put("value", value);			
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}
	
	public String charge() {
		logger.info("联通华建支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("联通华建支付->得到参数：jsonString=" + jsonString);		
				
		String bankid = null;
		String paytype = null;
		String accesstype = null;
		String amt = null;
		String bankaccount = null;
		String channel = null;
		String subchannel = null;
		String userno = null;
		String ladderpresentflag = null;
		String continuebettype = null;
		String orderid = null;
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额																				// (单位：分)
			bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号			
		} catch (Exception e) {
			logger.error("联通华建支付->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
				
		try {			
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&userno=").append(userno)
					.append("&type=2").append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag)
					.append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("联通华建支付充值->生成交易记录：url=" + url + ",param=" + param.toString());	
		 
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("联通华建支付充值->生成交易记录，返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!ErrorCode.OK.value.equals(errorCode)) {				
				logger.info("联通华建支付充值->生成交易记录，出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("联通华建支付充值->交易号ttransactionId=" + ttransactionId);
			
			
			String merchantOrderTime = DateUtil.format("yyyyMMddHHmmss", new Date());
			LthjUtil lthjUtil = new LthjUtil();
			String merid = chargeconfigService.getChargeconfig("liantonghuajian.merid");
			String returnurl = chargeconfigService.getChargeconfig("liantonghuajian.returnurl");
			String orderSubmitMessage = lthjUtil.createOrderSubmitMessage(ttransactionId, amt, merchantOrderTime, merid, returnurl);
			url = chargeconfigService.getChargeconfig("liantonghuajian.requrl");//requrl;
			logger.info("联通华建支付充值->提交订单：url=" + url + ",param=" + orderSubmitMessage);
			XmlHttpConnection xmlhttpconnection =  new XmlHttpConnection(url, TIME_OUT);
			xmlhttpconnection.sendMsg(orderSubmitMessage);
			result = xmlhttpconnection.getReMeg();			
			logger.info("联通华建支付充值->提交订单，返回 result=" + result);
			Map<String, String> mapRespMessage = XMLUtil.xml2Map(result);
			if (!ORDER_SUBMIT_OK.equals(mapRespMessage.get(KEY_RESPCODE))) {
				logger.info("联通华建支付充值->提交订单失败");
				errorCode = ErrorCode.Charge_OrderSubmitFail.value;	
				this.printErrorJson(errorCode);
				return null;
			}
						
			String lanchPayMessage = lthjUtil.createLanchPayMessage(ttransactionId, merchantOrderTime, merid, returnurl);
			this.printJson(errorCode, lanchPayMessage);			
		} catch (Exception e) {
			logger.error("联通华建支付充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
				
		logger.info("联通华建银联手机在线支付->结束");
		return null;
	}
	
	
}
