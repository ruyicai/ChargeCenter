package com.ruyicai.charge.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.FileUtil;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.domain.Tcard;
import com.ruyicai.charge.domain.Tcardbat;
import com.ruyicai.charge.service.CardService;
import com.ruyicai.charge.exception.RuyicaiException;

/**
 * 如意彩充值卡充值
 * 适用于手机客户端、web网站、wap网站
 */
public class RuyicaiCardChargeAction  implements ServletRequestAware,
		ServletResponseAware {
	private HttpServletRequest request;
	private HttpServletResponse response;	
	private String jsonString;
	private Logger logger = Logger.getLogger(RuyicaiCardChargeAction.class);
	private final static BigDecimal CHARGE_TYPE_DIRECT_CHARGE = BigDecimal.ONE; //充值类型：可直接充值
	private final static BigDecimal CHARGE_TYPE_REGISTER = new BigDecimal(2);//充值类型：注册充值
		
	@Autowired
	private CardService cardService;
	

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
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
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
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
			logger.error("发生异常："+e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印错误JSON信息
	 */
	private void printErrorJson(String errorCode, String value) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);		
			retMap.put("value", value);
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常："+e.toString());
			e.printStackTrace();
		}
	}		

	private String encodeUrl(String str) {
		String ret = null;
		try {
			ret = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("URLEncoder失败，str:" + str);
			e.printStackTrace();
		}		
		return ret;
	}
	
	/**
	 * 如意彩充值卡充值
	 * 适用于手机客户端、web网站、wap网站
	 * @return
	 */
	public String ruyicaiCardCharge() {		
		logger.info("如意彩充值卡充值->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("如意彩充值卡充值->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();

		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			String userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			String bankid = map.containsKey("bankid") ? map.get("bankid").toString() : "";// 银行ID 支付平台--支付宝:zfb001;如意彩充值卡:ryc001;易宝:y00003。
			String paytype = map.containsKey("paytype") ? map.get("paytype").toString() : "";// 支付方式  :卡别标识--01:银行卡;02:非银行卡;03:自有账户自有卡
			String accesstype = map.containsKey("accesstype") ? map.get("accesstype").toString() : "";// 接入方式 --W:Wap;B:Web;C:手机客户端
			String cardno = map.containsKey("cardno") ? map.get("cardno").toString() : "";//
			String cardpwd = map.containsKey("cardpwd") ? map.get("cardpwd").toString() : "";//
			String channel = map.containsKey("channel") ? map.get("channel").toString() : " ";
			String subchannel = map.containsKey("subchannel") ? map.get("subchannel").toString() : " ";// 用户表大客户号
			String agencyno = map.containsKey("agencyno") ? map.get("agencyno").toString() : " ";// 
			String chargetype = map.containsKey("chargetype") ? map.get("chargetype").toString() : " ";//充值类型： 1：可充值卡 2：注册卡			
			
			if (null == chargetype || "".equals(chargetype.trim())) {
				errorCode = ErrorCode.Charge_JsonStringChargeTypeError.value;
				logger.info("如意彩充值卡充值-->Json串参数有误：chargetype为空，errorCode=" + errorCode + "；卡号：cardno:" + cardno
						+ "；chargetype=" + chargetype);				
				this.printErrorJson(errorCode);
				return null;
				
			}			
			BigDecimal chargetypeBigDecimal = new BigDecimal(Long.valueOf(chargetype).longValue()); 
			if (!CHARGE_TYPE_DIRECT_CHARGE.equals(chargetypeBigDecimal) && !CHARGE_TYPE_REGISTER.equals(chargetypeBigDecimal)) {
				errorCode = ErrorCode.Charge_JsonStringChargeTypeError.value;
				logger.info("如意彩充值卡充值-->Json串参数有误：chargetype参数取值错误，errorCode=" + errorCode + "；卡号：cardno:" + cardno
						+  "；chargetype=" + chargetypeBigDecimal);				
				this.printErrorJson(errorCode);
				return null;
			}			
			if (null==agencyno || "".equals(agencyno)) {
				agencyno = " ";
			}
			
			
			ResourceBundle rbint = ResourceBundle.getBundle("charge");
			String type = rbint.getString("despositByCardType");//平台充值卡交易类型
			String jrtno = rbint.getString("jrtNo");//获得金软通的渠道号
			
			logger.info("如意彩充值卡充值->转换前cardno=" + cardno);
			if (cardno != null && cardno.length() > 0) {
				int length = cardno.trim().length();
				for (int i = 1; i <= 16 - length; i++) {
					cardno = "0" + cardno;
				}
			} else {
				logger.info("如意彩充值卡充值->传入的充值卡号为空 ，error=240001，cardno=" + cardno);
				errorCode = "240001";
				this.printErrorJson(errorCode);
				return null;
			}
			logger.info("如意彩充值卡充值->转换后cardno=" + cardno);
			
			//充值金额，单位为分
			BigDecimal balance = new BigDecimal(0);
			try {
				Tcard tcard = Tcard.findTcard(cardno);
				if (tcard == null) {
					errorCode = "240002";
					logger.info("如意彩充值卡充值--> 数据库中对应的平台充值卡记录不存在，errorCode="
							+ errorCode + "；卡号：cardno:" + cardno);
					this.printErrorJson(errorCode);
					return null;
				}

				BigDecimal cardtype = tcard.getType();
				if (CHARGE_TYPE_REGISTER.equals(cardtype) && !CHARGE_TYPE_REGISTER.equals(chargetypeBigDecimal)) {
					errorCode = ErrorCode.Charge_JsonStringChargeTypeError.value;
					logger.info("如意彩充值卡充值-->Json串参数有误：chargetype参数取值错误，errorCode=" + errorCode + "；卡号：cardno:" + cardno
							+ "；数据库中卡类型：type:" + cardtype + "；而json串卡类型：chargetype:" + chargetypeBigDecimal);				
					this.printErrorJson(errorCode);
					return null;
				} 
				if (!CHARGE_TYPE_REGISTER.equals(cardtype) && CHARGE_TYPE_REGISTER.equals(chargetypeBigDecimal)) {
					errorCode = ErrorCode.Charge_JsonStringChargeTypeError.value;
					logger.info("如意彩充值卡充值-->Json串参数有误：chargetype参数取值错误，errorCode=" + errorCode + "；卡号：cardno:" + cardno
							+ "；数据库中卡类型：type:" + cardtype + "；而json串卡类型：chargetype:" + chargetypeBigDecimal);				
					this.printErrorJson(errorCode);
					return null;
				} 
				
				String password = tcard.getPassword();
				if (!cardpwd.equals(password)) {
					errorCode = "240003";
					logger.info("如意卡充值->数据库中对应的平台充值卡记录密码与用户输入的不符，errorCode="
							+ errorCode + "；数据库中：password:" + password
							+ "；用户输入cardpwd:" + cardpwd + "；卡号：cardno:"
							+ cardno);
					this.printErrorJson(errorCode);
					return null;
				}

				BigDecimal state = tcard.getState();
				if (!(new BigDecimal(2)).equals(state)) {
					errorCode = "240004";
					logger.info("如意卡充值->数据库中对应的平台充值卡记录状态不为激活状态，errorCode="
							+ errorCode + "；数据库中：state:" + state
							+ "；卡号：cardno:" + cardno);
					this.printErrorJson(errorCode);
					return null;
				}
				
				SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
			    String endtime = format.format(new Date());
			    String endtime2 = format.format(tcard.getEndtime());
			    logger.info("如意卡充值->卡本地时间：endtime=" + endtime + "；卡失效时间endtime2=" + endtime2);
			    if (endtime.compareTo(endtime2) >= 0){
			    	errorCode = "240104";
			    	logger.info("如意卡充值->数据库中对应的平台充值卡记录已过期，errorCode=" + errorCode 
			    			+ "；卡本地时间：endtime=" + endtime + "；卡失效时间endtime2=" + endtime2 + "；卡号：cardno:" + cardno);	
			    	this.printErrorJson(errorCode);
					return null;
			    }

				BigDecimal amt = tcard.getAmt();
				logger.info("如意彩充值卡充值->充值金额（元），amt=" + amt);
				if ((new BigDecimal(0)).compareTo(amt) >= 0) {
					errorCode = "240005";
					logger.info("如意卡充值->数据库中对应的平台充值卡金额记录小于0，errorCode="
							+ errorCode + "；数据库中：amt:" + amt + "；卡号：cardno:"
							+ cardno);
					this.printErrorJson(errorCode);
					return null;
				}
				balance = amt.multiply(new BigDecimal(100));
				logger.info("如意彩充值卡充值->充值金额（分），balance=" + balance);
				
			} catch (Exception e) {
				errorCode = "240006";
				logger.error("如意卡充值->校验如意卡信息出现错误，errorCode=" + errorCode
						+ "；卡号：cardno:" + cardno, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			}	   
		   
		   
		   if(bankid==null || "".equals(bankid)) {
			   bankid = jrtno;
		   }			
		   
			StringBuffer param = new StringBuffer();
			param.append("userno=").append(userno).append("&amt=").append(balance)
					.append("&accesstype=").append(accesstype)
					.append("&subchannel=").append(subchannel)
					.append("&channel=").append(channel).append("&draw=0")
					.append("&type=").append(type).append("&fee=0")
					.append("&memo=").append(encodeUrl("如意彩卡充值"))
					.append("&bankaccount=").append(cardno)
					.append("&bankid=").append(bankid)
					.append("&paytype=").append(paytype);
			logger.info("如意彩充值卡充值->充值：url="
					+ ConfigUtil.getConfig("lottery.properties", "lotteryDirectChargeProcess")
					+ " ,param=" + param.toString());
		
			String result = HttpRequest.doPostRequest(
					ConfigUtil.getConfig("lottery.properties", "lotteryDirectChargeProcess"),
					param.toString());
			logger.info("如意彩充值卡充值->返回 result=" + result);
			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";		
			if (!"0".equals(errorCode)) {
				logger.info("如意彩充值卡充值->充值出现错误 errorCode=" + errorCode);
				this.printErrorJson(errorCode);
				return null;
			}			
			String ttransactionid = mapResult.get("value").toString();
			logger.info("如意彩充值卡充值->交易号 ttransactionid=" + ttransactionid);
			
			try {							
				int ret = cardService.cardChargeSuccessProcess(cardno, new BigDecimal(3), ttransactionid, agencyno, userno);				
			} catch (RuyicaiException e) {
				errorCode = e.getErrorCode().value;
				logger.error("如意卡充值->卡充值成功处理出错，errorCode=" + errorCode
						+ "；卡号：cardno:" + cardno, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			} catch (Exception e) {
				errorCode = ErrorCode.ERROR.value;
				logger.error("如意卡充值->卡充值成功处理出错，errorCode=" + errorCode
						+ "；卡号：cardno:" + cardno, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			}		

		} catch (Exception e) {
			logger.info("如意彩充值卡充值->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}
		try {
			retMap.put("error_code", errorCode);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("如意彩充值卡充值->出错", e);
		}
		logger.info("如意彩充值卡充值->结束");
		return null;
	}
	
	/**
	 * 如意彩卡成功处理
	 */
	public String cardChargeSuccessProcess() {
		logger.info("如意彩卡成功处理->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("如意彩卡成功处理->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();

		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			String cardno = map.containsKey("cardno") ? map.get("cardno").toString() : "";//
			String userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号			
			String ttransactionid = map.containsKey("ttransactionid") ? map.get("ttransactionid").toString() : "";// 			
			String agencyno =  map.containsKey("agencyno") ? map.get("agencyno").toString() : " ";// 
			if (null==agencyno || "".equals(agencyno)) {
				agencyno = " ";
			}
			
			try {							
				int ret = cardService.cardChargeSuccessProcess(cardno, new BigDecimal(3), ttransactionid, agencyno, userno);				
			} catch (RuyicaiException e) {
				errorCode = e.getErrorCode().value;
				logger.error("如意彩卡成功处理->卡充值成功处理出错，errorCode=" + errorCode
						+ "；卡号：cardno:" + cardno);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			} catch (Exception e) {
				errorCode = ErrorCode.ERROR.value;
				logger.error("如意彩卡成功处理->卡充值成功处理出错，errorCode=" + errorCode
						+ "；卡号：cardno:" + cardno, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			}
			
		} catch (Exception e) {
				logger.error("如意彩卡成功处理->执行过程中出现异常", e);
				errorCode = ErrorCode.ERROR.value;
		}
		
		try {
			retMap.put("error_code", errorCode);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("如意彩卡成功处理->出错", e);
		}
		
		logger.info("如意彩卡成功处理->结束");
		return null;
	}
	
	public String getBeginno() {
		logger.info("如意彩卡获取beginno->开始");
		String errorCode = ErrorCode.OK.value;		
		Map<String, String> retMap = new HashMap<String, String>();
		Long ret = 0L;

		try {
			ret = Tcardbat.findLastCardNo();
			if (null == ret) {
				ret = 1L;
			} else {
				ret += 1L;
			}
		} catch (RuyicaiException e) {
			errorCode = e.getErrorCode().value;
			logger.error("如意彩卡获取beginno->出错，errorCode=" + errorCode, e);
			e.printStackTrace();
			this.printErrorJson(errorCode);
			return null;
		} catch (Exception e) {
			errorCode = ErrorCode.ERROR.value;
			logger.error("如意彩卡获取beginno->出错，errorCode=" + errorCode, e);
			e.printStackTrace();
			this.printErrorJson(errorCode);
			return null;
		}		

		logger.info("如意彩卡获取beginno->ret=" + ret);
		
		try {
			retMap.put("error_code", errorCode);
			retMap.put("value", String.valueOf(ret));
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("如意彩卡获取beginno->出错", e);
		}
		
		logger.info("如意彩卡获取beginno->结束");
		return null;
	}
	
	/**
	 * 生成卡
	 * @return
	 */
	public String genCard() {
		logger.info("如意彩卡生成卡->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("如意彩卡生成卡->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();

		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			int amt = map.containsKey("amt") ? Integer.parseInt(map.get("amt").toString()) : -1;//面值
			int type = map.containsKey("type") ? Integer.parseInt(map.get("type").toString()) : -1;//种类    1普通卡   8VIP卡 2:注册卡
			int cardfrom = map.containsKey("cardfrom") ? Integer.parseInt(map.get("cardfrom").toString()) : -1;//来源 0招商银行积分 1工商银行积分 2建设银行积分
			String agencyno = map.containsKey("agencyno") ? map.get("agencyno").toString() : "";//渠道编号
			int beginno = map.containsKey("beginno") ? Integer.parseInt(map.get("beginno").toString()) : -1;//开始号码
			int endno = map.containsKey("endno") ? Integer.parseInt(map.get("endno").toString()) : -1;//个数				
			String endtime = map.containsKey("endtime") ? map.get("endtime").toString() : "";//失效日期
			String manager = map.containsKey("manager") ? map.get("manager").toString() : "";//
		    String channel = map.containsKey("channel") ? map.get("channel").toString() : "";//
		    
		    if(-1==amt || -1==type || -1==cardfrom || "".equals(agencyno) || -1==beginno || -1==endno || "".equals(endtime) || "".equals(manager) || "".equals(channel)) {
		    	errorCode = ErrorCode.Charge_JsonStringError.value;
		    	this.printErrorJson(errorCode);
				return null;
		    }
		
			try {							
				int ret = cardService.genCard(amt, type, cardfrom, agencyno, beginno, endno, endtime, manager, channel);				
			} catch (RuyicaiException e) {
				errorCode = e.getErrorCode().value;
				logger.error("如意彩卡生成卡->生成卡出错，errorCode=" + errorCode, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			} catch (Exception e) {
				errorCode = ErrorCode.ERROR.value;
				logger.error("如意彩卡生成卡->生成卡出错，errorCode=" + errorCode, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			}		    
			
		} catch (Exception e) {
			logger.error("如意彩卡生成卡->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}

		try {
			retMap.put("error_code", errorCode);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("如意彩卡生成卡->出错", e);
		}
		
		logger.info("如意彩卡生成卡->结束");
		return null;
	}
	
	public String getCanSells() {
		logger.info("如意彩卡获取可用数量->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("如意彩卡获取可用数量->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();
		Long ret = 0L;
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			int amt = map.containsKey("amt") ? Integer.parseInt(map.get("amt").toString()) : -1;//面值
			int type = map.containsKey("type") ? Integer.parseInt(map.get("type").toString()) : -1;//种类    1普通卡   8VIP卡
			int cardfrom = map.containsKey("cardfrom") ? Integer.parseInt(map.get("cardfrom").toString()) : -1;//来源 0招商银行积分 1工商银行积分 2建设银行积分			
		    String channel = map.containsKey("channel") ? map.get("channel").toString() : "";//
		    
		    if(-1==amt || -1==type || -1==cardfrom || "".equals(channel)) {
		    	errorCode = ErrorCode.Charge_JsonStringError.value;
		    	this.printErrorJson(errorCode);
				return null;
		    }
		
			try {							
				ret = Tcard.findCountByAmtTypeCardfromChannel(amt, type, cardfrom, channel, BigDecimal.ONE);//state=1 未激活的卡
				logger.info("如意彩卡获取可用数量->获取可用数量，ret=" + ret);
			} catch (RuyicaiException e) {
				errorCode = e.getErrorCode().value;
				logger.error("如意彩卡获取可用数量->获取可用数量出错，errorCode=" + errorCode, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			} catch (Exception e) {
				errorCode = ErrorCode.ERROR.value;
				logger.error("如意彩卡获取可用数量->获取可用数量出错，errorCode=" + errorCode, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			}		    
			
		} catch (Exception e) {
			logger.error("如意彩卡获取可用数量->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}

		try {
			retMap.put("error_code", errorCode);
			retMap.put("value",  String.valueOf(ret));
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("如意彩卡获取可用数量->出错", e);
		}
		
		logger.info("如意彩卡获取可用数量->结束");
		return null;
	}
	
	public String sell() {
		logger.info("如意彩卡销售->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("如意彩卡销售->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();
		Long count = 0L;	
		String url = "";
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			int amt = map.containsKey("amt") ? Integer.parseInt(map.get("amt").toString()) : -1;//面值
			int type = map.containsKey("type") ? Integer.parseInt(map.get("type").toString()) : -1;//种类    1普通卡   8VIP卡
			int cardfrom = map.containsKey("cardfrom") ? Integer.parseInt(map.get("cardfrom").toString()) : -1;//来源 0招商银行积分 1工商银行积分 2建设银行积分			
		    String channel = map.containsKey("channel") ? map.get("channel").toString() : "";//
		    int sellamt = map.containsKey("sellamt") ? Integer.parseInt(map.get("sellamt").toString()) : -1;//销售数量
		    String agencyno = map.containsKey("agencyno") ? map.get("agencyno").toString() : "";//? 可有可无
		    
		    if(-1==amt || -1==type || -1==cardfrom || "".equals(channel) || -1==sellamt) {
		    	errorCode = ErrorCode.Charge_JsonStringError.value;
		    	this.printErrorJson(errorCode);
				return null;
		    }
		
			try {							
				count = Tcard.findCountByAmtTypeCardfromChannel(amt, type, cardfrom, channel, BigDecimal.ONE);//state=1 未激活的卡
				logger.info("如意彩卡销售->获取可用数量count=" + count + "；销售数量sellamt=" + sellamt);
				if (new Long(sellamt).compareTo(count) >0) {
					errorCode = ErrorCode.Charge_SellAmtError.value;					
					this.printErrorJson(errorCode);
					return null;
				}
				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
				Date now = new Date();// 系统当前时间
				String dir = format1.format(now);//卡文件目录
				String filename = format2.format(now);// 卡文件名称
				String path = request.getSession().getServletContext().getRealPath("/")
						+ "download/card/" + dir + "/";//文件所在的目录
				String fileName = filename + "card.txt";//文件名
				url = path + fileName;
				File file = FileUtil.create(url);
				if (file == null) {
					errorCode = ErrorCode.Charge_CreateFileError.value;					
					this.printErrorJson(errorCode);
					return null;
				}
				
				int ret = cardService.sell(amt, type, cardfrom, channel, sellamt, agencyno, file);
				
			} catch (RuyicaiException e) {
				errorCode = e.getErrorCode().value;
				logger.error("如意彩卡销售->errorCode=" + errorCode, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			} catch (Exception e) {
				errorCode = ErrorCode.ERROR.value;
				logger.error("如意彩卡销售->errorCode=" + errorCode, e);
				e.printStackTrace();
				this.printErrorJson(errorCode);
				return null;
			}		    
			
		} catch (Exception e) {
			logger.error("如意彩卡销售->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}

		try {
			retMap.put("error_code", errorCode);
			retMap.put("value", url);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("如意彩卡销售->出错", e);
		}
		
		logger.info("如意彩卡销售->结束");
		return null;
	}
	
	public String getFile() {
		logger.info("如意彩卡获取文件->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("如意彩卡获取文件->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();	
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);				
		    String url = map.containsKey("url") ? map.get("url").toString() : "";//
		    
		    if("".equals(url)) {
		    	errorCode = ErrorCode.Charge_JsonStringError.value;
		    	logger.info("如意彩卡获取文件->json串有误，url=" + url);
				return null;
		    }
		
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-Disposition", "attachment;filename=date.txt");
			if (!"".equals(url)) {
				FileInputStream fis = new FileInputStream(url);
				ServletOutputStream sos = response.getOutputStream();
				byte[] b = new byte[1024];
				while (fis.read(b) != -1) {
					sos.write(b);
				}
				int bytelength = 0;
				while ((bytelength = fis.read(b)) != -1) {
					sos.write(b, 0, bytelength);
				}
				fis.close();
				sos.close();
			}					    
			
		} catch (Exception e) {
			logger.error("如意彩卡获取文件->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}
		
		
		logger.info("如意彩卡获取文件->结束");
		return null;
	}
	
	public String getChannel() {		
		logger.info("如意彩充值卡获取channel->开始");
		String errorCode = ErrorCode.OK.value;
		String value = "";
		logger.info("如意彩充值卡获取channel->得到参数：jsonString=" + jsonString);
		Map<String, String> retMap = new HashMap<String, String>();	
		
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);			
			String cardno = map.containsKey("cardno") ? map.get("cardno").toString() : "";//
			String cardpwd = map.containsKey("cardpwd") ? map.get("cardpwd").toString() : "";//			
			
			logger.info("如意彩充值卡获取channel->转换前cardno=" + cardno);
			if (cardno != null && cardno.length() > 0) {
				int length = cardno.trim().length();
				for (int i = 1; i <= 16 - length; i++) {
					cardno = "0" + cardno;
				}
			} else {
				logger.info("如意彩充值卡获取channel->传入的充值卡号为空 ，error=240001，cardno=" + cardno);
				errorCode = "240001";
				this.printErrorJson(errorCode, value);
				return null;
			}
			logger.info("如意彩充值卡获取channel->转换后cardno=" + cardno);
			
			String channel = "";
			String chargetype = "";			
			try {
				Tcard tcard = Tcard.findTcard(cardno);
				if (tcard == null) {
					errorCode = "240002";
					logger.info("如意彩充值卡获取channel--> 数据库中对应的平台充值卡记录不存在，errorCode="
							+ errorCode + "；卡号：cardno:" + cardno);
					this.printErrorJson(errorCode, value);
					return null;
				}

				BigDecimal cardtype = tcard.getType();
				if (CHARGE_TYPE_REGISTER.equals(cardtype)) {
					chargetype = CHARGE_TYPE_REGISTER.toString();//2
				} else {
					chargetype =  CHARGE_TYPE_DIRECT_CHARGE.toString();//1
				}
				
				channel = tcard.getChannel();
				if(null==channel || "".equals(channel)) {
					errorCode = ErrorCode.Charge_TcardChannelNullError.value;
					logger.info("如意彩充值卡获取channel->该卡的channel为空，errorCode="
							+ errorCode + "；数据库中：channel:" + channel + "；卡号：cardno:"
							+ cardno);
					this.printErrorJson(errorCode, value);
					return null;
				}
				
				value = channel + "|" + chargetype;
				logger.info("如意彩充值卡获取channel->value=channel+|+chargetype=" + value + "，errorCode=" + errorCode
						+ "；卡号：cardno:" + cardno);
							
				String password = tcard.getPassword();
				if (!cardpwd.equals(password)) {
					errorCode = "240003";
					logger.info("如意彩充值卡获取channel->数据库中对应的平台充值卡记录密码与用户输入的不符，errorCode="
							+ errorCode + "；数据库中：password:" + password
							+ "；用户输入cardpwd:" + cardpwd + "；卡号：cardno:"
							+ cardno);
					this.printErrorJson(errorCode, value);
					return null;
				}

				BigDecimal state = tcard.getState();
				if (!(new BigDecimal(2)).equals(state)) {
					errorCode = "240004";
					logger.info("如意彩充值卡获取channel->数据库中对应的平台充值卡记录状态不为激活状态，errorCode="
							+ errorCode + "；数据库中：state:" + state
							+ "；卡号：cardno:" + cardno);
					this.printErrorJson(errorCode, value);
					return null;
				}
				
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			    String endtime = format.format(new Date());
			    String endtime2 = format.format(tcard.getEndtime());
			    logger.info("如意彩充值卡获取channel->卡本地时间：endtime=" + endtime + "；卡失效时间endtime2=" + endtime2);
			    if (endtime.compareTo(endtime2) >= 0){
			    	errorCode = "240104";
			    	logger.info("如意彩充值卡获取channel->数据库中对应的平台充值卡记录已过期，errorCode=" + errorCode 
			    			+ "；卡本地时间：endtime=" + endtime + "；卡失效时间endtime2=" + endtime2 + "；卡号：cardno:" + cardno);	
			    	this.printErrorJson(errorCode, value);
					return null;
			    }

				BigDecimal amt = tcard.getAmt();
				logger.info("如意彩充值卡获取channel->充值金额（元），amt=" + amt);
				if ((new BigDecimal(0)).compareTo(amt) >= 0) {
					errorCode = "240005";
					logger.info("如意彩充值卡获取channel->数据库中对应的平台充值卡金额记录小于0，errorCode="
							+ errorCode + "；数据库中：amt:" + amt + "；卡号：cardno:"
							+ cardno);
					this.printErrorJson(errorCode, value);
					return null;
				}				
				
			} catch (Exception e) {
				errorCode = "240006";
				logger.error("如意彩充值卡获取channel->校验如意卡信息出现错误，errorCode=" + errorCode
						+ "；卡号：cardno:" + cardno, e);
				e.printStackTrace();
				this.printErrorJson(errorCode, value);
				return null;
			} 	
			

		} catch (Exception e) {
			logger.error("如意彩充值卡获取channel->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;
		}
		try {
			retMap.put("error_code", errorCode);
			retMap.put("value", value);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			logger.error("如意彩充值卡获取channel->出错", e);
		}
		logger.info("如意彩充值卡获取channel->结束");
		return null;
	}

}
