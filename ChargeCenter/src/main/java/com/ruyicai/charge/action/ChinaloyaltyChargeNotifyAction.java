package com.ruyicai.charge.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.TransactionMapUtil;

public class ChinaloyaltyChargeNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	private Logger logger = Logger.getLogger(ChinaloyaltyChargeNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Autowired
	ChargeUtil chargeUtil;
	
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
	
	public String bgRet() {
        logger.info("银商资讯支付通知处理->开始");		
        String merKey = ConfigUtil.getConfig("charge.properties", "chinaloyalty.merkey");
        String params = request.getQueryString();
        String type = request.getParameter("type");// 4
		String merId = request.getParameter("merId");
		String orderId = request.getParameter("orderId");
		String amount = request.getParameter("amount");
		String cardNo = request.getParameter("cardNo");//卡号
		String tranId = request.getParameter("tranId");//支付流水号
		String tranDate = request.getParameter("tranDate");//支付日期 YYYYMMDD
		String tranTime = request.getParameter("tranTime");//支付时间 HHMMSS
		String mac = request.getParameter("mac");
		String txnid = request.getParameter("txnid");//预付卡流水号
		logger.info("银商资讯支付通知处理->params=" + params + "；type=" + type + "；merId=" + merId + "；orderId=" + orderId
				+ "；amount=" + amount + "；cardNo=" + cardNo+ "；tranId=" + tranId + "；tranDate=" + tranDate
				+ "；tranTime=" + tranTime + "；mac=" + mac + "；txnid=" + txnid);
		
		if (!checkMac(mac, merId + orderId + amount + merKey)) {
			logger.info("银商资讯支付通知处理->BgReturn Check Fail!");
			return null;
		}
		
		logger.info("银商资讯支付通知处理->BgReturn Check OK!");
		String result = null;
		String drawamt = "0";
		String url = null;
		String ttransactionid = TransactionMapUtil.getTransactionid(orderId);
		String retcode = "1";
		StringBuffer param = new StringBuffer();
		param.append("ttransactionid=").append(ttransactionid).append("&bankorderid=").append(tranId)
		.append("&bankordertime=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
		.append("&banktrace=").append(" ").append("&retcode=").append(retcode).append("&retmemo=").append(retcode)
		.append("&amt=").append(amount).append("&drawamt=").append(drawamt);
		
		logger.info("银商资讯支付通知处理->交易成功处理->开始!");			
		try {
			url = ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
			logger.info("银商资讯支付通知处理->交易成功处理->url=" + url + "；param=" + param.toString());
			result = HttpRequest.doPostRequest(url, param.toString());
		} catch (IOException e) {
			logger.error("银商资讯支付通知处理->交易成功处理->出现异常!");
			e.printStackTrace();
			return null;
		}
		
		logger.info("银商资讯支付通知处理->交易成功处理->返回result:" + result);
		chargeUtil.afterCharge(result);
		logger.info("银商资讯支付通知处理->交易成功处理->结束!");
		
		/**
		if (支付成功)){						
			//支付成功
		} else {
			logger.info("银商资讯支付通知处理->交易失败处理->开始!");			
			try {
				url = ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
				logger.info("银商资讯支付通知处理->交易失败处理->url=" + url + "；param=" + param.toString());
				result = HttpRequest.doPostRequest(url, param.toString());
			} catch (IOException e) {
				logger.error("银商资讯支付通知处理->交易失败处理->出现异常!", e);
				e.printStackTrace();
				return null;
			}
			
			logger.info("银商资讯支付通知处理->交易失败处理->返回result:" + result);
			logger.info("银商资讯支付通知处理->交易失败处理->结束!");			
		}*/
		
		logger.info("银联资讯支付通知处理->结束");
		return null;
	}
		
	private boolean checkMac(String mac, String str) {
		String mac2 = DigestUtils.md5Hex(str);
		return mac.equals(mac2);
	}
}
