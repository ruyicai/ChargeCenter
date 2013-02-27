/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */
package com.ruyicai.charge.alipay.client.security;

/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */

import java.io.InputStream;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * 
 * 
 * @author feng.chenf
 * @version $Id: ClientConfig.java, v 0.1 2008-12-24 上午09:31:39 feng.chenf Exp $
 */
public class ClientConfig {
	 private final static Logger logger=Logger.getLogger(ClientConfig.class);
	 
    /**
     * 测试环境 http://115.124.16.62 mobileprod.alipay.net
     * 
     * 支付宝开放平台服务调用的地址 线上环境请改为https://wappaygw.alipay.com(新)
     * 
     */
    private String serverUrl    = "http://115.124.16.62";//"http://121.0.29.46";

    /**
     * 支付宝开放平台服务调用的端口 
     */
    private String serverPort   = "80";

    /**
     * 商户的partnerId
     */
    private String partnerId    = "";

    /**
     * 商户的安全配置号
     */
    private String secId        = "";

    /**
     * 商户的私钥
     */
    private String prikey       = "";

    /**
     * 商户的公钥
     */
    private String pubkey       = "";

    /**
     * 验证支付宝开放平台签名的公钥
     */
    private String alipayVeriPubKey = "";
    
    /**
     * 给支付宝开放平台加密数据用的公钥
     */
    private String alipayEncPubKey = "";

    /**
     * 签名的算法 本次示例采用的是RSA
     */
    private String signAlgo     = "RSA";

    /**
     * 加密的算法 本次示例使用的是RSA
     */
    private String encryptAlgo  = "RSA";

    public ClientConfig() {
        try {
        	ResourceBundle resourceBundle=null;
    		try{
    		resourceBundle=ResourceBundle.getBundle("charge");//读取属性文件
    		this.partnerId=resourceBundle.getString("partnerId");//商户的partnerId
    		this.secId=resourceBundle.getString("secId");//商户的安全配置号
    		this.signAlgo=resourceBundle.getString("signAlgo");//签名的算法 本次示例采用的是RSA
    		this.prikey=resourceBundle.getString("prikey");//商户的私钥
    		this.pubkey=resourceBundle.getString("pubkey");//加密的算法 本次示例使用的是RSA
    		this.alipayVeriPubKey=resourceBundle.getString("alipayVeriPubKey");//商户的公钥
    		this.alipayEncPubKey=resourceBundle.getString("alipayEncPubKey");//验证支付宝开放平台签名的公钥
    		this.encryptAlgo=resourceBundle.getString("encryptAlgo");//加密的算法 本次示例使用的是RSA
    		}catch (Exception e) {
				//System.out.println("Exception1:"+e.toString()+"Exception2:"+e.getMessage());
    			logger.info("Exception1:"+e.toString()+"Exception2:"+e.getMessage());
			}
//            InputStream iss = this.getClass().getClassLoader().getResourceAsStream(
//                "com/alipay/client/config/config.xml");
//            DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
//            Document doc = dombuilder.parse(iss);
//
//            NodeList paramNode = doc.getElementsByTagName("partnerId");
//            partnerId = paramNode.item(0).getFirstChild().getNodeValue().trim();
//
//            NodeList secNode = doc.getElementsByTagName("secId");
//            secId = secNode.item(0).getFirstChild().getNodeValue().trim();
//
//            NodeList signAlgoNode = doc.getElementsByTagName("signAlgo");
//            this.signAlgo = signAlgoNode.item(0).getFirstChild().getNodeValue().trim();
//
//            NodeList prikeyNode = doc.getElementsByTagName("prikey");
//            this.prikey = prikeyNode.item(0).getFirstChild().getNodeValue().trim();
//
//            NodeList pubkeyNode = doc.getElementsByTagName("pubkey");
//            this.pubkey = pubkeyNode.item(0).getFirstChild().getNodeValue().trim();
//
//            NodeList alipayVeriPubKeyNode = doc.getElementsByTagName("alipayVeriPubKey");
//            this.alipayVeriPubKey = alipayVeriPubKeyNode.item(0).getFirstChild().getNodeValue().trim();
//            
//            NodeList alipayEncPubKeyNode = doc.getElementsByTagName("alipayEncPubKey");
//            this.alipayEncPubKey = alipayEncPubKeyNode.item(0).getFirstChild().getNodeValue().trim();

        } catch (Exception e) {
            //异常处理
            //此处为演示代码 直接输出错误信息
        	logger.info(e);
            e.printStackTrace();
        }
    }

    /**
     * @return Returns the serverUrl.
     */
    public String getServerUrl() {
        return serverUrl;
    }

    /**
     * @param serverUrl The serverUrl to set.
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * @return Returns the serverPort.
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * @param serverPort The serverPort to set.
     */
    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * @return Returns the partnerId.
     */
    public String getPartnerId() {
        return partnerId;
    }

    /**
     * @param partnerId The partnerId to set.
     */
    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    /**
     * @return Returns the secId.
     */
    public String getSecId() {
        return secId;
    }

    /**
     * @param secId The secId to set.
     */
    public void setSecId(String secId) {
        this.secId = secId;
    }

    /**
     * @return Returns the prikey.
     */
    public String getPrikey() {
        return prikey;
    }

    /**
     * @param prikey The prikey to set.
     */
    public void setPrikey(String prikey) {
        this.prikey = prikey;
    }

    /**
     * @return Returns the pubkey.
     */
    public String getPubkey() {
        return pubkey;
    }

    /**
     * @param pubkey The pubkey to set.
     */
    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    /**
     * @return Returns the signAlgo.
     */
    public String getSignAlgo() {
        return signAlgo;
    }

    /**
     * @param signAlgo The signAlgo to set.
     */
    public void setSignAlgo(String signAlgo) {
        this.signAlgo = signAlgo;
    }

    /**
     * @return Returns the encryptAlgo.
     */
    public String getEncryptAlgo() {
        return encryptAlgo;
    }

    /**
     * @param encryptAlgo The encryptAlgo to set.
     */
    public void setEncryptAlgo(String encryptAlgo) {
        this.encryptAlgo = encryptAlgo;
    }

    /**
     * @return Returns the alipayVeriPubKey.
     */
    public String getAlipayVeriPubKey() {
        return alipayVeriPubKey;
    }

    /**
     * @param alipayVeriPubKey The alipayVeriPubKey to set.
     */
    public void setAlipayVeriPubKey(String alipayVeriPubKey) {
        this.alipayVeriPubKey = alipayVeriPubKey;
    }

    /**
     * @return Returns the alipayEncPubKey.
     */
    public String getAlipayEncPubKey() {
        return alipayEncPubKey;
    }

    /**
     * @param alipayEncPubKey The alipayEncPubKey to set.
     */
    public void setAlipayEncPubKey(String alipayEncPubKey) {
        this.alipayEncPubKey = alipayEncPubKey;
    }

}
