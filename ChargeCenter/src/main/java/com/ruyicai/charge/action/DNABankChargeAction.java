package com.ruyicai.charge.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.ruyicai.charge.dna.pay.DNATransactionClientService;
import com.ruyicai.charge.dna.thirdpart.PosMessage;
import com.ruyicai.charge.dna.v2.pay.PayWhitelistToDnaParameter;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.StringUtil;

/**
 * DNA银行卡充值
 * 含白名单、灰名单用户充值
 * 扩展参数expand:开户人姓名,开卡人开卡证件号,开户行所在地,开卡证件所在地,接电话手机号,白名单标识
 */
public class DNABankChargeAction  implements ServletRequestAware, ServletResponseAware {
	private static final String DNA_VERSION_V2 = "V2";
	private HttpServletRequest request;
	private HttpServletResponse response;	
	private String jsonString;
	private Logger logger = Logger.getLogger(DNABankChargeAction.class);
	@Autowired 
	ChargeconfigService chargeconfigService;
	@Autowired 
	DNATransactionClientService dnaTransactionClientService;
	
	@Resource(name="newwaydna")
	com.ruyicai.charge.dna.v2.pay.DNATransactionClientService dnaTransactionClientServicev2;
	
	@Value("${dna.version}")
	private String dnaVersion;
	
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("setServletRequest error:", e);
		}
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		this.response = arg0;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private boolean checkParam(String userno, String transactionMoney, String cardNumber, 
			String accesstype, String cardType, String bankId, String expand) {
		boolean ret = true;
		
		if(StringUtil.isEmpty(userno)){			
			logger.info("DNA银行卡充值->传入的客户编号(userno)为空 ");		
			ret = false;
			return ret;
		}
		
		if(StringUtil.isEmpty(transactionMoney)){
			logger.info("DNA银行卡充值->transactionMoney为空");		
			ret = false;
			return ret;
		}
		
		if(StringUtil.isEmpty(cardNumber)){
			logger.info("DNA银行卡充值->cardNumber为空");			
			ret = false;
			return ret;
		}
		
		//获得用户充值接入参数 接 C、B、W
		if(StringUtil.isEmpty(accesstype)){
			logger.info("DNA银行卡充值->传入的接入参数(accesstype)为空 ");
			ret = false;
			return ret;			
		}

        //卡别标识+卡序列标识
		if(StringUtil.isEmpty(cardType)){
			logger.info("DNA银行卡充值->传入的卡别标识+卡序列标识(cardType)为空");		
			ret = false;
			return ret;
		}
		
		//获得支付编号如 支付宝，易宝 ，DNA，财付通等
		if(StringUtil.isEmpty(bankId)){
			logger.info("DNA银行卡充值->传入的支付编号(bankId)为空");	
			ret = false;
			return ret;
		}
		
		// 扩展参数
		if (StringUtil.isEmpty(expand)) {
			logger.info("DNA银行卡充值->传入的扩展参数(expand)为空");
			ret = false;
			return ret;
		}

		return ret;
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
			logger.error("printErrorJson发生异常：", e);	
		}
	}
			
	private Map<String, Object> getDnaByUsernoAndState(String userno, int state) throws Exception {
		String httpPostLotteryUrl = chargeconfigService.getChargeconfig("lotteryFindDNABindingByUsernoAndState");//ConfigUtil.getConfig("lottery.properties", "lotteryFindDNABindingByUsernoAndState");
		logger.info("DNA银行卡充值->根据userno及状态获取DNA绑定信息->url：" + httpPostLotteryUrl + "param：userno=" + userno + "&state=" + state);
		String result = HttpRequest.doPostRequest(httpPostLotteryUrl, "userno=" + userno + "&state=" + state);	
		
		logger.info("DNA银行卡充值->提交根据userno及状态获取DNA绑定信息请求->返回结果(result)=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		if(!"0".equals(errorCode)){
			logger.info("DNA银行卡充值->根据手机号和状态获取DNA绑定信息失败->errorCode=" + errorCode);	
			if (errorCode.equals(ErrorCode.Tdnabind_NotExists.value)) {
				return null;
			} else {
				throw new Exception("根据手机号和状态获取DNA绑定信息失败，错误码为=" + errorCode);
			}						
		}
		
		Map<String, Object> mapValue = (Map<String, Object>) mapResult.get("value");
		mapValue.put("errorCode", errorCode);
		return mapValue;//return mapValue.get("bankcardno").toString();
	}
	
	
	
	//DNA账户绑定表修改数据
	private void modifyTdnabind(String mobileid, String userName, String cardNumber, String accountAddress, 
			String documentNumber, String documentAddress, int state, String userno, String bankName) throws Exception {
		String strNewDnabind = "mobileid=" + mobileid + "&name=" + encodeUrl(userName) + "&bankcardno=" + cardNumber + "&bankaddress=" 
			+ encodeUrl(accountAddress) + "&certid=" + documentNumber + "&certidaddress=" + encodeUrl(documentAddress) + "&state=" 
			+ state + "&userno=" + userno + "&bankname=" + encodeUrl(bankName);
		String httpPostLotteryUrl = chargeconfigService.getChargeconfig("lotteryModifyTdnabind");//ConfigUtil.getConfig("lottery.properties","lotteryModifyTdnabind");
		logger.info("DNA银行卡充值->DNA账户绑定信息修改，url：" + httpPostLotteryUrl + ",param：" + strNewDnabind);
		String result = HttpRequest.doPostRequest(httpPostLotteryUrl, strNewDnabind);
		logger.info("DNA银行卡充值->DNA账户绑定信息修改->result=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode = mapResult.containsKey("errorCode") ? mapResult.get("errorCode").toString() : "";
		if (!"0".equals(errorCode)) {
			throw new Exception("DNA账户绑定信息修改失败，错误码为=" + errorCode);
		}			
	}
	
	private String encodeUrl(String str) {
		String ret = null;
		try {
			ret = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("str=" + str + ";URLEncoder失败:", e);
		}		
		return ret;
	}
	
	/**
	 * DNA银行卡充值
	 * @return
	 */
	public String dnaBankCharge() {
		logger.info("DNA银行卡充值开始");
		logger.info("DNA银行卡充值->得到参数：jsonString=" + jsonString);
		
		String errorCode = ErrorCode.OK.value;		
		Map<String, Object> mapJson = null; //客户端请求JSON串
		String userno = null;//客户编号，（以前传的是手机号（mobileCode），现改为userno）
		String amt = null;//充值金额(transactionMoney)
		String cardNumber = null;//用户充值卡号
		String accesstype = null;// 接入参数
		String cardType = null;//卡别标识.卡序列标识
		String bankId = null;//支付类型
		String channel = "";//
		String subchannel = "";//
		String ladderpresentflag = null;
		String continuebettype = null;
		String orderid = null;
		String expand = null;
		
		try {
			mapJson = JsonUtil.transferJson2Map(jsonString);// 获取请求信息
	    	userno = mapJson.containsKey("userno") ? mapJson.get("userno").toString() : "";
	    	amt = mapJson.containsKey("amt") ? mapJson.get("amt").toString() : "";
	    	cardNumber  = mapJson.containsKey("cardno") ? mapJson.get("cardno").toString() : "";
	    	accesstype = mapJson.containsKey("accesstype") ? mapJson.get("accesstype").toString() : "";
	    	cardType = mapJson.containsKey("cardtype") ? mapJson.get("cardtype").toString() : "";
	    	bankId = mapJson.containsKey("bankid") ? mapJson.get("bankid").toString() : "";
	    	channel = mapJson.containsKey("channel") ? mapJson.get("channel").toString() : "";
	    	subchannel = mapJson.containsKey("subchannel") ? mapJson.get("subchannel").toString() : ""; 	    	
	    	expand = mapJson.containsKey("expand") ? mapJson.get("expand").toString() : ""; 	    	
	    	ladderpresentflag = mapJson.containsKey("ladderpresentflag") ? mapJson.get("ladderpresentflag").toString() : "1";//是否参加充值送彩金活动 0:不参加1：参加
	    	continuebettype = mapJson.containsKey("continuebettype") ? mapJson.get("continuebettype").toString() : "";// 继续投注类型
			orderid = mapJson.containsKey("orderid") ? mapJson.get("orderid").toString() : "";// 订单编号
			
			if(!this.checkParam(userno, amt, cardNumber, accesstype, cardType, bankId, expand)) {
				errorCode = ErrorCode.PARAMTER_ERROR.value;//客户端参数错误 000018	
	    		this.printErrorJson(errorCode);// 	
				return null;
			}
    	} catch(Exception e){
    		logger.error("DNA银行卡充值->获取客户端参数时发生异常:", e);    		
    		errorCode = ErrorCode.ERROR.value;
    		this.printErrorJson(errorCode);    		
			return null;
    	}

		Map<String, String> retMap = new HashMap<String, String>();
		String remark = "";
		
		try {
			// DNA灰名单充值需要的额外参数
			// expand 扩展参数
			String userName = null;// 开卡人姓名
			String documentNumber = null;// 开卡人开卡证件号
			String accountAddress = null;// 开户行所在地
			String documentAddress = null;// 开卡证件所在地
			String accessMobile = null;// 接听DNA语音电话的电话号码
			String isWhite = null;// 白名单标识，默认是true即白名单充值，false为灰名单充值。
			String bankName = "";// 开户行名称
			String ip = "127.0.0.1";// ?
			int key = 0;// 0:白名单充值 1:灰名单充值

			String[] strs = expand.split(",");
			userName = strs[0];
			documentNumber = strs[1];
			accountAddress = strs[2];
			documentAddress = strs[3];
			accessMobile = strs[4];
			isWhite = strs[5];
			if (strs.length == 7) {
				bankName = strs[6];
			}

			logger.info("DNA银行卡充值->扩展参数(expand)=" + expand + "；开卡人姓名=" + userName + "；开卡人开卡证件号=" + documentNumber 
					+ "；开户行所在地=" + accountAddress + "；开卡证件所在地=" + documentAddress + "；接听DNA语音电话的电话号码=" + accessMobile
					+ "；白名单标识(默认是true即白名单充值，false为灰名单充值)=" + isWhite + "；开户行名称=" + bankName);

			// 判断DNA是白名单还是灰名单充值方式
			if ("true".equals(isWhite)) {
				documentAddress = "";
				accountAddress = "";
				documentNumber = "";
				userName = "";
				key = 0;
				logger.info("DNA银行卡充值->白名单充值");
			} else if ("false".equals(isWhite)) {
				key = 1;
				logger.info("DNA银行卡充值->灰名单充值");
			} else {
				logger.info("DNA银行卡充值->白名单标识(默认是true即白名单充值，false为灰名单充值)：" + isWhite);
				errorCode = "000018";// 客户端参数错误
				this.printErrorJson(errorCode);
				return null;
			}

			/*
			 * 根据userno读取DNA银行卡绑定表绑定记录，如果为空则插入一条绑定记录
			 * 否则根据充值方式[白/灰]判断绑定信息是否正确
			 */
			logger.info("DNA银行卡充值->根据userno读取DNA绑定信息开始");
			int state = 1;// ?
			String mobilecode = null;

			Map<String, Object> mapDna = null;
			mapDna = this.getDnaByUsernoAndState(userno, state);

			String bankcardno = null;
			if (mapDna != null && !mapDna.isEmpty()) {
				bankcardno = mapDna.get("bankcardno").toString();
				mobilecode = mapDna.get("mobileid").toString();
			} else {
				mobilecode = accessMobile;
			}
			logger.info("DNA银行卡充值->根据userno读取DNA绑定信息结束, bankcardno=" + bankcardno);

			if (!StringUtil.isEmpty(bankcardno)) {
				logger.info("DNA银行卡充值->用户(userno):" + userno + "存在DNA账户绑定信息，比较客户端的充值参数与数据库绑定记录的是否一致:");
				if (!bankcardno.equals(cardNumber)) {
					logger.error("用户在客户端传入的银行卡号与数据库保存的绑定记录不一致，数据里的bankcardno=" + bankcardno + "；用户从客户端传入的银行卡cardNumber=" + cardNumber);
					errorCode = "000019";
					this.printErrorJson(errorCode);
					return null;
				}

				// orderType00 =
				// userName+"|"+documentNumber+"|"+accountAddress+"|01|"+userName+"|"+(ip.equals("")?"127.0.0.1":ip)+"|"+(documentAddress.equals("")?"身份证地址":documentAddress);
				if (mapDna != null && !mapDna.isEmpty()) {
					userName = mapDna.get("name") == null ? "" : mapDna.get("name").toString();
					documentNumber = mapDna.get("certid") == null ? "" : mapDna.get("certid").toString();
					accountAddress = mapDna.get("bankaddress") == null ? "" : mapDna.get("bankaddress").toString();
					documentAddress = mapDna.get("certidaddress") == null ? "" : mapDna.get("certidaddress").toString();
				}				
			} else {
				    // 修改DNA账户绑定表信息
					state = 0;// ?
					logger.info("DNA银行卡充值->向DNA账户绑定表修改数据:手机号(mobileCode):" + mobilecode + "；开户姓名:" + userName 
							+ "；银行卡号:" + cardNumber + "；开户行地址:" + accountAddress + "；身份证号码:" + documentNumber 
							+ "；身份证所在地:" + documentAddress + "；绑定有效状态:" + 0 + "；" + "绑定时间:（在这儿为空）；userno:" + userno 
							+ "；开户行名称：" + bankName);					
					this.modifyTdnabind(mobilecode, userName, cardNumber, accountAddress, documentNumber, documentAddress, state, userno, bankName);
					logger.info("DNA银行卡充值->DNA账户绑定记录修改成功");				
			}

			logger.info("DNA银行卡充值->提交订单到DNA支付平台");

			Float amount = 0f;
			amount = Float.parseFloat(amt) / 100;// 订单金额（元）
			String transactionId = "";// 交易流水号
			String type = "2";// 交易类型：用户充值

			// 接听DNA语音电话的手机号码是accessMobile而不是mobileCode
			Map map = null;
			if (DNA_VERSION_V2.equals(dnaVersion)){
				map = dnaTransactionClientServicev2.payWhitelistToDna(new PayWhitelistToDnaParameter(accessMobile, cardNumber, amount+ "", userName, documentNumber,
						accountAddress, ip, documentAddress, key, userno, accesstype, cardType, bankId,
						type, amt, channel, subchannel, ladderpresentflag, continuebettype, orderid));
				errorCode = (String) map.get("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					com.ruyicai.charge.dna.v2.thirdpart.PosMessage pm = (com.ruyicai.charge.dna.v2.thirdpart.PosMessage) map.get("pm");
					transactionId = (String) map.get("transactionId");
					errorCode = pm.getRespCode();// DNA返回码
					remark = pm.getRemark(); // DNA充值结果描述信息				
				}
			}else {
				map = dnaTransactionClientService.payWhitelistToDna(accessMobile, cardNumber, amount+ "", userName, documentNumber,
						accountAddress, ip, documentAddress, key, userno, accesstype, cardType, bankId,
						type, amt, channel, subchannel, ladderpresentflag, continuebettype, orderid);
				errorCode = (String) map.get("errorCode");
				if (errorCode.equals(ErrorCode.OK.value)) {
					PosMessage pm = (PosMessage) map.get("pm");
					transactionId = (String) map.get("transactionId");
					errorCode = pm.getRespCode();// DNA返回码
					remark = pm.getRemark(); // DNA充值结果描述信息				
				}
			}
			
			if (errorCode.equals("00A3")) {// 充值受理成功;
				logger.info("DNA银行卡充值->充值受理成功，平台生成交易记录成功");
			} else {
				logger.info("DNA银行卡充值->充值受理失败");
			}
			logger.info("DNA返回：errorCode=" + errorCode + "；原因remark=" + remark + "；充值金额为:transactionMoney=:" + amt + "分；amount=" 
			+ amount + "元；充值方式:DNA，生成交易记录的流水号=" + transactionId);
			
			retMap.put("error_code", errorCode);
			retMap.put("remark", remark);
		} catch (Exception e) {
			logger.error("DNA银行卡充值->处理发生异常:", e);
		}

		try {
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("DNA银行卡充值->响应Json串:error_code=" + errorCode + ",响应客户端时发生异常:", e);
		}
		
		logger.info("DNA银行卡充值结束");		
		return null;
	}
}
