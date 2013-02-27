package com.ruyicai.charge;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.shenzhoufu.ShenzhoufuChargeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/applicationContext.xml", "classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class MemcacheTest {
	@Autowired
	ChargeconfigService chargeconfigService;
	@Autowired
	ShenzhoufuChargeService shenzhoufuChargeService;

	//@Test
	public void test1() throws Exception {
		String shenzhoufuWeight = chargeconfigService.getChargeconfig("mobilephonecard.chargetype.shenzhoufu.weight");
		System.out.println("shenzhoufuWeight=" + shenzhoufuWeight);
	}
	
	@Test
	public void test2() throws Exception {
		try {
		String jsonString="{\"continuebettype\":\"\",\"bankaccount\":\"0\",\"bankid\":\"szf001\",\"cardpwd\":\"3233333\",\"accesstype\":\"W\",\"userno\":\"00000033\",\"amt\":\"5000\",\"subchannel\":\"00092493\",\"paytype\":\"0803\",\"cardmoney\":\"5000\",\"cardno\":\"111111111111111222\",\"orderid\":\"\",\"channel\":\"1\",\"ladderpresentflag\":\"0\"}";
		shenzhoufuChargeService.directChargeCommon(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
