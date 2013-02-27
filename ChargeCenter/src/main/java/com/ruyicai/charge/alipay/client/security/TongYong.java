package com.ruyicai.charge.alipay.client.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


public class TongYong {
	
  private final static Logger logger=Logger.getLogger(TongYong.class);
	
  
	//读取属性文件
	ResourceBundle rbint = ResourceBundle.getBundle("charge");
	
	//客户端配置
	private ClientConfig  clientConfig = new ClientConfig();

	//安全管理类
	private SecurityManagerImpl securityManager  = new SecurityManagerImpl();
    
	
	/**
	 * 调用alipay.wap.auth.authAndExecute服务的时候需要跳转到支付宝的页面，组装跳转url
	 * @param reqParams
	 * @return
	 * @throws Exception
	 */
	public String getRedirectUrl(Map<String, String> reqParams, String invokeUrl) throws Exception {
//		String redirectUrl = "http://115.124.16.62"+""+ ":" + clientConfig.getServerPort()
//		+ "/service/rest.htm?";
		String redirectUrl = invokeUrl;//rbint.getString("invokeUrl");
		redirectUrl = redirectUrl + ParameterUtil.mapToUrl(reqParams);
		return redirectUrl;
	}
	
	/**
	 * 准备alipay.wap.auth.authAndExecute服务的参数
	 * @param request
	 * @param requestToken
	 * @return
	 */
	public Map<String, String> prepareAuthParamsMap(HttpServletRequest request, String requestToken,String call_back_url, String partnerId) {
		Map<String, String> requestParams = new HashMap<String, String>();
		try {
			String reqData = "<auth_and_execute_req><request_token>" + requestToken
			+ "</request_token></auth_and_execute_req>";
			requestParams.put("req_data", reqData);
			requestParams.putAll(prepareCommonParams(partnerId));
			//客户端返回call_back_url
			//String callbackUrl = request.getParameter("call_back_url").trim();
			//回调url，如果在参数中加入该值，交易支付成功之后会返回"success"和请求的request_token
			//String callBackUrl = "http://localhost:8080/waptest0504/";
//		    ////String callbackUrl = rbint.getString("call_back_url");
			String callbackUrl = call_back_url;
			requestParams.put("call_back_url",callbackUrl);
			requestParams.put("service", "alipay.wap.auth.authAndExecute");
		} catch (Exception e) {
			logger.error("第二次请求参数构造 出现异常  Exception1:"+e.toString()+",Exception2:"+e.getMessage());
		}
		
		return requestParams;
	}
	
	 /**
	 * 准备通用参数
	 * @param request
	 * @return
	 */
	public Map<String, String> prepareCommonParams(String partnerId) {
		Map<String, String> commonParams = new HashMap<String, String>();
		try {
			String service = rbint.getString("service");//获得接口名称
			commonParams.put("service", service);
			String secId = rbint.getString("secId");//商户的安全配置号
			commonParams.put("sec_id", secId);
			String partner = partnerId;//rbint.getString("partnerId");//商户的partnerId
			commonParams.put("partner", partner);
//			String callBackUrl = rbint.getString("callback_url");//返回地址
//			commonParams.put("call_back_url", callBackUrl);
			String format = rbint.getString("format");//请求参数格式
			commonParams.put("format", format);
			String v = rbint.getString("version");//接口版本号 
			commonParams.put("v", v);
			logger.info("prepareCommonParams=="+commonParams);
		} catch (Exception e) {
			logger.info("获取通用参数出现异常  Exception1:"+e.toString()+",Exception2:"+e.getMessage());
		}
		
		return commonParams;
	}
    
	
	/**
	 * 调用支付宝开放平台的服务
	 * @param reqParams 请求参数
	 * @return
	 * @throws Exception
	 */
	public ResponseResult send(Map<String, String> reqParams) throws Exception {
		String response = "";
		String invokeUrl =rbint.getString("invokeUrl");//获得请求地址
		
//		String invokeUrl = clientConfig.getServerUrl()+ "/service/rest.htm?";
		
		logger.info("第一次请求:invokeUrl=="+invokeUrl);
        try {
        	ResourceBundle resource = ResourceBundle.getBundle("charge");
        	String zhengShuLuJing=resource.getString("ZFBZhengShu");//获得支付证书路径
        	logger.info("支付证书路径zhengShuLuJing="+zhengShuLuJing);
        	//System.setProperty("javax.net.ssl.trustStore",zhengShuLuJing.trim());
        	logger.info("开始获取证书密码");
        	//System.setProperty("javax.net.ssl.trustStorePassword", "jrtruyicai");//证书密码
        	
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("获得支付宝证书出现异常Exception:"+e.toString());
		}
		logger.info("开始请求第一次链接");
		URL serverUrl = new URL(invokeUrl);
		HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();

		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.connect();
		String params = ParameterUtil.mapToUrl(reqParams);
		conn.getOutputStream().write(params.getBytes());

		InputStream is = conn.getInputStream();
		logger.info("开始请求第一次链接结束");
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		
		logger.info("buffer=" + buffer.toString());
		response = URLDecoder.decode(buffer.toString(), "utf-8");
		in.close();
		is.close();
		conn.disconnect();
		
		////logger.info("开始请求第一次链接支付宝返回信息buffer="+buffer.toString());
		
		return praseResult(response);
	}

