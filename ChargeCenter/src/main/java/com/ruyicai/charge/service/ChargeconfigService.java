package com.ruyicai.charge.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.domain.Chargeconfig;
import com.ruyicai.charge.util.ConfigUtil;

@Service
public class ChargeconfigService {
	private Logger logger = Logger.getLogger(ChargeconfigService.class);
	private final static String CACHE_PREFIX = "charge_";
			
     @Autowired 
     MemcachedService<Chargeconfig> memcachedService;
     
     public String getChargeconfig(String key) {
		logger.info("getChargeconfig key=" + key);
		Chargeconfig cc = null;
		try {
			cc = memcachedService.get(CACHE_PREFIX + key);
			if (null == cc) {
				cc = Chargeconfig.findChargeconfig(key);
				if (null == cc) {
					cc = this.createOrModifyChargeconfig(key);
				}
				if (null != cc) {
					memcachedService.set(CACHE_PREFIX + key, cc);
				}
			}
		} catch (Exception e) {
			logger.error("getChargeconfig error:", e);
		}
		logger.info("Chargeconfig.toString()=" + cc.toString());
		
		String ret = null;
		if (null != cc) {
			ret = cc.getValue();
		}
		return ret;
     }
     
	private Chargeconfig createOrModifyChargeconfig(String key) {
		Chargeconfig cc = null;
		String memo = null;
		String value = null;

		if (key.equals("partnerId")) {
			memo = "支付宝合作商户ID";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("sellerAccountName")) {
			memo = "支付宝卖家帐号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipay.single.trade.query.url")) {
			memo = "支付宝单笔交易查询地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("prikey")) {
			memo = "支付宝商户私钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipayVeriPubKey")) {
			memo = "支付宝开放平台签名公钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("JinRuanTongSubject")) {
			memo = "商品名称";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipay.security.bgreturl")) {
			memo = "支付宝安全支付后台通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipay.wap.channel.bgreturl")) {
			memo = "支付宝wap快捷支付后台通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipay.wap.channel.callbackurl")) {
			memo = "支付宝wap快捷支付页面跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipay.wap.channel.traderequrl")) {
			memo = "支付宝wap快捷支付请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipay.wap.channel.returnurl")) {
			memo = "支付宝wap快捷支付UC浏览器页面跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("alipay.wap.channel.merchanturl")) {
			memo = "支付宝wap快捷支付未完成支付页面跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.partner.id")) {
			memo = "支付宝批量付款合作商户ID";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.bgreturl")) {
			memo = "支付宝批量付款后台通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.account.name")) {
			memo = "支付宝批量付款付款账号名";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.email")) {
			memo = "支付宝批量付款付款账号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.pay.gateway")) {
			memo = "支付宝批量付款网关1";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.pay.gateway2")) {
			memo = "支付宝批量付款网关2";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.key")) {
			memo = "支付宝批量付款签名秘钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.pay.memo")) {
			memo = "支付宝批量付款备注";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpay.pay.gatewaynotify.https")) {
			memo = "支付宝批量付款网关3";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("msBankhttps_Url")) {
			memo = "民生银行支付请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("msBankNotify_url")) {
			memo = "民生银行支付后台通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("msBank_KeyMiMa")) {
			memo = "民生银行商户私钥密码";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("msBankCorpID")) {
			memo = "民生银行商户合作ID";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbYuyinKey")) {
			memo = "支付宝语音充值商户密钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbYuyinReqUrl")) {
			memo = "支付宝语音充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbYuyinNotifyUrl")) {
			memo = "支付宝语音充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbYuyinReturnUrl")) {
			memo = "支付宝语音充值返回地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbSellEmail")) {
			memo = "支付宝语音充值卖家账号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbYuyinGoods")) {
			memo = "支付宝语音充值商品名称";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbYuyinDesc")) {
			memo = "支付宝语音充值商品名称备注";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbValidateUrl")) {
			memo = "支付宝语音充值验证地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbwebhttps_url")) {
			memo = "支付宝Web充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbwebcharSet")) {
			memo = "支付宝Web充值页面编码";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbwebkey")) {
			memo = "支付宝Web充值安全校验码";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbwebsellemall")) {
			memo = "支付宝Web充值卖家账号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("jinRuanTong_show_url")) {
			memo = "支付宝Web页面跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbwebnotifyurl")) {
			memo = "支付宝Web充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("zfbwebnotifycheckurl")) {
			memo = "支付宝Web充值校验地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("notifyUrl")) {
			memo = "支付宝Wap充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("call_back_url")) {
			memo = "支付宝Wap充值返回地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("invokeUrl")) {
			memo = "支付宝Wap充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("shenzhoufu.requrl")) {
			memo = "神州付充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("shenzhoufu.merid")) {
			memo = "神州付合作商户ID";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("shenzhoufu.returnurl")) {
			memo = "神州付充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("shenzhoufu.merusername")) {
			memo = "神州付合作商户名称";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("shenzhoufu.merusermail")) {
			memo = "神州付合作商户账号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("shenzhoufu.privatekey")) {
			memo = "神州付充值私钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("shenzhoufu.deskey")) {
			memo = "神州付充值DES秘钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_version_id")) {
			memo = "19pay充值版本号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_merchant_id")) {
			memo = "19pay充值合作商户ID";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_key")) {
			memo = "19pay充值商户密钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_currency")) {
			memo = "19pay充值货币类型";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_notify_url")) {
			memo = "19pay充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_order_pdesc")) {
			memo = "19pay充值商品描述";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_url")) {
			memo = "19pay充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("NP_url")) {
			memo = "19pay充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay_bankid")) {
			memo = "易宝支付bankid";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay_MerId")) {
			memo = "易宝支付商户编号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay.web.pagereturlsuccess")) {
			memo = "易宝支付跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepaycardnotifyurl")) {
			memo = "易宝WebCard充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay_key")) {
			memo = "易宝WebCard充值密钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepaychargeurl")) {
			memo = "易宝WebCard充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("wapYeepayCommonReqURL")) {
			memo = "易宝WapBank充值通用请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("CCBWAPYeepayCommonReqURL")) {
			memo = "易宝WapBank充值建行请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("keyValue")) {
			memo = "易宝WapBank充值商家密钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("wapMobileBankCommonResponseURL")) {
			memo = "易宝WapBank充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("return_url_web")) {
			memo = "易宝WapBank充值返回地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay.htmlcommon.p5pid")) {
			memo = "易宝htmlcommon充值商品名称";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay.htmlcommon.p8url")) {
			memo = "易宝htmlcommon充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay.cardpro.p5pid")) {
			memo = "易宝cardpro充值商品名称";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay.cardpro.requrl")) {
			memo = "易宝cardpro充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("yeepay.cardpro.p8url")) {
			memo = "易宝cardpro充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("mobilephonecard.chargetype.nineteenpay.weight")) {
			memo = "19pay充值权重";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("mobilephonecard.chargetype.shenzhoufu.weight")) {
			memo = "神州付充值权重";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.merid")) {
			memo = "银联电子支付有卡商户编号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.payment.bgreturl")) {
			memo = "银联电子支付有卡通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.payment.pagereturl")) {
			memo = "银联电子支付有卡跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.merkey.filepath")) {
			memo = "银联电子支付有卡证书地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.pubkey.filepath")) {
			memo = "银联电子支付有卡公钥证书地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.payment.url")) {
			memo = "银联电子支付请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.version")) {
			memo = "银联电子支付版本";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.curyid")) {
			memo = "银联电子支付货币类型";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.transtype.payment")) {
			memo = "银联电子支付交易类型";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.payment.status.ok")) {
			memo = "银联电子支付付款成功状态";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.payment.pagereturlsuccess")) {
			memo = "银联电子支付跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.merid2")) {
			memo = "银联电子支付无卡商户编号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.payment.bgreturl2")) {
			memo = "银联电子支付无卡通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.payment.pagereturl2")) {
			memo = "银联电子支付无卡跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.merkey.filepath2")) {
			memo = "银联电子支付无卡证书地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("chinapay.pubkey.filepath2")) {
			memo = "银联电子支付无卡公钥证书地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNAMerchantNo")) {
			memo = "DNA充值商户编号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNAWebServiceAddress")) {
			memo = "DNA充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNAReturnUrl")) {
			memo = "DNA充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNAWebServiceName")) {
			memo = "DNA生产服务器命名空间";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DnaPayStorePassword")) {
			memo = "DNA证书密钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNAMerNo")) {
			memo = "DNA商户类型";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNAMerchantPw")) {
			memo = "DNA商户密钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNATerminalNo")) {
			memo = "DNA系统终端编号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lakala.merid")) {
			memo = "拉卡拉充值商户编号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lakala.requrl")) {
			memo = "拉卡拉充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lakala.returnurl")) {
			memo = "拉卡拉充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lakala.merurl")) {
			memo = "拉卡拉充值跳转地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("liantonghuajian.merid")) {
			memo = "联通华建充值商户编号";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("liantonghuajian.requrl")) {
			memo = "联通华建充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("liantonghuajian.returnurl")) {
			memo = "联通华建充值通知地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lotteryReqUrl")) {
			memo = "lottery充值请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lotteryFailUrl")) {
			memo = "lottery充值失败处理地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lotterySuccessUrl")) {
			memo = "lottery充值成功处理地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("lotteryFindDNABindingByUsernoAndState")) {
			memo = "lottery按用户编号和状态获取DNA绑定信息";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("lotteryModifyTdnabind")) {
			memo = "lottery修改DNA绑定信息";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("lotteryGetTtransactionById")) {
			memo = "lottery根据ID获取交易信息";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("lotteryModifyTdnabindState")) {
			memo = "lottery修改DNA绑定状态";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("DNARSAPayAddress")) {
			memo = "易联支付请求地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNANameSpace")) {
			memo = "易联支付请求地址命名空间";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("GDYILIAN_CERT_PUB_64")) {
			memo = "易联支付请求秘钥";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("DNAV2ReturnUrl")) {
			memo = "易联支付异步地址";
			value = ConfigUtil.getConfig("charge.properties", key);
		} else if (key.equals("batchpaysetbatchno")) {
			memo = "lottery支付宝批量付款设置批次号";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("batchpaysuccessprocess")) {
			memo = "lottery支付宝批量付款成功处理";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("checkcashdetailids")) {
			memo = "lottery支付宝批量付款校验提现";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("batchpayfailprocess")) {
			memo = "lottery支付宝批量付款失败处理";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else if (key.equals("findByUserno")) {
			memo = "lottery根据用户编号获取用户信息";
			value = ConfigUtil.getConfig("lottery.properties", key);
		} else {
			logger.error("Chargeconfig key=" + key + "不存在");
			return cc;
		}

		logger.info("key=" + key + ", memo=" + memo + ", value=" + value);
		cc = Chargeconfig.createOrModifyChargeconfig(key, memo, value);
		return cc;
	}
  
     public Chargeconfig createOrModifyChargeconfig(String key, String memo, String value) {
    	 logger.info("key=" + key + ", memo=" + memo + ", value=" + value);	
    	 Chargeconfig cc = Chargeconfig.createOrModifyChargeconfig(key, memo, value);
		 return cc;
     }
     
     public Chargeconfig modifyChargeconfig(String key, String memo, String value) {
    	 Chargeconfig cc = Chargeconfig.createOrModifyChargeconfig(key, memo, value);
		 memcachedService.set(CACHE_PREFIX + key, cc); 
		 return cc;
     }

    public Chargeconfig modifyChargeconfig(String key, String weight) {
    	 Chargeconfig cc = Chargeconfig.modifyChargeconfig(key, weight);
		 memcachedService.set(CACHE_PREFIX + key, cc); 
		 return cc;
     }
     
     
}
