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

import com.ruyicai.charge.consts.TransactionState;
import com.ruyicai.charge.nineteenpay.NineteenPayService;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.shenzhoufu.ShenzhoufuNotifyCode;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.HttpRequest;

public class ShenzhoufuChargeNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	private Logger logger = Logger.getLogger(ShenzhoufuChargeNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Autowired
	ChargeUtil chargeUtil;
	@Autowired
	NineteenPayService nineteenPayService;
	@Autowired 
	ChargeconfigService chargeconfigService;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private void printStr(String str) {
		try {;			
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(str);
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：", e);
			e.printStackTrace();
		}
	}
	
	public String bgRet() {
		logger.info("神州付通知处理->开始");
		String version = request.getParameter("version"); // 获取神州付消费接口的版本号
		String merId = chargeconfigService.getChargeconfig("shenzhoufu.merid");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.merid");//request.getParameter("merId"); // 获取商户ID
		String payMoney = request.getParameter("payMoney"); // 获取消费金额
		String orderId = request.getParameter("orderId"); // 获取商户订单号
		String payResult = request.getParameter("payResult"); // 获取交易结果,1 成功 0失败
		String privateField = request.getParameter("privateField"); // 获取商户私有数据
		String payDetails = request.getParameter("payDetails"); // 获取消费详情
		String returnMd5String = request.getParameter("md5String"); // 获取MD5加密串
		String signString = request.getParameter("signString"); // 神州付证书签名
		String cardMoney = request.getParameter("cardMoney"); // 
		String privateKey = chargeconfigService.getChargeconfig("shenzhoufu.privatekey");//ConfigUtil.getConfig("charge.properties", "shenzhoufu.privatekey"); //	
		String errcode = request.getParameter("errcode"); 
		
		logger.info("神州付通知处理->version=" + version + "；merId=" + merId + "；payMoney=" + payMoney + "；orderId=" + orderId
				+ "；payResult=" + payResult + "；privateField=" + privateField+ "；payDetails=" + payDetails + "；returnMd5String=" + returnMd5String
				+ "；signString=" + signString + "；cardMoney=" + cardMoney + "；privateKey=" + privateKey + "；errcode=" + errcode);
		

	    ///生成加密串,注意顺序  下面的if else判断如果采用返回模式1请用不加竖线的，如果是返回模式2请用加竖线的
	    String combineString;
	    if (cardMoney != null) {
	        combineString = version + "|" + merId + "|" + payMoney + "|" + cardMoney + "|" + orderId + "|" + payResult + "|" + privateField + "|" + payDetails + "|" + privateKey;
	    } else {
	        combineString = version + merId + payMoney + orderId + payResult + privateField + payDetails + privateKey;
	    }		
	    String md5String = DigestUtils.md5Hex(combineString); 
	    
		boolean isOK = false;		
		isOK = md5String.equals(returnMd5String);
		
		if(isOK) {
			String result = null;			
			String amt = payMoney;
			String drawamt = "0";//ChargeUtil.getDrawamt(amt);
			String url = null;
			StringBuffer param = new StringBuffer();
			param.append("ttransactionid=").append(orderId).append("&bankorderid=").append(orderId)
			.append("&bankordertime=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
			.append("&banktrace=").append(" ").append("&retcode=").append(payResult)
			.append("&retmemo=").append(" ").append("&amt=").append(amt).append("&drawamt=").append(drawamt);
			
			String state = TransactionState.fail.value().toString();
			
			if(payResult.equals("1")) {
				logger.info("神州付通知处理->交易成功处理->开始!");
				try {
					url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
					logger.info("神州付通知处理->交易成功处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
					
					logger.info("神州付通知处理->交易成功处理->返回result:" + result);
					chargeUtil.afterCharge(result);
					
					state = TransactionState.ok.value().toString();
					nineteenPayService.modifyNineteenpay(orderId, cardMoney, errcode, ShenzhoufuNotifyCode.getMemo(errcode), null, state);
					logger.info("神州付通知处理->交易成功处理->结束!");
				} catch (IOException e) {
					logger.error("神州付通知处理->交易成功处理->出现异常!", e);				
					return null;
				}				
				
				this.printStr(orderId);		
				
			} else {
				logger.info("神州付通知处理->交易失败处理->开始!");
				try {
					url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
					logger.info("神州付通知处理->交易失败处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
					logger.info("神州付通知处理->交易失败处理->返回result:" + result);
					
					state = TransactionState.fail.value().toString();
					nineteenPayService.modifyNineteenpay(orderId, cardMoney, errcode, ShenzhoufuNotifyCode.getMemo(errcode), null, state);
					logger.info("神州付通知处理->交易失败处理->结束!");
				} catch (IOException e) {
					logger.error("神州付通知处理->交易失败处理->出现异常!", e);
					return null;
				}
				
			}
				
		} else {
			logger.error("神州付通知处理->MD5验证失败!");
		}
		
		logger.info("神州付通知处理->结束");
		return null;
	}
}
