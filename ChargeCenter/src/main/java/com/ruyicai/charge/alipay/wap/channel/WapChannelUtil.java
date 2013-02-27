package com.ruyicai.charge.alipay.wap.channel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.ruyicai.charge.util.ConfigUtil;

public class WapChannelUtil {
	public final static String INPUT_CHARSET = "UTF-8";	
	public final static String REQ_URL = ConfigUtil.getConfig("charge.properties", "alipay.single.trade.query.url");
	public final static String SERVICE_MOBILE_MERCHANT_PAYCHANNEL = "mobile.merchant.paychannel";
	public final static String SIGN_TYPE_MD5 = "MD5";
	public final static String PARTNER = ConfigUtil.getConfig("charge.properties", "partnerId");
	public final static String KEY = ConfigUtil.getConfig("charge.properties", "zfbYuyinKey");
	public final static String IS_SUCCESS = "is_success";
	public final static String T = "T";
	public final static String SIGN_ALGO_RSA = "0001";
	public final static String SELLER = ConfigUtil.getConfig("charge.properties", "sellerAccountName");//卖家帐号
	public final static String NOTIFY_URL = ConfigUtil.getConfig("charge.properties", "alipay.wap.channel.bgreturl");// 接收支付宝发送的通知的url		
	public final static	String CALLBACK_URL = ConfigUtil.getConfig("charge.properties", "alipay.wap.channel.callbackurl");; //支付成功跳转链接	
	public final static	String RETURN_URL = ConfigUtil.getConfig("charge.properties", "alipay.wap.channel.returnurl");; //UC浏览器安全支付会使用这个url跳转链接 和callbackUrl值一样
	public final static	String MERCHANT_URL = ConfigUtil.getConfig("charge.properties", "alipay.wap.channel.merchanturl");; //未完成支付，用户点击链接返回商户url
	public final static String SUBJECT = ConfigUtil.getConfig("charge.properties", "JinRuanTongSubject");//商品名称
	public final static String BODY = ConfigUtil.getConfig("charge.properties", "JinRuanTongSubject");//商品具体描述
	public final static String SERVICE_ALIPAY_WAP_TRADE_CREATE_DIRECT = "alipay.wap.trade.create.direct"; 
	public final static String FORMAT_XML = "xml"; 
	public final static String V = "2.0"; 
	public final static String TRADE_REQ_URL = ConfigUtil.getConfig("charge.properties", "alipay.wap.channel.traderequrl");
	public final static String RSA_PRIVATE = ConfigUtil.getConfig("charge.properties", "prikey");
	public final static String RSA_ALIPAY_PUBLIC = ConfigUtil.getConfig("charge.properties", "alipayVeriPubKey");//pubkey  why?
	public final static String SERVICE_ALIPAY_WAP_AUTH_AUTHANDEXECUTE = "alipay.wap.auth.authAndExecute"; 
	
	/**
     * 将Map中的数据组装成url
     * @param params
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String mapToUrl(Map<String, String> params) throws UnsupportedEncodingException {
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
}
 