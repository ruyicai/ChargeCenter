package com.ruyicai.charge.action;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.StringUtil;
import com.ruyicai.charge.yeepay.cardpro.NonBankcardPaymentResult;
import com.ruyicai.charge.yeepay.cardpro.NonBankcardService;
import com.ruyicai.charge.yeepay.htmlcommon.PaymentForOnlineService;
import com.ruyicai.charge.yeepay.util.CardTypeManager;

public class YeepayChargeAction implements ServletRequestAware,
		ServletResponseAware {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(YeepayChargeAction.class);
	
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired
	CardTypeManager cardTypeManager;


	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private void printErrorJson(String errorCode) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：", e);
			e.printStackTrace();
		}
	}
	
	private void printJson(String errorCode, String payform, String payUrl) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);		
			retMap.put("pay_url", payUrl + "?" + payform);			
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}
	
	private void printJson(Map<String, String> retMap) {
		try {			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}
	
	public String htmlcommonCharge() {
		logger.info("易宝支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("易宝支付充值->得到参数：jsonString=" + jsonString);

		String bankid = null;
		String paytype = null;
		String accesstype = null;
		String amt = null;
		String bankaccount = null;
		String channel = null;
		String subchannel = null;
		String userno = null;
		String pdfrpid = null;
		String b2bc = null;
		String isbank = null;		
		String ladderpresentflag = null;
		String continuebettype = null;
		String orderid = null;
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式 M商户 BWC
			amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额																				// (单位：分)
			bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			pdfrpid = map.containsKey("pdfrpid") ? map.get("pdfrpid").toString() : "";// 支付通道
			b2bc = map.containsKey("b2bc") ? map.get("b2bc").toString() : "b2c";//b2b b2c
		    isbank = map.containsKey("isbank") ? map.get("isbank").toString() : "true";//true false
		    ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
		    continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : null;// 继续投注类型
			orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
		} catch (Exception e) {
			logger.error("易宝支付充值->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
				
		String type = "true".equals(isbank) ? "2" : "10";
		String pd_FrpId2 = pdfrpid;
		if ("10".equals(type)) {
			pd_FrpId2 = cardTypeManager.getUnBankCode(paytype.substring(2, 4));			
		} 
		
		try {			
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(pd_FrpId2).append("&userno=").append(userno)
					.append("&type=").append(type).append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag).append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("易宝支付充值->生成交易记录：url=" + url + " ,param=" + param.toString());	
		
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("易宝支付充值->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!"0".equals(errorCode)) {				
				logger.info("易宝支付充值->生成交易记录出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("易宝支付充值->交易号ttransactionId=" + ttransactionId);	
			
			String merid = chargeconfigService.getChargeconfig("yeepay_MerId");//ConfigUtil.getConfig("charge.properties", "yeepay.merid");//
			String keyvalue = chargeconfigService.getChargeconfig("yeepay_key");//ConfigUtil.getConfig("charge.properties", "yeepay.keyvalue");// 商家密钥
			String requrl = "";
			if ("W".equals(accesstype) && "true".equals(isbank)) {
				 requrl = chargeconfigService.getChargeconfig("wapYeepayCommonReqURL");//ConfigUtil.getConfig("charge.properties", "yeepay.htmlcommon.waprequrl");//交易请求地址
			} else {
				 requrl = chargeconfigService.getChargeconfig("CCBWAPYeepayCommonReqURL");//ConfigUtil.getConfig("charge.properties", "yeepay.htmlcommon.requrl");//交易请求地址
			}		
			String p5pid = chargeconfigService.getChargeconfig("yeepay.htmlcommon.p5pid");//ConfigUtil.getConfig("charge.properties", "yeepay.htmlcommon.p5pid");//商品名称
			String p8url = chargeconfigService.getChargeconfig("yeepay.htmlcommon.p8url");//ConfigUtil.getConfig("charge.properties", "yeepay.htmlcommon.p8url");// 商户接收支付成功数据的地址
			DecimalFormat df = new DecimalFormat("#0.00");
			String amt2 = df.format(Double.valueOf(amt) / 100);//单位为元
			
			// 商家设置用户购买商品的支付信息
			String p0_Cmd = StringUtil.formatString("Buy"); // 在线支付请求，固定值 ”Buy”
			String p1_MerId = StringUtil.formatString(merid); // 商户编号
			String p2_Order = StringUtil.formatString(ttransactionId); // 商户订单号
			String p3_Amt = StringUtil.formatString(amt2); // 支付金额
			String p4_Cur = StringUtil.formatString("CNY"); // 交易币种
			String p5_Pid = StringUtil.formatString(p5pid); // 商品名称
			String p6_Pcat = StringUtil.formatString(b2bc); // 商品种类
			String p7_Pdesc = StringUtil.formatString(p5pid); // 商品描述
			String p8_Url = StringUtil.formatString(p8url); // 商户接收支付成功数据的地址
			String p9_SAF = StringUtil.formatString("0"); // 需要填写送货信息 0：不需要 1:需要
			String pa_MP = StringUtil.formatString(accesstype); // 商户扩展信息
			String pd_FrpId = StringUtil.formatString(pdfrpid); // 支付通道编码
			pd_FrpId = pd_FrpId.toUpperCase();// 银行编号必须大写
			String pr_NeedResponse = StringUtil.formatString("1"); // 默认为"1"，需要应答机制
			String hmac = StringUtil.formatString(""); // 交易签名串

			// 获得MD5-HMAC签名
			hmac = PaymentForOnlineService.getReqMd5HmacForOnlinePayment(
					p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid,
					p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP, pd_FrpId,
					pr_NeedResponse, keyvalue);
			
			param.delete(0, param.length()); 
			param.append("p0_Cmd=").append(p0_Cmd).append("&p1_MerId=").append(p1_MerId).append("&p2_Order=").append(p2_Order)
			.append("&p3_Amt=").append(p3_Amt).append("&p4_Cur=").append(p4_Cur).append("&p5_Pid=").append(p5_Pid)
			.append("&p6_Pcat=").append(p6_Pcat).append("&p7_Pdesc=").append(p7_Pdesc).append("&p8_Url=").append(p8_Url)
			.append("&p9_SAF=").append(p9_SAF).append("&pa_MP=").append(pa_MP).append("&pd_FrpId=").append(pd_FrpId)
			.append("&pr_NeedResponse=").append(pr_NeedResponse).append("&hmac=").append(hmac);
			this.printJson(errorCode, param.toString(), requrl);
			
		} catch (Exception e) {
			logger.error("易宝支付充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
			this.printErrorJson(errorCode);
			return null;
		}

		logger.info("易宝支付充值->结束");
		return null;
	}
	
	public String cardproCharge() {
		logger.info("易宝支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("易宝支付充值->得到参数：jsonString=" + jsonString);

		String bankid = null;
		String paytype = null;
		String accesstype = null;
		String amt = null;
		String bankaccount = null;
		String channel = null;
		String subchannel = null;
		String userno = null;
		String pdfrpid = null;
		String b2bc = null;
		String cardno = null;
		String cardpwd = null;		
		String cardamt = null;
		String verifyamt = null;
		String ladderpresentflag = null;
		String continuebettype = null;
		String orderid = null;
			
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式 M商户 BWC
			amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额																				// (单位：分)
			bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			cardno = map.containsKey("cardno") ? map.get("cardno").toString() : "";// 卡号
			cardpwd = map.containsKey("cardpwd") ? map.get("cardpwd").toString() : "";// 卡密
			cardamt = map.containsKey("cardamt") ? map.get("cardamt").toString() : "";// 卡面额
			verifyamt = map.containsKey("verifyamt") ? map.get("verifyamt").toString() : "true";// 支付通道
			pdfrpid = map.containsKey("pdfrpid") ? map.get("pdfrpid").toString() : "";// 支付通道
			b2bc = map.containsKey("b2bc") ? map.get("b2bc").toString() : "b2c";//b2b b2c
			ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
		} catch (Exception e) {
			logger.error("易宝支付充值->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		String pd_FrpId2 = cardTypeManager.getUnBankCode(paytype.substring(2, 4));
		
		try {			
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(pd_FrpId2).append("&userno=").append(userno)
					.append("&type=10").append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag).append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("易宝支付充值->生成交易记录：url=" + url + " ,param=" + param.toString());	
		
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("易宝支付充值->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!"0".equals(errorCode)) {				
				logger.info("易宝支付充值->生成交易记录出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("易宝支付充值->交易号ttransactionId=" + ttransactionId);	
						
			String p5pid = chargeconfigService.getChargeconfig("yeepay.cardpro.p5pid");//ConfigUtil.getConfig("charge.properties", "yeepay.cardpro.p5pid");//商品名称
			String p8url = chargeconfigService.getChargeconfig("yeepay.cardpro.p8url");;//ConfigUtil.getConfig("charge.properties", "yeepay.cardpro.p8url");// 商户接收支付成功数据的地址
			DecimalFormat df = new DecimalFormat("#0.00");
			String amt2 = df.format(Double.valueOf(amt) / 100);//单位为元
			
			// 商家设置用户购买商品的支付信息		
			String p2_Order = StringUtil.formatString(ttransactionId); // 商户订单号
			String p3_Amt = StringUtil.formatString(amt2); // 支付金额
			String p4_verifyAmt = StringUtil.formatString(verifyamt);//是否较验订单金额
			String p5_Pid = StringUtil.formatString(p5pid); // 商品名称			
			String p6_Pcat = StringUtil.formatString(b2bc); // 商品种类
			String p7_Pdesc = StringUtil.formatString(p5pid); // 商品描述
			String p8_Url = StringUtil.formatString(p8url); // 商户接收支付成功数据的地址
			String pa_MP = StringUtil.formatString(accesstype); // 商户扩展信息			
			String pa7_cardAmt = StringUtil.formatString(cardamt); // 卡面额组
			String pa8_cardNo = StringUtil.formatString(cardno); // 卡号组
			String pa9_cardPwd = StringUtil.formatString(cardpwd); // 卡密组
			String pd_FrpId = StringUtil.formatString(pdfrpid); // 支付通道编码
			pd_FrpId = pd_FrpId.toUpperCase();// 银行编号必须大写
			String pr_NeedResponse = StringUtil.formatString("1"); // 默认为"1"，需要应答机制			
			String pz_userId = StringUtil.formatString("");// 用户唯一标识		
			String pz1_userRegTime = StringUtil.formatString("");// 用户的注册时间		

			NonBankcardPaymentResult rs = NonBankcardService.pay(p2_Order,
					p3_Amt, p4_verifyAmt, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url,
					pa_MP, pa7_cardAmt, pa8_cardNo, pa9_cardPwd, pd_FrpId,
					pr_NeedResponse, pz_userId, pz1_userRegTime, chargeconfigService.getChargeconfig("yeepay_MerId"), chargeconfigService.getChargeconfig("yeepay.cardpro.requrl"));
			
			String r0_Cmd = rs.getR0_Cmd();
			String r1_Code = rs.getR1_Code();
			String r6_Order = rs.getR6_Order();
			String rq_ReturnMsg = rs.getRq_ReturnMsg();
			logger.info("提交返回参数列表：业务类型r0_Cmd=" + r0_Cmd + ";消费请求结果r1_Code=" + r1_Code + ";商户订单号r6_Order=" + r6_Order + ";错误信息rq_ReturnMsg=" + rq_ReturnMsg);
			
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);		
			retMap.put("r0_Cmd", r0_Cmd);
			retMap.put("r1_Code", r1_Code);	
			retMap.put("r6_Order", r6_Order);
			retMap.put("rq_ReturnMsg", rq_ReturnMsg);
			this.printJson(retMap);
			
		} catch (Exception e) {
			logger.error("易宝支付充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
			this.printErrorJson(errorCode);
			return null;
		}
		
		logger.info("易宝支付充值->结束");
		return null;
	}
}
