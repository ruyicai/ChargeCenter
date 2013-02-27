package com.ruyicai.charge.nineteenpay;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
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

import com.ruyicai.charge.consts.TransactionState;
import com.ruyicai.charge.domain.Nineteenpay;
import com.ruyicai.charge.nineteenpay.util.CardTypeManager;
import com.ruyicai.charge.nineteenpay.util.NineteenPayUtil;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ChargeUtil;
import com.ruyicai.charge.util.CipherUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.KeyedDigestMD5;
import com.ruyicai.charge.util.Md5Encrypt;

@Service
public class NineteenPayService {

	private Logger logger = Logger.getLogger(NineteenPayService.class);
	
	@Autowired
	ChargeconfigService chargeconfigService;
	@Autowired
	ChargeUtil chargeUtil;

	public Map<String, String> charge(String userno, String bankid, String accesstype,
			String paytype, String bankaccount, String amt, String jsonString, String channel, String subchannel,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("19Pay充值开始");
		String errorCode = ErrorCode.OK.value;
		Map<String, String> retMap = new HashMap<String, String>();
		
		logger.info("19Pay充值->得到参数：jsonString=" + jsonString);
		String jsonString2 = request.getParameter("jsonString");
		logger.info("19Pay充值->得到参数：jsonString2=" + jsonString2);
		
		Map<String, Object> map = JsonUtil.transferJson2Map(jsonString2);
		String cardNo = map.containsKey("card_no") ? map.get("card_no").toString() : ""; // 点卡卡号
		String cardPassword = map.containsKey("card_pwd") ? map.get("card_pwd").toString() : ""; // 点卡密码
		String totalAmt = map.containsKey("totalAmount") ? map.get("totalAmount").toString() : "";
		String ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
		String continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
		String orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 号
		
		String transactionId = "";// 平台交易流水号
		CardTypeManager cardManager = new CardTypeManager();
		String pd_FrpId = cardManager.getPmId(paytype.substring(2, 4));// 支付方式编码
		StringBuffer retStr = new StringBuffer();
		retStr.append("userno=").append(userno).append("&type=10").append("&bankid=").append(bankid).append("&accesstype=")
				.append(accesstype).append("&amt=").append(amt).append("&paytype=").append(paytype).append("&bankaccount=")
				.append(pd_FrpId).append("&channel=").append(channel).append("&subchannel=").append(subchannel)
				.append("&ladderpresentflag=").append(ladderpresentflag).append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
		String param = retStr.toString();
		String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
		logger.info("19Pay充值->url=" + url + ";param=" + param);
		String result = HttpRequest.doPostRequest(url, param);
		
		logger.info("result=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		
		if (!"0".equals(errorCode)) {
			logger.info("生成交易记录出现错误 errorCode=" + errorCode);			
		} else {
			String version_id = chargeconfigService.getChargeconfig("NP_version_id");//ConfigUtil.getConfig("charge.properties", "NP_version_id"); // 版本号
			String merchant_id = chargeconfigService.getChargeconfig("NP_merchant_id");//ConfigUtil.getConfig("charge.properties", "NP_merchant_id"); // 商户号
			String order_date = new SimpleDateFormat("yyyyMMdd").format(new Date());// 订单日期
			transactionId = mapResult.containsKey("value")? mapResult.get("value").toString() : "";
			String amount = (new DecimalFormat("#0.00")).format((Long.parseLong(amt) / 100));// 充值金额
			String key = chargeconfigService.getChargeconfig("NP_key");//ConfigUtil.getConfig("charge.properties", "NP_key"); // 商户密钥
			String cardnum1 = CipherUtil.encryptData(cardNo, key); // 卡号码DES加密
			String cardnum2 = CipherUtil.encryptData(cardPassword, key);
			String currency = chargeconfigService.getChargeconfig("NP_currency");//ConfigUtil.getConfig("charge.properties", "NP_currency"); // 货币类型
			String pm_id = pd_FrpId;// 支付方式id
			String pc_id = cardManager.getPcId(paytype.substring(2, 4));// 支付渠道id
			String notify_url = chargeconfigService.getChargeconfig("NP_notify_url");//ConfigUtil.getConfig("charge.properties", "NP_notify_url"); // 后台通知地址
			String select_amount = String.valueOf((Integer.parseInt(totalAmt) / 100));// 充值卡面值
			String order_pdesc = chargeconfigService.getChargeconfig("NP_order_pdesc");//ConfigUtil.getConfig("charge.properties", "NP_order_pdesc"); // 商品描述--[可为空]
			
			createNineteenpay(transactionId, userno, cardNo, cardPassword, amt, totalAmt, paytype);			
			
			StringBuilder builder = new StringBuilder();
			builder.append("version_id=").append(version_id).append("&merchant_id=").append(merchant_id)
					.append("&order_date=").append(order_date).append("&order_id=").append(transactionId)
					.append("&amount=").append(amount).append("&currency=").append(currency).append("&cardnum1=").append(cardnum1)
					.append("&cardnum2=").append(cardnum2).append("&pm_id=").append(pm_id).append("&pc_id=").append(pc_id)
					.append("&merchant_key=").append(key);
			logger.info("商户MD5验证摘要源串verifystring:" + builder.toString());
			
			String verifystring = KeyedDigestMD5.getKeyedDigest(builder.toString(), "");
			String order_pname = cardManager.getCardName(paytype.substring(2, 4));// 订单商品
			builder = new StringBuilder();
			builder.append("version_id=").append(version_id)
					.append("&merchant_id=").append(merchant_id)
					.append("&order_date=").append(order_date)
					.append("&order_id=").append(transactionId)
					.append("&amount=").append(amount).append("&currency=")
					.append(currency).append("&cardnum1=").append(cardnum1)
					.append("&cardnum2=").append(cardnum2).append("&pm_id=")
					.append(pm_id).append("&pc_id=").append(pc_id)
					.append("&verifystring=").append(verifystring)
					.append("&order_pdesc=").append(order_pdesc)
					.append("&user_name=").append("&user_phone=")
					.append("&user_mobile=").append("&user_email=")
					.append("&order_pname=").append(order_pname)
					.append("&select_amount=").append(select_amount)
					.append("&notify_url=");
			builder.append(URLEncoder.encode(notify_url, "UTF-8"));
			logger.info("param: " + builder.toString());
			
			String res = "";
			String requrl = chargeconfigService.getChargeconfig("NP_url");//ConfigUtil.getConfig("charge.properties", "NP_url")
			res = NineteenPayUtil.http(requrl, builder.toString());// 发送支付请求
			logger.info("支付请求受理结果:" + res);// 支付响应XML数据			
		}
		
		retMap.put("error_code", errorCode); // 充值受理成功
		logger.info("19Pay充值结束");
		return retMap;
	}
	
	public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("-----------------19pay 后台通知 处理开始-------------------");
		/*
		 * 一、获取高阳支付结果通知数据
		 */
		String version_id =request.getParameter("version_id");		// 版本号
		String merchant_id =request.getParameter("merchant_id") ;	//	商户代码
		String verifystring =request.getParameter("verifystring") ;	// 验证摘要串
		String order_date =request.getParameter("order_date") ;		// 订单日期
		String order_id =request.getParameter("order_id") ;			// 商户订单号
		String result =request.getParameter("result") ;				// 支付结果
		String amount =request.getParameter("amount") ;				// 金额
		String currency =request.getParameter("currency") ;			// 币种
		String pay_sq =request.getParameter("pay_sq") ;				// 支付流水号
		String pay_date =request.getParameter("pay_date") ;			// 支付时间
		String count =request.getParameter("count") ;				// 卡支付次数
		String card_num1 =request.getParameter("card_num1") ;		// 卡号
		String card_pwd1 =request.getParameter("card_pwd1") ;		// 卡密码
		String pm_id1 =request.getParameter("pm_id1") ;				// 支付方式
		String pc_id1 =request.getParameter("pc_id1") ;				// 支付通道编号
		String card_status1 =request.getParameter("card_status1") ;	// 卡支付状态
		String card_code1 =request.getParameter("card_code1") ;		// 卡支付错误码
		String card_date1 =request.getParameter("card_date1") ;		// 卡支付完成时间
		String r1 =request.getParameter("r1") ;                     // 卡支付扩展字段
		String mess="version_id="+version_id+",merchant_id="+merchant_id+",verifystring="+verifystring+",order_date="+order_date;
		mess=mess+",order_id="+order_id+",result="+result+",amount="+amount+",currency="+currency+",pay_sq="+pay_sq;
		mess=mess+",pay_date="+pay_date+",count="+count+",card_num1="+card_num1+",card_pwd1="+card_pwd1+",pm_id1="+pm_id1;
		mess=mess+",pc_id1="+pc_id1+",card_status1="+card_status1+",card_code1="+card_code1+",card_date1="+card_date1+",r1="+r1;
		logger.info("19Pay-Notify-Data:"+mess);		
		String merchant_key = chargeconfigService.getChargeconfig("NP_key");//ConfigUtil.getConfig("charge.properties","NP_key");// 商户密钥
		String ori = "version_id=" + version_id + "&merchant_id=" + merchant_id
		+ "&order_id=" + order_id + "&result=" + result
		+ "&order_date=" + order_date + "&amount=" + amount
		+ "&currency=" + currency + "&pay_sq=" + pay_sq + "&pay_date="
		+ pay_date + "&count=" + count + "&card_num1=" + card_num1
		+ "&card_pwd1=" + card_pwd1 + "&pc_id1=" + pc_id1
		+ "&card_status1=" + card_status1 + "&card_code1=" + card_code1
		+ "&card_date1=" + card_date1 + "&r1=" + r1 + "&merchant_key="
		+ merchant_key;
		String vsMD5 = Md5Encrypt.md5(ori);
		logger.info("支付平台MD5验证摘要源串vs:"+ori+";vsMD5="+vsMD5);
		if(verifystring!=null)
		{
			PrintWriter wr = response.getWriter();
			if(!verifystring.equals(vsMD5))// 验证失败
			{
				logger.error("验证MD5摘要串失败,充值处理失败.回写N.");
				wr.write("N");
			}
			else
			{
				wr.write("Y");
				logger.info("验证MD5摘要串通过.回写Y.");
			}
			if(wr!=null) wr.close();
		}
		if(order_id == null || "".equals(order_id)){  //验证订单号是否为空
			logger.error("订单:"+order_id+"支付平台返回订单号为空,充值处理失败.");
			return ;
		}
		
		String amt = BigDecimal.valueOf(Float.parseFloat(amount)).multiply(new BigDecimal(100)).toString();
		String drawamt = "0";//ChargeUtil.getDrawamt(amt);
		String state = TransactionState.fail.value().toString();
		logger.info("支付结果result="+result);
		if(result!=null && !"".equals(result.trim()))
		{
			if("Y".equals(result))//----------------------------支付结果成功----------------------------//
			{
				logger.info("支付结果成功result=="+result);
				if("00000".equals(card_code1) ||"00001".equals(card_code1) ||"00003".equals(card_code1))//*********返回码充值成功*********//
				{
					logger.info("返回码充值成功card_code1=="+card_code1);
					state = TransactionState.ok.value().toString();
					/*
					 * 因运营商系统偶尔会出现处理错误的情况,失败的订单将修改为成功的,
					 * 这种情况19pay会再次通知商家系统,商家系统应该接收这种失败改成功的订单通知.
					 * 首先判断订单是否曾经已经处理过且订单状态为失败的,如果是则首先修改为处理中.
					 */
					String retmemo = URLEncoder.encode(NineteenpayCode.getMemo(card_code1), "UTF-8");
					if (null == retmemo || "".equals(retmemo) || " ".equals(retmemo)) {
						retmemo = URLEncoder.encode((card_code1 + ":未知"), "UTF-8");
					}
					
					StringBuffer param = new StringBuffer();
					param.append("ttransactionid=")
							.append(order_id)
							.append("&bankorderid=")
							.append(order_id)
							.append("&bankordertime=")
							.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date()))
							.append("&banktrace=")
							.append(pay_sq)
							.append("&retcode=")
							.append(card_code1)
							.append("&retmemo=")
							//.append(URLEncoder.encode("19pay充值成功", "UTF-8"))
							//.append(URLEncoder.encode(NineteenpayCode.getMemo(card_code1), "UTF-8"))
							.append(retmemo)
							.append("&amt=")
							.append(amt)
							.append("&drawamt=")
							.append(drawamt);
					String lotterySuccessUrl = chargeconfigService.getChargeconfig("lotterySuccessUrl");//ConfigUtil.getConfig("charge.properties", "lotterySuccessUrl")
					logger.info("充值成功请求：url="
							+ lotterySuccessUrl + ",param=" + param.toString());
					String res = HttpRequest
							.doPostRequest(lotterySuccessUrl, param.toString());
					logger.info("充值成功处理返回: result=" + res);
					chargeUtil.afterCharge(res);
				}
				else//*********返回码充值失败*********//
				{
					logger.info("返回码充值失败card_code1=="+card_code1);
					state = TransactionState.fail.value().toString();
					
					StringBuffer param = new StringBuffer();
					param.append("ttransactionid=")
							.append(order_id)
							.append("&bankorderid=")
							.append(order_id)
							.append("&bankordertime=")
							.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date()))
							.append("&retcode=")
							.append(card_code1)
							.append("&retmemo=")
							.append(URLEncoder.encode(NineteenpayCode.getMemo(card_code1), "UTF-8"))
							.append("&amt=")
							.append(amt);
					String lotteryFailUrl = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl")
					logger.info("充值失败请求：url=" + lotteryFailUrl + ",param=" + param.toString());
					String res = HttpRequest.doPostRequest(lotteryFailUrl, param.toString());
					logger.info("充值失败处理返回: result=" + res);
					logger.info("订单:"+order_id+",高阳充值失败card_code1="+card_code1);
				}
			}
			else					//----------------------------支付结果失败----------------------------//
			{
				logger.info("支付结果失败result=="+result);
				StringBuffer param = new StringBuffer();
				param.append("ttransactionid=")
						.append(order_id)
						.append("&bankorderid=")
						.append(order_id)
						.append("&bankordertime=")
						.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(new Date()))
						.append("&retcode=")
						.append(card_code1)
						.append("&retmemo=")
						.append(URLEncoder.encode(NineteenpayCode.getMemo(card_code1), "UTF-8"))
						.append("&amt=")
						.append(new BigDecimal(Float.parseFloat(amount))
								.multiply(new BigDecimal(100)).toString());
				String lotteryFailUrl = chargeconfigService.getChargeconfig("lotteryFailUrl");//ConfigUtil.getConfig("charge.properties", "lotteryFailUrl")
				logger.info("充值失败请求：url="
						+ lotteryFailUrl + ",param=" + param.toString());
				String res = HttpRequest
						.doPostRequest(lotteryFailUrl, param.toString());
				logger.info("充值失败处理返回: result=" + res);
				logger.info("订单:"+order_id+",高阳充值失败card_code1="+card_code1);
			}
		}
		
		modifyNineteenpay(order_id, r1, card_code1, NineteenpayCode.getMemo(card_code1), null, state);
		logger.info("-----------------19pay 后台通知 处理完毕-------------------");
	}
	
	public void createNineteenpay(String transactionid, String userno,
			String cardno, String cardpwd, String amt, String totalamt,
			String cardtype) {
		try {
			Nineteenpay nineteenpay = Nineteenpay.createNineteenpay(transactionid, userno, cardno,
					cardpwd, amt, totalamt, cardtype);
			logger.info("createNineteenpay:" + nineteenpay.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("createNineteenpay error:", e);
		}
	}
	
	public void modifyNineteenpay(String transactionid, String balance, String retcode, String retmemo, String memo, String state) {
		try {
			Nineteenpay nineteenpay = Nineteenpay.modifyNineteenpay(transactionid, balance, retcode, retmemo, memo, state);
			logger.info("modifyNineteenpay:" + nineteenpay.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("modifyNineteenpay error:", e);
		}
	}
}
