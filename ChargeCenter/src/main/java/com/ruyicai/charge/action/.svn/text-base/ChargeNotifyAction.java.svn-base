package com.ruyicai.charge.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.alipay.client.security.ClientConfig;
import com.ruyicai.charge.alipay.client.security.DirectTradeCreateResNotify;
import com.ruyicai.charge.alipay.client.security.SecurityManager;
import com.ruyicai.charge.alipay.client.security.SecurityManagerImpl;
import com.ruyicai.charge.alipay.client.security.XMapUtil;
import com.ruyicai.charge.nineteenpay.NineteenPayService;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.CheckURL;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DigestUtil;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.MSBankDecryptAndEnvelop;
import com.ruyicai.charge.util.SignatureHelper;
import com.ruyicai.charge.util.TransactionMapUtil;
import com.ruyicai.charge.yeepay.YeePayWebCardService;

public class ChargeNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	private Logger logger = Logger.getLogger(ChargeNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired
	ChargeUtil chargeUtil;
	@Autowired
	NineteenPayService nineteenPayService;
	@Autowired
	YeePayWebCardService yeePayWebCardService;
	
	
	/**
	 * 民生银行支付通知处理
	 * 
	 * @return
	 */
	public String msBankChargeWebServlet() {
		logger.info("民生银行支付通知处理开始");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String payresult = "";// 获得民生银行返回的加密信息
		try {
			payresult = request.getParameter("payresult");
			logger.info("银行的加密信息  payresult=" + payresult);
		} catch (Exception e) {
			logger.error("获得银行加密信息出现异常 Exception1:" + e.toString());
			return null;
		}

		String envolopData = "";// 解密后的数据
		String keyMiMa = "";// 商户私钥密码
		try {
			keyMiMa = chargeconfigService.getChargeconfig("msBank_KeyMiMa");//ConfigUtil.getConfig("charge.properties","msBank_KeyMiMa");// 商户私钥密码
			logger.info("商户私钥密码  keyMiMa=" + keyMiMa);
			envolopData = MSBankDecryptAndEnvelop.Decrypt(payresult, keyMiMa);
			logger.info("民生银行 解密后的数据  envolopData=" + envolopData);
		} catch (Exception e) {
			logger.error("解密后出现错误  Exception1:", e);
			return null;
		}

		String[] dataArray = null;
		String billNo = "";// 订单号
		String txAmt = "";// 交易金额
		String billstatus = "";// 状态
		String corpID = "";// 商户Id
		try {
			dataArray = envolopData.split("\\|");// 获得支付宝数组数据
			billNo = dataArray[0];
			logger.info("交易号  billNo=" + billNo);
			txAmt = dataArray[2];
			logger.info("交易金额  txAmt=" + txAmt);
			billstatus = dataArray[5];
			logger.info("交易状态  billstatus=" + billstatus);
			corpID = dataArray[1];
			logger.info("商户 corpID=" + corpID);
		} catch (Exception e) {
			logger.error("获得数组出现异常 ", e);
			return null;
		}
		
		String amt = BigDecimal.valueOf(Float.parseFloat(txAmt)).multiply(new BigDecimal(100)).toString();		
		if (!"0".equals(billstatus)) {// 交易不成功
			logger.error("交易失败   billstatus=" + billstatus);
			String result = null;
			try {
				String lotteryFailUrl = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl")
				result = HttpRequest.doPostRequest(
						lotteryFailUrl,
						"ttransactionid="
								+ TransactionMapUtil.getTransactionid(billNo)
								+ "&bankordertime="
								+ sdf.format(new Date())
								+ "&retcode="
								+ billstatus
								+ "&amt="
								+ amt);
			} catch (IOException e) {
				logger.error("交易失败处理出现异常：" + e);
				e.printStackTrace();
			}
			

			logger.info("TransactionMapUtil.getTransactionid(billNo):" + TransactionMapUtil.getTransactionid(billNo));
			logger.info("amt:"+ amt);
			
			logger.info("民生银行充值通知交易失败处理返回(value=0为成功) result=" + result);	
			
		} else {
			logger.info("交易成功 billstatus=" + billstatus);
			String result = null;			
			String drawamt = ChargeUtil.getDrawamt(amt);
			try {
				String lotterySuccessUrl = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl")
				result = HttpRequest.doPostRequest(
						lotterySuccessUrl,
						"ttransactionid="
								+ TransactionMapUtil.getTransactionid(billNo)
								+ "&bankorderid="
								+ billNo
								+ "&bankordertime="
								+ sdf.format(new Date())
								+ "&banktrace="
								+ billNo
								+ "&retcode="
								+ billstatus
								+ "&retmemo=0&amt="
								+ amt
								+ "&drawamt="
								+ drawamt);
				logger.info("result=" + result);				
				chargeUtil.afterCharge(result);				
			} catch (IOException e) {
				logger.error("交易成功处理出现异常：" + e);
				e.printStackTrace();
			}
			
			logger.info("民生银行充值通知交易成功处理返回(value=0为成功) result=" + result);
		}

		logger.info("民生银行支付通知处理结束");
		return null;
	}

	/**
	 * 支付宝语音充值通知处理
	 * 
	 * @return
	 */
	public String zfbYuyinChargeServlet() {
		logger.info("支付宝语音充值通知处理开始");
		String key = null;
		String ttransactionId = "";
		String total_fee = "";// 交易总金
		try {
			key = chargeconfigService.getChargeconfig("zfbYuyinKey");//ConfigUtil.getConfig("charge.properties", "zfbYuyinKey");// 金软通密钥
			String partnerid = chargeconfigService.getChargeconfig("partnerId");//ConfigUtil.getConfig("charge.properties", "zfbYuyinPartnerId");// 商户编号
			String validateUrl = chargeconfigService.getChargeconfig("zfbValidateUrl");//ConfigUtil.getConfig("charge.properties", "zfbValidateUrl");// 语音充值验证地址
			// **********************************************************************************
			// 如果您服务器不支持https交互，可以使用http的验证查询地址
			// ***注意下面的注释，如果在测试的时候导致response等于空值的情况，请将下面一个注释，打开上面一个验证连接
			// String alipayNotifyURL =
			// "https://www.alipay.com/cooperate/gateway.do?service=notify_verify"
			String alipayNotifyURL = validateUrl + "partner=" + partnerid
					+ "&notify_id=" + request.getParameter("notify_id");
			logger.info("支付宝语音充值验证的 请求地址 alipayNotifyURL=" + alipayNotifyURL);

			// 获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的
			String responseTxt = CheckURL.check(alipayNotifyURL);
			logger.info("支付宝语音充值验证返回的 结果 responseTxt=" + responseTxt);
		} catch (Exception e) {
			logger.error("发送支付宝语音充值 验证请求出现异常  Exception1:" + e.toString()
					+ ",Exception2:" + e.getMessage());
		}
		// **********************************************************************************

		Map params = new HashMap();
		// 获得POST 过来参数设置到新的params中
		Map requestParams = request.getParameterMap();
		logger.info("支付宝语音充值返回的参数 requestParams=" + requestParams.toString());
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化（现在已经使用）
			// valueStr=new String
			// (valueStr.getBytes("ISO-8859-1"),"GBK");///没有用
			params.put(name, valueStr);
			// logger.info("支付宝语音充值返回的参数"+name+"="+valueStr);
		}
		String mysign = "";

		try {
			mysign = SignatureHelper.sign(params, key);
			logger.info("本地的mysign=" + mysign);
		} catch (Exception e) {
			logger.error("本地校验支付宝语音充值参数返回出现异常  Exception1:" + e.toString()
					+ ",Exception2:" + e.getMessage());
			mysign = "";
		}
		String sign = "";
		String tradeStatus = "";// 交易状态
		String tradeNo = "";// 外部交易号
		String buyer_id = "";// 买家账号
		try {
			sign = request.getParameter("sign");// 获得支付宝的sign
			tradeStatus = request.getParameter("trade_status");// 获得支付宝交易的状态
			total_fee = request.getParameter("total_fee");// 交易总金额
			ttransactionId = request.getParameter("out_trade_no");// 外部交易号
			tradeNo = request.getParameter("trade_no");// 外部交易号
			buyer_id = request.getParameter("buyer_id");// 买家账号
			logger.info("支付宝返回的sign=" + sign);
			logger.info("支付宝返回的交易状态trade_status=" + tradeStatus);
			logger.info("支付宝返回的交易金额  total_fee=" + total_fee);
			logger.info("支付宝返回的外部交易号 out_trade_no=" + ttransactionId);
			logger.info("支付宝返回的交易号 trade_no=" + tradeNo);
			logger.info("支付宝返回的买家账号 buyer_id=" + buyer_id);
		} catch (Exception e) {
			logger.error("获取支付宝返回的sign出现异常  Exception1:" + e.toString()
					+ ",Exception2:" + e.getMessage());
			// sign="";
		}
		try {
			// //设置回写流对象
			PrintWriter out = response.getWriter();

			if (!mysign.equals(sign)) {// 判断sign校验是否有效
				logger.error("sign校验有误  本地mysign=" + mysign + ",支付宝返回的的sign="
						+ sign);
				out.println("fail");
				return null;
			}

			String amt = BigDecimal.valueOf(Float.parseFloat(total_fee)).multiply(new BigDecimal(100)).toString();
			String drawamt = ChargeUtil.getDrawamt(amt);
			
			if (!"TRADE_FINISHED".equals(tradeStatus)
					&& !"TRADE_SUCCESS".equals(tradeStatus)) {// 判断支付宝返回的交易状态是否是成功的
				logger.error("支付宝返回的交易状态不为成功  tradeStatus=" + tradeStatus);
				String result = null;
				try {
					String lotteryFailUrl = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl")
					result = HttpRequest
							.doPostRequest(
									lotteryFailUrl,
									"ttransactionid="
											+ ttransactionId
											+ "&bankordertime="
											+ new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new Date())
											+ "&banktrace= "
											+ "&retcode="
											+ tradeStatus
											+ "&retmemo="
											+ tradeStatus
											+ "&amt="
											+ amt);
				} catch (IOException e) {
					logger.error("交易失败处理出现异常：" + e);
					e.printStackTrace();
				}
				
				logger.info("交易失败处理返回result:" + result);
				return null;
			}
			out.println("success");// 回写success流
			StringBuffer param = new StringBuffer();
			param.append("ttransactionid=")
					.append(ttransactionId)
					.append("&bankorderid=")
					.append(tradeNo)
					.append("&bankordertime=")
					.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date()))
					.append("&banktrace=")
					.append(" ")
					.append("&retcode=")
					.append(tradeStatus)
					.append("&retmemo=")
					.append(tradeStatus)
					.append("&amt=")
					.append(amt)
					.append("&fee=0")
					.append("&drawamt=")
					.append(drawamt);
			String lotterySuccessUrl = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl")
			logger.info("充值成功请求：url="
					+ lotterySuccessUrl + ",param=" + param.toString());
			String result = HttpRequest
					.doPostRequest(lotterySuccessUrl, param.toString());
			// String result = "{\"value\":\"null\",\"errorCode\":\"0\"}";
			logger.info("充值成功处理返回: result=" + result);			
			chargeUtil.afterCharge(result);
		} catch (Exception e) {
			logger.error("返回出现异常", e);
			return null;
		}
		logger.info("用户充值成功 交易Id=" + ttransactionId + ",充值金额  amt=" + total_fee);
		logger.info("支付宝语音充值通知处理结束");
		return null;
	}

	public String zfbWebChargeServlet() {
		logger.info("支付宝WEB通知开始处理!");
		// **********************************************************************************
		// 如果您服务器不支持https交互，可以使用http的验证查询地址
		/*
		 * 注意下面的注释，如果在测试的时候导致response等于空值的情况，请将下面一个注释，打开上面一个验证连接，另外检查本地端口，
		 * 请挡开80或者443端口
		 */
		// String alipayNotifyURL =
		// "https://www.alipay.com/cooperate/gateway.do?service=notify_verify"
		String partner = "";
		String privateKey = "";
		String alipayNotifyURL = "";
		String sign = "";// 签名验证
		String status = "";// 支付宝交易状态
		try {
			partner = chargeconfigService.getChargeconfig("partnerId");//ConfigUtil.getConfig("charge.properties", "zfbwebpartnerId"); // 支付宝合作伙伴id (账户内提取)
			privateKey = chargeconfigService.getChargeconfig("zfbwebkey");//ConfigUtil.getConfig("charge.properties", "zfbwebkey"); // 支付宝安全校验码(账户内提取)
			alipayNotifyURL = chargeconfigService.getChargeconfig("zfbwebnotifycheckurl") 
					+ "partner="
					+ partner
					+ "&notify_id=" + request.getParameter("notify_id");//ConfigUtil.getConfig("charge.properties", "zfbwebnotifycheckurl")
			sign = request.getParameter("sign");
			status = request.getParameter("trade_status");
			if (status != null) {
				status = status.trim();
			}
		} catch (Exception e) {
			logger.error("构造支付宝通知验证请求出现异常", e);
			return null;
		}

		// 获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的
		String responseTxt = "";
		try {
			logger.info("查询支付宝通知的正确性  alipayNotifyURL=" + alipayNotifyURL);
			responseTxt = CheckURL.check(alipayNotifyURL);// 发送请求查看交易通知的正确性
			logger.info("交易验证的结果  responseTxt=" + responseTxt);
		} catch (Exception e) {
			logger.error("获得支付宝ATN返回结果出现异常", e);
			return null;
		}
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();// 获得通知里面的参数
		logger.info("支付宝返回的map集合参数  requestParams=" + requestParams);
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();// 获得支付宝通知参数名称
			String[] values = (String[]) requestParams.get(name);// 获得支付宝参数值
			String valueStr = "";

			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);

		}

		String mysign = SignatureHelper.sign(params, privateKey);// 本地获得支付宝sign参数
		logger.info("支付宝返回的参数 params=" + params);
		// 最好是在异步做日志动作，可以记录mysign、sign、resposenTXT和其他值，方便以后查询错误。
		// if(!"true".equals(responseTxt)){////判断支付宝通知的合法性
		// logger.info("本交易不是支付宝的交易 responseTxt="+responseTxt);
		// return ;
		// }
		logger.info("支付宝返回的校验 sign=" + sign);
		logger.info("本地校验的 sign值=" + mysign);
		String ttransactionId = request.getParameter("out_trade_no");// 外部交易号
		String tradeNo = request.getParameter("trade_no");// 支付宝交易号
		String total_fee = request.getParameter("total_fee");// 充值金额
		String buyer_id = request.getParameter("buyer_id");// 买家在支付宝Id
		try {
			// //设置回写流对象
			PrintWriter out = response.getWriter();
			if (!mysign.equals(sign)) {// sign 校验有误
				/* 可以在不同状态下获取订单信息，操作商户数据库使数据同步 */
				logger.info("校验sign失败");
				out.println("fail");
				return null;
			}
			
			String amt = BigDecimal.valueOf(Float.parseFloat(total_fee)).multiply(new BigDecimal(100)).toString();
			String drawamt = ChargeUtil.getDrawamt(amt);
			// 交易处理失败
			if (!"TRADE_FINISHED".equals(status)
					&& !"TRADE_SUCCESS".equals(status)) {
				// 在这里可以写入数据处理,
				// 注意一定要返回给支付宝一个成功的信息
				logger.error("交易处理失败 支付宝trade_status=" + status);
				String result = null;
				try {
					String lotteryFailUrl = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl")
					result = HttpRequest
							.doPostRequest(
									lotteryFailUrl,
									"ttransactionid="
											+ ttransactionId
											+ "&bankordertime="
											+ new SimpleDateFormat(
													"yyyy-MM-dd HH:mm:ss")
													.format(new Date())
											+ "&banktrace= "
											+ "&retcode="
											+ status
											+ "&retmemo="
											+ status
											+ "&amt="
											+ amt);
				} catch (IOException e) {
					logger.error("交易失败处理出现异常：" + e);
					e.printStackTrace();
				}
				
				logger.info("交易失败处理返回result:" + result);
				return null;
			}
			
			logger.info("支付宝返回的交易成功状态  trade_status=" + status);
			out.println("success");// 回写success流

			StringBuffer param = new StringBuffer();
			param.append("ttransactionid=")
					.append(ttransactionId)
					.append("&bankorderid=")
					.append(tradeNo)
					.append("&bankordertime=")
					.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date()))
					.append("&banktrace=")
					.append(" ")
					.append("&retcode=")
					.append(status)
					.append("&retmemo=")
					.append(status)
					.append("&amt=")
					.append(amt)
					.append("&drawamt=")
					.append(drawamt);
			String lotterySuccessUrl = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl")
			logger.info("充值成功请求：url="
					+ lotterySuccessUrl + ",param=" + param.toString());
			String result = HttpRequest
					.doPostRequest(lotterySuccessUrl, param.toString());
			// String result = "{\"value\":\"null\",\"errorCode\":\"0\"}";
			logger.info("充值成功处理返回: result=" + result);
			chargeUtil.afterCharge(result);
		} catch (Exception e) {
			logger.error("返回出现异常", e);
			return null;
		}
		logger.info("用户充值成功 交易Id=" + ttransactionId + ",充值金额  amt=" + total_fee);
		logger.info("支付宝WEB充值通知处理结束");
		return null;
	}
	
	public String zfbWapChargeServlet() {
		logger.info("支付宝WAP通知处理->开始");	
		logger.info("支付宝WAP通知处理->支付宝访问我们的地址url=" + request.getContextPath());
		
		// 获得通知参数
		Map map = request.getParameterMap();
		Map<String, String> returnMap = new HashMap<String, String>();// 获得map参数
		logger.info("支付宝WAP通知处理->获得通知参数:map=" + map.toString());
		
		// 获得通知签名
		String sign = "";// 获得签名
		String service = "";// 获得创建交易的接口名称
		String v = "";// 接口版本号
		String sec_id = "";// 安全配置号
		// 获得待验签名的数据
		String notify_data = "";
		try {
			sign = (String) ((Object[]) map.get("sign"))[0];
			service = (String) ((Object[]) map.get("service"))[0];
			v = (String) ((Object[]) map.get("v"))[0];
			sec_id = (String) ((Object[]) map.get("sec_id"))[0];
			notify_data = (String) ((Object[]) map.get("notify_data"))[0];

			returnMap.put("sign", sign);
			returnMap.put("service", service);
			returnMap.put("v", v);
			returnMap.put("sec_id", sec_id);
			returnMap.put("notify_data", notify_data);

		} catch (Exception e) {
			logger.error("支付宝WAP通知处理->获得支付宝通知参数出现异常  Exception:" + e.getMessage() + ",Exception2:" + e.toString());
			return null;
		}
		
		ClientConfig clientConfig = new ClientConfig();
		SecurityManager securityManager = new SecurityManagerImpl();
		try {
			logger.info("支付宝WAP通知处理->解密前的notify_data=" + notify_data);
			notify_data = securityManager.decrypt(clientConfig.getEncryptAlgo(), notify_data, clientConfig.getPrikey());// 对支付宝返回的业务参数解密xml数据
			returnMap.put("notify_data", notify_data);// 向map中重新赋值notify_data
			logger.info("支付宝WAP通知处理->解密后的notify_data=" + notify_data);

		} catch (Exception e) {
			logger.error("支付宝WAP通知处理->解密xml形式的支付宝通知业务数据出现异常 Exception:" + e.getMessage() + ",Exception2:" + e.toString());
			return null;
		}
		
		String verifyData = "";
		try {
			// 构建需验证签名的数据
			verifyData = "service=" + service + "&v=" + v + "&sec_id=" + sec_id + "&notify_data=" + notify_data;
			// verifyData = ParameterUtil.getSignData(returnMap);//对支付宝通知参数进行排序
			logger.info("支付宝WAP通知处理->支付宝通知参数排序后的通知参数verifyData=" + verifyData.toString());
		} catch (Exception e) {
			logger.error("支付宝WAP通知处理->支付宝通知参数排序出现异常  Exception:" + e.getMessage() + ",Exception2:" + e.toString());
			return null;
		}

		boolean verified = false;
		// 对支付宝验签名
		try {
			logger.info("支付宝WAP通知处理->支付宝的 sign=" + sign);
			verified = securityManager.verify(clientConfig.getSignAlgo(), verifyData, sign, clientConfig.getAlipayVeriPubKey());
			logger.info("支付宝WAP通知处理->验证签名状态  verified=" + verified);
		} catch (Exception e) {
			logger.error("支付宝WAP通知处理->验证签名出现异常 Exception:" + e.getMessage() + ",Exception2:" + e.toString());
			return null;
		}
		
		if (!verified) {
			logger.error("支付宝WAP通知处理->支付宝签名验证失败");
			return null;
		}

		// 解析支付宝返回的xml业务数据
		DirectTradeCreateResNotify directTradeCreateRes = null;
		XMapUtil.register(DirectTradeCreateResNotify.class);
		try {
			directTradeCreateRes = (DirectTradeCreateResNotify) XMapUtil.load(new ByteArrayInputStream(notify_data.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			logger.error("支付宝WAP通知处理->解析xml形式的支付宝通知业务数据出现异常 Exception:" + e.getMessage() + ",Exception2:" + e.toString());
		} catch (Exception e) {
			logger.error("支付宝WAP通知处理->解析xml形式的支付宝通知业务数据出现异常 Exception:" + e.getMessage()
					+ ",Exception2:" + e.toString());
		}

		String tradeStatus = directTradeCreateRes.tradeStatus;
		String outTradeNo = directTradeCreateRes.getOutTradeNo();// 支付宝通知业务参数中的金软通平台的交易Id
		logger.info("支付宝WAP通知处理->支付宝通知业务参数中的金软通平台的交易Id=" + outTradeNo);
		String totalFee = directTradeCreateRes.getTotalFee();
		logger.info("支付宝WAP通知处理->支付宝通知业务参数中的金软通平台的交易totalFee=" + totalFee);
		
		String url = "";
		String param = "";
		String result = null;
		String amt = BigDecimal.valueOf(Float.parseFloat(totalFee)).multiply(new BigDecimal(100)).toString();
		String drawamt = ChargeUtil.getDrawamt(amt);
		// 支付宝充值用户充值金额是否付款成功
		if (!"TRADE_FINISHED".equals(tradeStatus) && !"TRADE_SUCCESS".equals(tradeStatus)) {
			logger.error("支付宝WAP通知处理->交易处理失败， 用户未支付充值金额");			
			logger.error("支付宝WAP通知处理->交易处理失败tradeStatus=" + tradeStatus);			
			try {
				url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
				param = "ttransactionid="
					+ outTradeNo
					+ "&bankordertime="
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
					+ "&banktrace= "
					+ "&retcode="
					+ tradeStatus
					+ "&retmemo="
					+ tradeStatus
					+ "&amt="
					+ amt;
				logger.info("支付宝WAP通知处理->交易处理失败, url=" + url +  "；param=" + param);
				result = HttpRequest.doPostRequest(url, param);;
				logger.info("支付宝WAP通知处理->交易处理失败处理，返回result:" + result);
			} catch (IOException e) {
				logger.error("交易失败处理出现异常：", e);
				e.printStackTrace();
			}			
			return null;
		}
		
		//创建向支付宝回写success流对象		
		try {
			PrintWriter out;
			out = response.getWriter();
			//向支付宝回写success
			out.print("success");
		} catch (IOException e) {			
			e.printStackTrace();
			logger.error("支付宝WAP通知处理->向支付宝回写success出错："  + e);
			return null;
		}	
		
		String tradeNo = directTradeCreateRes.getTradeNo();//支付宝的交易Id
		try {
			StringBuffer param2 = new StringBuffer();
			param2.append("ttransactionid=")
					.append(outTradeNo)
					.append("&bankorderid=")
					.append(tradeNo)
					.append("&bankordertime=")
					.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
					.append("&banktrace=")
					.append(" ")
					.append("&retcode=")
					.append(tradeStatus)
					.append("&retmemo=")
					.append(tradeStatus)
					.append("&amt=")
					.append(amt)
					.append("&drawamt=")
					.append(drawamt);

			url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
			param = param2.toString();
			logger.info("支付宝WAP通知处理->充值成功请求：url=" + url + ",param2=" + param);
			result = HttpRequest.doPostRequest(url, param);
			logger.info("支付宝WAP通知处理->充值成功处理返回: result=" + result);
			chargeUtil.afterCharge(result);
		} catch (IOException e) {
			logger.error("交易成功处理出现异常：" + e);
			e.printStackTrace();
		}
		
		logger.info("支付宝WAP通知处理->结束");
		return null;
	}
	
	public void nineteenPayChargeServlet() {
		try {
			nineteenPayService.notify(request, response);
		} catch (Exception e) {
			logger.error("19pay通知处理出现错误", e);
		}
	}
	
	public void yeePayWebCardChargeServlet() {
		try {
			yeePayWebCardService.notify(request, response);
		} catch (Exception e) {
			logger.error("YeePayWebCard通知处理出现错误", e);
		}
	}
	
	public void yeepayWapBankChargeServlet() {
		logger.info("yeepayWapBank通知处理->开始");	
		String call_back_url = "";// //后台回调地址
		try {
			String p1_MerId = request.getParameter("p1_MerId");// 银行返回信息中的商户编号
			String r0_Cmd = request.getParameter("r0_Cmd");
			String r1_Code = request.getParameter("r1_Code");
			String r2_TrxId = request.getParameter("r2_TrxId");
			String r3_Amt = request.getParameter("r3_Amt");
			String r4_Cur = request.getParameter("r4_Cur");
			String r5_Pid = request.getParameter("r5_Pid");
			String r6_Order = request.getParameter("r6_Order");
			String r7_Uid = request.getParameter("r7_Uid");
			String r8_MP = request.getParameter("r8_MP");
			String r9_BType = request.getParameter("r9_BType");
			String rb_BankId = request.getParameter("rb_BankId");
			String ro_BankOrderId = request.getParameter("ro_BankOrderId");//银行订单号
			String rp_PayDate = request.getParameter("rp_PayDate");
			String rq_CardNo = request.getParameter("rq_CardNo");
			String ru_Trxtime = request.getParameter("ru_Trxtime");
			String hmac = request.getParameter("hmac");
			String sessionId = request.getParameter("sessionId");
			logger.info("yeepayWapBank通知处理->易宝返回的sessionId=" + sessionId);
			
			try {
				call_back_url = request.getParameter("call_back_url");
				logger.info("yeepayWapBank通知处理->易宝返回的异步返回地址参数:call_back_url=" + call_back_url);
			} catch (Exception e) {
				logger.error("yeepayWapBank通知处理->获得回调地址出现异常Exception:" + e.toString());
				call_back_url = "";
				return;
			}
			
			logger.info("yeepayWapBank通知处理->p1_MerId:" + p1_MerId + ",r0_Cmd:"
					+ r0_Cmd + ",r1_Code:" + r1_Code + ",r2_TrxId:" + r2_TrxId
					+ ",r3_Amt:" + r3_Amt + ",r4_Cur:" + r4_Cur + ",r5_Pid:"
					+ r5_Pid + ",r6_Order:" + r6_Order + ",r7_Uid:" + r7_Uid
					+ ",r8_MP:" + r8_MP + ",r9_BType:" + r9_BType
					+ ",rb_BankId:" + rb_BankId + ",ro_BankOrderId:"
					+ ro_BankOrderId + ",rp_PayDate:" + rp_PayDate
					+ ",rq_CardNo:" + rq_CardNo + ",ru_Trxtime:" + ru_Trxtime
					+ ",hmac:" + hmac);
			if (!"1".equals(r9_BType) && !"2".equals(r9_BType)) { // 只有类型为1的处理。
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ ",r9_BType=" + r9_BType + ",类型不为1!");
				return;
			}
			if (r6_Order == null || "".equals(r6_Order)) { //查找订单号是否为空
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order + "订单号为空");
				return;
			}
			
			
			// 验证mac值
			String keyValue = chargeconfigService.getChargeconfig("keyValue");//ConfigUtil.getConfig("charge.properties", "keyValue");// 商家密钥
			StringBuffer sValue = new StringBuffer();
			sValue.append(p1_MerId);
			sValue.append(r0_Cmd);
			sValue.append(r1_Code);
			sValue.append(r2_TrxId);
			sValue.append(r3_Amt);
			sValue.append(r4_Cur);
			sValue.append(r5_Pid);
			sValue.append(r6_Order);
			sValue.append(r7_Uid);
			sValue.append(r8_MP);
			sValue.append(r9_BType);
			String myHmac = DigestUtil.hmacSign(sValue.toString(), keyValue);
			if (!myHmac.equals(hmac)) {
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ ",hmac值验证失败,hmac:" + hmac + ",myHmac:" + myHmac);
				return;
			}
			
			String merId = chargeconfigService.getChargeconfig("yeepay_MerId");//ConfigUtil.getConfig("charge.properties", "p1_MerId"); // 本地文件存储的商户编号
			if (merId == null || "".equals(merId)) {// 本地文件存储的商户编号不允许为空
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ ",本地存储的商户编号为空,merId:" + merId);
				return;
			}
			if (p1_MerId == null || "".equals(p1_MerId)) {// 银行返回信息中的商户编号不允许为空
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ ",银行返回的商户编号为空,p1_MerId:" + p1_MerId);
				return;
			}
			if (!merId.equals(p1_MerId)) {// 银行返回信息中的商户编号必须与本地文件存储的商户编号
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ ",银行返回的商户编号与本地文件存储的商户编号不一致,merId:" + merId
						+ ",p1_MerId:" + p1_MerId);
				return;
			}
			if (r1_Code == null || "".equals(r1_Code)) { // 银行返回状态信息不能为空
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ ",银行返回的状态信息r1_Code:" + r1_Code + "为空。");
				return;
			}

			int type = Integer.parseInt(ConfigUtil.getConfig(
					"charge.properties", "despositByBankType"));// 银行卡充值交易类型
			int type2 = Integer.parseInt(ConfigUtil.getConfig(
					"charge.properties", "despositByBankType"));// 点卡充值交易类型
			
			int toSuccess = 0 ;//是否需要响应
			if ("2".equals(r9_BType)) { //点对点响应
				PrintWriter pw = response.getWriter();
				pw.write("success");
				logger.info("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ "回写success流。");
				toSuccess = 1;// 点对点通讯，需要更新充值金额。返回银行success串，但无需跳转页面
			} else { // 页面响应

			}

			String url = "";
			String param = "";
			String result = null;
			
			String amt = BigDecimal.valueOf(Float.parseFloat(r3_Amt)).multiply(new BigDecimal(100)).toString();
			String drawamt = ChargeUtil.getDrawamt(amt);
			if(!"1".equals(r1_Code)){//判断银行充值是否失败 
				logger.info("yeepayWapBank通知处理->返回码 r1_Code=" + r1_Code);
				logger.error("yeepayWapBank通知处理->r6_Order:" + r6_Order + ",银行充值失败");
				//1为成功，其余为失败
				
				try {
					url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
					param = "ttransactionid="
						+ r6_Order
						+ "&bankordertime="
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
						+ "&banktrace=" + r2_TrxId
						+ "&retcode="
						+ r1_Code
						+ "&retmemo="
						+ r1_Code
						+ "&amt="
						+ amt;
					logger.info("yeepayWapBank通知处理->交易处理失败, url=" + url +  "；param=" + param);
					result = HttpRequest.doPostRequest(url, param);
					logger.info("yeepayWapBank通知处理->交易处理失败处理，返回json:" + result);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				
			} else {
				try {
					StringBuffer param2 = new StringBuffer();
					param2.append("ttransactionid=")
							.append(r6_Order)
							.append("&bankorderid=")
							.append(ro_BankOrderId)
							.append("&bankordertime=")
							.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
							.append("&banktrace=")
							.append(r2_TrxId)
							.append("&retcode=")
							.append(r1_Code) 
							.append("&retmemo=")
							.append(r1_Code)
							.append("&amt=")
							.append(amt)
							.append("&drawamt=")
							.append(drawamt);

					url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
					param = param2.toString();
					logger.info("yeepayWapBank通知处理->充值成功请求：url=" + url + ",param2=" + param);
					result = HttpRequest.doPostRequest(url, param);
					logger.info("yeepayWapBank通知处理->充值成功处理返回: result=" + result);
					chargeUtil.afterCharge(result);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}			
			
			//点对点通讯，需要更新充值金额。但无需跳转页面
			if (toSuccess == 1) {
				logger.info("yeepayWapBank通知处理->r6_Order:" + r6_Order
						+ " 点对点响应，支付订单已支付! 已发送success信息给银行!");
				return;
			}
			
			// 回调地址判断
			if (call_back_url == null || call_back_url.trim().length() == 0) {
				response.sendRedirect(chargeconfigService.getChargeconfig("return_url_web") + "?sessionId=" + sessionId);//return_url_web
			} else {
				response.sendRedirect(chargeconfigService.getChargeconfig("return_url_web") + "?sessionId=" + sessionId);//return_url_web
			}
			
		} catch (Exception e) {
			logger.error("yeepayWapBank通知处理->出现异常Exception:" + e.toString());			
			e.printStackTrace();
			return;
		}


		
		
		logger.info("yeepayWapBank通知处理->结束");	
	}
	

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}
}
