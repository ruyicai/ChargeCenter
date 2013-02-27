package com.ruyicai.charge.shenzhoufu;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.consts.TransactionState;
import com.ruyicai.charge.nineteenpay.NineteenPayService;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;

@Service
public class ShenzhoufuChargeService {
	private Logger logger = Logger.getLogger(ShenzhoufuChargeService.class);
	
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired
	NineteenPayService nineteenPayService;
	
	public String directChargeCommon(String jsonString) {
		String errorCode = ErrorCode.OK.value;
		logger.info("神州付充值->得到参数：jsonString=" + jsonString);

		String bankid = null;
		String paytype = null;
		String accesstype = null;
		String amt = null;
		String bankaccount = null;
		String channel = null;
		String subchannel = null;
		String userno = null;
		String cardno = null;
		String cardpwd = null;
	    String cardmoney = null;
	    String cardtype = null;
	    String cardmoney2 = null;
	    String ladderpresentflag = null;
	    String continuebettype = null;
		String orderid = null;
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式 M商户 BWC
			amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额																				// (单位：分)
			bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			cardno = map.containsKey("cardno") ? map.get("cardno").toString() : "";// 
			cardpwd = map.containsKey("cardpwd") ? map.get("cardpwd").toString() : "";// 
			cardmoney = map.containsKey("cardmoney") ? map.get("cardmoney").toString() : "";//单位分
			//cardtype = map.containsKey("cardtype") ? map.get("cardtype").toString() : "";//0：移动 1：联通 2：电信
			ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
			
			bankaccount = getCardCode(paytype);
			cardtype = getCardType(paytype);//0：移动 1：联通 2：电信
			cardmoney2 = String.valueOf(Integer.parseInt(cardmoney)/100);//单位元				
			
		} catch (Exception e) {
			logger.error("神州付充值->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;			
			return errorCode;
		}		
		
		try {			
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&userno=").append(userno)
					.append("&type=10").append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag).append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("神州付充值->生成交易记录：url=" + url + " ,param=" + param.toString());	
		
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("神州付充值->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!"0".equals(errorCode)) {				
				logger.info("神州付充值->生成交易记录出现错误 errorCode=" + errorCode);
				return errorCode;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("神州付充值->交易号ttransactionId=" + ttransactionId);	
			
			
			nineteenPayService.createNineteenpay(ttransactionId, userno, cardno, cardpwd, amt, cardmoney, paytype);			
			
			String requrl = chargeconfigService.getChargeconfig("shenzhoufu.requrl");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.requrl");//交易请求地址
			String version = "3";//ConfigUtil.getConfig("charge.properties", "shenzhoufu.version");//版本号
			String merId = chargeconfigService.getChargeconfig("shenzhoufu.merid");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.merid");//
			String payMoney = amt; 
			String orderId = ttransactionId;
			String returnUrl = chargeconfigService.getChargeconfig("shenzhoufu.returnurl");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.returnurl");			
			String merUserName = chargeconfigService.getChargeconfig("shenzhoufu.merusername");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.merusername");//
			String merUserMail = chargeconfigService.getChargeconfig("shenzhoufu.merusermail");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.merusermail");//
			String privateField = "directChargeForRYC"; 
			String privateKey = chargeconfigService.getChargeconfig("shenzhoufu.privatekey");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.privatekey");
			String desKey = chargeconfigService.getChargeconfig("shenzhoufu.deskey");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.deskey");
			String cardInfo = ServerConnSzxUtils.getDesEncryptBase64String(cardmoney2, cardno, cardpwd, desKey);			
			String verifyType = "1";//MD5 校验
			String combineString = version + merId + payMoney + orderId + returnUrl + cardInfo + privateField + verifyType + privateKey;
			String md5String = DigestUtils.md5Hex(combineString); //md5加密串
			String cardTypeCombine = cardtype;
			String signString = "";
			
			
			param.delete(0, param.length()); 
			param.append("version=").append(version).append("&merId=").append(merId).append("&payMoney=").append(payMoney)
			.append("&orderId=").append(orderId).append("&returnUrl=").append(returnUrl).append("&cardInfo=").append(URLEncoder.encode(cardInfo, "utf-8"))
			.append("&merUserName=").append(merUserName).append("&merUserMail=").append(merUserMail).append("&privateField=").append(privateField)
			.append("&verifyType=").append(verifyType).append("&cardTypeCombine=").append(cardTypeCombine).append("&md5String=").append(md5String)
			.append("&signString=").append(signString);
			
			logger.info("神州付充值->提交神州付请求 requrl=" + requrl + "；param=" + param.toString());
			result = HttpRequest.doPostRequest(requrl, param.toString(), "szfResponseCode");
			logger.info("神州付充值->神州付返回 result=" + result);
			if(!result.equals(ShenzhoufuRequestCode.pt8.value())) {
				errorCode = result;
				
				String state = TransactionState.fail.value().toString();
				nineteenPayService.modifyNineteenpay(ttransactionId, "0", result, ShenzhoufuRequestCode.getMemo(result), null, state);
			} 
			
		} catch (Exception e) {
			logger.error("神州付充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
			return errorCode;
		}

		return errorCode;
	}
	
	private String getCardCode(String payType) {
		String str = payType.substring(2, 4);
		String ret = "0";
		if (str.equals("03")) {			
			ret = "YD";// 移动卡
		} else if (str.equals("06")) {			
			ret = "LT";// 联通卡
		} else if (str.equals("21")) {			
			ret = "DX";// 电信卡
		}
		return ret;
	}
	
	private String getCardType(String payType) {
		String str = payType.substring(2, 4);
		String ret = "0";
		if (str.equals("03")) {			
			ret = "0";// 移动卡
		} else if (str.equals("06")) {			
			ret = "1";// 联通卡
		} else if (str.equals("21")) {			
			ret = "2";// 电信卡
		}
		return ret;
	}

}
