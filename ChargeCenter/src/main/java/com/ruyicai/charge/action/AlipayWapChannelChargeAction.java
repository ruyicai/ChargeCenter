package com.ruyicai.charge.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
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

import com.ruyicai.charge.alipay.client.security.DirectTradeCreateRes;
import com.ruyicai.charge.alipay.client.security.StringUtil;
import com.ruyicai.charge.alipay.client.security.XMapUtil;
import com.ruyicai.charge.alipay.wap.channel.MD5Signature;
import com.ruyicai.charge.alipay.wap.channel.ParameterUtil;
import com.ruyicai.charge.alipay.wap.channel.RSASignature;
import com.ruyicai.charge.alipay.wap.channel.ResponseResult;
import com.ruyicai.charge.alipay.wap.channel.WapChannelUtil;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;


public class AlipayWapChannelChargeAction implements ServletRequestAware,
		ServletResponseAware {
	@Autowired
	ChargeconfigService chargeconfigService;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(AlipayWapChannelChargeAction.class);
	

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
	private void printJson(String errorCode, String value) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);
			retMap.put("value", value);

			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：", e);
			e.printStackTrace();
		}
	}
	
	public String charge() {
		logger.info("支付宝手机网站支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("支付宝手机网站支付充值->得到参数：jsonString=" + jsonString);		
		
		String bankid = null;
		String paytype = null;
		String accesstype = null;
		String amt = null;
		String bankaccount = null;
		String channel = null;
		String subchannel = null;
		String userno = null;
		String ladderpresentflag = null;
		String cashiercode = null;
		String continuebettype = null;
		String orderid = null;
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额																				// (单位：分)
			bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "6";// 银行账户
			channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			cashiercode = map.containsKey("cashiercode") ? map.get("cashiercode").toString() : "";// cashiercode
			continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
		} catch (Exception e) {
			logger.error("支付宝手机网站支付充值->获取Json串参数异常", e);
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
			logger.info("支付宝手机网站支付充值->生成交易记录：url=" + url + " ,param=" + param.toString());	
		
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("支付宝手机网站支付充值->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!"0".equals(errorCode)) {				
				logger.info("支付宝手机网站支付充值->生成交易记录出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("支付宝手机网站支付充值->交易号ttransactionId=" + ttransactionId);
						
			String merid = chargeconfigService.getChargeconfig("partnerId");
			String bgreturl = chargeconfigService.getChargeconfig("alipay.wap.channel.bgreturl");
			String pagereturl = chargeconfigService.getChargeconfig("alipay.wap.channel.callbackurl");
			String seller = chargeconfigService.getChargeconfig("sellerAccountName");
			String returnurl = chargeconfigService.getChargeconfig("alipay.wap.channel.returnurl");
			String merchanturl = chargeconfigService.getChargeconfig("alipay.wap.channel.merchanturl"); 
			String subject = chargeconfigService.getChargeconfig("JinRuanTongSubject"); 
			
			Map<String, String> reqParams = prepareTradeRequestParamsMap(ttransactionId, amt, userno, cashiercode, merid, bgreturl, pagereturl, seller, returnurl, merchanturl, subject);
			
			String reqUrl = chargeconfigService.getChargeconfig("alipay.wap.channel.traderequrl");//WapChannelUtil.TRADE_REQ_URL
			String signAlgo = WapChannelUtil.SIGN_ALGO_RSA;
			String sign = sign(reqParams, signAlgo, WapChannelUtil.RSA_PRIVATE);
			reqParams.put("sign", sign);
			logger.info("支付宝手机网站支付充值->sign=" + sign);
			
			ResponseResult resResult = new ResponseResult();
			String businessResult = "";
			resResult = send(reqParams, reqUrl, signAlgo);
			
			if (resResult.isSuccess()) {
				businessResult = resResult.getBusinessResult();
			} else {
				logger.info("支付宝手机网站支付充值->出错信息:resResult.getErrorMessage().getDetail()=" + resResult.getErrorMessage().getDetail());	
				errorCode = ErrorCode.ERROR.value;
				this.printErrorJson(errorCode);
				return null;
			}
			
			DirectTradeCreateRes directTradeCreateRes = null;
			XMapUtil.register(DirectTradeCreateRes.class);
			directTradeCreateRes = (DirectTradeCreateRes) XMapUtil.load(new ByteArrayInputStream(businessResult.getBytes("UTF-8")));
			
			// 开放平台返回的内容中取出request_token
			String requestToken = directTradeCreateRes.getRequestToken();
			Map<String, String> authParams = prepareAuthParamsMap(requestToken, merid);
			
			String authSign = sign(authParams,signAlgo, WapChannelUtil.RSA_PRIVATE);//对调用授权请求数据签名
			authParams.put("sign", authSign);
			String redirectURL = getRedirectUrl(authParams, reqUrl);;
		
			if (!StringUtil.isNotBlank(redirectURL)) {
				logger.info("支付宝手机网站支付充值->redirectURL为空， redirectURL=" + redirectURL);
				errorCode = ErrorCode.ERROR.value;
				this.printErrorJson(errorCode);
				return null;
			}
			
			logger.info("支付宝手机网站支付充值->redirectURL=" + redirectURL);
			this.printJson(errorCode, redirectURL);
		} catch (Exception e) {
			logger.error("支付宝手机网站支付充值->执行过程中出现异常", e);
			e.printStackTrace();
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		logger.info("支付宝手机网站支付充值->结束");
		return null;
	}
	
	public static void main(String[] args) {
		String str = null;//new AlipayWapChannelChargeAction().getPayChannels("123");
		System.out.println("str=" + str);
	}
		
	public String getPayChannels() {
		logger.info("支付宝手机网站支付充值->获取支付前置列表->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("支付宝手机网站支付充值->获取支付前置列表->得到参数：jsonString=" + jsonString);
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			String outUser = map.containsKey("userno") ? map.get("userno").toString() : "";//userno
				
			Map<String, String> reqParams = new HashMap<String, String>();
		
			// 准备请求数据
			reqParams.put("service", WapChannelUtil.SERVICE_MOBILE_MERCHANT_PAYCHANNEL);// 支付前置服务名称
			reqParams.put("partner", chargeconfigService.getChargeconfig("partnerId")); // 合作商户ID  WapChannelUtil.PARTNER
			reqParams.put("sign_type", WapChannelUtil.SIGN_TYPE_MD5); // 签名类型
			reqParams.put("_input_charset", WapChannelUtil.INPUT_CHARSET);// 参数编码字符集
			if(!StringUtil.isBlank(outUser)){	          
	            reqParams.put("out_user", outUser); //外部用户ID
	        }
			String signData = ParameterUtil.getSignData(reqParams);// 待签名数据
			logger.info("signData=" + signData);
			String sign = MD5Signature.sign(signData, WapChannelUtil.KEY); // 签名
			reqParams.put("sign", sign);
			String params = this.mapToUrl(reqParams);
			logger.info("params=" + params);

			String businessResult = getPayChannelList(params);
			String isSuccess = getXmlValue(businessResult, WapChannelUtil.IS_SUCCESS);
			if (!isSuccess.equalsIgnoreCase(WapChannelUtil.T)) {
				String error = getXmlValue(businessResult, "error");// 获取错误码
				logger.error("支付宝手机网站支付充值->获取支付前置列表->失败，错误码=" + error);               
				errorCode = ErrorCode.ERROR.value;
				this.printErrorJson(errorCode);
				return null;
			}
			
			String result = getXmlValue(businessResult, "result");
			String sign2 = getXmlValue(businessResult, "sign");
			logger.info("result=" + result);
			logger.info("sign2=" + sign2);
			
			String content = "result=" + result;
		    if (!MD5Signature.verify(content, sign2, WapChannelUtil.KEY, WapChannelUtil.INPUT_CHARSET)) {
		    	logger.error("支付宝手机网站支付充值->获取支付前置列表->验证签名失败");
		    	errorCode = ErrorCode.ERROR.value;
				this.printErrorJson(errorCode);
				return null;
		    }
		    
		    this.printJson(errorCode, result);	        
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝手机网站支付充值->获取支付前置列表->出现异常：", e);
		}
		
		logger.info("支付宝手机网站支付充值->获取支付前置列表->结束");
		return null;
	}
	
	/**
     * 将Map中的数据组装成url
     * @param params
     * @return
     * @throws UnsupportedEncodingException 
     */
    private String mapToUrl(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (isFirst) {
                sb.append(key + "=" + URLEncoder.encode(value, "utf-8"));
                isFirst = false;
            } else {
                if (value != null) {
                    sb.append("&" + key + "=" + URLEncoder.encode(value, "utf-8"));
                } else {
                    sb.append("&" + key + "=");
                }
            }
        }
        return sb.toString();
    }
    
	private String getPayChannelList(String params) throws IOException {
		String url = WapChannelUtil.REQ_URL; // 支付前置服务器地址
		URL serverUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
		conn.setRequestMethod("GET"); // http get方式调用接口
		conn.setDoOutput(true);
		conn.connect();
		conn.getOutputStream().write(params.getBytes());

		InputStream is = conn.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}

		// String response = URLDecoder.decode(buffer.toString(), "utf-8");		
		String response = buffer.toString();// 得到返回数据		
		conn.disconnect();
		logger.info("response=" + response);
		return response;
	}
	
	private String getXmlValue(String xml, String name) {
		if (StringUtil.isBlank(xml) || StringUtil.isBlank(name)) {
			return "";
		}
		int start = xml.indexOf("<" + name + ">");
		start += (name.length() + 2);// 去掉本字符串和"<"、">"的长度
		int end = xml.indexOf("</" + name + ">");
		if (end > start && end <= (xml.length() - name.length() - 2)) {
			return xml.substring(start, end);
		} else {
			return "";
		}
	}
		
	private Map<String, String> prepareTradeRequestParamsMap(String orderId, String amt, String outUser, String cashierCode, String PARTNER, String NOTIFY_URL, String CALLBACK_URL, String SELLER, String RETURNURL, String MERCHANTURL, String SUBJECT) throws UnsupportedEncodingException {
		String partner = PARTNER;//WapChannelUtil.PARTNER;
		String seller = SELLER;//WapChannelUtil.SELLER;
		String notifyUrl = NOTIFY_URL;//WapChannelUtil.NOTIFY_URL;
		String callbackUrl = CALLBACK_URL;//WapChannelUtil.CALLBACK_URL;
		String returnUrl = RETURNURL;//WapChannelUtil.RETURN_URL;
		String merchantUrl = MERCHANTURL;//WapChannelUtil.MERCHANT_URL;
		String subject = SUBJECT;//WapChannelUtil.SUBJECT;
		String v = WapChannelUtil.V;
		String format = WapChannelUtil.FORMAT_XML;
		String secId = WapChannelUtil.SIGN_ALGO_RSA;
		String service = WapChannelUtil.SERVICE_ALIPAY_WAP_TRADE_CREATE_DIRECT;
		
		DecimalFormat df = new DecimalFormat("#0.00");
		String totalFee = df.format(Double.valueOf(amt) / 100);// 商品总价

		Map<String, String> requestParams = new HashMap<String, String>();
		if (StringUtil.isBlank(outUser)) {
			outUser = "";
		}

		// req_data的内容
		StringBuffer sbReqData = new StringBuffer();
		sbReqData.append("<direct_trade_create_req><subject>").append(subject)
				.append("</subject><out_trade_no>").append(orderId)
				.append("</out_trade_no><total_fee>").append(totalFee)
				.append("</total_fee><seller_account_name>").append(seller)
				.append("</seller_account_name><notify_url>").append(notifyUrl)
				.append("</notify_url><call_back_url>").append(returnUrl)
				.append("</call_back_url><out_user>").append(outUser)
				.append("</out_user><merchant_url>").append(merchantUrl)
				.append("</merchant_url>");

		// 如果cashierCode不为空就组装此参数
		if (StringUtil.isNotBlank(cashierCode)) {
			sbReqData.append("<cashier_code>").append(cashierCode)
					.append("</cashier_code>");
		}
		sbReqData.append("</direct_trade_create_req>");
		String reqData = sbReqData.toString();
		logger.info("reqData=" + reqData);

		requestParams.put("req_data", reqData);
		requestParams.put("req_id", System.currentTimeMillis() + "");
		requestParams.put("service", service);
		requestParams.put("sec_id", secId);
		requestParams.put("partner", partner);
		requestParams.put("format", format);
		requestParams.put("v", v);
		
		logger.info("requestParams=" + requestParams.toString());
		return requestParams;
	}
	
	private String sign(Map<String, String> reqParams,String signAlgo,String key) {
		String signData = ParameterUtil.getSignData(reqParams);
		String sign = "";
		try {
			sign = RSASignature.sign(signData, key, "");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("sign error:", e);
		}
		return sign;
	}
	
	private ResponseResult send(Map<String, String> reqParams, String reqUrl, String secId) throws Exception {
		String response = "";
		String invokeUrl = reqUrl;
		URL serverUrl = new URL(invokeUrl);
		HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();

		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.connect();
		String params = ParameterUtil.mapToUrl(reqParams);
		conn.getOutputStream().write(params.getBytes());

		InputStream is = conn.getInputStream();

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		response = URLDecoder.decode(buffer.toString(), "utf-8");
		conn.disconnect();
		logger.info("response=" + response);
		return praseResult(response, secId);
	}

	/**
	 * 解析支付宝返回的结果
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ResponseResult praseResult(String response, String secId) throws Exception {
		// 调用成功
		HashMap<String, String> resMap = new HashMap<String, String>();
		String v = ParameterUtil.getParameter(response, "v");
		String service = ParameterUtil.getParameter(response, "service");
		String partner = ParameterUtil.getParameter(response, "partner");
		String sign = ParameterUtil.getParameter(response, "sign");
		String reqId = ParameterUtil.getParameter(response, "req_id");
		resMap.put("v", v);
		resMap.put("service", service);
		resMap.put("partner", partner);
		resMap.put("sec_id", secId);
		resMap.put("req_id", reqId);
		String businessResult = "";
		ResponseResult result = new ResponseResult();
		if (response.contains("<err>")) {
			result.setSuccess(false);
			businessResult = ParameterUtil.getParameter(response, "res_error");
			// 转换错误信息
			XMapUtil.register(com.ruyicai.charge.alipay.client.security.ErrorCode.class);
			com.ruyicai.charge.alipay.client.security.ErrorCode errorCode = (com.ruyicai.charge.alipay.client.security.ErrorCode) XMapUtil.load(new ByteArrayInputStream(businessResult.getBytes("UTF-8")));
			result.setErrorMessage(errorCode);
		} else {
		    businessResult = ParameterUtil.getParameter(response, "res_data");
            result.setSuccess(true);            
            String resData= RSASignature.decrypt(businessResult, WapChannelUtil.RSA_PRIVATE);//对返回的res_data数据先用商户私钥解密
            result.setBusinessResult(resData);
            resMap.put("res_data", resData);            
    		String verifyData = ParameterUtil.getSignData(resMap);//获取待签名数据
    		logger.info("verifyData=" + verifyData);
    		boolean verified = RSASignature.doCheck(verifyData, sign, WapChannelUtil.RSA_ALIPAY_PUBLIC, WapChannelUtil.INPUT_CHARSET);//对待签名数据使用支付宝公钥验签名
    		if (!verified) {
    			throw new Exception("验证签名失败");
    		}
		}
		
		return result;
	}
	
	private Map<String, String> prepareAuthParamsMap(String requestToken, String partner) {
		Map<String, String> requestParams = new HashMap<String, String>();
		String reqData = "<auth_and_execute_req><request_token>" + requestToken + "</request_token></auth_and_execute_req>";
		requestParams.put("req_data", reqData);
		requestParams.put("sec_id", WapChannelUtil.SIGN_ALGO_RSA);
		requestParams.put("partner", partner);//requestParams.put("partner", WapChannelUtil.PARTNER);
		requestParams.put("format", WapChannelUtil.FORMAT_XML);
		requestParams.put("v", WapChannelUtil.V);
		requestParams.put("service", WapChannelUtil.SERVICE_ALIPAY_WAP_AUTH_AUTHANDEXECUTE);
		return requestParams;
	}
	
	private String getRedirectUrl(Map<String, String> reqParams,String reqUrl) throws Exception {
		String redirectUrl = reqUrl + "?";
		redirectUrl = redirectUrl + ParameterUtil.mapToUrl(reqParams);
		return redirectUrl;
	}

}