	/**
	 * 解析支付宝返回的结果
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ResponseResult praseResult(String response) throws Exception {
		//调用成功
		logger.info("response======"+response);
		
		HashMap<String, String> resMap = new HashMap<String, String>();
		String v = ParameterUtil.getParameter(response, "v");
		String service = ParameterUtil.getParameter(response, "service");		
		String partner = ParameterUtil.getParameter(response, "partner");
		String secId = ParameterUtil.getParameter(response, "sec_id");
		String sign = ParameterUtil.getParameter(response, "sign");
		String reqId = ParameterUtil.getParameter(response, "req_id");
		resMap.put("v", v);
		resMap.put("service", service);
		resMap.put("partner", partner);
		resMap.put("sec_id", secId);
		resMap.put("req_id", reqId);
		
		logger.info("resMap===="+resMap);
		//        resMap.put("reqId", reqId);
		//        resMap.put("sessionId", sessionId);
		String businessResult = "";
		ResponseResult result = new ResponseResult();
		if (response.contains("<err>")) {
			result.setSuccess(false);
			businessResult = ParameterUtil.getParameter(response, "res_error");

			//转换错误信息
			XMapUtil.register(ErrorCode.class);
			ErrorCode errorCode = (ErrorCode) XMapUtil.load(new ByteArrayInputStream(businessResult
					.getBytes("UTF-8")));
			result.setErrorMessage(errorCode);

			resMap.put("res_error", ParameterUtil.getParameter(response, "res_error"));
		} else {
			logger.info("支付宝第一次返回值加密前的值 response==="+response);
			businessResult = ParameterUtil.getParameter(response, "res_data");//从返回参数中返回res_data值
			
			logger.info("支付宝第一次返回值加密前的值res_data==="+businessResult);
			
			businessResult = decryptResData(businessResult);//对businessResult进行解密
			result.setSuccess(true);
			result.setBusinessResult(businessResult);
			resMap.put("res_data", businessResult);
		}
		String verifyData = ParameterUtil.getSignData(resMap);// 参数排序 
		
		logger.info("对支付宝第一次请求返回值进行排序verifyData=="+verifyData);
		
		boolean verified = securityManager.verify(clientConfig.getSignAlgo(), verifyData, sign,
				clientConfig.getAlipayVeriPubKey());//验证支付宝开放平台签名的公钥

		if (!verified) {
			throw new Exception("验证签名失败");
		}
		return result;
	}
    /**
	 * 对参数进行签名
	 * @param reqParams
	 * @return
	 */
	public String sign(Map<String, String> reqParams) {
		String signData = ParameterUtil.getSignData(reqParams);
		String sign = "";
		
		String getPrikey=clientConfig.getPrikey();
		logger.info("私钥 getPrikey==="+getPrikey);
		
		try {
			sign = securityManager.sign(clientConfig.getSignAlgo(), signData, clientConfig
					.getPrikey());
		} catch (Exception e1) {
			logger.error("参数签名出现异常  Exception1:"+e1.toString()+",Exception2:"+e1.getMessage());
		}
		return sign;
	}
    
	/**
	 * 使用自己的私钥解密返回的结果，只需要对res_data的内容解密
	 * @param resData
	 * @return
	 * @throws Exception
	 */
	public String decryptResData(String resData) throws Exception {
		String data ;
		String getPrikey=clientConfig.getPrikey();
		logger.info("私钥 clientConfig.getPrikey()======"+getPrikey+"");
		data = securityManager.decrypt(clientConfig.getEncryptAlgo(), resData, clientConfig
				.getPrikey());
		logger.info("解密后的支付宝第一次返回值date===="+data);		
				
		return data;
	}
	
}
