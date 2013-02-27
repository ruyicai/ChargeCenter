package com.ruyicai.charge.liantonghuajian;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;


import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.XMLUtil;

public class LthjUtil {
	private static Logger logger = Logger.getLogger(LthjUtil.class);
	
	private static String merchantName = ConfigUtil.getConfig("charge.properties", "liantonghuajian.mername");
	private static String merchantId = ConfigUtil.getConfig("charge.properties", "liantonghuajian.merid");
	private static String merchantOrderDesc = ConfigUtil.getConfig("charge.properties", "liantonghuajian.merchantorderdesc");
	private static String transTimeout = "";
	private static String merchantPublicCerFile = ConfigUtil.getConfig("charge.properties", "liantonghuajian.publickey.filepath");
	private static String merchantPrivateCerFile = ConfigUtil.getConfig("charge.properties", "liantonghuajian.privatekey.filepath");//
	private static String alias = ConfigUtil.getConfig("charge.properties", "liantonghuajian.privatekey.alias");
	private static String password = ConfigUtil.getConfig("charge.properties", "liantonghuajian.privatekey.password");			
	private static String backEndUrl = ConfigUtil.getConfig("charge.properties", "liantonghuajian.returnurl");
	private static String qianzhiPublicCerFile = ConfigUtil.getConfig("charge.properties", "liantonghuajian.qianzhi.publickey.filepath");
	
	private final static String TRANSTYPE_QUERYORDER = "01";

	public String createOrderSubmitMessage(String merchantOrderId, String merchantOrderAmt, String merchantOrderTime, String merid, String notifyurl) {				
		//String merchantOrderTime = DateUtil.format("yyyyMMddHHmmss", new Date());		
		//merchantId merid
		//backEndUrl notifyurl
		String originalSign7 = CreateOriginalSign7(merchantName, merid, merchantOrderId, merchantOrderTime, merchantOrderAmt, 
				merchantOrderDesc, transTimeout);				
		String xmlSign7 = createSign(originalSign7, alias, password, getMerchantPrivateCerFileInputStream());		
		String ret = createOrderSubmitXML(merchantName, merid, merchantOrderId, merchantOrderTime, merchantOrderAmt, merchantOrderDesc, transTimeout, notifyurl, xmlSign7, getMerchantPublicCer());
		return ret;
	}
	
