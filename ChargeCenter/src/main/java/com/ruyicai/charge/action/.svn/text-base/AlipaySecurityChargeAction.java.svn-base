package com.ruyicai.charge.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.alipay.security.RSASignature;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;


public class AlipaySecurityChargeAction implements ServletRequestAware,
		ServletResponseAware {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(AlipaySecurityChargeAction.class);
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
		} catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 打印JSON信息
	 */
	private void printJson(String errorCode, String signString) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);
			retMap.put("sign_string", signString);

			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}
	
	public String charge() {
		logger.info("支付宝安全支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("支付宝安全支付充值->得到参数：jsonString=" + jsonString);		
		
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
			logger.error("支付宝安全支付充值->获取Json串参数异常", e);
			e.printStackTrace();
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
				
		try {			
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&userno=").append(userno)
					.append("&type=2").append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag).append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("支付宝安全支付充值->生成交易记录：url=" + url + " ,param=" + param.toString());	
		
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("支付宝安全支付充值->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!"0".equals(errorCode)) {				
				logger.info("支付宝安全支付充值->生成交易记录出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("支付宝安全支付充值->交易号ttransactionId=" + ttransactionId);
						
			// 支付订单数据准备
			String orderInfo = getOrderInfo(ttransactionId, amt);
			String sign = sign(orderInfo);
			logger.info("支付宝安全支付充值->orderInfo=" + orderInfo + "；sign=" + sign);
			String signString = getSignString(orderInfo, sign);			
			
			this.printJson(errorCode, signString);

		} catch (Exception e) {
			logger.error("支付宝安全支付充值->执行过程中出现异常", e);
			e.printStackTrace();
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		logger.info("支付宝安全支付充值->结束");
		return null;
	}
	
	public static void main(String[] args) {
		String str = "1000000000000001234560211234567";	
		System.out.println("str=" + str);
	}
	
	private String getOrderInfo(String orderId, String amt) {
		 String partner = chargeconfigService.getChargeconfig("partnerId");//合作商户ID
		 String seller = chargeconfigService.getChargeconfig("sellerAccountName");//卖家帐号
		 String outTradeNo = orderId;//外部交易号
		 String subject = chargeconfigService.getChargeconfig("JinRuanTongSubject");//商品名称
		 String body = subject;//商品具体描述
		 
		 DecimalFormat df = new DecimalFormat("#0.00");
		 String totalFee = df.format(Double.valueOf(amt) / 100);// 商品总价
		 
		 String notifyUrl = chargeconfigService.getChargeconfig("alipay.security.bgreturl");
		 
		 StringBuffer orderInfo = new StringBuffer();
		 orderInfo.append("partner=\"").append(partner).append("\"&");
		 orderInfo.append("seller=\"").append(seller).append("\"&");
		 orderInfo.append("out_trade_no=\"").append(outTradeNo).append("\"&");
		 orderInfo.append("subject=\"").append(subject).append("\"&");
		 orderInfo.append("body=\"").append(body).append("\"&");
		 orderInfo.append("total_fee=\"").append(totalFee).append("\"&");
		 orderInfo.append("notify_url=\"").append(notifyUrl).append("\"");
		 logger.info("orderInfo=" + orderInfo);
		 return orderInfo.toString();
	}
	
	/**
	 * 对参数进行签名
	 * @param signData 待签名数据，key rsa商户私钥
	 * @return
	 */
	private String sign(String orderInfo) {	
		String sign = "";
		String key = chargeconfigService.getChargeconfig("prikey");//ConfigUtil.getConfig("charge.properties", "prikey");
		
		try {
			sign = RSASignature.sign(orderInfo, key);
			logger.info("sign=" + sign);
			sign = URLEncoder.encode(sign, "utf-8");
			logger.info("sign(URLEncoder)=" + sign);
		} catch (Exception e) {
			logger.error("支付宝安全支付充值->签名失败，异常信息：", e);
			e.printStackTrace();
		}
		return sign;
	}
	
	private String getSignString(String orderInfo, String sign) {
		StringBuffer signString = new StringBuffer();
		signString.append(orderInfo).append("&sign=\"").append(sign).append("\"&");
		signString.append("sign_type=\"RSA\"");		
		logger.info("signString:" + signString.toString());		
		return signString.toString();
	}
}
