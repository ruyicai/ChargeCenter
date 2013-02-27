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

import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.service.FundService;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.JsonUtil;
import com.ruyicai.charge.util.StringUtil;

public class AccountAction implements ServletRequestAware, ServletResponseAware {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String jsonString;
	private Logger logger = Logger.getLogger(AccountAction.class);
	
	@Autowired
	private FundService fundService;

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
			retMap.put("errorCode", errorCode);

			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：" + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 打印JSON信息
	 */
	private void printJson(String errorCode, String value) {
		try {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("errorCode", errorCode);
			retMap.put("value", value);

			response.setCharacterEncoding("utf-8");
			response.getWriter().write(JsonUtil.toJson(retMap));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("发生异常：", e);
		}
	}
	
	public String deductDrawAmountByCharge() {
		logger.info("减少可提现余额->开始");
		String errorCode = ErrorCode.OK.value;
		logger.info("减少可提现余额->得到参数：jsonString=" + jsonString);		
		
		String userno = null;
		String ttransactionid = null;
				
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			userno = map.containsKey("userno") ? map.get("userno").toString() : "";// 用户编号
			ttransactionid = map.containsKey("ttransactionid") ? map.get("ttransactionid").toString() : "";//交易ID
			
			if (StringUtil.isEmpty(userno)) {
				logger.info("减少可提现余额->获取Json串中userno为空");
				errorCode = ErrorCode.UserMod_UserNoEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}			
			if (StringUtil.isEmpty(ttransactionid)) {
				logger.info("减少可提现余额->获取Json串中ttransactionid为空");
				errorCode = ErrorCode.Ttransaction_IdEmpty.value;	
				this.printErrorJson(errorCode);
				return null;
			}			
		} catch (Exception e) {
			logger.error("减少可提现余额->获取Json串参数异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);
			return null;
		}
				
		try {
			String drawamt = fundService.deductDrawAmount(userno, ttransactionid).toString();			
			this.printJson(errorCode, drawamt);
		} catch (RuyicaiException e) {
			errorCode = e.getErrorCode().value;
			this.printErrorJson(errorCode);		
		} catch (Exception e) {
			logger.error("减少可提现余额->执行过程中出现异常", e);
			errorCode = ErrorCode.ERROR.value;	
			this.printErrorJson(errorCode);			
		}
		
		logger.info("减少可提现余额->结束");
		return null;
	}

}
