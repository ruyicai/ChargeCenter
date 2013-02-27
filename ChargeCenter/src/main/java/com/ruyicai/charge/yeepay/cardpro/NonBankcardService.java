package com.ruyicai.charge.yeepay.cardpro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.yeepay.htmlcommon.DigestUtil;

public class NonBankcardService {
	private static Logger log = Logger.getLogger(NonBankcardService.class);
	private static String p0_Cmd = "ChargeCardDirect"; // 请求命令名称
	private static String decodeCharset = "GBK"; // 字符方式
	private static String p1_MerId = ConfigUtil.getConfig("charge.properties", "yeepay.merid"); // 商户编号
	private static String keyValue = ConfigUtil.getConfig("charge.properties", "yeepay.keyvalue"); // 商户密钥
	private static String annulCardReqURL = ConfigUtil.getConfig("charge.properties", "yeepay.cardpro.requrl"); // 请求地址

	// 卡号卡密采用算法模式，当前固定为该值
	private static String annulCardReqDESMode = "1";
	// 使用应答机制
	private static String NEEDRESPONSE = "1";

	/**
	 * 消费请求 该方法是根据《易宝支付非银行卡支付专业版接口文档 v3.0》对发起支付请求进行的封装
	 * 具体参数含义请仔细阅读《易宝支付非银行卡支付专业版接口文档 v3.0》 商户订单号
	 * 
	 * @param p2_Order
	 *            订单金额
	 * @param p3_Amt
	 *            是否较验订单金额
	 * @param p4_verifyAmt
	 *            产品名称
	 * @param p5_Pid
	 *            产品类型
	 * @param p6_Pcat
	 *            产品描述
	 * @param p7_Pdesc
	 *            通知地址
	 * @param p8_Url
	 *            扩展信息
	 * @param pa_MP
	 *            卡面额组
	 * @param pa7_cardAmt
	 *            卡号组
	 * @param pa8_cardNo
	 *            支付方式
	 * @param pd_FrpId
	 *            通知是否需要应答
	 * @param pr_NeedResponse
	 *            用户ID
	 * @param pz_userId
	 *            用户注册时间
	 * @param pz1_userRegTime
	 * @return
	 */
	public static NonBankcardPaymentResult pay(String p2_Order, String p3_Amt,
			String p4_verifyAmt, String p5_Pid, String p6_Pcat,
			String p7_Pdesc, String p8_Url, String pa_MP, String pa7_cardAmt,
			String pa8_cardNo, String pa9_cardPwd, String pd_FrpId,
			String pr_NeedResponse, String pz_userId, String pz1_userRegTime, String merid, String requrl) {

		// 卡号和卡密不得为空
		if (pa8_cardNo == null || pa8_cardNo.equals("") || pa9_cardPwd == null
				|| pa9_cardPwd.equals("")) {
			log.error("pa7_cardNo or pa8_cardPwd is empty.");
			throw new RuntimeException("pa7_cardNo or pa8_cardPwd is empty.");
		}

		// 生成hmac，保证交易信息不被篡改,关于hmac详见《易宝支付非银行卡支付专业版接口文档 v3.0》
		String hmac = "";
		hmac = DigestUtil.getHmac(new String[] { p0_Cmd, p1_MerId, p2_Order,
				p3_Amt, p4_verifyAmt, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, pa_MP,
				pa7_cardAmt, pa8_cardNo, pa9_cardPwd, pd_FrpId,
				pr_NeedResponse, pz_userId, pz1_userRegTime }, keyValue);
		// 封装请求参数，参数说明详见《易宝支付非银行卡支付专业版接口文档 v3.0》
		Map reqParams = new HashMap();
		reqParams.put("p0_Cmd", p0_Cmd);
		reqParams.put("p1_MerId", merid);//p1_MerId
		reqParams.put("p2_Order", p2_Order);
		reqParams.put("p3_Amt", p3_Amt);
		reqParams.put("p4_verifyAmt", p4_verifyAmt);
		reqParams.put("p5_Pid", p5_Pid);
		reqParams.put("p6_Pcat", p6_Pcat);
		reqParams.put("p7_Pdesc", p7_Pdesc);
		reqParams.put("p8_Url", p8_Url);
		reqParams.put("pa_MP", pa_MP);
		reqParams.put("pa7_cardAmt", pa7_cardAmt);
		reqParams.put("pa8_cardNo", pa8_cardNo);
		reqParams.put("pa9_cardPwd", pa9_cardPwd);
		reqParams.put("pd_FrpId", pd_FrpId);
		reqParams.put("pr_NeedResponse", pr_NeedResponse);
		reqParams.put("pz_userId", pz_userId);
		reqParams.put("pz1_userRegTime", pz1_userRegTime);
		reqParams.put("hmac", hmac);
		List responseStr = null;
		try {
			// 发起支付请求
			log.debug("Begin http communications,request params[" + reqParams
					+ "]");
			responseStr = HttpUtils.URLPost(requrl, reqParams);//annulCardReqURL
			log.debug("End http communications.responseStr:" + responseStr);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		if (responseStr.size() == 0) {
			log.error("no response.");
			throw new RuntimeException("no response.");
		}
		// 创建非银行卡专业版消费请求结果
		NonBankcardPaymentResult rs = new NonBankcardPaymentResult();
		// 解析易宝支付返回的消费请求结果,关于返回结果数据详见《易宝支付非银行卡支付专业版接口文档 v3.0》
		for (int t = 0; t < responseStr.size(); t++) {
			String currentResult = (String) responseStr.get(t);
			log.debug("responseStr[" + t + "]:" + currentResult);
			if (currentResult == null || currentResult.equals("")) {
				continue;
			}
			int i = currentResult.indexOf("=");
			log.debug("i=" + i);
			int j = currentResult.length();
			if (i >= 0) {
				log.debug("find =.");
				String sKey = currentResult.substring(0, i);
				String sValue = currentResult.substring(i + 1);
				if (sKey.equals("r0_Cmd")) {
					rs.setR0_Cmd(sValue);
				} else if (sKey.equals("r1_Code")) {
					rs.setR1_Code(sValue);
				} else if (sKey.equals("r6_Order")) {
					rs.setR6_Order(sValue);
				} else if (sKey.equals("rq_ReturnMsg")) {
					rs.setRq_ReturnMsg(sValue);
				} else if (sKey.equals("hmac")) {
					rs.setHmac(sValue);
				} else {
					log.error("throw exception:" + currentResult);
					throw new RuntimeException(currentResult);
				}
			} else {
				log.error("throw exception:" + currentResult);
				throw new RuntimeException(currentResult);
			}
		}
		// 不成功则抛出异常
		if (!rs.getR1_Code().equals("1")) {
			log.error("errorCode:" + rs.getR1_Code() + ";errorMessage:"
					+ rs.getRq_ReturnMsg());
			throw new RuntimeException("errorCode:" + rs.getR1_Code()
					+ ";errorMessage:" + rs.getRq_ReturnMsg());
		}
		String newHmac = "";
		newHmac = DigestUtil.getHmac(
				new String[] { rs.getR0_Cmd(), rs.getR1_Code(),
						rs.getR6_Order(), rs.getRq_ReturnMsg() }, keyValue);
		// hmac不一致则抛出异常
		if (!newHmac.equals(rs.getHmac())) {
			log.error("交易签名被篡改");
			throw new RuntimeException("交易签名被篡改");
		}
		return (rs);
	}

	/**
	 * 校验交易结果通知 该方法是根据《易宝支付非银行卡支付专业版接口文档 v3.0》对易宝支付返回扣款数据进行校验
	 * 具体参数含义请仔细阅读《易宝支付非银行卡支付专业版接口文档 v3.0》 业务类型
	 * 
	 * @param r0_Cmd
	 *            支付结果
	 * @param r1_Code
	 *            商户编号
	 * @param p1_MerId
	 *            商户订单号
	 * @param p2_Order
	 *            成功金额
	 * @param p3_Amt
	 *            支付方式
	 * @param p4_FrpId
	 *            卡序列号组
	 * @param p5_CardNo
	 *            确认金额组
	 * @param p6_confirmAmount
	 *            实际金额组
	 * @param p7_realAmount
	 *            卡状态组
	 * @param p8_cardStatus
	 *            扩展信息
	 * @param p9_MP
	 *            支付余额
	 * @param pb_BalanceAmt
	 *            余额卡号
	 * @param pc_BalanceAct
	 *            签名数据
	 * @param hmac
	 */
	public static boolean verifyCallback(String r0_Cmd, String r1_Code,
			String p1_MerId, String p2_Order, String p3_Amt, String p4_FrpId,
			String p5_CardNo, String p6_confirmAmount, String p7_realAmount,
			String p8_cardStatus, String p9_MP, String pb_BalanceAmt,
			String pc_BalanceAct, String hmac) {

		log.debug("Recv payment result:[r0_Cmd=" + r0_Cmd + ";r1_Code="
				+ r1_Code + ";p1_MerId=" + p1_MerId + ";p2_Order=" + p2_Order
				+ ";p3_Amt=" + p3_Amt + ";p4_FrpId=" + p4_FrpId + ";p5_CardNo="
				+ p5_CardNo + ";p6_confirmAmount=" + p6_confirmAmount
				+ ";p7_realAmount=" + p7_realAmount + ";p8_cardStatus="
				+ p8_cardStatus + ";p9_MP=" + p9_MP + ";pb_BalanceAmt="
				+ pb_BalanceAmt + ";pc_BalanceAct=" + pc_BalanceAct + ";hmac="
				+ hmac);

		String newHmac = DigestUtil.getHmac(new String[] { r0_Cmd, r1_Code,
				p1_MerId, p2_Order, p3_Amt, p4_FrpId, p5_CardNo,
				p6_confirmAmount, p7_realAmount, p8_cardStatus, p9_MP,
				pb_BalanceAmt, pc_BalanceAct }, keyValue);
//		if (!hmac.equals(newHmac)) {
//			String errorMessage = "交易签名被篡改!";
//			log.debug(errorMessage);
//			throw new RuntimeException(errorMessage);
//		}
//		if (!r1_Code.equals("1")) {
//			throw new RuntimeException("Payment is fail!r1_Code=" + r1_Code);
//		}
		return hmac.equals(newHmac);
	}

	/**
	 * 该方法是根据《易宝支付非银行卡支付专业版接口文档 v3.0》生成一个模拟的交易结果通知串.
	 * 商户使用模拟的交易结果通知串可以直接测试自己的交易结果接收程序(callback)的正确性.
	 * 实际的交易结果通知机制以《易宝支付非银行卡支付专业版接口文档 v3.0》为准，该方法只是 模拟了交易结果通知串.
	 * 
	 * @param p2_Order
	 * @param p3_Amt
	 * @param p8_Url
	 * @param pa7_cardNo
	 * @param pa8_cardPwd
	 * @param pa_MP
	 * @return
	 */
	public static String generationTestCallback(String p2_Order, String p3_Amt,
			String P4_verifyAmount, String p5_Pid, String p6_Pcat,
			String p7_Pdesc, String p8_Url, String pa_MP, String pa7_cardAmt,
			String pa8_cardNo, String pa9_cardPwd, String pd_FrpId,
			String pr_NeedResponse, String pz_userId, String pz1_userRegTime) {
		String callback = "";
		Map reqParams = new HashMap();
		reqParams.put("p0_Cmd", p0_Cmd);
		reqParams.put("p1_MerId", p1_MerId);
		reqParams.put("p2_Order", p2_Order);
		reqParams.put("p3_Amt", p3_Amt);
		reqParams.put("P4_verifyAmount", P4_verifyAmount);
		reqParams.put("p5_Pid", p5_Pid);
		reqParams.put("p6_Pcat", p6_Pcat);
		reqParams.put("p7_Pdesc", p7_Pdesc);
		reqParams.put("p8_Url", p8_Url);
		reqParams.put("pa_MP", pa_MP);
		reqParams.put("pa7_cardAmt", pa7_cardAmt);
		reqParams.put("pa8_cardNo", pa8_cardNo);
		reqParams.put("pa9_cardPwd", pa9_cardPwd);
		reqParams.put("pd_FrpId", pd_FrpId);
		reqParams.put("pr_NeedResponse", pr_NeedResponse);
		reqParams.put("pz_userId", pz_userId);
		reqParams.put("pz1_userRegTime", pz1_userRegTime);
		List responseStr = null;
		try {
			// 发起支付请求
			log.debug("Begin http communications,request params[" + reqParams
					+ "]");
			responseStr = HttpUtils
					.URLPost(
							"http://tech.yeepay.com:8080/robot/generationCallback.action",
							reqParams);
			log.debug("End http communications.responseStr:" + responseStr);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		if (responseStr.size() == 0) {
			log.error("no response.");
			throw new RuntimeException("no response.");
		}
		for (int t = 0; t < responseStr.size(); t++) {
			String currentResult = (String) responseStr.get(t);
			log.debug("responseStr[" + t + "]:" + currentResult);
			if (currentResult == null || currentResult.equals("")) {
				continue;
			}
			callback = (String) responseStr.get(t);
		}
		return (callback);
	}
}
