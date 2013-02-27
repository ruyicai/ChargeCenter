package com.ruyicai.charge.nineteenpay.util;

/**
 * 19pay-点卡支付-点卡类型管理
 * @author shenpenglan
 *
 */
public class CardTypeManager {

	private String pmId="";// 支付方式编码
	private String pcId="";// 支付渠道编码
	private String name="";// 点卡名称
	
    /**
     * 针对高阳捷迅点卡支付平台
     * 获取支付方式和支付渠道编码
     * @author shenpenglan 
     */
    public void get19PayCardPmId(String cardType)
    {
    	if(cardType.equals("03"))// 全国[移动]充值卡
    	{
    		pmId="CMJFK";
    		pcId="CMJFK00010001";
    		name="全国移动充值卡";
    	}
    	else if(cardType.equals("06"))// 全国[联通]一卡充
    	{
    		pmId="LTJFK";
    		pcId="LTJFK00020000";
    		name="全国联通一卡充";
    	}
    	else if(cardType.equals("21"))// 中国[电信]充值付费卡
    	{
    		pmId="DXJFK";
    		pcId="DXJFK00010001";
    		name="中国电信充值付费卡";
    	}
    	else if(cardType.equals("22"))// 福建[移动]呱呱通充值卡
    	{
    		pmId="CMJFK";
    		pcId="CMJFK00010014";
    		name="福建移动呱呱通充值卡";
    	}
    	else if(cardType.equals("23"))// 江苏[移动]充值卡
    	{
    		pmId="CMJFK";
    		pcId="CMJFK00010111";
    		name="江苏移动充值卡";
    	}
    	else if(cardType.equals("24"))// 浙江[移动]缴费券
    	{
    		pmId="CMJFK";
    		pcId="CMJFK00010112";
    		name="浙江移动缴费券";
    	}
    }
    
    /**
     * 获取支付方式编码
     * @param cardType
     * @return
     */
    public String getPcId(String cardType)
    {
    	this.get19PayCardPmId(cardType);
    	return pcId;
    }
    
    /**
     * 获取支付渠道编码
     * @param cardType
     * @return
     */
    public String getPmId(String cardType)
    {
    	this.get19PayCardPmId(cardType);
    	return pmId;
    }
    
    /**
     * 获取点卡名称
     * @param cardType
     * @return
     */
    public String getCardName(String cardType)
    {
    	this.get19PayCardPmId(cardType);
    	return name;
    }
}
