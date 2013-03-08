package com.ruyicai.charge;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.charge.alipay.tradequery.AlipayConfig;
import com.ruyicai.charge.alipay.tradequery.AlipayService;
import com.ruyicai.charge.alipay.tradequery.SingleTradeQueryXML;
import com.ruyicai.charge.chinapay.tradequery.ChinapayTradequery;
import com.ruyicai.charge.chinapay.tradequery.PaymentType;
import com.ruyicai.charge.chinapay.tradequery.TradeStatus;
import com.ruyicai.charge.dna.pay.DNATransactionClientService;
import com.ruyicai.charge.service.ZfbService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.JsonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/applicationContext.xml" })
public class ChargeTest {
	@Autowired
	DNATransactionClientService dnaTransactionClientService;
	
	
	@Test
	public void test() throws Exception {
		
	}
	
	//@Test
	public void testAlipayTradeQuery() throws Exception {
		String partner = ConfigUtil.getConfig("charge.properties", "zfbwebpartnerId"); //支付宝合作伙伴id
		String key = ConfigUtil.getConfig("charge.properties", "zfbwebkey"); // 支付宝安全校验码
		String requrl = ConfigUtil.getConfig("charge.properties", "alipay.single.trade.query.url");
		String ttransactionid = "BJ201203020000000000000003423936";
		String tradeno = "";//"2012030267420579";
		
		System.out.println("requrl=" + requrl);
		System.out.println("partner=" + partner);
		System.out.println("key=" + key);
		
		String xml = "";
		try {
			xml = AlipayService.PostXml(partner, ttransactionid, tradeno, AlipayConfig.INPUT_CHARSET, key, AlipayConfig.SIGN_TYPE, requrl);
		} catch (Exception e) {			
			e.printStackTrace();
		}		
		System.out.println("xml=" + xml);
		
		Map<String, String> map = new HashMap<String, String>();
		//map = XMLUtil.xml2Map(xml);
		map = SingleTradeQueryXML.xml2Map(xml);
		if (map.containsKey("trade_status")) {
			map.put("trade_status", TradeStatus.getMemo(map.get("trade_status")));
		}
		if (map.containsKey("payment_type")) {
			map.put("payment_type", PaymentType.getMemo(map.get("payment_type")));
		}		
		String json = JsonUtil.toJson(map);
		System.out.println("json=" + json);
	}
	
	//Test
	public void testChinapayTradeQuery() throws Exception {
		String ttransactionid = "TE201203010000000000000000131042";//"BJ201203010000000000000003422239";	
		String gateid = "";
		String transdate = "20120301";//"20120301";	
	
		
		System.out.println("ttransactionid=" + ttransactionid);
		System.out.println("gateid=" + gateid);
		
		Map<String, String> map = null;
		try {
			map = ChinapayTradequery.singleQuery(gateid, ttransactionid, transdate);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		System.out.println("map=" + map);
	}
	
	
	//@Test
	public void testDnapayTradeQuery() throws Exception {
		String ttransactionid = "BJ201203020000000000000003423950";			
		System.out.println("ttransactionid=" + ttransactionid);
		
		Map<String, String> map = null;
		try {
			//map = new DNATransactionClientService().orderQuery(ttransactionid);
			map = dnaTransactionClientService.orderQuery(ttransactionid);
		} catch (Exception e) {			
			e.printStackTrace();
		}		
		System.out.println("map=" + map);
	}
	
	
	    //@Test
		public void testStr() throws Exception {
			String Amount = "000000050.00";
			String Amount1 = "000000050.12";
			String A = new BigDecimal(Amount).toString();
			String A1 = new BigDecimal(Amount1).toString();	
			System.out.println("Amount=" + Amount);
			System.out.println("A=" + A);
			System.out.println("Amount1=" + Amount1);
			System.out.println("A1=" + A1);
			
			String OrderNo = "12BJ201203020000000000000003423950|";
			String OrderNo1 = OrderNo.substring(2, OrderNo.length()-1);
			System.out.println("OrderNo=" + OrderNo);
			System.out.println("OrderNo1=" + OrderNo1);
		}
		
}
