package com.ruyicai.charge.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.alipay.client.security.DirectTradeCreateRes;
import com.ruyicai.charge.alipay.client.security.ResponseResult;
import com.ruyicai.charge.alipay.client.security.StringUtil;
import com.ruyicai.charge.alipay.client.security.TongYong;
import com.ruyicai.charge.alipay.client.security.XMapUtil;
import com.ruyicai.charge.consts.TransactionType;
import com.ruyicai.charge.nineteenpay.NineteenPayService;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.shenzhoufu.ShenzhoufuChargeService;
import com.ruyicai.charge.util.AlipayUtil;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.MSBankDecryptAndEnvelop;
import com.ruyicai.charge.util.TransactionMapUtil;
import com.ruyicai.charge.util.Weight;
import com.ruyicai.charge.yeepay.YeePayWebCardService;
import com.ruyicai.charge.yeepay.util.CardTypeManager;
import com.ruyicai.charge.yeepay.util.PaymentForOnlineService;

public class ChargeAction implements ServletRequestAware, ServletResponseAware {
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired
	ShenzhoufuChargeService shenzhoufuChargeService;
	@Autowired
	NineteenPayService nineteenPayService;
	@Autowired
	YeePayWebCardService yeePayWebCardService;
	@Autowired
	CardTypeManager cardTypeManager;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Logger logger = Logger.getLogger(ChargeAction.class);
	private String jsonString;
	private String bankid;
	private String paytype;
	private String accesstype;
	private String amt;
	private String bankaccount;
	private String channel;
	private String subchannel;
	private String userno;