	private String createOrderSubmitXML(String merchantName, String merchantId, String merchantOrderId, String merchantOrderTime, 
			String merchantOrderAmt, String merchantOrderDesc, String transTimeout, String backEndUrl, String sign, String merchantPublicCer) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8' ?>")
				.append("<upomp  application='SubmitOrder.Req' version='1.0.0'>")
				.append("<merchantName>").append(merchantName).append("</merchantName>")
				.append("<merchantId>").append(merchantId).append("</merchantId>")
				.append("<merchantOrderId>").append(merchantOrderId).append("</merchantOrderId>")
				.append("<merchantOrderTime>").append(merchantOrderTime).append("</merchantOrderTime>")
				.append("<merchantOrderAmt>").append(merchantOrderAmt).append("</merchantOrderAmt>")
				.append("<merchantOrderDesc>").append(merchantOrderDesc).append("</merchantOrderDesc>")
				.append("<transTimeout>").append(transTimeout).append("</transTimeout>")
				.append("<backEndUrl>").append(backEndUrl).append("</backEndUrl>")
				.append("<sign>").append(sign).append("</sign>")
				.append("<merchantPublicCert>").append(merchantPublicCer).append("</merchantPublicCert>")
				.append("</upomp>");
		logger.info("OrderSubmitXML=" + sb.toString());
		return sb.toString();	
	}
	
	private String createLanchPayXML(String merchantId, String merchantOrderId, String merchantOrderTime, String backEndUrl, String sign) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8' ?>")
				.append("<upomp  application='LanchPay.Req' version='1.0.0'>")
				.append("<merchantId>").append(merchantId).append("</merchantId>")
				.append("<merchantOrderId>").append(merchantOrderId).append("</merchantOrderId>")
				.append("<merchantOrderTime>").append(merchantOrderTime).append("</merchantOrderTime>")				
				.append("<backEndUrl>").append(backEndUrl).append("</backEndUrl>")
				.append("<sign>").append(sign).append("</sign>")				
				.append("</upomp>");
		logger.info("createLanchPayXML=" + sb.toString());
		return sb.toString();	
	}

	private String CreateOriginalSign7(String merchantName, String merchantId, String merchantOrderId, String merchantOrderTime, 
			String merchantOrderAmt, String merchantOrderDesc, String transTimeout) {
		StringBuilder sb = new StringBuilder();
		sb.append("merchantName=").append(merchantName).append("&merchantId=").append(merchantId)
				.append("&merchantOrderId=").append(merchantOrderId).append("&merchantOrderTime=").append(merchantOrderTime)
				.append("&merchantOrderAmt=").append(merchantOrderAmt).append("&merchantOrderDesc=").append(merchantOrderDesc)
				.append("&transTimeout=").append(transTimeout);
		return sb.toString();
	}
	
	public String createLanchPayMessage(String merchantOrderId, String merchantOrderTime, String merid, String notifyurl) {
		//merchantId merid
		//backEndUrl notifyurl
		String originalSign3 = CreateOriginalSign3(merid, merchantOrderId, merchantOrderTime);				
		String xmlSign3 = createSign(originalSign3, alias, password, getMerchantPrivateCerFileInputStream());		
		String ret = createLanchPayXML(merid, merchantOrderId, merchantOrderTime, notifyurl, xmlSign3);
		return ret;
	}

	private String CreateOriginalSign3(String merchantId, String merchantOrderId, String merchantOrderTime) {
		StringBuilder sb = new StringBuilder();
		sb.append("merchantId=").append(merchantId).append("&merchantOrderId=").append(merchantOrderId).append("&merchantOrderTime=")
		.append(merchantOrderTime);
		return sb.toString();
	}
		
	private String createSign(String originalString, String alias, String password, InputStream privateSign) {
		try {
			byte[] signsMD5 = MD5(originalString);
			byte[] signsRSA = rsaEncode(signsMD5, alias, password, privateSign);
			return new String(Base64.encode(signsRSA));
		} catch (Exception e) {
			logger.error("createSign error：", e);
		}
		return null;
	}

	private byte[] MD5(String src) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(src.getBytes("utf-8"));
			byte[] digest = messageDigest.digest();
			return digest;
		} catch (NoSuchAlgorithmException e) {
			logger.error("MD5 error, NoSuchAlgorithmException:", e);
			return null;
		} catch (UnsupportedEncodingException e) {
			logger.error("MD5 error, UnsupportedEncodingException:", e);
			return null;
		}

	}

	private byte[] rsaEncode(byte[] signsRSA, String alias, String pwd, InputStream dataSign) {		
		try {
			KeyStore store = KeyStore.getInstance("PKCS12");
			InputStream inStream = dataSign;
			store.load(inStream, pwd.toCharArray());
			inStream.close();
	
			PrivateKey pKey = (PrivateKey) store.getKey(alias, pwd.toCharArray());
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pKey);

			return cipher.doFinal(signsRSA);
		} catch (Exception e) {
			logger.error("rsaEncode error:", e);
		}

		return null;
	}
		
	private InputStream getMerchantPrivateCerFileInputStream() {
		InputStream merchantPrivateCerFileInputStream = null;
		try {
			merchantPrivateCerFileInputStream = new FileInputStream(merchantPrivateCerFile);
		} catch (FileNotFoundException e) {
			logger.error("getMerchantPrivateCerFileInputStream error:", e );
			return null;
		}
		return merchantPrivateCerFileInputStream;
	}
	
	private InputStream getPublicCerFileInputStream(String publicCerFile) {
		InputStream merchantPrivateCerFileInputStream = null;
		try {
			merchantPrivateCerFileInputStream = new FileInputStream(publicCerFile);
		} catch (FileNotFoundException e) {
			logger.error("getPublicCerFileInputStream error:", e );
			return null;
		}
		return merchantPrivateCerFileInputStream;
	}
	
	private String getMerchantPublicCer() {
		String merchantPublicCer = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream inStream = getPublicCerFileInputStream(merchantPublicCerFile);
			Certificate c = cf.generateCertificate(inStream);
			inStream.close();
			merchantPublicCer = new String(Base64.encode(c.getEncoded()));
					
		} catch (Exception e) {
			logger.error("getMerchantPublicCer error:", e);
		}
		return merchantPublicCer;
	}
	
	private String CreateNotitfyOriginalSign(String transType, String merchantId, String merchantOrderId, 
			String merchantOrderAmt, String settleDate, String setlAmt, String setlCurrency, String converRate,
			String cupsQid, String cupsTraceNum, String cupsTraceTime, String cupsRespCode, String cupsRespDesc, String respCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("transType=").append(transType).append("&merchantId=").append(merchantId)
				.append("&merchantOrderId=").append(merchantOrderId).append("&merchantOrderAmt=").append(merchantOrderAmt)
				.append("&settleDate=").append(settleDate).append("&setlAmt=").append(setlAmt).append("&setlCurrency=").append(setlCurrency)
				.append("&converRate=").append(converRate).append("&cupsQid=").append(cupsQid).append("&cupsTraceNum=").append(cupsTraceNum)
				.append("&cupsTraceTime=").append(cupsTraceTime).append("&cupsRespCode=").append(cupsRespCode)
				.append("&cupsRespDesc=").append(cupsRespDesc).append("&respCode=").append(respCode);
		logger.info("sb=" + sb.toString());
		return sb.toString();
	}
	
	private byte[] getMD5(String sign, String publicCerFile) {
		byte[] ret = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream inStream = getPublicCerFileInputStream(publicCerFile);
			Certificate c = cf.generateCertificate(inStream);
			inStream.close();
			byte[] rsaEncode = Base64.decode(sign);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, c.getPublicKey());
			ret = cipher.doFinal(rsaEncode);
		} catch (Exception e) {
			logger.error("getMD5 error:", e);
		}
		return ret;
	}
	
	private byte[] CreateNotitfyMessageMD5(Map<String, String> map) {
		String transType = map.get("transType").toString();
		String merchantId = map.get("merchantId").toString();
		String merchantOrderId = map.get("merchantOrderId").toString();	
		String merchantOrderAmt = map.get("merchantOrderAmt").toString();
		String settleDate = map.get("settleDate").toString();
		String setlAmt = map.get("setlAmt").toString();
		String setlCurrency = map.get("setlCurrency").toString();
		String converRate = map.get("converRate").toString();
		String cupsQid = map.get("cupsQid").toString();
		String cupsTraceNum = map.get("cupsTraceNum").toString();
		String cupsTraceTime = map.get("cupsTraceTime").toString();
		String cupsRespCode = map.get("cupsRespCode").toString();
		String cupsRespDesc = map.get("cupsRespDesc").toString();
		String respCode = map.get("respCode").toString();
		String notitfyOriginalSign = CreateNotitfyOriginalSign(transType, merchantId, merchantOrderId, merchantOrderAmt, 
				settleDate, setlAmt, setlCurrency, converRate, cupsQid, cupsTraceNum, cupsTraceTime, cupsRespCode, cupsRespDesc, respCode);		
		byte[] bytes = MD5(notitfyOriginalSign);
		return bytes;
	}
	
	public boolean verifyNotifyMessageSign(Map<String, String> map) {
		boolean ret = false;		
		String sign = map.get("sign").toString();
		byte[] b1 = getMD5(sign, qianzhiPublicCerFile);
		byte[] b2 = CreateNotitfyMessageMD5(map);
		ret = ArrayUtils.isEquals(b1, b2);
		return ret;
	}
	
	public String createTransNotifyRspMessage(String transType, String merchantOrderId, String respCode, String merid) {
		//merchantId merid
		return createTransNotifyRspXML(transType, merid, merchantOrderId, respCode);	
	}
	
	private String createTransNotifyRspXML(String transType, String merchantId, String merchantOrderId, String respCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8' ?>")
				.append("<upomp  application='TransNotify.Rsp' version='1.0.0'>")
				.append("<transType>").append(transType).append("</transType>")
				.append("<merchantId>").append(merchantId).append("</merchantId>")
				.append("<merchantOrderId>").append(merchantOrderId).append("</merchantOrderId>")
				.append("<respCode>").append(respCode).append("</respCode>")								
				.append("</upomp>");
		logger.info("createTransNotifyRspXML=" + sb.toString());
		return sb.toString();	
	}
	
	public String createQueryOrderSubmitMessage(String merchantOrderId, String merchantOrderTime) {					
		String originalSign4 = CreateOriginalSign4(TRANSTYPE_QUERYORDER, merchantId, merchantOrderId, merchantOrderTime);				
		String xmlSign4 = createSign(originalSign4, alias, password, getMerchantPrivateCerFileInputStream());		
		String ret = createQueryOrderSubmitXML(TRANSTYPE_QUERYORDER, merchantId, merchantOrderId, merchantOrderTime, xmlSign4, getMerchantPublicCer());
		return ret;
	}
	
	private String CreateOriginalSign4(String transType, String merchantId, String merchantOrderId, String merchantOrderTime) {
		StringBuilder sb = new StringBuilder();
		sb.append("transType=").append(transType).append("&merchantId=").append(merchantId).append("&merchantOrderId=")
		.append(merchantOrderId).append("&merchantOrderTime=").append(merchantOrderTime);
		return sb.toString();
	}
	
	private String createQueryOrderSubmitXML(String transType, String merchantId, String merchantOrderId, String merchantOrderTime, 
			String sign, String merchantPublicCer) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version='1.0' encoding='UTF-8' ?>")
				.append("<upomp  application='QueryOrder.Req' version='1.0.0'>")
				.append("<transType>").append(transType).append("</transType>")
				.append("<merchantId>").append(merchantId).append("</merchantId>")
				.append("<merchantOrderId>").append(merchantOrderId).append("</merchantOrderId>")
				.append("<merchantOrderTime>").append(merchantOrderTime).append("</merchantOrderTime>")
				.append("<sign>").append(sign).append("</sign>")
				.append("<merchantPublicCert>").append(merchantPublicCer).append("</merchantPublicCert>")
				.append("</upomp>");
		logger.info("QueryOrderSubmitXML=" + sb.toString());
		return sb.toString();	
	}
}
