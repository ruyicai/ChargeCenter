package com.ruyicai.charge.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import chinapay.PrivateKey;
import chinapay.SecureLink;

import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.StringUtil;

public class ChinapayChargeAction implements ServletRequestAware,
		ServletResponseAware {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(ChinapayChargeAction.class);
	
	@Autowired 
	ChargeconfigService chargeconfigService;

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
			retMap.put("error_code", errorCode);
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印JSON信息
	 */
	private void printJson(String errorCode, String payform, String payUrl) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);		
			retMap.put("pay_url", payUrl + "?" + payform);			
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}
	
	//有卡
	public String charge() {
		logger.info("银联电子支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("银联电子支付充值->得到参数：jsonString=" + jsonString);		
		/**String orderCommitJSP = "order-commit";*/		
		
		String bankid = null;
		String paytype = null;
		String accesstype = null;
		String amt = null;
		String bankaccount = null;
		String channel = null;
		String subchannel = null;
		String userno = null;
		String gateid = null;
		String ladderpresentflag = null;
		String continuebettype = null;
		String orderid = null;
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额																				// (单位：分)
			bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			gateid = "";// 网关
			ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
		} catch (Exception e) {
			logger.error("银联电子支付充值->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		String merid = chargeconfigService.getChargeconfig("chinapay.merid");//ConfigUtil.getConfig("charge.properties", "chinapay.merid");
		String bgreturl = chargeconfigService.getChargeconfig("chinapay.payment.bgreturl");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.bgreturl");
		String pagereturl = chargeconfigService.getChargeconfig("chinapay.payment.pagereturl");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.pagereturl");
		String merkeypath = chargeconfigService.getChargeconfig("chinapay.merkey.filepath");//ConfigUtil.getConfig("charge.properties", "chinapay.merkey.filepath");
		String requrl = chargeconfigService.getChargeconfig("chinapay.payment.url");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.url");
		
		chargeCommon(bankid, paytype, accesstype, amt, bankaccount, channel, subchannel, userno, gateid, merid, bgreturl, pagereturl, merkeypath, ladderpresentflag, continuebettype, orderid, requrl);
		
		logger.info("银联电子支付充值->结束");
		return null;
	}
	
	//无卡
	public String charge2() {
		logger.info("银联电子支付充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("银联电子支付充值->得到参数：jsonString=" + jsonString);		
		/**String orderCommitJSP = "order-commit";*/		
		
		String bankid = null;
		String paytype = null;
		String accesstype = null;
		String amt = null;
		String bankaccount = null;
		String channel = null;
		String subchannel = null;
		String userno = null;
		String gateid = null;
		String ladderpresentflag = null;
		String continuebettype = null;
		String orderid = null;
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额																				// (单位：分)
			bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			gateid = map.containsKey("gateid") ? map.get("gateid").toString() : "";// 网关
			ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
		} catch (Exception e) {
			logger.error("银联电子支付充值->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		String merid = chargeconfigService.getChargeconfig("chinapay.merid2");//cc.getMerid();//ConfigUtil.getConfig("charge.properties", "chinapay.merid2");
		String bgreturl = chargeconfigService.getChargeconfig("chinapay.payment.bgreturl2");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.bgreturl2");
		String pagereturl = chargeconfigService.getChargeconfig("chinapay.payment.pagereturl2");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.pagereturl2");
		String merkeypath = chargeconfigService.getChargeconfig("chinapay.merkey.filepath2");//ConfigUtil.getConfig("charge.properties", "chinapay.merkey.filepath2");
		String requrl = chargeconfigService.getChargeconfig("chinapay.payment.url");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.url");
		
		chargeCommon(bankid, paytype, accesstype, amt, bankaccount, channel, subchannel, userno, gateid, merid, bgreturl, pagereturl, merkeypath, ladderpresentflag, continuebettype, orderid, requrl);
				
		logger.info("银联电子支付充值->结束");
		return null;
	}
	
	private String chargeCommon(String bankid, String paytype, String accesstype, String amt, String bankaccount, String channel, 
			String subchannel, String userno, String gateid, String merid, String bgreturl, String pagereturl, String merkeypath, 
			String ladderpresentflag, String continuebettype, String orderid, String requrl) {		
		String errorCode = ErrorCode.OK.value;
		
		try {			
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&userno=").append(userno)
					.append("&type=2").append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag)
					.append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("银联电子支付充值->生成交易记录：url=" + url + " ,param=" + param.toString());	
		
			String result = HttpRequest.doPostRequest(url, param.toString());			
			logger.info("银联电子支付充值->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
	
			if (!"0".equals(errorCode)) {				
				logger.info("银联电子支付充值->生成交易记录出现错误 errorCode=" + errorCode);				
				this.printErrorJson(errorCode);
				return null;
			} 
			String ttransactionId = mapResult.get("value").toString();
			logger.info("银联电子支付充值->交易号ttransactionId=" + ttransactionId);
			
			
			// 支付订单数据准备
			String MerId = merid;
			String OrdId = ttransactionId.substring(16);
			String Version = chargeconfigService.getChargeconfig("chinapay.version");//ConfigUtil.getConfig("charge.properties", "chinapay.version");
			String TransAmt = StringUtil.format(amt, 12);// 12
			String CuryId = chargeconfigService.getChargeconfig("chinapay.curyid");//ConfigUtil.getConfig("charge.properties", "chinapay.curyid");// 3
			String TransDate = StringUtil.getNowString();// 8
			String TransType = chargeconfigService.getChargeconfig("chinapay.transtype.payment");//ConfigUtil.getConfig("charge.properties", "chinapay.transtype.payment");// 4
			String BgRetUrl = bgreturl;
			String PageRetUrl = pagereturl;
			String GateId = gateid; //""
			String Priv1 = ttransactionId; //ConfigUtil.getConfig("charge.properties", "chinapay.priv1");			
			String ChkValue = null;
			String MerKeyPath = merkeypath;
			String pay_url = requrl;//ConfigUtil.getConfig("charge.properties", "chinapay.payment.url");
			logger.info("银联电子支付充值->MerId=" + MerId + "；OrdId=" + OrdId + "；Version=" + Version + "；TransAmt=" + TransAmt
					+ "；CuryId=" + CuryId + "；TransDate=" + TransDate+ "；TransType=" + TransType + "；BgRetUrl=" + BgRetUrl
					+ "；PageRetUrl=" + PageRetUrl + "；GateId=" + GateId + "；Priv1=" + Priv1 
					+ "；MerKeyPath=" + MerKeyPath + "；pay_url=" + pay_url);
			
			boolean buildOK = false;
			int KeyUsage = 0;
			PrivateKey key = new PrivateKey();
			try {
				buildOK = key.buildKey(MerId, KeyUsage, MerKeyPath);
			} catch (Exception e) {
				logger.error("银联电子支付充值->build error!:" + e);
				e.printStackTrace();				
			}
			if (!buildOK) {
				logger.error("银联电子支付充值->build error!");
				errorCode = ErrorCode.Charge_BuildKeyError.value;
				this.printErrorJson(errorCode);
				return null;
			}
			
			try {
				SecureLink sl = new SecureLink(key);
				ChkValue = sl.Sign(MerId + OrdId + TransAmt + CuryId
						+ TransDate + TransType + Priv1);
			} catch (Exception e) {
				logger.error("银联电子支付充值->sign error!");
				e.printStackTrace();
				errorCode = ErrorCode.Charge_SignError.value;
				this.printErrorJson(errorCode);
				return null;				
			}
			logger.info("银联电子支付充值->ChkValue=" + ChkValue);		
						
			
			param.delete(0, param.length());  
			param.append("MerId=").append(MerId).append("&OrdId=").append(OrdId).append("&TransAmt=").append(TransAmt)
			.append("&TransDate=").append(TransDate).append("&TransType=").append(TransType).append("&Version=").append(Version)
			.append("&CuryId=").append(CuryId).append("&GateId=").append(GateId).append("&PageRetUrl=").append(PageRetUrl)
			.append("&BgRetUrl=").append(BgRetUrl).append("&Priv1=").append(Priv1).append("&ChkValue=").append(ChkValue);
			this.printJson(errorCode, param.toString(), pay_url);

		} catch (Exception e) {
			logger.error("银联电子支付充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
		
		return null;
	}
	

}
