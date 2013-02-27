package com.ruyicai.charge.yeepay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.util.ConfigUtil;

@Service
public class CardTypeManager {
	private final static Logger logger = Logger.getLogger(CardTypeManager.class);

	/**
     * 根据平台点卡代号生成支付平台点卡代号[01-->JUNNET]
     * 针对易宝支付平台
     * @author spl 
     */
    public String getUnBankCode(String cardType)
    {
    	String cardId="";
    	if(cardType.equals("01"))// 骏网一卡通
    	{
    		cardId="JUNNET";
    	}
    	if(cardType.equals("02"))// 盛大卡
    	{
    		cardId="SNDACARD";
    	}
    	if(cardType.equals("03"))// 神州行
    	{
    		cardId="SZX";
    	}
    	if(cardType.equals("04"))// 征途卡
    	{
    		cardId="ZHENGTU";
    	}
    	if(cardType.equals("05"))// Q币卡
    	{
    		cardId="QQCARD";
    	}
    	if(cardType.equals("06"))// 联通卡
    	{
    		cardId="UNICOM";
    	}
    	if(cardType.equals("07"))// 久游卡
    	{
    		cardId="JIUYOU";
    	}
    	if(cardType.equals("08"))// 易宝一卡通
    	{
    		cardId="YPCARD";
    	}
    	if(cardType.equals("09"))// 网易卡
    	{
    		cardId="NETEASE";
    	}
    	if(cardType.equals("10"))// 完美卡
    	{
    		cardId="WANMEI";
    	}
    	if(cardType.equals("11"))// 搜狐卡
    	{
    		cardId="SOHU";
    	}
    	if(cardType.equals("12")) 
    	{
    		cardId="TELECOM";//电信卡  后加
    	}
    	return cardId;
    }
    
    
    /**
     * 根据平台银行卡代号生成支付平台银行卡代号[01-->CMBCHINA-WAP]
     * 针对易宝支付平台
     * @author spl
     */
    public static String getBankCode(String bankCardType)
    {
    	String bandkId="";
    	if(bankCardType.equals("01"))// 招商银行
    	{
    		bandkId="CMBCHINA-WAP";
    	}
    	if(bankCardType.equals("02"))// 建设银行
    	{
    		bandkId="CCB-PHONE";
    	}
    	if(bankCardType.equals("03"))// 工商银行
    	{
    		bandkId="ICBC-WAP";
    	}
    	return bandkId;
    }
    
    /**
     * 银行卡地址生成
     * @param rspMap
     * @return
     */
    public String getCmppSubmitReqUrlBank(Map<String, String> rspMap,String nodeAuthorizationURL){
    	String wapPointReqURL=nodeAuthorizationURL;//易宝充值请求地址
        StringBuffer stringBuffer = new StringBuffer(wapPointReqURL+"?");
        Set<Entry<String, String>> entries = rspMap.entrySet();
        for (Entry<String, String> entry : entries){
            try{
                if(entry.getValue()!=null){
                    stringBuffer.append(URLEncoder.encode(entry.getKey(),"GBK")).append("=").append(URLEncoder.encode(entry.getValue(),"GBK")).append("&");
                }
            }catch(UnsupportedEncodingException e){
                logger.error("易宝银行卡充值参数编码出现异常--------Exception:"+e.toString());
            }
        }
        if(stringBuffer.length()>0 && stringBuffer.charAt(stringBuffer.length()-1)=='&'){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        return stringBuffer.toString();
    }
    
    //生成URL地址
    public String getCmppSubmitReqUrl(Map<String, String> rspMap, String requrl){
    	String wapPointReqURL = requrl;//ConfigUtil.getConfig("charge.properties", "yeepaychargeurl");
        StringBuffer stringBuffer = new StringBuffer(wapPointReqURL);
        Set<Entry<String, String>> entries = rspMap.entrySet();
        for (Entry<String, String> entry : entries){
            try{
                if(entry.getValue()!=null){
                    stringBuffer.append(URLEncoder.encode(entry.getKey(),"GBK")).append("=").append(URLEncoder.encode(entry.getValue(),"GBK")).append("&");
                }
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        if(stringBuffer.length()>0 && stringBuffer.charAt(stringBuffer.length()-1)=='&'){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        return stringBuffer.toString();
    }
}