	/**
	 * 民生银行充值
	 * 
	 * @param mobileid
	 * @param bankid
	 * @param paytype
	 * @param accesstype
	 * @param amt
	 * @param bankaccount
	 * @param subchannel
	 * @return
	 */
	public String msBankChargeWeb() {
		logger.info("民生银行充值开始");
		Map<String, String> retMap = new HashMap<String, String>();
		String errorCode = ErrorCode.OK.value;
		logger.info("得到参数：jsonString=" + jsonString);
		Map<String, Object> map = null;

		try {
			map = JsonUtil.transferJson2Map(jsonString);
			String userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 客户编号 userno
			String bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			String paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			String accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			String amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额
			String bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			String channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			String subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			String ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			//String fromcharset = map.containsKey("fromcharset") ? map.get("fromcharset").toString() : "utf-8";//temp
			//String tocharset = map.containsKey("tocharset") ? map.get("tocharset").toString() : "utf-8";//temp
			String continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			String orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
			
			String msBankhttps_Url = chargeconfigService.getChargeconfig("msBankhttps_Url");//ConfigUtil.getConfig("charge.properties", "msBankhttps_Url");
			logger.info("民生银行请求地址 msBankhttps_Url=" + msBankhttps_Url);
			String msBankNotify_url = chargeconfigService.getChargeconfig("msBankNotify_url");//ConfigUtil.getConfig("charge.properties", "msBankNotify_url");
			logger.info("民生银行返回地址  msBankNotify_url=" + msBankNotify_url);
			String keyMiMa = chargeconfigService.getChargeconfig("msBank_KeyMiMa");//ConfigUtil.getConfig("charge.properties","msBank_KeyMiMa");// 商户私钥密码
			logger.info("商户私钥密码  keyMiMa=" + keyMiMa);
			String subjectString = chargeconfigService.getChargeconfig("JinRuanTongSubject");//ConfigUtil.getConfig("charge.properties", "JinRuanTongSubject");
			logger.info("商品名称subjectString=" + subjectString);
			String idString = chargeconfigService.getChargeconfig("msBankCorpID");//ConfigUtil.getConfig("charge.properties", "msBankCorpID");
			logger.info("金软通的在民生银行的商户ID idString=" + idString);

			String httpPostLotteryUrl = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			String type = TransactionType.yinhangkachongzhi.value().toString();// 充值交易类型
			String param = "userno=" + userno + "&type=" + type + "&bankid=" + bankid + "&accesstype=" + accesstype + "&amt=" + amt 
			               + "&paytype=" + paytype + "&bankaccount=" + bankaccount + "&channel=" + channel + "&subchannel=" + subchannel
			               + "&ladderpresentflag=" + ladderpresentflag + "&continuebettype=" + continuebettype  + "&orderid=" + orderid;
			logger.info("httpPostLotteryUrl:" + httpPostLotteryUrl + "；param:" + param);
			String result = HttpRequest.doPostRequest(httpPostLotteryUrl, param);

			logger.info("请求lottery返回result:" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			String errorCode2 = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
			if (!"0".equals(errorCode2)) {
				logger.info("生成交易记录出现错误 errorCode=" + errorCode2);
				errorCode = errorCode2;
			} else {								
				String ttransactionid =  mapResult.get("value").toString();

				logger.info("拆分前的交易 ID=" + ttransactionid);
				String tranId = ttransactionid;
				ttransactionid = ttransactionid.substring(ttransactionid.length() - 20);
				logger.info("拆分后的交易 ID=" + ttransactionid);
				TransactionMapUtil.setTransactionMap(tranId, ttransactionid, "msBank");
				SimpleDateFormat dfDateFormat = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat dfDateFormat2 = new SimpleDateFormat("HHmmss");
				Date date = new Date();
				String riqiString = dfDateFormat.format(date);
				String timeString = dfDateFormat2.format(date);

				// 民生待提交的参数赋值
				String billNo = ttransactionid;// 订单号
				String txAmt = new DecimalFormat("#00.0").format(new BigDecimal(Integer.parseInt(amt)).divide(new BigDecimal(100)));//交易金额 (单位：元)
				String PayerCurr = "01";// 人民币
				String txDate = riqiString;// 交易日期 格式yyyyMMdd
				String txTime = timeString;// 交易时间 hhmmss
				String corpID = idString;// 商户代码 由民生银行统一分配
				String corpName = subjectString;// 商户名称
				String CorpRetType = "0";// 及时发回
				String retUrl = msBankNotify_url;// 处理结果返回

				//logger.info("fromcharset=" + fromcharset + ";tocharset=" + tocharset);
				//corpName = new String(corpName.getBytes(fromcharset), tocharset);
				String content = billNo + "|" + txAmt + "|" + PayerCurr + "|" + txDate + "|" + txTime + "|" + corpID + "|" + corpName
						+ "|" + "|" + "|" + CorpRetType + "|" + retUrl + "|";
				logger.info("加密前的加密串 zhengheString=" + content);
				
				//String tempContent = new String(content.getBytes("UTF-8"), "GBK");
				//logger.info("加密前的加密串 tempContent=" + content);
				
				String msBankMac = MSBankDecryptAndEnvelop.Envelop(content, keyMiMa);// 参数加密
				logger.info("加密后的参数密文 msBankMac=" + msBankMac);

				retMap.put("transation_id", ttransactionid);// 交易号
				retMap.put("requrl", msBankhttps_Url);// 链接地址 
				retMap.put("msBank_Mac", msBankMac);// 密文
			}
		} catch (Exception e) {
			logger.error("执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}

		try {
			retMap.put("error_code", errorCode);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("打印JSON出现异常", e);
		}
		logger.info("民生银行充值结束");
		return null;
	}

	/**
	 * 支付宝语音充值
	 * 
	 * @param mobileid
	 * @param bankid
	 * @param paytype
	 * @param accesstype
	 * @param amt
	 * @param bankaccount
	 * @param subchannel
	 * @param userno
	 * @return
	 */
	public String zfbYuyinCharge() {
		logger.info("支付宝语音充值开始");
		Map<String, String> retMap = new HashMap<String, String>();
		String errorCode = ErrorCode.OK.value;
		logger.info("得到参数：jsonString=" + jsonString);
		Map<String, Object> map = null;

		String ItemUrl = "";// 地址封装
		try {
			map = JsonUtil.transferJson2Map(jsonString);
			String mobileid = map.containsKey("mobileid") ? map.get("mobileid").toString() : "";// 手机号
			String bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			String paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			String accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			String amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额(单位：分)
			String bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			String channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			String subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			String userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			String ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			String continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			String orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
			
			StringBuffer param = new StringBuffer();
			param.append("&bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&subchannel=").append(subchannel)
					.append("&userno=").append(userno).append("&type=2").append("&channel=").append(channel).append("&ladderpresentflag=").append(ladderpresentflag)
					.append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String httpPostLotteryUrl =  chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("支付宝语音充值生成交易记录：url=" + httpPostLotteryUrl + " ,param=" + param.toString());
			String result = HttpRequest.doPostRequest(httpPostLotteryUrl, param.toString());
			
			logger.info("返回 return=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			String errorCode2 = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";	
			
			if (!"0".equals(errorCode2)) {
				logger.info("生成交易记录出现错误 errorCode=" + errorCode2);
				errorCode = errorCode2;
			} else {
				String key = chargeconfigService.getChargeconfig("zfbYuyinKey");//ConfigUtil.getConfig("charge.properties", "zfbYuyinKey");// 金软通密钥
				logger.info("读取到的金软通密钥是 key=" + key);
				String partnerid = chargeconfigService.getChargeconfig("partnerId");//ConfigUtil.getConfig("charge.properties", "zfbYuyinPartnerId");// 商户编号
				String yuyinurl = chargeconfigService.getChargeconfig("zfbYuyinReqUrl");//ConfigUtil.getConfig("charge.properties", "zfbYuyinReqUrl");// 语音充值请求地址
				String notifyUrl = chargeconfigService.getChargeconfig("zfbYuyinNotifyUrl");//ConfigUtil.getConfig("charge.properties", "zfbYuyinNotifyUrl");// 语音充值通知地址
				String returnUrl = map.containsKey("retUrl") ? map.get("retUrl").toString() : chargeconfigService.getChargeconfig("zfbYuyinReturnUrl");
					//ConfigUtil.getConfig("charge.properties", "zfbYuyinReturnUrl");// 语音充值返回地址
				String sell_email = chargeconfigService.getChargeconfig("zfbSellEmail");//ConfigUtil.getConfig("charge.properties", "zfbSellEmail");// 卖家账号
				String goods = chargeconfigService.getChargeconfig("zfbYuyinGoods");//ConfigUtil.getConfig("charge.properties", "zfbYuyinGoods");// 支付宝语音充值商品名称
				String desc = chargeconfigService.getChargeconfig("zfbYuyinDesc");//ConfigUtil.getConfig("charge.properties", "zfbYuyinDesc");

				// 构造请求地址
				String service = "direct_ivr_trade_create"; // 语音支付服务
				String sign_type = "MD5"; // 签名方式
				String input_charset = "UTF-8"; // 编码方式
				DecimalFormat df = new DecimalFormat("#0.00");
				String total_fee = df.format(new BigDecimal(Float.parseFloat(amt)).divide(new BigDecimal(100)));
				String quantity = "1"; // 商品数量默认为1
				String is_ivr_pay = "T"; // T/F ，如果为空则同F处理，不支持语音支付，只会创建普通的即时到帐
				String receive_mobile = mobileid;
				String payment_type = "1"; // 支付宝类型.1代表商品购买（目前填写1即可，不可以修改）

				logger.info("构造请求地址参数 --- ");
				String ttransactionid = mapResult.get("value").toString();
				retMap.put("transation_id", ttransactionid);
				ItemUrl = AlipayUtil.CreateUrl2(yuyinurl, service, partnerid, goods, desc, ttransactionid, total_fee, payment_type, 
						sell_email, notifyUrl, is_ivr_pay, receive_mobile, key, sign_type, returnUrl, quantity, input_charset);
				logger.info("构造请求地址返回 ItemUrl=" + ItemUrl);
			}
			
		} catch (Exception e) {
			logger.error("执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}

		try {			
			retMap.put("requrl", ItemUrl);
			retMap.put("error_code", errorCode);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("打印JSON出错", e);
		}
		logger.info("支付宝语音充值结束");
		return null;
	}

	/**
	 * 支付宝WEB充值
	 * 
	 * @return
	 */
	public String zfbWebCharge() {
		logger.info("支付宝WEB充值开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();
		Map<String, Object> map = null;
		
		try {
			map = JsonUtil.transferJson2Map(jsonString);			
			String bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			String paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			String accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			String amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额 (单位：分)
			String bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			String channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			String subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			String userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			String ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			String continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			String orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
			
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&userno=").append(userno)
					.append("&type=2").append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag).append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
			String httpPostLotteryUrl = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
			logger.info("支付宝WEB充值生成交易记录：url=" + httpPostLotteryUrl + " ,param=" + param.toString());			
			String result = HttpRequest.doPostRequest(httpPostLotteryUrl, param.toString());// 访问lottery生成交易记录
			
			logger.info("返回 return=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			String errorCode2 = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
			String ttransactionid = "";
			if (!"0".equals(errorCode2)) {
				logger.info("生成交易记录出现错误 errorCode=" + errorCode2);
				errorCode = errorCode2;
			} else {
				ttransactionid = mapResult.get("value").toString();
				String zfbwebhttps_url = chargeconfigService.getChargeconfig("zfbwebhttps_url");//ConfigUtil.getConfig("charge.properties", "zfbwebhttps_url"); // 支付接口（不可以修改）
				String service = "create_direct_pay_by_user"; // 快速付款交易服务（不可以修改）
				String sign_type = "MD5"; // 文件加密机制（不可以修改）
				String out_trade_no = ttransactionid; // 商户网站订单
				String input_charset = chargeconfigService.getChargeconfig("zfbwebcharSet");//ConfigUtil.getConfig("charge.properties", "zfbwebcharSet"); // 页面编码（不可以修改）
				String partner = chargeconfigService.getChargeconfig("partnerId");//ConfigUtil.getConfig("charge.properties", "zfbwebpartnerId"); // 支付宝合作伙伴id (账户内提取)
				String key = chargeconfigService.getChargeconfig("zfbwebkey");//ConfigUtil.getConfig("charge.properties", "zfbwebkey"); // 支付宝安全校验码(账户内提取)
				String body = "RUYICAI";
				DecimalFormat df = new DecimalFormat("#0.00");
				String total_fee = df.format(new BigDecimal(Float.parseFloat(amt)).divide(new BigDecimal(100)));// 商品描述
				String payment_type = "1"; // 支付宝类型
				String seller_email = chargeconfigService.getChargeconfig("zfbwebsellemall");//ConfigUtil.getConfig("charge.properties", "zfbwebsellemall"); // 卖家支付宝帐户,例如：gwl25@126.com
				String subject = chargeconfigService.getChargeconfig("JinRuanTongSubject");//ConfigUtil.getConfig("charge.properties", "JinRuanTongSubject"); // 商品名称
				String show_url = chargeconfigService.getChargeconfig("jinRuanTong_show_url");//ConfigUtil.getConfig("charge.properties", "jinRuanTong_show_url"); // 根据集成的网站而定例如：http://wow.alipay.com

				String notify_url = chargeconfigService.getChargeconfig("zfbwebnotifyurl");//ConfigUtil.getConfig("charge.properties", "zfbwebnotifyurl"); // 通知接收URL(本地测试时，服务器返回无法测试)[// http://格式的完整路径]
				String tempReturl = map.containsKey("retUrl") ? map.get("retUrl").toString() : "";
				String tempSessionid = map.containsKey("sessionId") ? map.get("sessionId").toString() : "";
				String return_url = tempReturl + (tempReturl.indexOf("?")>0?"&":"?") + "sessionId=" + tempSessionid; // 支付完成后跳转返回的网址URL[// http://格式的完整路径]

				String paymethod = map.containsKey("paymethod") ? map.get("paymethod").toString() : "directPay";// 充值方式选择:bankPay(网银);cartoon(卡通);// directPay(余额)。
				String defaultbank = map.containsKey("defaultbank") ? map.get("defaultbank").toString() : "CMB";
				String token = map.containsKey("token") ? map.get("token").toString() : null;
				if (!"directPay".equals(paymethod)) {
					token = null;
				}
				logger.info("支付宝.web.充值:paygateway=" + zfbwebhttps_url + ",service=" + service + ",sign_type=" + sign_type
						+ ",out_trade_no=" + out_trade_no + "," + ",input_charset=" + input_charset + ",partner=" + partner 
						+ ",key=" + key + ",body=" + body + ",total_fee=" + total_fee + "," + "payment_type=" + payment_type 
						+ ",seller_email=" + seller_email + ",subject=" + subject + ",show_url=" + show_url + "," + "notify_url=" + notify_url 
						+ ",return_url=" + return_url + ",paymethod=" + paymethod + ",defaultbank=" + defaultbank + ",token=" + token);
				String requrl = AlipayUtil.CreateUrl(zfbwebhttps_url, service, sign_type, out_trade_no, input_charset, partner, key, show_url, 
						body, total_fee, payment_type, seller_email, subject, notify_url, return_url, paymethod, defaultbank, token);				
				logger.info("requrl=" + requrl);// 日志提示
				
				retMap.put("transation_id", ttransactionid);
				retMap.put("requrl", requrl);
				logger.info("支付宝.Web.银行卡充值,交易流水号:" + ttransactionid + ",响应Json串:error_code=000000" + ",transation_id="
						+ ttransactionid + ",requrl=" + requrl);
			}

		} catch (Exception e) {
			logger.error("执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}
		try {
			retMap.put("error_code", errorCode);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("支付宝WEB充值出错", e);
		}
		logger.info("支付宝WEB充值结束");
		return null;
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
	private void printJson(Map<String, String> retMap) {
		try {
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 支付宝Wap充值
	 * @return
	 */
	public String zfbWapCharge() {
		logger.info("支付宝Wap充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("支付宝Wap充值->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();
		Map<String, Object> map = null;
		
		try {
			map = JsonUtil.transferJson2Map(jsonString);
			String bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			String paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			String accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			String amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额 (单位：分)
			String bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			String channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			String subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			String userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			String buyeraccountname = map.containsKey("buyeraccountname") ? map.get("buyeraccountname").toString() : "";// 买家账号
			String callbackurl = map.containsKey("callbackurl") ? map.get("callbackurl").toString() : "";// 回调地址
			String ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			String continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			String orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
			
			String transactionId = "";
			try {
				StringBuffer param = new StringBuffer();
				param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=").append(accesstype)
				.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&userno=").append(userno).append("&type=2")
				.append("&channel=").append(channel).append("&subchannel=").append(subchannel).append("&ladderpresentflag=").append(ladderpresentflag)
				.append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
				String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
				logger.info("支付宝Wap充值->生成交易记录：url=" + url + " ,param=" + param.toString());
				String result = HttpRequest.doPostRequest(url, param.toString());
				logger.info("支付宝Wap充值->生成交易记录->返回 return=" + result);
				Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
				errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
				transactionId = mapResult.containsKey("value")? mapResult.get("value").toString() : "";
			} catch (Exception e) {
				logger.error("支付宝Wap充值->生成交易记录->出现错误");
				e.printStackTrace();
				errorCode = ErrorCode.ERROR.value;
				this.printErrorJson(errorCode);
				return null;
			}
			
			if (!"0".equals(errorCode)) {
				logger.info("支付宝Wap充值->生成交易记录出现错误 errorCode=" + errorCode);
				this.printErrorJson(errorCode);
				return null;
			} else {
				String subject = chargeconfigService.getChargeconfig("JinRuanTongSubject");//ConfigUtil.getConfig("charge.properties", "JinRuanTongSubject");// 商品名称				
				String outTradeNo = transactionId;// 外部交易号

				DecimalFormat df = new DecimalFormat("#0.00");
				String totalFee = df.format(Double.valueOf(amt) / 100);// 商品总价
				logger.info("支付宝Wap充值->amt=" + amt + "分；商品总价totalfee=" + totalFee + "元");

				String sellerAccountName = chargeconfigService.getChargeconfig("sellerAccountName");//ConfigUtil.getConfig("charge.properties", "sellerAccountName");// 卖家帐号(金软通的账号)
				String buyerAccountName = buyeraccountname; // 买家账号
				String notifyUrl = chargeconfigService.getChargeconfig("notifyUrl");//ConfigUtil.getConfig("charge.properties", "notifyUrl");// 接收支付宝发送的通知的url

				Map<String, String> requestParams = new HashMap<String, String>();
				TongYong tongYong = new TongYong();// 支付宝通用方法调用

				try {
					// req_data的内容
					String reqData = "<direct_trade_create_req><subject>" + subject + "</subject><out_trade_no>" + outTradeNo
							+ "</out_trade_no><total_fee>" + totalFee + "</total_fee><seller_account_name>"
							+ sellerAccountName + "</seller_account_name><buyer_account_name>"
							+ buyerAccountName + "</buyer_account_name><notify_url>" + notifyUrl
							+ "</notify_url></direct_trade_create_req>";
					requestParams.put("req_data", reqData);
					requestParams.put("req_id", System.currentTimeMillis() + "");
					requestParams.putAll(tongYong.prepareCommonParams(chargeconfigService.getChargeconfig("partnerId")));
					logger.info("支付宝Wap充值->第一次请求的参数map, prepareTradeRequestParamsMap==" + requestParams);
				} catch (Exception e) {
					logger.error("支付宝Wap充值->获取业务参数出现异常  Exception1:" + e.toString() + ",Exception2:" + e.toString());
					errorCode = ErrorCode.ERROR.value;
					this.printErrorJson(errorCode);
					return null;
				}

				String call_back_url = callbackurl;
				if (null==callbackurl || "".equals(callbackurl)) {
					call_back_url = chargeconfigService.getChargeconfig("call_back_url");//ConfigUtil.getConfig("charge.properties", "call_back_url");
				}
				String sign = tongYong.sign(requestParams);// 密码校验
				logger.info("支付宝Wap充值->第一次校验 sign=" + sign);
				requestParams.put("sign", sign);
				ResponseResult resResult = new ResponseResult();
				String businessResult = "";

				try {
					resResult = tongYong.send(requestParams);
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error("支付宝Wap充值->第一次向支付宝发送创建交易请求:===Exception1:" + e1.toString() + ",Exception2:" + e1.getMessage());
					errorCode = ErrorCode.ERROR.value;
					this.printErrorJson(errorCode);
					return null;
				}
				if (resResult.isSuccess()) {//
					businessResult = resResult.getBusinessResult();
					logger.info("支付宝Wap充值->业务成功结果token:===" + businessResult);
				} else {
					logger.info("支付宝Wap充值->业务失败结果");
					errorCode = ErrorCode.ERROR.value;
					this.printErrorJson(errorCode);
					return null;
				}
				// 读取支付宝xml格式信息返回数据
				DirectTradeCreateRes directTradeCreateRes = null;
				XMapUtil.register(DirectTradeCreateRes.class);

				try {
					directTradeCreateRes = (DirectTradeCreateRes) XMapUtil.load(new ByteArrayInputStream(businessResult.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					logger.error("支付宝Wap充值->读取xml内容出现异常Exception1:" + e.toString() + ",Exception2:" + e.toString());
					errorCode = ErrorCode.ERROR.value;
					this.printErrorJson(errorCode);
					return null;
				} catch (Exception e) {
					logger.error("支付宝Wap充值->读取xml内容出现异常Exception1:" + e.toString() + ",Exception2:" + e.toString());
					errorCode = ErrorCode.ERROR.value;
					this.printErrorJson(errorCode);
					return null;
				}
				// 开放平台返回的内容中取出request_token（对返回的内容要先用私钥解密，再用支付宝的公钥验签名）
				String requestToken = directTradeCreateRes.getRequestToken();

				Map<String, String> authParams = tongYong.prepareAuthParamsMap(request, requestToken, call_back_url, chargeconfigService.getChargeconfig("partnerId"));// 第二次请求参数构造
				String authSign = tongYong.sign(authParams);// 第二次授权并执行参数签名
				authParams.put("sign", authSign);

				logger.info("支付宝Wap充值->第二次授权并执行请求的地址参数 authParams=" + authParams);
				String redirectURL = "";// 构造好第二次请求地址
				try {
					redirectURL = tongYong.getRedirectUrl(authParams, chargeconfigService.getChargeconfig("invokeUrl"));//构造好第二次请求地址
					logger.info("支付宝Wap充值->第二次请求地址 redirectURL=" + redirectURL);
				} catch (Exception e) {
					e.printStackTrace();
					errorCode = ErrorCode.ERROR.value;
					this.printErrorJson(errorCode);
					return null;
				}

				if (StringUtil.isNotBlank(redirectURL)) {
					retMap.put("error_code", ErrorCode.OK.value);
					retMap.put("transation_id", transactionId);
					retMap.put("requrl", redirectURL);
					String retjson = JsonUtil.toJson(retMap);
					logger.info("支付宝Wap充值->返回的jsonObject=" + retjson);
					
					try {
						// 演示使用
						// response.sendRedirect(redirectURL);
						// 真实做法
						response.setCharacterEncoding("UTF-8");
						response.getWriter().write(retjson);
						response.getWriter().flush();
						response.getWriter().close();
					} catch (IOException e) {
						logger.error("支付宝Wap充值->发送Json串时出现异常" + e.toString());
						e.printStackTrace();
						errorCode = ErrorCode.ERROR.value;
						this.printErrorJson(errorCode);
						return null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("支付宝Wap充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
			this.printErrorJson(errorCode);
			return null;
		}
		return null;
	}
	
	public void nineteenpayCharge() {
		logger.info("19pay充值开始");
		logger.info("得到参数：jsonString=" + jsonString);
		String errorCode = ErrorCode.OK.value;
		
		Map<String, Object> maptemp =  JsonUtil.transferJson2Map(jsonString);
		String ladderpresentflag = maptemp.containsKey("ladderpresentflag") ? maptemp.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
		String chargetype = maptemp.containsKey("chargetype") ? maptemp.get("chargetype").toString() : "";//nineteenpay shenzhoufu
		String continuebettype = maptemp.containsKey("continuebettype") ? maptemp.get("continuebettype").toString() : "";// 继续投注类型
		String orderid = maptemp.containsKey("orderid") ? maptemp.get("orderid").toString() : "";// 订单编号
		
//		if (null == chargetype || "".equals("")) {
//			chargetype = ConfigUtil.getConfig("charge.properties", "mobilephonecard.chargetype");
//		}
		
		Weight weight = Weight.getInstance();
		chargetype = weight.getNextChargeType();
		if (StringUtil.isEmpty(chargetype)) {
			chargetype = "nineteenpay";//默认nineteenpay
		}
		
		logger.info("充值方式：chargetype=" + chargetype);
		
		if ("shenzhoufu".equals(chargetype)) {
			String  cardno = maptemp.containsKey("card_no") ? maptemp.get("card_no").toString() : "";
			String  cardpwd = maptemp.containsKey("card_pwd") ? maptemp.get("card_pwd").toString() : "";
			String  cardmoney = maptemp.containsKey("totalAmount") ? maptemp.get("totalAmount").toString() : "";
			
			Map<String, String> jsonMap = new HashMap<String, String>();	
			jsonMap.put("bankid", "szf001");
			jsonMap.put("paytype", "08" + paytype.substring(2, 4));
			if (null != accesstype) {
				jsonMap.put("accesstype", accesstype);	
			} 			
			jsonMap.put("amt", amt);
			if (null != bankaccount) {
				jsonMap.put("bankaccount", bankaccount);
			} 
			if (null != channel) {
				jsonMap.put("channel", channel);
			} 
			if (null != subchannel) {
				jsonMap.put("subchannel", subchannel);
			}		
			jsonMap.put("userno", userno);
			jsonMap.put("cardno", cardno);
			jsonMap.put("cardpwd", cardpwd);
			jsonMap.put("cardmoney", cardmoney);
			jsonMap.put("ladderpresentflag", ladderpresentflag);
			if (null != continuebettype) {
				jsonMap.put("continuebettype", continuebettype);
			} 
			if (null != orderid) {
				jsonMap.put("orderid", orderid);
			}		
			errorCode = shenzhoufuChargeService.directChargeCommon(JsonUtil.toJson(jsonMap));
			
		} else {
			//nineteenpay
			try {
				Map<String, String> map = nineteenPayService.charge(userno, bankid, accesstype,
						paytype, bankaccount, amt, jsonString, channel, subchannel, request, response);
				errorCode = map.containsKey("error_code") ? map.get("error_code").toString() : "";
			} catch (Exception e) {
				logger.error("19apy充值出现异常", e);
				errorCode = ErrorCode.ERROR.value;
			}
		}		
		
		
		Map<String, String> retMap = new HashMap<String, String>();		
		try {
			retMap.put("error_code", errorCode);
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e1) {
			logger.error("打印Json出错", e1);
		}
		
		logger.info("19pay充值结束");
	}

	public void yeePayWebCardCharge() {
		logger.info("yeePayWebCard充值开始");
		logger.info("jsonString: " + jsonString);
		String errorCode = ErrorCode.OK.value;
		
		Map<String, String> retMap = new HashMap<String, String>();
		String bankid2 = chargeconfigService.getChargeconfig("yeepay_bankid");//ConfigUtil.getConfig("charge.properties", "yeepay_bankid");//"y00004";易宝2
		
		try {
			Map<String, String> map = yeePayWebCardService.charge(userno, bankid2, accesstype, paytype, 
					bankaccount, amt, jsonString, channel, subchannel, request, response);
			errorCode = map.containsKey("error_code") ? map.get("error_code").toString() : "";
		} catch (Exception e) {
			logger.error("yeePayWebCard充值出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}
		
		try {
			retMap.put("error_code", errorCode);
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e1) {
			logger.error("打印Json出错", e1);
		}
		
		logger.info("yeePayWebCard充值结束");
	}
	
	public String yeepayWapBankCharge() {
		logger.info("yeepayWapBank充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("yeepayWapBank充值->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();

		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);;
			String bankid = chargeconfigService.getChargeconfig("yeepay_bankid");//ConfigUtil.getConfig("charge.properties", "yeepay_bankid");//"y00004";默认易宝2支付  //map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
			String paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
			String accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
			String amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额 (单位：分)
			String bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
			String channel = map.containsKey("channel") ? map.get("channel").toString() : null;
			String subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
			String userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			String ladderpresentflag = map.containsKey("ladderpresentflag") ? map.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
			String continuebettype = map.containsKey("continuebettype") ? map.get("continuebettype").toString() : "";// 继续投注类型
			String orderid = map.containsKey("orderid") ? map.get("orderid").toString() : "";// 订单编号
			
			String transactionId = "";
			try {
				StringBuffer param = new StringBuffer();
				param.append("bankid=").append(bankid).append("&paytype=").append(paytype).append("&accesstype=")
						.append(accesstype).append("&amt=").append(amt).append("&bankaccount=").append(bankaccount)
						.append("&userno=").append(userno).append("&type=2").append("&channel=").append(channel)
						.append("&subchannel=").append(subchannel).append("&ladderpresentflag=").append(ladderpresentflag)
						.append("&continuebettype=").append(continuebettype).append("&orderid=").append(orderid);
				String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
				logger.info("yeepayWapBank充值->生成交易记录：url=" + url + " ,param=" + param.toString());
				
				String result = HttpRequest.doPostRequest(url, param.toString());
				logger.info("yeepayWapBank充值->生成交易记录->返回 return=" + result);
				Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
				errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
				transactionId = mapResult.containsKey("value")? mapResult.get("value").toString() : "";
			} catch (Exception e) {
				logger.error("yeepayWapBank充值->生成交易记录->出现错误");
				e.printStackTrace();
				errorCode = ErrorCode.ERROR.value;
				this.printErrorJson(errorCode);
				return null;
			}
			
			if (!"0".equals(errorCode)) {
				logger.info("yeepayWapBank充值->生成交易记录出现错误 errorCode=" + errorCode);
				this.printErrorJson(errorCode);
				return null;
			} else {
				String bankCode = CardTypeManager.getBankCode(paytype.substring(2, 4));
				String nodeAuthorizationURL = chargeconfigService.getChargeconfig("wapYeepayCommonReqURL");//ConfigUtil.getConfig("charge.properties", "wapYeepayCommonReqURL");//交易请求地址
				if(bankCode!=null && "CCB-PHONE".equals(bankCode.toUpperCase().trim())){
		    		nodeAuthorizationURL = chargeconfigService.getChargeconfig("CCBWAPYeepayCommonReqURL");//ConfigUtil.getConfig("charge.properties", "CCBWAPYeepayCommonReqURL");//建行交易请求地址
		    	}
				String keyValue = chargeconfigService.getChargeconfig("keyValue");//ConfigUtil.getConfig("charge.properties", "keyValue");//商家密钥
				
				String p0_Cmd = "Buy";//在线支付请求，固定值 ”Buy”
				String p1_MerId = chargeconfigService.getChargeconfig("yeepay_MerId");//ConfigUtil.getConfig("charge.properties", "p1_MerId");// 商户编号	
				String p2_Order = transactionId;//商户订单号
				DecimalFormat df = new DecimalFormat("#0.00");
				String p3_Amt = df.format(Double.valueOf(amt) / 100);//给银行的金额(元)
				String p4_Cur = "CNY";//支付金额
				String p5_Pid = "bankChargeForJRT";//商品名称
				String p6_Pcat = "bankChargeForJRT";//商品种类
				String p7_Pdesc = "bankChargeForJRT";//商品描述
				String p8_Url = chargeconfigService.getChargeconfig("wapMobileBankCommonResponseURL");//ConfigUtil.getConfig("charge.properties", "wapMobileBankCommonResponseURL");//商户接收支付成功数据的地址
				String p9_SAF = "0";//需要填写送货信息 0：不需要  1:需要
				String pa_MP = "bankChargeForJRT";//商户扩展信息
				String pd_FrpId = bankCode.toUpperCase().trim();//支付通道编码: 银行编号必须大写
				String pr_NeedResponse = "1";//是否需要应答机制
				String hmac = "";
				
				// 获得MD5-HMAC签名
				hmac = PaymentForOnlineService.getReqMd5HmacForOnlinePayment(
						p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, 
						p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
				logger.info("yeepayWapBank充值->交易流水号:" + transactionId + ",userno:" + userno + ",商户密钥keyValue:" + keyValue
						+ ",Yeepay请求地址nodeAuthorizationURL:" + nodeAuthorizationURL + ",用户充值的支付信息p0_Cmd:" + p0_Cmd
						+ ",p1_MerId:" + p1_MerId + "," + "p2_Order:" + p2_Order + ",p3_Amt:" + p3_Amt + ",p4_Cur:" + p4_Cur
						+ ",p5_Pid:" + p5_Pid + ",p6_Pcat:" + p6_Pcat + ",p7_Pdesc:" + p7_Pdesc + ",p8_Url:" + p8_Url
						+ ",p9_SAF:" + p9_SAF + ",pa_MP:" + pa_MP + ",pd_FrpId:" + pd_FrpId + ",pr_NeedResponse:"
						+ pr_NeedResponse + ",hmac:" + hmac);
				
			    Map<String,String> rspMap = new HashMap<String,String>();		        
		        rspMap.put("p0_Cmd",p0_Cmd);
		        rspMap.put("p1_MerId", p1_MerId);
		        rspMap.put("p2_Order", p2_Order);
		        rspMap.put("p3_Amt", p3_Amt);
		        rspMap.put("p4_Cur", p4_Cur);
		        rspMap.put("p5_Pid", p5_Pid);
		        rspMap.put("p6_Pcat", p6_Pcat);
		        rspMap.put("p7_Pdesc", p7_Pdesc);
		        rspMap.put("p8_Url", p8_Url);
				rspMap.put("p9_SAF", p9_SAF);
				rspMap.put("pa_MP", pa_MP);
				rspMap.put("pd_FrpId", pd_FrpId);
				rspMap.put("pr_NeedResponse", pr_NeedResponse);
				rspMap.put("hmac", hmac);        
				
				try {
					StringBuffer url = new StringBuffer(nodeAuthorizationURL);
					url.append("?p0_Cmd=" + p0_Cmd + "&p1_MerId=" + p1_MerId
							+ "&p2_Order=" + p2_Order + "&p3_Amt=" + p3_Amt);
					url.append("&p4_Cur=" + p4_Cur + "&p5_Pid=" + p5_Pid
							+ "&p6_Pcat=" + p6_Pcat + "&p7_Pdesc=" + p7_Pdesc);
					url.append("&p8_Url=" + p8_Url + "&p9_SAF=" + p9_SAF
							+ "&pa_MP=" + pa_MP + "&pd_FrpId=" + pd_FrpId);
					url.append("&pr_NeedResponse=" + pr_NeedResponse + "&hmac="
							+ hmac);
					logger.info("yeepayWapBank充值->编码前的地址 url=" + url.toString());
					
					String urString= cardTypeManager.getCmppSubmitReqUrlBank(rspMap,nodeAuthorizationURL);//url编码					
					logger.info("yeepayWapBank充值->编码后的地址 url=" + urString.toString());	
					
					errorCode = ErrorCode.OK.value;
			      
					retMap.put("error_code", errorCode);
					retMap.put("transation_id", transactionId);
					retMap.put("requrl", urString.toString());
					
					String retJson = JsonUtil.toJson(retMap);
					logger.info("yeepayWapBank充值->返回的JSONObject内容 ==" + retJson);
					logger.info("yeepayWapBank充值->交易流水号:" + transactionId + ",响应Json串:error_code=" + errorCode + ",transation_id="
							+ transactionId + ",requrl=" + url);
					
					response.setCharacterEncoding("UTF-8");			
					response.getWriter().write(retJson);
					response.getWriter().flush();
					response.getWriter().close();
				} catch (IOException e) {
					logger.error("yeepayWapBank充值->交易流水号:" + transactionId + "userno:" + userno + ",商户密钥keyValue:" + keyValue
							+ ",Yeepay请求地址nodeAuthorizationURL:" + nodeAuthorizationURL + ",用户充值的支付信息p0_Cmd:"
							+ p0_Cmd + ",p1_MerId:" + p1_MerId + "," + "p2_Order:" + p2_Order + ",p3_Amt:" + p3_Amt
							+ ",p4_Cur:" + p4_Cur + ",p5_Pid:" + p5_Pid + ",p6_Pcat:" + p6_Pcat + ",p7_Pdesc:" + p7_Pdesc
							+ ",p8_Url:" + p8_Url + ",p9_SAF:" + p9_SAF + ",pa_MP:" + pa_MP + ",pd_FrpId:" + pd_FrpId
							+ ",pr_NeedResponse:" + pr_NeedResponse + ",hmac:" + hmac);
					logger.error("yeepayWapBank充值->发生异常:" + e.toString());					
					e.printStackTrace();
					errorCode = ErrorCode.ERROR.value;
					this.printErrorJson(errorCode);
					return null;
				}
			}
		} catch (Exception e) {
			logger.error("yeepayWapBank充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
			this.printErrorJson(errorCode);
			return null;
		}	
		
		logger.info("yeepayWapBank充值->结束");	
		return null;		
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
	public void setServletResponse(HttpServletResponse request) {
		this.response = request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
		Map<String, Object> map =  JsonUtil.transferJson2Map(jsonString);
		bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID
		paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式
		accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式
		amt = map.containsKey("amt") ? map.get("amt").toString() : "";// 交易金额(单位：分)
		bankaccount = map.containsKey("bankaccount") ? map.get("bankaccount").toString() : "0";// 银行账户
		channel = map.containsKey("channel") ? map.get("channel").toString() : null;
		subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : null;// 用户表大客户号
		userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
	}
	
	public String resetweight() {
		logger.info("resetweight start");
		String errorCode = ErrorCode.OK.value;
		try {
			logger.info("得到参数：jsonString=" + jsonString);			
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);;			
			String nineteenpay = map.containsKey("nineteenpay") ? map.get("nineteenpay").toString() : "0";
			String shenzhoufu = map.containsKey("shenzhoufu") ? map.get("shenzhoufu").toString() : "0";
			BigDecimal nineteenpayWeight = new BigDecimal(nineteenpay);
			BigDecimal shenzhoufuWeight = new BigDecimal(shenzhoufu);
			
			logger.info("nineteenpayWeight=" + nineteenpayWeight + ";shenzhoufuWeight=" + shenzhoufuWeight);	
			Weight weight = Weight.getInstance();
			weight.init(nineteenpayWeight, shenzhoufuWeight);
			chargeconfigService.modifyChargeconfig("mobilephonecard.chargetype.nineteenpay.weight", nineteenpayWeight.toString());
			chargeconfigService.modifyChargeconfig("mobilephonecard.chargetype.shenzhoufu.weight", shenzhoufuWeight.toString());
		} catch(Exception e) {
			errorCode = ErrorCode.ERROR.value;	
			e.printStackTrace();
			logger.error("resetweight error, ", e);
		}
		logger.info("resetweight end");
		this.printErrorJson(errorCode);
		return null;
	}
		
	public String getweight() {
		logger.info("getweight start");
		Map<String, String> retMap = new HashMap<String, String>();
		String errorCode = ErrorCode.OK.value;
		try {		
			String nineteenpayWeight = chargeconfigService.getChargeconfig("mobilephonecard.chargetype.nineteenpay.weight");//ConfigUtil.getConfig("charge.properties", "mobilephonecard.chargetype.nineteenpay.weight");
			String shenzhoufuWeight = chargeconfigService.getChargeconfig("mobilephonecard.chargetype.shenzhoufu.weight");//ConfigUtil.getConfig("charge.properties", "mobilephonecard.chargetype.shenzhoufu.weight");
			logger.info("nineteenpayWeight=" + nineteenpayWeight + ";shenzhoufuWeight=" + shenzhoufuWeight);
			retMap.put("nineteenpayWeight", nineteenpayWeight);
			retMap.put("shenzhoufuWeight", shenzhoufuWeight);
		} catch(Exception e) {
			errorCode = ErrorCode.ERROR.value;	
			e.printStackTrace();
			logger.error("getweight error, ", e);
		}
		retMap.put("error_code", errorCode);
		logger.info("getweight end");
		this.printJson(retMap);
		return null;
	}
	
	public String modifychargeconfig() {
		logger.info("modifychargeconfig start");
		String errorCode = ErrorCode.OK.value;
		try {
			logger.info("得到参数：jsonString=" + jsonString);			
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			String id = map.containsKey("id") ? map.get("id").toString().trim() : "";
			String memo = map.containsKey("memo") ? map.get("memo").toString().trim() : "";
			String value = map.containsKey("value") ? map.get("value").toString().trim()  : "";
			
			logger.info("id=" + id + ";memo=" + memo + ";value=" + value);	
			
			if (StringUtil.isEmpty(id) || StringUtil.isEmpty(memo) || StringUtil.isEmpty(value)) {
				errorCode = ErrorCode.PARAMTER_ERROR.value;
			} else {
				chargeconfigService.modifyChargeconfig(id, memo, value);
			}
			
		} catch(Exception e) {
			errorCode = ErrorCode.ERROR.value;	
			e.printStackTrace();
			logger.error("modifychargeconfig error, ", e);
		}
		logger.info("modifychargeconfig end");
		this.printErrorJson(errorCode);
		return null;
	}
}
