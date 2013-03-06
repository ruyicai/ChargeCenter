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

import com.ruyicai.charge.alipay.tradequery.AlipayConfig;
import com.ruyicai.charge.alipay.tradequery.AlipayService;
import com.ruyicai.charge.alipay.tradequery.SingleTradeQueryXML;
import com.ruyicai.charge.chinapay.tradequery.ChinapayTradequery;
import com.ruyicai.charge.chinapay.tradequery.PaymentType;
import com.ruyicai.charge.chinapay.tradequery.TradeStatus;
import com.ruyicai.charge.dna.v2.pay.DNATransactionClientService;
import com.ruyicai.charge.lakala.MD5Util;
import com.ruyicai.charge.liantonghuajian.LthjUtil;
import com.ruyicai.charge.liantonghuajian.XmlHttpConnection;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.StringUtil;
import com.ruyicai.charge.util.XMLUtil;

public class TradeQueryAction implements ServletRequestAware, ServletResponseAware {
	@Autowired 
	ChargeconfigService chargeconfigService;
	
	@Autowired 
	DNATransactionClientService dnaTransactionClientService;
	
	private final static int TIME_OUT = 6000;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(TradeQueryAction.class);
	
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
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(map));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：", e);
		}
	}
	
	public String alipay() {
		logger.info("支付宝订单查询->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("支付宝订单查询->得到参数：jsonString=" + jsonString);		
		
		String tradeno = null;
		String ttransactionid = null;
				
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			tradeno = map.containsKey("tradeno") ? map.get("tradeno").toString() : "";//支付宝交易号
			ttransactionid = map.containsKey("ttransactionid") ? map.get("ttransactionid").toString() : "";//交易ID
			
			/**			
			if (StringUtil.isEmpty(tradeno)) {
				logger.info("支付宝订单查询->获取Json串中tradeno为空");
				errorCode = ErrorCode.TradeQuery_tradenoIsEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}*/
			
			/**
			if (StringUtil.isEmpty(ttransactionid)) {
				logger.info("支付宝订单查询->获取Json串中ttransactionid为空");
				errorCode = ErrorCode.Ttransaction_IdEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}*/	
			if (StringUtil.isEmpty(tradeno) && StringUtil.isEmpty(ttransactionid)) {
				logger.info("支付宝订单查询->获取Json串中ttransactionid和tradeno都为空");
				errorCode = ErrorCode.TradeQuery_transnoOrTransactionidIsEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝订单查询->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		try {
			String partner = chargeconfigService.getChargeconfig("partnerId");//ConfigUtil.getConfig("charge.properties", "zfbwebpartnerId"); //支付宝合作伙伴id
			String key = chargeconfigService.getChargeconfig("zfbwebkey");//ConfigUtil.getConfig("charge.properties", "zfbwebkey"); // 支付宝安全校验码
			String requrl = chargeconfigService.getChargeconfig("alipay.single.trade.query.url");//ConfigUtil.getConfig("charge.properties", "alipay.single.trade.query.url");
			String xml = AlipayService.PostXml(partner, ttransactionid, tradeno, AlipayConfig.INPUT_CHARSET, key, AlipayConfig.SIGN_TYPE, requrl);
			logger.info("xml=" + xml);
			
			Map<String, String> map = new HashMap<String, String>();
			map = SingleTradeQueryXML.xml2Map(xml);
			if (map.containsKey("trade_status")) {
				map.put("trade_status2", map.get("trade_status"));
				map.put("trade_status", TradeStatus.getMemo(map.get("trade_status")));				
			}
			if (map.containsKey("payment_type")) {
				map.put("payment_type", PaymentType.getMemo(map.get("payment_type")));
			}		
			map.put("errorCode", errorCode);
			this.printJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝订单查询->出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		return null;
	}
	
	public String chinapay() {
		logger.info("上海银联订单查询->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("上海银联订单查询->得到参数：jsonString=" + jsonString);		
		
		String gateid = null;
		String ttransactionid = null;
		String transdate = null;		
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			gateid = map.containsKey("gateid") ? map.get("gateid").toString() : "";//gateid
			ttransactionid = map.containsKey("ttransactionid") ? map.get("ttransactionid").toString() : "";//商户交易ID
			transdate = map.containsKey("transdate") ? map.get("transdate").toString() : "";//订单交易日期			
			
			if (StringUtil.isEmpty(ttransactionid)) {
				logger.info("上海银联有卡订单查询->获取Json串中ttransactionid为空");
				errorCode = ErrorCode.Ttransaction_IdEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}
			if (StringUtil.isEmpty(transdate)) {
				logger.info("上海银联有卡订单查询->获取Json串中transdate为空");
				errorCode = ErrorCode.TradeQuery_transdateIsEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上海银联有卡订单查询->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		try {			
			Map<String, String> map = ChinapayTradequery.singleQuery(gateid, ttransactionid, transdate);
			logger.info("map=" + map);
			//map.put("errorCode", errorCode);
			this.printJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上海银联订单查询->出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		return null;
	}
	
	public String dnapay() {
		logger.info("DNA订单查询->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("DNA订单查询->得到参数：jsonString=" + jsonString);		
		
		String ttransactionid = null;
				
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);		
			ttransactionid = map.containsKey("ttransactionid") ? map.get("ttransactionid").toString() : "";//商户交易ID		
			
			if (StringUtil.isEmpty(ttransactionid)) {
				logger.info("DNA订单查询->获取Json串中ttransactionid为空");
				errorCode = ErrorCode.Ttransaction_IdEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DNA订单查询->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		try {			
			Map<String, String> map = dnaTransactionClientService.orderQuery(ttransactionid);
			logger.info("map=" + map);
			//map.put("errorCode", errorCode);
			this.printJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DNA订单查询->出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		return null;
	}
	
	
	public String lthjpay() {
		logger.info("联通华建银联手机支付订单查询->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("联通华建银联手机支付订单查询->得到参数：jsonString=" + jsonString);		
		
		String ttransactionid = null;
		String ordertime = null;		
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			ttransactionid = map.containsKey("ttransactionid") ? map.get("ttransactionid").toString() : "";//商户交易ID
			ordertime = map.containsKey("ordertime") ? map.get("ordertime").toString() : "";//商户订单时间			
			
			if (StringUtil.isEmpty(ttransactionid)) {
				logger.info("联通华建银联手机支付订单查询->获取Json串中ttransactionid为空");
				errorCode = ErrorCode.Ttransaction_IdEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}
			if (StringUtil.isEmpty(ordertime)) {
				logger.info("联通华建银联手机支付订单查询->获取Json串中ordertime为空");
				ordertime =  this.getPlattimeOfTransaction(ttransactionid);
				logger.info("联通华建银联手机支付订单查询->根据交易ID得到的ordertime为" + ordertime);
				if (null == ordertime) {
					errorCode = ErrorCode.TradeQuery_transdateIsEmpty.value;	
					this.printErrorJson(errorCode);
					return null;
				}
				long l = Long.parseLong(ordertime);;
				Date date = new Date(l);
				ordertime = DateUtil.format("yyyyMMddHHmmss", date);
				logger.info("联通华建银联手机支付订单查询->ordertime=" + ordertime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("联通华建银联手机支付订单->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
				
		
		try {			
			LthjUtil lthjUtil = new LthjUtil();
			String queryOrderSubmitMessage = lthjUtil.createQueryOrderSubmitMessage(ttransactionid, ordertime);
			String url = chargeconfigService.getChargeconfig("liantonghuajian.requrl");//ConfigUtil.getConfig("charge.properties", "liantonghuajian.requrl");
			logger.info("联通华建银联手机支付订单查询->提交订单查询：url=" + url + ",param=" + queryOrderSubmitMessage);
			XmlHttpConnection xmlhttpconnection =  new XmlHttpConnection(url, TIME_OUT);
			xmlhttpconnection.sendMsg(queryOrderSubmitMessage);
			String result = xmlhttpconnection.getReMeg();			
			logger.info("联通华建银联手机支付订单查询->提交订单查询，返回 result=" + result);			
			Map<String, String> map = XMLUtil.xml2Map(result);
			map.put("errorCode", errorCode);
			logger.info("map=" + map);
			this.printJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("联通华建银联手机支付订单查询->出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		return null;
	}
	
	private String getPlattimeOfTransaction(String ttransactionid) {
		String plattime = null;
		String url = ConfigUtil.getConfig("lottery.properties", "lotteryGetTtransactionById");
		String param = "id="  + ttransactionid;
		
		String result = null;
		try {
			logger.info("根据交易ID获取交易信息->请求地址url：" + url +  "，请求参数param：" + param);
			result = HttpRequest.doPostRequest(url, param);
		} catch (Exception e) {
			logger.error("根据交易ID获取交易信息->出现错误:", e);
			e.printStackTrace();
		}
		
		logger.info("根据交易ID获取交易信息->返回结果result=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		if(!"0".equals(errorCode)){
			if (errorCode.equals(ErrorCode.Ttransaction_NotExists.value)) {
				logger.info("根据交易ID获取交易信息->交易号" + param + "不存在");
			} else {
				logger.info("根据交易ID获取交易信息->出现错误->errorCode:" + errorCode + "；result:" + result);						
			}			
			return plattime;			
		}
		
		Map<String,Object> mapValue = (Map<String, Object>) mapResult.get("value");
		if (mapValue!=null && !mapValue.isEmpty()) {
			plattime = mapValue.get("plattime").toString();
		}		
		return plattime;
	}
	
	public String lakalapay() {
		logger.info("拉卡拉支付订单查询->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("拉卡拉支付订单查询->得到参数：jsonString=" + jsonString);		
		
		String ttransactionid = null;
		String ordertime = null;		
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			ttransactionid = map.containsKey("ttransactionid") ? map.get("ttransactionid").toString() : "";//商户交易ID
			ordertime = map.containsKey("ordertime") ? map.get("ordertime").toString() : "";//商户订单时间			
			
			if (StringUtil.isEmpty(ttransactionid)) {
				logger.info("拉卡拉支付订单查询->获取Json串中ttransactionid为空");
				errorCode = ErrorCode.Ttransaction_IdEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}
			if (StringUtil.isEmpty(ordertime)) {
				logger.info("拉卡拉支付订单查询->获取Json串中ordertime为空");
				ordertime =  this.getPlattimeOfTransaction(ttransactionid);
				logger.info("拉卡拉支付订单查询->根据交易ID得到的ordertime为" + ordertime);
				if (null == ordertime) {
					errorCode = ErrorCode.TradeQuery_transdateIsEmpty.value;	
					this.printErrorJson(errorCode);
					return null;
				}
				long l = Long.parseLong(ordertime);;
				Date date = new Date(l);
				ordertime = DateUtil.format("yyyyMMdd", date);
				logger.info("拉卡拉支付订单查询->ordertime=" + ordertime);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("拉卡拉支付订单->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
				
		
		try {
			String params = this.getLakalaReqParams(ordertime, ttransactionid);
			String url = chargeconfigService.getChargeconfig("lakala.requrl");;
			logger.info("拉卡拉支付订单查询->提交订单查询：url=" + url + ",param=" + params);
			String result = HttpRequest.doPostRequest(url, params);		
			logger.info("拉卡拉支付订单查询->提交订单查询，返回 result=" + result);			
			//Map<String, String> map = XMLUtil.xml2Map(result);
			//map.put("errorCode", errorCode);
			//logger.info("map=" + map);
			//this.printJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("拉卡拉支付订单查询->出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		return null;
	}
	
	private String getLakalaReqParams(String orderDate, String orderId) {
		String ret = null;
		String verId = "20060301";
		String merId = "3G100000302";
		String macType = "2";
		String merKey = "RYCPHJYT9OU2YT3WEQOKL0EWDFV3BNM";
		String retMode = "1";
		
		StringBuilder sbVerifyStringOriginal = new StringBuilder();
		sbVerifyStringOriginal.append("ver_id=").append(verId).append("&mer_id=").append(merId).append("&order_date=").append(orderDate)
		.append("&order_id=").append(orderId).append("&mac_type=").append(macType).append("&mer_key=").append(merKey);
	    logger.info("verifyStringOriginal=" + sbVerifyStringOriginal.toString());
	    MD5Util md5 = new MD5Util();
	    String verifyString = md5.md5(sbVerifyStringOriginal.toString().getBytes());
	    logger.info("verifyString=" + verifyString);
	    
	    StringBuilder sbParams = new StringBuilder();
	    sbParams.append("ver_id=").append(verId).append("&mer_id=").append(merId).append("&order_id=").append(orderId)
	    .append("&verify_string=").append(verifyString).append("&order_date=").append(orderDate)
	    .append("&mac_type=").append(macType).append("&ret_mode=").append(retMode);
	    
	    ret = sbParams.toString();
	    logger.info("ret=" + ret);
		return ret;
	}
	
	public static void main(String[] args) {
//		String partner = ConfigUtil.getConfig("charge.properties", "zfbwebpartnerId"); //支付宝合作伙伴id
//		String key = ConfigUtil.getConfig("charge.properties", "zfbwebkey"); // 支付宝安全校验码
//		String requrl = ConfigUtil.getConfig("charge.properties", "alipay.single.trade.query.url");
//		String ttransactionid = "BJ201203020000000000000003423936";
//		String tradeno = "2012030267420579";
//		
//		System.out.println("partner=" + partner);
//		System.out.println("key=" + key);
//		
//		String xml = "";
//		try {
//			xml = AlipayService.PostXml(partner, ttransactionid, tradeno, AlipayConfig.INPUT_CHARSET, key, AlipayConfig.SIGN_TYPE, requrl);
//		} catch (Exception e) {			
//			e.printStackTrace();
//		}
//		
//		System.out.println("xml=" + xml);
		
		//======拉卡拉测试
		try {
			String url = "http://pgs.lakala.com.cn/tradeSearch/ndsinglesearch";
			String orderId = "BJ201210160000000000000004396253";
			String orderDate = "20121016";
			String params = new TradeQueryAction().getLakalaReqParams(orderDate, orderId);
			System.out.println("拉卡拉支付订单查询->提交订单查询：url=" + url + ",param=" + params);
			String result = HttpRequest.doPostRequest(url, params);
			System.out.println("拉卡拉支付订单查询->提交订单查询，返回 result=" + result);
			String[] arr = result.split("\\|");
			int i=0;
			for (String str : arr) {
				i++;
				System.out.println("拉卡拉支付订单查询->提交订单查询，返回 str" + i + "=" + str);
			}
			
			
			String accountDate = arr[0];
			String amount = arr[1];
			String payMethod = arr[2];
			String merId = arr[3];
			String orderDate2 = arr[4];
			String orderId2 = arr[5];
			String paySeq = arr[6];
			String result2 = arr[7];
			String verId = arr[8];
			String verifyString = arr[9];
			String macType = "2";
			String merKey = "RYCPHJYT9OU2YT3WEQOKL0EWDFV3BNM";
			
			StringBuffer  sb = new StringBuffer();
			sb.append("ver_id=").append(verId).append("&mer_id=").append(merId).append("&order_date=").append(orderDate)
			.append("&order_id=").append(orderId).append("&amount=").append(amount).append("&result=").append(result2)
			.append("&mac_type=").append(macType).append("&mer_key=").append(merKey);
			
			System.out.println("拉卡拉支付订单查询->提交订单查询，sb=" + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
