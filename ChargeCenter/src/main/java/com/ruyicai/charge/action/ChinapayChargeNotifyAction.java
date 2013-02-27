package com.ruyicai.charge.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import chinapay.PrivateKey;
import chinapay.SecureLink;

import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;

public class ChinapayChargeNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	private final static String MERID_PUBKEY = "999999999999999"; 
	private Logger logger = Logger.getLogger(ChinapayChargeNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@Autowired
	ChargeUtil chargeUtil;
	@Autowired 
	ChargeconfigService chargeconfigService;
	
	private String orderId;
	private String transAmt;
	private String transDate;
	private String status;
	private String error;
	private String url;
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(String transAmt) {
		this.transAmt = transAmt;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		// try {
		// arg0.setCharacterEncoding("gb2312");
		// } catch (UnsupportedEncodingException e) {
		// logger.info("request:转换编码出错");
		// e.printStackTrace();
		// }

		this.request = arg0;
	}
	
	//有卡
	public String bgRet() {
        logger.info("银联电子支付通知处理->开始");		
		String pubkeypath = chargeconfigService.getChargeconfig("chinapay.pubkey.filepath");//ConfigUtil.getConfig("charge.properties", "chinapay.pubkey.filepath");		
		bgRetCommon(pubkeypath);
		logger.info("银联电子支付通知处理->结束");
		return null;
	}
	
	//无卡
	public String bgRet2() {
        logger.info("银联电子支付通知处理->开始");		
		String pubkeypath = chargeconfigService.getChargeconfig("chinapay.pubkey.filepath2");//ConfigUtil.getConfig("charge.properties", "chinapay.pubkey.filepath2");		
		bgRetCommon(pubkeypath);
		logger.info("银联电子支付通知处理->结束");
		return null;
	}
	
	private String bgRetCommon(String pubkeypath) {
		String PubKeyPath = pubkeypath;
		String StatusOK = chargeconfigService.getChargeconfig("chinapay.payment.status.ok");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.status.ok");
		String Version = request.getParameter("version");
		String MerId = request.getParameter("merid");
		String OrdId = request.getParameter("orderno");
		String TransAmt = request.getParameter("amount");// 12
		String CuryId = request.getParameter("currencycode");// 3
		String TransDate = request.getParameter("transdate");// 8
		String TransType = request.getParameter("transtype");// 4
		String Status = request.getParameter("status");
		String BgRetUrl = request.getParameter("BgRetUrl");
		String PageRetUrl = request.getParameter("PageRetUrl");
		String GateId = request.getParameter("GateId");
		String Priv1 = request.getParameter("Priv1");
		String ChkValue = request.getParameter("checkvalue");
		logger.info("银联电子支付通知处理->MerId=" + MerId + "；OrdId=" + OrdId + "；Version=" + Version + "；TransAmt=" + TransAmt
				+ "；CuryId=" + CuryId + "；TransDate=" + TransDate+ "；TransType=" + TransType + "；BgRetUrl=" + BgRetUrl
				+ "；PageRetUrl=" + PageRetUrl + "；GateId=" + GateId + "；Priv1=" + Priv1 + "；PubKeyPath=" + PubKeyPath
				+ "；StatusOK=" + StatusOK + "；Status=" + Status + "；ChkValue=" + ChkValue);
		
		boolean buildOK = false;
		boolean res = false;
		int KeyUsage = 0;
		PrivateKey key = new PrivateKey();
		try {
			buildOK = key.buildKey(MERID_PUBKEY, KeyUsage, PubKeyPath);
		} catch (Exception e) {
			logger.error("银联电子支付通知处理->build key error!");
			e.printStackTrace();
			return null;
		}
		if (!buildOK) {
			logger.error("银联电子支付通知处理->build error!");			
			return null;
		}
		try {
			SecureLink sl = new SecureLink(key);
			res = sl.verifyTransResponse(MerId, OrdId, TransAmt, CuryId, TransDate, TransType, Status, ChkValue);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("银联电子支付通知处理->verifyTransResponse出错!");
			return null;
		}
		
		if (res) {
			logger.info("银联电子支付通知处理->BgReturn Check OK!");
			String result = null;			
			long amt = Long.valueOf(TransAmt);
			String drawamt = ChargeUtil.getDrawamt(String.valueOf(amt));
			String url = null;
			StringBuffer param = new StringBuffer();
			param.append("ttransactionid=").append(Priv1).append("&bankorderid=").append(OrdId)
			.append("&bankordertime=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
			.append("&banktrace=").append(" ").append("&retcode=").append(Status)
			.append("&retmemo=").append(Status).append("&amt=").append(String.valueOf(amt))
			.append("&drawamt=").append(drawamt);
			
			if (Status.equals(StatusOK)){
				//1001表示支付成功
				logger.info("银联电子支付通知处理->交易成功处理->开始!");
				
				try {
					url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
					logger.info("银联电子支付通知处理->交易成功处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
				} catch (IOException e) {
					logger.error("银联电子支付通知处理->交易成功处理->出现异常!");
					e.printStackTrace();
					return null;
				}
				
				logger.info("银联电子支付通知处理->交易成功处理->返回result:" + result);
				chargeUtil.afterCharge(result);
				logger.info("银联电子支付通知处理->交易成功处理->结束!");				
				
			} else {
				logger.info("银联电子支付通知处理->交易失败处理->开始!");
				
				try {
					url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
					logger.info("银联电子支付通知处理->交易失败处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
				} catch (IOException e) {
					logger.error("银联电子支付通知处理->交易失败处理->出现异常!", e);
					e.printStackTrace();
					return null;
				}
				
				logger.info("银联电子支付通知处理->交易失败处理->返回result:" + result);
				logger.info("银联电子支付通知处理->交易失败处理->结束!");			
			}
		}
		
		return null;
	}
	
	//有卡
	public String pageRet() {
		logger.info("银联电子支付页面接受应答通知处理->开始");		
		String pubkeypath = chargeconfigService.getChargeconfig("chinapay.pubkey.filepath");//ConfigUtil.getConfig("charge.properties", "chinapay.pubkey.filepath");
		return pageRetCommon(pubkeypath);		
	}
	
	//无卡
	public String pageRet2() {
		logger.info("银联电子支付页面接受应答通知处理->开始");		
		String pubkeypath = chargeconfigService.getChargeconfig("chinapay.pubkey.filepath2");//ConfigUtil.getConfig("charge.properties", "chinapay.pubkey.filepath2");
		return pageRetCommon(pubkeypath);	
	}
	
	private String pageRetCommon(String pubkeypath) {		
		String PubKeyPath = pubkeypath;
		String StatusOK = chargeconfigService.getChargeconfig("chinapay.payment.status.ok");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.status.ok");
		String url = chargeconfigService.getChargeconfig("chinapay.payment.pagereturlsuccess");//ConfigUtil.getConfig("charge.properties", "chinapay.payment.pagereturlsuccess");
		String Version = request.getParameter("version");
		String MerId = request.getParameter("merid");
		String OrdId = request.getParameter("orderno");
		String TransAmt = request.getParameter("amount");// 12
		String CuryId = request.getParameter("currencycode");// 3
		String TransDate = request.getParameter("transdate");// 8
		String TransType = request.getParameter("transtype");// 4
		String Status = request.getParameter("status");
		String BgRetUrl = request.getParameter("BgRetUrl");
		String PageRetUrl = request.getParameter("PageRetUrl");
		String GateId = request.getParameter("GateId");
		String Priv1 = request.getParameter("Priv1");
		String ChkValue = request.getParameter("checkvalue");
		logger.info("银联电子支付页面接受应答通知处理->MerId=" + MerId + "；OrdId=" + OrdId + "；Version=" + Version + "；TransAmt=" + TransAmt
				+ "；CuryId=" + CuryId + "；TransDate=" + TransDate+ "；TransType=" + TransType + "；BgRetUrl=" + BgRetUrl
				+ "；PageRetUrl=" + PageRetUrl + "；GateId=" + GateId + "；Priv1=" + Priv1 + "；PubKeyPath=" + PubKeyPath
				+ "；StatusOK=" + StatusOK + "；Status=" + Status + "；ChkValue=" + ChkValue + "；url=" + url);
		
		long amt = Long.valueOf(TransAmt);
		String errorCode = ErrorCode.OK.value;		
		this.setOrderId(OrdId);
		this.setTransAmt(String.valueOf(amt));
		this.setTransDate(TransDate);
		this.setStatus(Status);	
		this.setUrl(url);
		String pageReturnJSP = "page-return";
		
		boolean buildOK = false;
		boolean res = false;
		int KeyUsage = 0;
		PrivateKey key = new PrivateKey();
		try {			
			buildOK = key.buildKey(MERID_PUBKEY, KeyUsage, PubKeyPath);
		} catch (Exception e) {
			errorCode = ErrorCode.Charge_BuildKeyError.value;
			logger.error("银联电子支付页面接受应答通知处理->build key error!errorCode=" + errorCode, e);
			this.setError(errorCode);
			e.printStackTrace();		
			return pageReturnJSP;
		}
		if (!buildOK) {
			errorCode = ErrorCode.Charge_BuildKeyError.value;
			logger.error("银联电子支付页面接受应答通知处理->build error!errorCode=" + errorCode);		
			this.setError(errorCode);
			return pageReturnJSP;
		}
		try {
			SecureLink sl = new SecureLink(key);
			res = sl.verifyTransResponse(MerId, OrdId, TransAmt, CuryId, TransDate, TransType, Status, ChkValue);
		} catch (Exception e) {
			errorCode = ErrorCode.Charge_VerifyTransResponseError.value;
			logger.error("银联电子支付页面接受应答通知处理->verifyTransResponse出错!errorCode=" + errorCode, e);		
			this.setError(errorCode);
			e.printStackTrace();			
			return pageReturnJSP;
		}		
		
		if (res) {
			logger.info("银联电子支付页面接受应答通知处理->verifyTransResponse->PageReturn Check OK!");	
		} else {
			logger.info("银联电子支付页面接受应答通知处理->verifyTransResponse->PageReturn Check Fail!");
			errorCode = ErrorCode.Charge_VerifyTransResponseError.value;
			this.setError(errorCode);
			return pageReturnJSP;
		}
		
		this.setError(errorCode);
		logger.info("银联电子支付页面接受应答通知处理->结束");	
		return pageReturnJSP;
	}

}
