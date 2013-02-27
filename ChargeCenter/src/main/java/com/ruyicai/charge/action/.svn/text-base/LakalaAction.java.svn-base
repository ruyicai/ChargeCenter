package com.ruyicai.charge.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.lakala.LakalaUtil;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;

public class LakalaAction implements ServletRequestAware, ServletResponseAware {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(LakalaAction.class);
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
	private void printJson(Map<String, String> map) {
		try {
			String json = JsonUtil.toJson(map);
			logger.info("json=" + json);
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(json);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("发生异常：", e);
		}
	}

	
	public String charge() {
		logger.info("拉卡拉支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("拉卡拉支付->得到参数：jsonString=" + jsonString);		
				
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
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID lkl001
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式 0910
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
			logger.error("拉卡拉支付->获取Json串参数异常", e);
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
			logger.info("拉卡拉支付充值->生成交易记录：url=" + url + ",param=" + param.toString());	
		 
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("拉卡拉支付充值->生成交易记录，返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!ErrorCode.OK.value.equals(errorCode)) {				
				logger.info("拉卡拉支付充值->生成交易记录，出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("拉卡拉支付充值->交易号ttransactionId=" + ttransactionId);
								
			LakalaUtil lakalaUtil = new LakalaUtil();
			Map<String, String> mapRet = lakalaUtil.getParams(ttransactionId, amt, chargeconfigService.getChargeconfig("lakala.merid"), chargeconfigService.getChargeconfig("lakala.requrl"), chargeconfigService.getChargeconfig("lakala.returnurl"));
			mapRet.put("error_code", errorCode);
			this.printJson(mapRet);			
		} catch (Exception e) {
			logger.error("拉卡拉支付充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);			
		}
				
		logger.info("拉卡拉支付充值->结束");
		return null;
	}
	
}
