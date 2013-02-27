package com.ruyicai.charge.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.charge.shenzhoufu.ShenzhoufuChargeService;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.JsonUtil;

public class ShenzhoufuChargeAction implements ServletRequestAware,
		ServletResponseAware {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(ShenzhoufuChargeAction.class);
	@Autowired
	ShenzhoufuChargeService shenzhoufuChargeService;

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	private void printErrorJson(String errorCode) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("error_code", errorCode);
			
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		}  catch (Exception e) {
			logger.error("发生异常：", e);
			e.printStackTrace();
		}
	}
	
	
	public String directCharge() {
		logger.info("神州付充值->开始");	
		String errorCode = ErrorCode.OK.value;
		logger.info("神州付充值->得到参数：jsonString=" + jsonString);
		errorCode = shenzhoufuChargeService.directChargeCommon(jsonString);
		this.printErrorJson(errorCode);
		logger.info("神州付充值->结束");
		return null;
	}
}
