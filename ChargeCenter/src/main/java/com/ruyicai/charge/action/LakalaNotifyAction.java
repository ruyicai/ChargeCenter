package com.ruyicai.charge.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.lakala.LakalaUtil;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.HttpRequest;

public class LakalaNotifyAction implements ServletRequestAware,
		ServletResponseAware {
	

	private Logger logger = Logger.getLogger(LakalaNotifyAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired
	ChargeUtil chargeUtil;

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		try {
			arg0.setCharacterEncoding("GBK");
		} catch (UnsupportedEncodingException e) {
			logger.error("request:转换编码出错：", e);
			e.printStackTrace();
		}
		this.request = arg0;
	}
	
	
	public String bgRet() {
		logger.info("拉卡拉支付通知处理->开始");	
		
		try {
			String ver = request.getParameter("VER");// 版本号
			String merid = request.getParameter("MERID");// 商户号
			String orderid = request.getParameter("ORDERID");// 订单号
			String amount = request.getParameter("AMOUNT");// 付款金额
			String randnum = request.getParameter("RANDNUM"); // 随机数
			String accountdate = request.getParameter("ACCOUNTDATE"); // 账务日期
			String platseq = request.getParameter("PLATSEQ");// 支付平台流水号
			String paymentseri = request.getParameter("PAYMENTSERI"); // 支付流水
			String terminalno = request.getParameter("TERMINALNO"); // 终端号
			String accountname = request.getParameter("ACCOUNTNAME"); // 用户account
																		// 默认txy
			String mactype = request.getParameter("MACTYPE");// 校验类型
			String mac = request.getParameter("MAC");// 校验数据
			String searchmobilenum = request.getParameter("SEARCHMOBILENUM");// 手机号

			ver = (ver == null ? "" : ver);
			merid = (merid == null ? "" : merid);
			orderid = (orderid == null ? "" : orderid);
			amount = (amount == null ? "" : amount);
			randnum = (randnum == null ? "" : randnum);
			accountdate = (accountdate == null ? "" : accountdate);
			platseq = (platseq == null ? "" : platseq);
			paymentseri = (paymentseri == null ? "" : paymentseri);
			terminalno = (terminalno == null ? "" : terminalno);
			accountname = (accountname == null ? "" : accountname);
			mactype = (mactype == null ? "" : mactype);
			mac = (mac == null ? "" : mac);
			searchmobilenum = (searchmobilenum == null ? "" : searchmobilenum);

			logger.info("ver=" + ver + ";merid=" + merid + ";orderid="
					+ orderid + "amount=" + amount + ";randnum=" + randnum
					+ ";accountdate=" + accountdate + "platseq=" + platseq
					+ ";paymentseri=" + paymentseri + ";terminalno="
					+ terminalno + "accountname=" + accountname + ";mactype="
					+ mactype + ";mac=" + mac + ";searchmobilenum="
					+ searchmobilenum);

			if (!ver.equals(LakalaUtil.version)) {
				logger.error("版本不一致，参数中ver=" + ver + ";配置参数中version="
						+ LakalaUtil.version);
				return null;
			}
			String merchantId = chargeconfigService.getChargeconfig("lakala.merid");//LakalaUtil.merchantId;
			if (!merid.equals(merchantId)) {
				logger.error("商户号不一致，参数中merid=" + merid + ";配置参数中merchantId="
						+ merchantId);
				return null;
			}
			if (!mactype.equals(LakalaUtil.macType)) {
				logger.error("校验类型不一致，参数中mactype=" + mactype + ";配置参数中macType="
						+ LakalaUtil.macType);
				return null;
			}

			StringBuffer sb = new StringBuffer();
			sb.append(ver).append(merid).append(LakalaUtil.pwd).append(orderid)
					.append(amount).append(randnum).append(accountdate)
					.append(platseq).append("2").append(paymentseri)
					.append(terminalno).append(accountname);
			String originalSign = sb.toString();
			logger.info("originalSign=" + originalSign);

			LakalaUtil lakalaUtil = new LakalaUtil();
			String mac2 = lakalaUtil.createSign(originalSign);
			logger.info("mac2=" + mac2);

			if (!mac.equals(mac2)) {
				logger.error("商户校签不通过，收到的验签数据mac=" + mac + ";实际得到的验签数据mac2="
						+ mac2);
				return null;
			}

			String result = null;
			String drawamt = ChargeUtil.getDrawamt(amount);
			String url = null;
			StringBuffer param = new StringBuffer();
			param.append("ttransactionid=")
					.append(orderid)
					.append("&bankorderid=")
					.append(paymentseri)
					.append("&bankordertime=")
					.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.format(new Date())).append("&banktrace=")
					.append(platseq).append("&retcode=").append(" ")
					.append("&retmemo=").append(" ").append("&amt=")
					.append(amount).append("&drawamt=").append(drawamt);

			//
			boolean flag = true;
			if (flag) {
				logger.info("拉卡拉支付通知处理->交易成功处理->开始!");
				try {
					url = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl");
					logger.info("拉卡拉支付通知处理->交易成功处理->url=" + url + "；param="
							+ param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
				} catch (IOException e) {
					logger.error("拉卡拉支付通知处理->交易成功处理->出现异常!", e);
					return null;
				}

				logger.info("拉卡拉支付通知处理->交易成功处理->返回result:" + result);
				chargeUtil.afterCharge(result);
				this.printSuccess();
				logger.info("拉卡拉支付通知处理->交易成功处理->结束!");
			} else {
				logger.info("拉卡拉支付通知处理->交易失败处理->开始!");

				try {
					url = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl");
					logger.info("拉卡拉支付通知处理->交易失败处理->url=" + url + "；param="
							+ param.toString());
					result = HttpRequest.doPostRequest(url, param.toString());
				} catch (IOException e) {
					logger.error("拉卡拉支付通知处理->交易失败处理->出现异常!", e);
					e.printStackTrace();
					return null;
				}

				logger.info("拉卡拉支付通知处理->交易失败处理->返回result:" + result);
				logger.info("拉卡拉支付通知处理->交易失败处理->结束!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("拉卡拉支付通知处理出现异常：", e);
		}
				
		logger.info("拉卡拉支付通知处理->结束");
		return null;
	}
	
	private void printSuccess() throws IOException {
		Document document = DocumentFactory.getInstance().createDocument();
		Element root = document.addElement("merpay");
		ArrayList al = new ArrayList();
		Element elmt = root.addElement("retcode");
		elmt.setText("0");
		al.add(elmt);

		elmt = root.addElement("merurl");
		elmt.setText(LakalaUtil.merUrl);
		al.add(elmt);

		root.setContent(al);
		StringWriter stringWriter = new StringWriter();
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding("GBK");
		XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);
		xmlWriter.write(document);
		PrintWriter out = response.getWriter();
		out.print(stringWriter.toString());		
	}
}
