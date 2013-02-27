package com.ruyicai.charge.alipay.tradequery;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SingleTradeQueryXML {
	private static Logger logger = Logger.getLogger(SingleTradeQueryXML.class);
	
	public static Map<String, String> xml2Map(String xml) {
		Map<String, String> map = new HashMap<String, String>();
		
		try {			
			Document document = DocumentHelper.parseText(xml);
			Element rootElement = document.getRootElement();			
			Iterator it = rootElement.elementIterator("response");			
			while (it.hasNext()) {
				Element element = (Element) it.next();				
				Iterator it1 = element.elementIterator("trade");				
				while (it1.hasNext()) {
					Element ele = (Element) it1.next();
					for(Iterator it2 = ele.elementIterator(); it2.hasNext();){   
						Element e = (Element) it2.next();				
						map.put(e.getName(), e.getText());
					}
				}
			}		
		} catch (Exception e) {
			logger.error("xml2Map error:", e);
		}
		return map;
	}
}
