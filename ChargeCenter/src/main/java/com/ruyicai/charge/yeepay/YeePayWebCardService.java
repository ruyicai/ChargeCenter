package com.ruyicai.charge.yeepay;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DigestUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.yeepay.util.CardTypeManager;

@Service
public class YeePayWebCardService {

	private Logger logger = Logger.getLogger(YeePayWebCardService.class);
	
	@Autowired
	ChargeconfigService chargeconfigService;
	@Autowired
	ChargeUtil chargeUtil;

	public Map<String, String> charge(String userno, String bankid, String accesstype,
			String paytype, String bankaccount, String amt, String jsonString, String channel, String subchannel,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("易宝web非银行充值开始");
		String errorcode = ErrorCode.OK.value;
		Map<String, String> retMap = new HashMap<String, String>();
		
		logger.info("易宝web非银行充值->得到参数：jsonString=" + jsonString);
		String jsonString2 = request.getParameter("jsonString");// 获取请求信息
		logger.info("易宝web非银行充值->得到参数：jsonString2=" + jsonString2);
		Map<String, Object> map = JsonUtil.transferJson2Map(jsonString2);
	
		String cardNo = map.containsKey("card_no") ? map.get("card_no").toString() : ""; // 点卡卡号
		String cardPassword = map.containsKey("card_pwd") ? map.get("card_pwd").toString() : ""; // 点卡密码
		String totalAmt = map.containsKey("totalAmount") ? map.get("totalAmount").toString() : "";
		String ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
		String continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
		String orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
		
		CardTypeManager util = new CardTypeManager();
		String pd_FrpId = util.getUnBankCode(paytype.substring(2, 4));
		DecimalFormat df = new DecimalFormat("#0.00");
		String amtTrans = df.format(Double.valueOf(amt) / 100);; // 订单金额（2位小数）
		String totalAmtTrans = df.format(Double.valueOf(totalAmt) / 100);; // （2位小数）
		StringBuffer retStr = new StringBuffer();
		retStr.append("userno=").append(userno).append("&type=10")
				.append("&bankid=").append(bankid).append("&accesstype=")
				.append(accesstype).append("&amt=").append(amt)
				.append("&paytype=").append(paytype).append("&bankaccount=")
				.append(pd_FrpId).append("&channel=").append(channel).append("&subchannel=").append(subchannel)
				.append("&ladderpresentflag=").append(ladderpresentflag)
				.append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
		String param = retStr.toString();
		String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
		String result = HttpRequest.doPostRequest(url, param);		
	
		logger.info("result=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		errorcode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		
		if (!"0".equals(errorcode)) {
			logger.info("生成交易记录出现错误 errorCode=" + errorcode);		
		} else {
			String transactionId = mapResult.containsKey("value")? mapResult.get("value").toString() : "";
			// 商家设置用户购买商品的支付信息
			String p0_Cmd = "ChargeCardDirect"; // 业务类型
			String p1_MerId = chargeconfigService.getChargeconfig("yeepay_MerId");//ConfigUtil.getConfig("charge.properties", "yeepay_MerId"); // 商户编号(读配置文件)
			String p2_Order = transactionId; // 商户订单号
			String p3_Amt = amtTrans.toString(); // 支付金额
			String p4_verifyAmt = "true"; // 是否较验订单金额
			String p5_Pid = "yeePayCardCharge"; // 产品名称
			String p6_Pcat = "yeePayCardCharge"; // 产品类型
			String p7_Pdesc = "yeePayCardCharge"; // 产品描述
			String p8_Url = chargeconfigService.getChargeconfig("yeepaycardnotifyurl");//ConfigUtil.getConfig("charge.properties", "yeepaycardnotifyurl");// 响应地址(读配置文件)
			String pa_MP = "yeePayCardCharge"; // 商户扩展信息
			String pa7_cardAmt = totalAmtTrans; // 卡面额组(充值卡的面值)
			String pa8_cardNo = cardNo; // 卡号组
			String pa9_cardPwd = cardPassword; // 卡密组
			String pr_NeedResponse = "1"; // 应答机制
			String pz_userId = ""; // 用户ID 用户在商户处的唯一ID
			String pz1_userRegTime = ""; // 用户在商户处注册的时间
			String hmac = "";// mac值
			String keyValue = chargeconfigService.getChargeconfig("yeepay_key");//ConfigUtil.getConfig("charge.properties", "yeepay_key"); // 密钥(读配置文件)
			hmac = DigestUtil.getHmac(new String[] { p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_verifyAmt, p5_Pid, p6_Pcat, p7_Pdesc,
					p8_Url, pa_MP, pa7_cardAmt, pa8_cardNo, pa9_cardPwd, pd_FrpId, pr_NeedResponse, pz_userId, pz1_userRegTime },
					keyValue);// 3.mac加密

			String loggerInfo = "p0_Cmd:" + p0_Cmd + ",p1_MerId:" + p1_MerId + ",p2_Order:" + p2_Order + ",p3_Amt:" + p3_Amt
					+ ",p4_verifyAmt:" + p4_verifyAmt + ",p5_Pid:" + p5_Pid + ",p6_Pcat:" + p6_Pcat + ",p7_Pdesc:" + p7_Pdesc
					+ ",p8_Url:" + p8_Url + ",pa_MP:" + pa_MP + ",pa7_cardAmt:" + pa7_cardAmt + ",pa8_cardNo:" + pa8_cardNo
					+ ",pa9_cardPwd:" + pa9_cardPwd + ",pd_FrpId:" + pd_FrpId + ",pr_NeedResponse:" + pr_NeedResponse + ",hmac:" + hmac;
			logger.info("易宝.Web.非银行卡充值,userno:" + userno + ",ttransactionID:" + transactionId + "," + loggerInfo);

			Map<String, String> rspMap = new HashMap<String, String>();
			rspMap.put("p0_Cmd", p0_Cmd);
			rspMap.put("p1_MerId", p1_MerId);
			rspMap.put("p2_Order", p2_Order);
			rspMap.put("p3_Amt", p3_Amt);
			rspMap.put("p4_verifyAmt", p4_verifyAmt);
			rspMap.put("p5_Pid", p5_Pid);
			rspMap.put("p6_Pcat", p6_Pcat);
			rspMap.put("p7_Pdesc", p7_Pdesc);
			rspMap.put("p8_Url", p8_Url);
			rspMap.put("pa_MP", pa_MP);
			rspMap.put("pa7_cardAmt", pa7_cardAmt);
			rspMap.put("pa8_cardNo", pa8_cardNo);
			rspMap.put("pa9_cardPwd", pa9_cardPwd);
			rspMap.put("pd_FrpId", pd_FrpId);
			rspMap.put("pr_NeedResponse", pr_NeedResponse);
			rspMap.put("pz_userId", pz_userId);
			rspMap.put("pz1_userRegTime", pz1_userRegTime);
			rspMap.put("hmac", hmac);
			logger.info("yeepay请求参数  rspMap=" + rspMap.toString());
						
			url = new CardTypeManager().getCmppSubmitReqUrl(rspMap, chargeconfigService.getChargeconfig("yeepaychargeurl"));
			logger.info("发给易宝的 url=" + url);
			String info = HttpRequest.doGetRequestGBK(url);
			logger.info("web网站点卡充值，服务器点对点发送请求易宝返回:" + info);
			// 响应web客户端
			logger.info("易宝.Web.非银行卡充值,响应客户端Json串:" + errorcode);
		}		
		
		retMap.put("error_code", errorcode);
		logger.info("易宝web非银行充值结束");		
		return retMap;
	}

	public void notify(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.info("---------------Yeepay Card 通知处理开始----------------------");
		PrintWriter pw = response.getWriter();
		pw.write("successForYeePayCardCharge!!");
		String r0_Cmd = request.getParameter("r0_Cmd");
		String r1_Code = request.getParameter("r1_Code");
		String p1_MerId = request.getParameter("p1_MerId");
		String p2_Order = request.getParameter("p2_Order");
		String p3_Amt = request.getParameter("p3_Amt");
		String p4_FrpId = request.getParameter("p4_FrpId");
		String p5_CardNo = request.getParameter("p5_CardNo");
		String p6_confirmAmount = request.getParameter("p6_confirmAmount");
		String p7_realAmount = request.getParameter("p7_realAmount");
		String p8_cardStatus = request.getParameter("p8_cardStatus");
		String p9_MP = request.getParameter("p9_MP");
		String pb_BalanceAmt = request.getParameter("pb_BalanceAmt");
		String pc_BalanceAct = request.getParameter("pc_BalanceAct");
		String hmac = request.getParameter("hmac");
		String keyValue = chargeconfigService.getChargeconfig("yeepay_key");//ConfigUtil.getConfig("charge.properties", "yeepay_key");
		String newHmac = DigestUtil.getHmac(new String[] {  r0_Cmd, r1_Code,p1_MerId,p2_Order,p3_Amt,p4_FrpId,
			   	p5_CardNo,p6_confirmAmount,p7_realAmount,p8_cardStatus,p9_MP,pb_BalanceAmt,pc_BalanceAct}, keyValue);
		logger.info("Method:doPost,p2_Order:"+p2_Order+",r0_Cmd:"+r0_Cmd+",r1_Code:"+r1_Code+",p1_MerId:"+p1_MerId+",p3_Amt:"+p3_Amt+",p4_FrpId:"+p4_FrpId+",p5_CardNo:"+p5_CardNo+",p6_confirmAmount:"+p6_confirmAmount+",p7_realAmount:"+p7_realAmount+",p8_cardStatus:"+p8_cardStatus+",p9_MP:"+p9_MP+",pb_BalanceAmt:"+pb_BalanceAmt+",pc_BalanceAct:"+pc_BalanceAct+",hmac:"+hmac+",keyValue:"+keyValue+",newHmac:"+newHmac);
		if(!hmac.equals(newHmac)){
			logger.error("Method:doPost,p2_Order:"+p2_Order+",hmac验证失败");
			return ;
		}
		if(p2_Order == null || "".equals(p2_Order)){  //验证订单号是否为空
			logger.error("Method:doPost,p2_Order:"+p2_Order+"商户订单号为空");
			return ;
		}
		if(!"1".equals(r1_Code)){// 充值失败处理
			logger.info("yeepay返回码  r1_Code="+r1_Code);
			return;
		}
		if(r1_Code == null || "".equals(r1_Code) ){  //银行返回状态信息不能为空
			logger.error("Method:doPost,p2_Order:"+p2_Order+"银行返回的状态信息为空");
			return;
		}
		
		String amt = BigDecimal.valueOf(Float.parseFloat(p3_Amt)).multiply(new BigDecimal(100)).toString();
		String drawamt = "0";//ChargeUtil.getDrawamt(amt);
		StringBuffer param = new StringBuffer();
		param.append("ttransactionid=")
				.append(p2_Order)
				.append("&bankorderid=")
				.append(p2_Order)
				.append("&bankordertime=")
				.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date()))
				.append("&banktrace=")
				.append(p2_Order)
				.append("&retcode=")
				.append(r1_Code)
				.append("&retmemo=")
				.append(r1_Code)
				.append("&amt=")
				.append(amt)
				.append("&drawamt=")
				.append(drawamt);
		String lotterySuccessUrl = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties","lotterySuccessUrl")
		logger.info("充值成功请求：url="
				+ lotterySuccessUrl + ",param=" + param.toString());
		String res = HttpRequest
				.doPostRequest(lotterySuccessUrl, param.toString());
		logger.info("充值成功处理返回: result=" + res);
		chargeUtil.afterCharge(res);
		return ;
	}
}
