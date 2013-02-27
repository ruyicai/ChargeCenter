package com.ruyicai.charge.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.alipay.client.security.DirectTradeCreateResNotify;
import com.ruyicai.charge.alipay.client.security.XMapUtil;
import com.ruyicai.charge.alipay.security.RSASignature;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.HttpRequest;

public class AlipaySecurityChargeNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	private Logger logger = Logger.getLogger(AlipaySecurityChargeNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Autowired
	ChargeUtil chargeUtil;
	@Autowired
	ChargeconfigService chargeconfigService;

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {		
		this.request = arg0;
	}

	public String bgRet() {
		logger.info("支付宝手机安全支付通知处理->开始");	
		logger.info("支付宝手机安全支付通知处理->支付宝访问我们的地址url=" + request.getContextPath());
				
		Map map = request.getParameterMap();//获得通知参数
		logger.info("支付宝手机安全支付通知处理->获得通知参数:map=" + map.toString());
				
		String sign = "";//获得签名		
		String notifyData = "";//获得待验签名的数据
		try {
			sign = (String) ((Object[]) map.get("sign"))[0];			
			notifyData = (String) ((Object[]) map.get("notify_data"))[0];
		} catch (Exception e) {
			logger.error("支付宝手机安全支付通知处理->获得支付宝通知参数出现异常，异常信息：", e);
			e.printStackTrace();
			return null;
		}
		logger.info("支付宝手机安全支付通知处理->sign=" + sign + ";notify_data=" + notifyData);
		
		
		String verifyData = getVerifyData(map);		
		boolean verified = false;
		
		//使用支付宝公钥验签名
		try {
			String rsaAlipayPublic = chargeconfigService.getChargeconfig("alipayVeriPubKey");//ConfigUtil.getConfig("charge.properties", "alipayVeriPubKey");//卖家帐号
			verified = RSASignature.doCheck(verifyData, sign, rsaAlipayPublic);
		} catch (Exception e) {
			logger.error("支付宝手机安全支付通知处理->验证签名，出现异常：", e);
			e.printStackTrace();
		}
		
		try {
			PrintWriter out = response.getWriter();
			if (verified) {
				logger.info("支付宝手机安全支付通知处理->验证签名通过");
				boolean ret = doBusinessProress(notifyData);// 根据交易状态处理业务逻辑				
				//当交易状态成功，处理业务逻辑成功。回写success
				if (ret) {
					out.print("success");
				}				
			} else {
				logger.info("支付宝手机安全支付通知处理->验证签名失败");
				out.print("fail");
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("支付宝手机安全支付通知处理->out.print出错：", e);
			return null;
		}
		
		logger.info("支付宝手机安全支付通知处理->结束");
		return null;
	}
	
	private String getVerifyData(Map map) {
		String notifyData = (String) ((Object[]) map.get("notify_data"))[0];
		return "notify_data=" + notifyData;
    }
	
	private boolean doBusinessProress(String notifyData) {
		boolean ret = false;
		// 解析支付宝返回的xml业务数据
		DirectTradeCreateResNotify directTradeCreateRes = null;
		XMapUtil.register(DirectTradeCreateResNotify.class);
		
		try {
			directTradeCreateRes = (DirectTradeCreateResNotify) XMapUtil.load(new ByteArrayInputStream(notifyData.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			logger.error("支付宝手机安全支付通知处理->解析xml形式的支付宝通知业务数据出现异常:", e);
			e.printStackTrace();
			return ret;
		} catch (Exception e) {
			logger.error("支付宝手机安全支付通知处理->解析xml形式的支付宝通知业务数据出现异常:", e);
			e.printStackTrace();
			return ret;
		}

		String tradeStatus = directTradeCreateRes.tradeStatus;
		String outTradeNo = directTradeCreateRes.getOutTradeNo();// 支付宝通知业务参数中的金软通平台的交易Id
		logger.info("支付宝手机安全支付通知处理->支付宝通知业务参数中的金软通平台的交易Id=" + outTradeNo);
		String totalFee = directTradeCreateRes.getTotalFee();
		logger.info("支付宝手机安全支付通知处理->支付宝通知业务参数中的金软通平台的交易totalFee=" + totalFee);

		String url = "";
		String param = "";
		String result = null;
		String amt = (new BigDecimal(totalFee)).multiply(new BigDecimal(100)).toString();
		String drawamt = ChargeUtil.getDrawamt(amt);
		
		// 支付宝充值用户充值金额是否付款成功
		if (!"TRADE_FINISHED".equals(tradeStatus) && !"TRADE_SUCCESS".equals(tradeStatus)) {
			logger.error("支付宝手机安全支付通知处理->交易处理失败, 用户未支付充值金额");
			logger.error("支付宝手机安全支付通知处理->交易处理失败，tradeStatus=" + tradeStatus);
			
			try {
				url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
				param = "ttransactionid=" + outTradeNo + "&bankordertime=" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) 
						+ "&banktrace= " + "&retcode=" + tradeStatus + "&retmemo=" + tradeStatus + "&amt=" + amt;
				logger.info("支付宝手机安全支付通知处理->交易处理失败请求, url=" + url + "；param=" + param);
				result = HttpRequest.doPostRequest(url, param);
				logger.info("支付宝手机安全支付通知处理->交易处理失败处理，返回result:" + result);
			} catch (IOException e) {
				logger.error("支付宝手机安全支付通知处理->交易失败处理出现异常：", e);
				e.printStackTrace();
				return ret;
			}
			return ret;
		}

		String tradeNo = directTradeCreateRes.getTradeNo();// 支付宝的交易Id
		try {
			StringBuffer param2 = new StringBuffer();
			param2.append("ttransactionid=").append(outTradeNo).append("&bankorderid=").append(tradeNo)
					.append("&bankordertime=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("&banktrace=")
					.append(" ").append("&retcode=").append(tradeStatus).append("&retmemo=").append(tradeStatus).append("&amt=")
					.append(amt).append("&drawamt=").append(drawamt);

			url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
			param = param2.toString();
			logger.info("支付宝手机安全支付通知处理->充值成功请求：url=" + url + ",param2=" + param);
			result = HttpRequest.doPostRequest(url, param);
			logger.info("支付宝手机安全支付通知处理->充值成功处理返回: result=" + result);
			chargeUtil.afterCharge(result);
		} catch (IOException e) {
			logger.error("支付宝手机安全支付通知处理->交易成功处理出现异常：" + e);
			e.printStackTrace();
			return ret;
		}
		
		ret = true;
		return ret;
	}
 }
