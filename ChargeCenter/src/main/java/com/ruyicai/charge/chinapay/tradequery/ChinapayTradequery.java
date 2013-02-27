package com.ruyicai.charge.chinapay.tradequery;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import chinapay.PrivateKey;
import chinapay.SecureLink;

import com.ruyicai.charge.action.TradeQueryAction;
import com.ruyicai.charge.chinapay.util.connection.CPHttpConnection;
import com.ruyicai.charge.chinapay.util.connection.Http;
import com.ruyicai.charge.chinapay.util.connection.HttpSSL;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.StringUtil;

public class ChinapayTradequery {
	private static Logger logger = Logger.getLogger(TradeQueryAction.class);
	
	public static final String URL = ConfigUtil.getConfig("charge.properties", "chinapay.single.trade.query.url");
	//public static final String URL = "http://payment-test.chinapay.com/QueryWeb/processQuery.jsp";
	public static final String TRANS_TYPE = ConfigUtil.getConfig("charge.properties", "chinapay.single.trade.query.transtype"); 
	public static final String VERSION = ConfigUtil.getConfig("charge.properties", "chinapay.single.trade.query.version"); 
	public static final String MER_ID = ConfigUtil.getConfig("charge.properties", "chinapay.merid");
	public static final String MER_KEY_PATH = "F:\\temp\\chinapay\\808080420206656\\MerPrK_808080420206656_20111027132709.key";//ConfigUtil.getConfig("charge.properties", "chinapay.merkey.filepath");
	public static final String MER_ID_2 = ConfigUtil.getConfig("charge.properties", "chinapay.merid2");
	public static final String MER_KEY_PATH_2 = "F:\\temp\\chinapay\\808080420206657\\MerPrK_808080420206657_20111027133313.key";//ConfigUtil.getConfig("charge.properties", "chinapay.merkey.filepath2");
	public static final String MER_ID_3 = ConfigUtil.getConfig("charge.properties", "chinapay.merid");//"808080101891701";//
	public static final String MER_KEY_PATH_3 = ConfigUtil.getConfig("charge.properties", "chinapay.merkey.filepath");//"F:\\temp\\chinapay\\808080101891701\\MerPrK_808080101891701_20111018155905.key";
	
	
	
	
	public static Map<String, String> singleQuery(String gateid, String ttransactionid, String transdate) {
		Map<String, String> map = new HashMap<String, String>();
		String errorCode = ErrorCode.OK.value;
		
		String merId = "";
		String merKeyPath = "";
		if (StringUtil.isEmpty(gateid)) {
			merId = MER_ID_3;
			merKeyPath = MER_KEY_PATH_3;
			logger.info("上海银联有卡订单查询->gateid=" + gateid);				
		} else {
			merId = MER_ID_2;
			merKeyPath = MER_KEY_PATH_2;
			logger.info("上海银联无卡订单查询->gateid=" + gateid);
		}
		String orderId = ttransactionid.substring(16);
		
		String chkValue = null;
		boolean buildOK = false;
		int KeyUsage = 0;
		PrivateKey key = new PrivateKey();
		try {
			buildOK = key.buildKey(merId, KeyUsage, merKeyPath);
		} catch (Exception e) {
			logger.error("上海银联订单查询->build error!:" + e);
			e.printStackTrace();				
		}
		if (!buildOK) {
			logger.error("上海银联订单查询->build error!");
			errorCode = ErrorCode.Charge_BuildKeyError.value;
			map.put("errorCode", errorCode);
			return map;
		}
		
		try {
			SecureLink sl = new SecureLink(key);
			chkValue = sl.Sign(merId + transdate + orderId + TRANS_TYPE);
		} catch (Exception e) {
			logger.error("上海银联订单查询->sign error!");
			e.printStackTrace();
			errorCode = ErrorCode.Charge_SignError.value;
			map.put("errorCode", errorCode);
			return map;				
		}
		logger.info("上海银联订单查询->chkValue=" + chkValue);		
		
		
		StringBuffer param = new StringBuffer();
		param.append("MerId=").append(merId).append("&TransType=").append(TRANS_TYPE).append("&OrdId=").append(orderId)
				.append("&TransDate=").append(transdate).append("&Version=").append(VERSION).append("&Resv=").append(" ")
				.append("&ChkValue=").append(chkValue);
		String result = "";
		try {
			logger.info("上海银联订单查询->URL=" + URL + ";param=" + param.toString());							
			String httpType = "SSL";
			String timeOut = "60000";
			result = sendHttpMsg(URL, param.toString(), httpType, timeOut);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上海银联订单查询->提交出错：", e);
		}
		logger.info("上海银联订单查询->返回 result=" + result);	
		
		map.put("errorCode", errorCode);
		map.put("result", result);
		return map;
	}
	
	/**
	 * 发送http post报文，并且接受响应信息
	 * @param strMsg 需要发送的交易报文,格式遵循httppost参数格式
	 * @return String 服务器返回响应报文,如果处理失败，返回空字符串
	 */
	private static String sendHttpMsg(String URL, String strMsg, String httpType, String timeOut) {
		String returnMsg = "";
		CPHttpConnection httpSend = null;
		if (httpType.equals("SSL")) {
			httpSend = new HttpSSL(URL, timeOut);
		} else {
			httpSend = new Http(URL, timeOut);
		}
		// 设置获得响应结果的限制
		httpSend.setLenType(0);
		// 设置字符编码
		httpSend.setMsgEncoding("GBK");
		int returnCode = httpSend.sendMsg(strMsg);
		if (returnCode == 1) {
			try {
				returnMsg = new String(httpSend.getReceiveData(), "GBK").trim();
				logger.info("接收到响应报文,returnMsg=[" + returnMsg + "]");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("[getReceiveData Error!]", e);
			}
		} else {
			logger.info(new StringBuffer("报文处理失败,失败代码=[").append(returnCode).toString());
		}
		return returnMsg;
	}

}
