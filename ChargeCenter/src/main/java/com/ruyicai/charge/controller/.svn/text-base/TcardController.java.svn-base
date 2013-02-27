package com.ruyicai.charge.controller;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.charge.service.CardService;
import com.ruyicai.charge.controller.ResponseData;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;

@RequestMapping("/tcards")
@Controller
public class TcardController {
	private Logger logger = Logger.getLogger(TcardController.class);
	
	@Autowired
	private CardService cardService;
	
	@RequestMapping(method = RequestMethod.POST, value = "/doCardChargeSuccessProcess")
	public @ResponseBody
	ResponseData cardChargeSuccessProcess(
			@RequestParam(value = "cardno") String cardno,
			@RequestParam(value = "state") BigDecimal state,			
			@RequestParam(value = "ttransactionid") String ttransactionid,
			@RequestParam(value = "agencyno") String agencyno,
			@RequestParam(value = "userno") String userno) {
	
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {		
			logger.info("如意彩卡充值成功处理->cardno=" + cardno + "；state=" + state + "；ttransactionid=" + ttransactionid
					+ "；agencyno=" + agencyno + "；userno=" + userno);
			int ret = cardService.cardChargeSuccessProcess(cardno, state, ttransactionid, agencyno, userno);
			rd.setValue(ret);			
		} catch (RuyicaiException e) {
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("直接充值处理出现异常", e);
			result = ErrorCode.ERROR;
		}

		rd.setErrorCode(result.value);
		return rd;
	}
}
