package com.ruyicai.charge.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLUtil {
	private static Logger logger = Logger.getLogger(XMLUtil.class);
	
	public static Map<String, String> xml2Map(String xml) {
		Map<String, String> map = new HashMap<String, String>();
		
		try {			
			Document document = DocumentHelper.parseText(xml);
			Element rootElement = document.getRootElement();
			for(Iterator it = rootElement.elementIterator(); it.hasNext();){   
				Element element = (Element) it.next();
				map.put(element.getName(), element.getText());
			}
		} catch (Exception e) {
			logger.error("xml2Map error:", e);
		}
		return map;
	}

	public static void main(String[] args) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><upomp application=\"SubmitOrder.Rsp\" version=\"1.0.0\"><merchantId>商户代码（15-24位数字）</merchantId><merchantOrderId>商户订单号</merchantOrderId><merchantOrderTime>商户订单时间(YYYYMMDDHHMMSS)</merchantOrderTime><merchantOrderAmt>商户订单金额（12位整数, 单位为分）</merchantOrderAmt><respCode>响应码(0000表示订单提交成功，其他表示失败)</respCode><respDesc>响应码描述</respDesc></upomp>";
	    logger.info("xml=" + xml);
	    
	    Map<String, String> map = xml2Map(xml);
	    logger.info(map);
	    logger.info("merchantId=" + map.get("merchantId"));
	    logger.info("merchantOrderId=" + map.get("merchantOrderId"));
	    logger.info("merchantOrderTime=" + map.get("merchantOrderTime"));
	    logger.info("merchantOrderAmt=" + map.get("merchantOrderAmt"));
	    logger.info("respCode=" + map.get("respCode"));
	    logger.info("respDesc=" + map.get("respDesc"));
	}
}
