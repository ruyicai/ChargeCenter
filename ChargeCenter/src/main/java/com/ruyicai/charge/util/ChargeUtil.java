package com.ruyicai.charge.util;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ChargeUtil {
	private Logger logger = Logger.getLogger(ChargeUtil.class);
	private static BigDecimal rate = new BigDecimal(Long.valueOf(ConfigUtil.getConfig("charge.properties", "charge.drawamt.rate")).longValue()); 
	
	public static String getDrawamt(String amt) {
		BigDecimal ret = BigDecimal.ZERO;
		BigDecimal amtBigDecimal = new BigDecimal(amt);
		ret = amtBigDecimal.multiply(rate).divide(new BigDecimal(100), 0, BigDecimal.ROUND_HALF_UP);
		return ret.toString();
	}
	
	public static BigDecimal getFee(BigDecimal amt, BigDecimal type, String bankid, String bankaccount) {
		BigDecimal feerate = BigDecimal.ZERO;		
		if ("gyj001".equals(bankid)) {
			if ("DXJFK".equals(bankaccount)) {
				feerate = new BigDecimal(400);
			} else if ("CMJFK".equals(bankaccount)) {
				feerate = new BigDecimal(400);
			} else if ("LTJFK".equals(bankaccount)) {
				feerate = new BigDecimal(400);
			} 
		}
		if ("y00003".equals(bankid)) {
			if ("JUNNET".equals(bankaccount)) {
				feerate = new BigDecimal(1800);
			} else if ("SNDACARD".equals(bankaccount)) {
				feerate = new BigDecimal(2000);
			} else if ("SZX".equals(bankaccount)) {
				feerate = new BigDecimal(600);
			} else if ("UNICOM".equals(bankaccount)) {
				feerate = new BigDecimal(800);
			} else if ("ZHENGTU".equals(bankaccount)) {
				feerate = new BigDecimal(2000);
			}
		}
		if ("y00004".equals(bankid)) {
			if ("JUNNET".equals(bankaccount)) {
				feerate = new BigDecimal(1800);
			} else if ("SNDACARD".equals(bankaccount)) {
				feerate = new BigDecimal(1800);				
			} else if ("ZHENGTU".equals(bankaccount)) {
				feerate = new BigDecimal(1800);
			} else if ("SZX".equals(bankaccount)) {
				feerate = new BigDecimal(400);
			} else if ("UNICOM".equals(bankaccount)) {
				feerate = new BigDecimal(400);
			} else if ("TELECOM".equals(bankaccount)) {
				feerate = new BigDecimal(400);
			} 
		}
		if ("szf001".equals(bankid)) {
			if ("YD".equals(bankaccount)) {
				feerate = new BigDecimal(300);
			} else if ("LT".equals(bankaccount)) {
				feerate = new BigDecimal(300);				
			} else if ("DX".equals(bankaccount)) {
				feerate = new BigDecimal(300);
			} 
		}		
		feerate = feerate.divide(new BigDecimal(10000));
		return amt.multiply(feerate);
	}
	
	public void  afterCharge(String result) {
		return;
		
		/**
		logger.info("result=" + result);
		
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		if (!"0".equals(errorCode)) {
			logger.info("充值成功处理出现异常，errorCode=" + errorCode);
			return;
		}
		
		Map<String,Object> mapValue = (Map<String, Object>) mapResult.get("value");
		String userno = mapValue.get("userno").toString();
		String id = mapValue.get("id").toString();
		String amt = mapValue.get("amt").toString();
		String type = mapValue.get("type").toString();
		String ladderpresentflag = mapValue.get("ladderpresentflag").toString();
		String accesstype = mapValue.get("accesstype")== null ? "W" : mapValue.get("accesstype").toString();
		String subChannel2 = mapValue.get("subchannel")== null ? "00092493" : mapValue.get("subchannel").toString();
		String channel2 = mapValue.get("channel")== null ? "1" : mapValue.get("channel").toString();
		
		if (ladderpresentflag==null || !"1".equals(ladderpresentflag)) {
			return;
		}
		
		String url = ConfigUtil.getConfig("lottery.properties", "findByUserno");
		StringBuffer param = new StringBuffer();
		param.append("userno=").append(userno);
		try {
			logger.info("请求参数：url=" + url + " ,param=" + param.toString());
			result = HttpRequest.doPostRequest(url, param.toString());
		} catch (IOException e) {			
			logger.error("根据userno获取用户信息->出现异常!", e);			
			return;
		}
		logger.info("根据userno获取用户信息->result=" + result);
		
		mapResult = JsonUtil.transferJson2Map(result);
		errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
		if (!"0".equals(errorCode)) {
			logger.error("根据userno获取用户信息出现异常，errorCode=" + errorCode);
			return;
		}		
		mapValue = (Map<String, Object>) mapResult.get("value");
		String subChannel = mapValue.get("subChannel")== null ? "00092493" : mapValue.get("subChannel").toString(); 
		String channel = mapValue.get("channel") == null ? "1" : mapValue.get("channel").toString();
		BigDecimal ladderpresentamt = BigDecimal.ZERO;
		
		if ("00092493".equals(subChannel)) {			
			if ("521".equals(channel)) {
				if (new BigDecimal(amt).compareTo(BigDecimal.ONE) >=0) {
					ladderpresentamt = new BigDecimal(amt).multiply(new BigDecimal(5)).divideToIntegralValue(new BigDecimal(100));// 百分比;
				}
			} else {
				if (new BigDecimal(amt).compareTo(new BigDecimal(10000)) >=0) {
					ladderpresentamt = new BigDecimal(amt).multiply(new BigDecimal(5)).divideToIntegralValue(new BigDecimal(100));// 百分比;
				}
			}
		}
		
		if (ladderpresentamt.compareTo(BigDecimal.ZERO) >0) {
			url = ConfigUtil.getConfig("lottery.properties", "directChargeProcess");
			param.delete(0, param.length()); 
			param.append("userno=").append(userno).append("&amt=").append(ladderpresentamt.toString())
			.append("&accesstype=").append(accesstype).append("&subchannel=").append(subChannel2)
			.append("&channel=").append(channel2).append("&draw=0").append("&type=13").append("&fee=0")
			.append("&memo=").append("充值赠送");
			
			try {
				logger.info("请求参数：url=" + url + " ,param=" + param.toString());
				result = HttpRequest.doPostRequest(url, param.toString());
			} catch (IOException e) {			
				logger.error("充值赠彩金->出现异常!", e);			
				return;
			}
			logger.info("充值赠彩金->result=" + result);
		} else {
			if (!TransactionType.yinhangkachongzhi.value().toString().equals(type)) {
				return;
			}
			url = ConfigUtil.getConfig("lottery.properties", "addDrawAmount");
			String drawamt = getDrawamt(amt);
			param.delete(0, param.length()); 
			param.append("userno=").append(userno).append("&ttransactionid=").append(id)
			.append("&drawamt=").append(drawamt);
			
			try {
				logger.info("请求参数：url=" + url + " ,param=" + param.toString());
				result = HttpRequest.doPostRequest(url, param.toString());
			} catch (IOException e) {			
				logger.error("增加用户可提现金额->出现异常!", e);			
				return;
			}
			logger.info("增加用户可提现金额->result=" + result);			
		}*/			
	}
		
}
