package com.ruyicai.charge.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.StringUtil;
import com.ruyicai.charge.yeepay.cardpro.NonBankcardService;
import com.ruyicai.charge.yeepay.htmlcommon.PaymentForOnlineService;

public class YeepayChargeNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	private Logger logger = Logger.getLogger(YeepayChargeNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired
	ChargeUtil chargeUtil;
	
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
		logger.info("易宝支付通知处理->开始");
		String merid = chargeconfigService.getChargeconfig("yeepay_MerId");//ConfigUtil.getConfig("charge.properties", "yeepay.merid");//
		String keyvalue = chargeconfigService.getChargeconfig("yeepay_key");//ConfigUtil.getConfig("charge.properties", "yeepay.keyvalue");// 商家密钥		
		String r0_Cmd = StringUtil.formatString(request.getParameter("r0_Cmd")); // 业务类型
		String p1_MerId = StringUtil.formatString(merid); // 商户编号
		String r1_Code = StringUtil.formatString(request.getParameter("r1_Code"));// 支付结果
		String r2_TrxId = StringUtil.formatString(request.getParameter("r2_TrxId"));// 易宝支付交易流水号
		String r3_Amt = StringUtil.formatString(request.getParameter("r3_Amt"));// 支付金额
		String r4_Cur = StringUtil.formatString(request.getParameter("r4_Cur"));// 交易币种
		String r5_Pid = StringUtil.formatString(request.getParameter("r5_Pid"));// 商品名称
		String r6_Order = StringUtil.formatString(request.getParameter("r6_Order"));// 商户订单号
		String r7_Uid = StringUtil.formatString(request.getParameter("r7_Uid"));// 易宝支付会员ID
		String r8_MP = StringUtil.formatString(request.getParameter("r8_MP"));// 商户扩展信息
		String r9_BType = StringUtil.formatString(request.getParameter("r9_BType"));// 交易结果返回类型
		String hmac = StringUtil.formatString(request.getParameter("hmac"));// 签名数据
		
		logger.info("易宝支付通知处理->MerId=" + merid + "；r0_Cmd=" + r0_Cmd + "；p1_MerId=" + p1_MerId + "；r1_Code=" + r1_Code
				+ "；r2_TrxId=" + r2_TrxId + "；r3_Amt=" + r3_Amt+ "；r4_Cur=" + r4_Cur + "；r5_Pid=" + r5_Pid
				+ "；r6_Order=" + r6_Order + "；r7_Uid=" + r7_Uid + "；r8_MP=" + r8_MP + "；r9_BType=" + r9_BType
				+ "；hmac=" + hmac);
		
		boolean isOK = false;
		// 校验返回数据包
		isOK = PaymentForOnlineService.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
				r8_MP, r9_BType, keyvalue);
		
		if(isOK) {
			String result = null;			
			String amt = BigDecimal.valueOf(Float.parseFloat(r3_Amt)).multiply(new BigDecimal(100)).toString();
			String drawamt = ChargeUtil.getDrawamt(amt);
			String url = null;
			StringBuffer param = new StringBuffer();
			param.append("ttransactionid=").append(r6_Order).append("&bankorderid=").append(r6_Order)
			.append("&bankordertime=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
			.append("&banktrace=").append(r2_TrxId).append("&retcode=").append(r1_Code)
			.append("&retmemo=").append(" ").append("&amt=").append(amt).append("&drawamt=").append(drawamt);
			
			if(r1_Code.equals("1")) {
				logger.info("易宝支付通知处理->交易成功处理->开始!");
				try {
					url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
					logger.info("易宝支付通知处理->交易成功处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());					
				} catch (IOException e) {
					logger.error("易宝支付通知处理->交易成功处理->出现异常!");
					e.printStackTrace();
					return null;
				}
				logger.info("易宝支付通知处理->交易成功处理->返回result:" + result);
				chargeUtil.afterCharge(result);
				logger.info("易宝支付通知处理->交易成功处理->结束!");
				
				if(r9_BType.equals("1")) {
					// 产品通用接口支付成功返回-浏览器重定向					
				} else if(r9_BType.equals("2")) {
					// 产品通用接口支付成功返回-服务器点对点通讯
					// 如果在发起交易请求时 设置使用应答机制时，必须应答以"success"开头的字符串，大小写不敏感
					this.printStr("SUCCESS");					
				}								
				this.sendRedirect(r8_MP);
				
			} else {
				logger.info("易宝支付通知处理->交易失败处理->开始!");
				try {
					url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
					logger.info("易宝支付通知处理->交易失败处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
				} catch (IOException e) {
					logger.error("易宝支付通知处理->交易失败处理->出现异常!", e);
					e.printStackTrace();
					return null;
				}
				logger.info("易宝支付通知处理->交易失败处理->返回result:" + result);
				logger.info("易宝支付通知处理->交易失败处理->结束!");
			}
				
		} else {
			logger.error("易宝支付通知处理->交易签名被篡改!");
		}
		
		logger.info("易宝支付通知处理->结束");
		return null;
	}
	
	public String bgRet2() {
		logger.info("易宝支付通知处理->开始");
		String merid = chargeconfigService.getChargeconfig("yeepay_MerId");//ConfigUtil.getConfig("charge.properties", "yeepay.merid");//			
		String r0_Cmd = StringUtil.formatString(request.getParameter("r0_Cmd")); // 业务类型
		String p1_MerId = StringUtil.formatString(merid); // 商户编号 //StringUtil.formatString(request.getParameter("p1_MerId"));
		String r1_Code = StringUtil.formatString(request.getParameter("r1_Code"));// 支付结果		
		String p2_Order = StringUtil.formatString(request.getParameter("p2_Order"));// 商户订单号		
		String p3_Amt = StringUtil.formatString(request.getParameter("p3_Amt"));// 成功金额		
		String p4_FrpId = StringUtil.formatString(request.getParameter("p4_FrpId"));// 支付方式		
		String p5_CardNo = StringUtil.formatString(request.getParameter("p5_CardNo"));// 卡序列号组		
		String p6_confirmAmount = StringUtil.formatString(request.getParameter("p6_confirmAmount"));// 确认金额组		
		String p7_realAmount = StringUtil.formatString(request.getParameter("p7_realAmount"));// 实际金额组		
		String p8_cardStatus = StringUtil.formatString(request.getParameter("p8_cardStatus"));// 卡状态组		
		String p9_MP = StringUtil.formatString(request.getParameter("p9_MP"));// 扩展信息
		String pb_BalanceAmt = StringUtil.formatString(request.getParameter("pb_BalanceAmt"));// 支付余额 注：此项仅为订单成功,并且需要订单较验时才会有值。失败订单的余额返部返回原卡密中		
		String pc_BalanceAct = StringUtil.formatString(request.getParameter("pc_BalanceAct"));// 余额卡号  注：此项仅为订单成功,并且需要订单较验时才会有值		
		String hmac	= StringUtil.formatString(request.getParameter("hmac"));// 签名数据
		
		logger.info("易宝支付通知处理->MerId=" + merid + "；r0_Cmd=" + r0_Cmd + "；p1_MerId=" + p1_MerId + "；r1_Code=" + r1_Code
				+ "；p2_Order=" + p2_Order + "；p3_Amt=" + p3_Amt+ "；p4_FrpId=" + p4_FrpId + "；p5_CardNo=" + p5_CardNo
				+ "；p6_confirmAmount=" + p6_confirmAmount + "；p7_realAmount=" + p7_realAmount + "；p8_cardStatus=" + p8_cardStatus 
				+ "；p9_MP=" + p9_MP + "；pb_BalanceAmt=" + pb_BalanceAmt + "；pc_BalanceAct=" + pc_BalanceAct 
				+ "；hmac=" + hmac);
		
		
		boolean isOK = false;
		// 校验返回数据包
		isOK = NonBankcardService.verifyCallback(r0_Cmd, r1_Code, p1_MerId,
				p2_Order, p3_Amt, p4_FrpId, p5_CardNo, p6_confirmAmount,
				p7_realAmount, p8_cardStatus, p9_MP, pb_BalanceAmt,
				pc_BalanceAct, hmac);

		if(isOK) {
			String result = null;			
			String amt = BigDecimal.valueOf(Float.parseFloat(p3_Amt)).multiply(new BigDecimal(100)).toString();
			String drawamt = "0";//ChargeUtil.getDrawamt(amt);
			String url = null;
			StringBuffer param = new StringBuffer();
			param.append("ttransactionid=").append(p2_Order).append("&bankorderid=").append(p2_Order)
			.append("&bankordertime=").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
			.append("&banktrace=").append(" ").append("&retcode=").append(r1_Code)
			.append("&retmemo=").append(" ").append("&amt=").append(amt).append("&drawamt=").append(drawamt);
			
			if(r1_Code.equals("1")) {
				logger.info("易宝支付通知处理->交易成功处理->开始!");
				try {
					url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
					logger.info("易宝支付通知处理->交易成功处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
				} catch (IOException e) {
					logger.error("易宝支付通知处理->交易成功处理->出现异常!");
					e.printStackTrace();
					return null;
				}
				logger.info("易宝支付通知处理->交易成功处理->返回result:" + result);
				chargeUtil.afterCharge(result);
				logger.info("易宝支付通知处理->交易成功处理->结束!");
				this.printStr("SUCCESS");				
			} else {
				logger.info("易宝支付通知处理->交易失败处理->开始!");
				try {
					url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
					logger.info("易宝支付通知处理->交易失败处理->url=" + url + "；param=" + param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
				} catch (IOException e) {
					logger.error("易宝支付通知处理->交易失败处理->出现异常!", e);
					e.printStackTrace();
					return null;
				}
				logger.info("易宝支付通知处理->交易失败处理->返回result:" + result);
				logger.info("易宝支付通知处理->交易失败处理->结束!");
			}
				
		} else {
			logger.error("易宝支付通知处理->交易签名被篡改!");
		}
		
		logger.info("易宝支付通知处理->结束");
		return null;
	}
	
	
	private String getPageRetUrl(String str) {
		String ret = chargeconfigService.getChargeconfig("yeepay.web.pagereturlsuccess");//ConfigUtil.getConfig("charge.properties", "yeepay.web.pagereturlsuccess");
		if (!StringUtils.isBlank(str)) {
//			if ("B".equals(str)) {
//				ret = ConfigUtil.getConfig("charge.properties", "yeepay.web.pagereturlsuccess");
//			} else if ("W".equals(str)) {
//				ret = ConfigUtil.getConfig("charge.properties", "yeepay.wap.pagereturlsuccess");
//			} else if ("C".equals(str)) {
//				ret = ConfigUtil.getConfig("charge.properties", "yeepay.client.pagereturlsuccess");
//			} else if ("M".equals(str)) {
//				ret = ConfigUtil.getConfig("charge.properties", "yeepay.b2bweb.pagereturlsuccess");
//			}
		}
		return ret;
	}
	
	private void sendRedirect(String url) {
		try {
			response.sendRedirect(this.getPageRetUrl(url));
		} catch (IOException e) {			
			logger.error("页面转发出错：", e);
		}
	}
	
}
