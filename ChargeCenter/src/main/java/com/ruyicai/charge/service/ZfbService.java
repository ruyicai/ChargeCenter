package com.ruyicai.charge.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;

@Service
public class ZfbService {
	private Logger logger = Logger.getLogger(ZfbService.class);
	
	public final static String WB_SECURITY_INFO = "wbSecurityInfo^";
	public final static String REG_DATE = "regDate";
	public final static String REG_NAME = "regName";
	public final static String DEP_ACCOUNT = "depAccount";
	public final static String EXTEND_PARAM = "extend_param";
	
	@Autowired 
	ChargeconfigService chargeconfigService;
	
	public String getZfbExtendParam(String userno) throws Exception {
		String url = chargeconfigService.getChargeconfig("findByUserno");
		StringBuffer param = new StringBuffer();
		param.append("userno=").append(userno);
		logger.info("支付宝外部商户信息传递->根据userno获取用户信息->url：" + url + "param:" + param.toString());
		String result = HttpRequest.doPostRequest(url, param.toString());
		logger.info("支付宝外部商户信息传递->根据userno获取用户信息->返回结果result=" + result);
		
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		if(!errorCode.equals(ErrorCode.OK.value)){
			logger.info("支付宝外部商户信息传递->根据userno获取用户信息->errorCode=" + errorCode);	
			if (errorCode.equals(ErrorCode.UserMod_UserNotExists.value)) {
				throw new Exception("根据userno获取用户信息失败，userno=" + userno + "用户不存在");
			} else {
				throw new Exception("根据userno获取用户信息失败，errorCode=" + errorCode);
			}						
		}
		
		Map<String, Object> mapValue = (Map<String, Object>) mapResult.get("value");
		String regtime = mapValue.get("regtime").toString();
		Date date = new Date(Long.valueOf(regtime).longValue());
		String regDate = DateUtil.format("yyyy-MM-dd", date);
		logger.info("regDate=" + regDate);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(REG_DATE, regDate);
		map.put(REG_NAME, userno);
		map.put(DEP_ACCOUNT, userno);
		
		String ret = WB_SECURITY_INFO + JsonUtil.toJson(map);
		logger.info("ret=" + ret);
		
		return ret;
	}
}
