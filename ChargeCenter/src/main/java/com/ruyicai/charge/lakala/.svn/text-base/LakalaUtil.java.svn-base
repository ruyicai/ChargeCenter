package com.ruyicai.charge.lakala;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.RandomUtil;

public class LakalaUtil {
	private Logger logger = Logger.getLogger(LakalaUtil.class);
	
	//public static String merchantName = ConfigUtil.getConfig("charge.properties", "lakala.mername");
	public final static String merchantId = ConfigUtil.getConfig("charge.properties", "lakala.merid");
	public final static String merchantProductName = ConfigUtil.getConfig("charge.properties", "lakala.merchantproductname");
	public final static String merchantOrderDesc = ConfigUtil.getConfig("charge.properties", "lakala.merchantorderdesc");
	public final static String version = ConfigUtil.getConfig("charge.properties", "lakala.version");
	public final static String macType = ConfigUtil.getConfig("charge.properties", "lakala.mactype");
	public final static String minCode = ConfigUtil.getConfig("charge.properties", "lakala.mincode");
	public final static String expiredTime = ConfigUtil.getConfig("charge.properties", "lakala.expiredtime");
	//public static String merchantPublicCerFile = ConfigUtil.getConfig("charge.properties", "lakala.publickey.filepath");//
	//public static String merchantPrivateCerFile = ConfigUtil.getConfig("charge.properties", "lakala.privatekey.filepath");//
	//public static String alias = ConfigUtil.getConfig("charge.properties", "lakala.privatekey.alias");//
	//public static String password = ConfigUtil.getConfig("charge.properties", "lakala.privatekey.password");//			
	public final static String pwd = ConfigUtil.getConfig("charge.properties", "lakala.password");//			
	public final static String backEndUrl = ConfigUtil.getConfig("charge.properties", "lakala.returnurl");
	public final static String requrl = ConfigUtil.getConfig("charge.properties", "lakala.requrl");
	public final static String androidUrl = ConfigUtil.getConfig("charge.properties", "lakala.androidurl");
	public final static String iphoneUrl = ConfigUtil.getConfig("charge.properties", "lakala.iphoneurl");
	public final static String merUrl = ConfigUtil.getConfig("charge.properties", "lakala.merurl");
	
	
	public final static String SEPARATOR = "|";
	public final static int RANDOM_NUM_BIT = 6;
	public final static String REQTYPE_UNOPAY = "unopay";
	
	private String createOriginalSign(String merchantOrderId, String merchantOrderAmt, String randnum, String MERID, String notifyurl) throws Exception {	
		//merchantId MERID
		//backEndUrl notifyurl
		StringBuffer sb = new StringBuffer();
		sb.append(version).append(SEPARATOR).append(MERID).append(SEPARATOR).append(pwd).append(SEPARATOR).append(merchantOrderId).append(SEPARATOR)
		.append(merchantOrderAmt).append(SEPARATOR).append(randnum).append(SEPARATOR).append(backEndUrl).append(SEPARATOR)
		.append(macType).append(SEPARATOR).append(minCode).append(SEPARATOR);
		
		logger.info("OriginalSign=" + sb.toString());
		return sb.toString();
	}
	
	public String createSign(String merchantOrderId, String merchantOrderAmt, String randnum, String MERID, String notifyurl) throws Exception {
		String originalSign = this.createOriginalSign(merchantOrderId, merchantOrderAmt, randnum, MERID, notifyurl);
		MD5Util md5 = new MD5Util();
		String mac = md5.md5(originalSign.getBytes());			
		logger.info("mac=" + mac);
		return mac;
	}
	
	public Map<String, String> getParams(String merchantOrderId, String merchantOrderAmt, String MERID, String url, String notifyurl) throws Exception {
		String randnum = RandomUtil.genRandomNum(RANDOM_NUM_BIT);
		String mac = this.createSign(merchantOrderId, merchantOrderAmt, randnum, MERID, notifyurl);	
		logger.info("randnum=" + randnum);
		
		String chargeTime = DateUtil.format(new Date());
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("VER", version);
		map.put("MERID", MERID);//merchantId
		map.put("ORDERID", merchantOrderId);
		map.put("AMOUNT", merchantOrderAmt);
		map.put("RANDNUM", randnum);
		map.put("MACTYPE", macType);
		map.put("MAC", mac);
		map.put("DESC", merchantOrderDesc);
		map.put("EXPIREDTIME", expiredTime);
		map.put("MINCODE", minCode);
		map.put("PRODUCTNAME", merchantProductName);		
		map.put("reqType", REQTYPE_UNOPAY);
		map.put("chargetime", chargeTime);
		map.put("url", url);//requrl
		map.put("notifyurl", notifyurl);//backEndUrl
		map.put("androidurl", androidUrl);
		map.put("iphoneurl", iphoneUrl);
				
		return map;
	}
	
	public String createSign(String originalSign) throws Exception {
		MD5Util md5 = new MD5Util();
		String mac = md5.md5(originalSign.getBytes());			
		logger.info("mac=" + mac);
		return mac;
	}
	
	public static void main(String[] args) {
		System.out.println("merchantId=" + merchantId);
	}
} 
