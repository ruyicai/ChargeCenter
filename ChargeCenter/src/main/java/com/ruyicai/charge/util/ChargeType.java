package com.ruyicai.charge.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ChargeType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Logger logger = Logger.getLogger(ChargeType.class);
	
	private static List<ChargeType> chargeTypes = new ArrayList<ChargeType>();
	
	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getType() {
		return type;
	}

	public void setType(BigDecimal type) {
		this.type = type;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	private String name;
	private BigDecimal type;
	private BigDecimal weight;
	
	public static List<ChargeType> findAllChargeType(BigDecimal nineteenpayWeight2, BigDecimal shenzhoufuWeight2) {
		//BigDecimal type;
		chargeTypes.clear();
		
		BigDecimal nineteenpayWeight = new BigDecimal(Long.valueOf(ConfigUtil.getConfig("charge.properties", "mobilephonecard.chargetype.nineteenpay.weight")).longValue()); 
		BigDecimal shenzhoufuWeight = new BigDecimal(Long.valueOf(ConfigUtil.getConfig("charge.properties", "mobilephonecard.chargetype.shenzhoufu.weight")).longValue()); 
		if (null != nineteenpayWeight2) {
			nineteenpayWeight = nineteenpayWeight2;
		}
		if (null != shenzhoufuWeight2) {
			shenzhoufuWeight = shenzhoufuWeight2;
		}
		
		ChargeType chargeTypeNineteenpay = new ChargeType();
		chargeTypeNineteenpay.setId("nineteenpay");
		chargeTypeNineteenpay.setName("nineteenpay");
		chargeTypeNineteenpay.setType(BigDecimal.ZERO);
		chargeTypeNineteenpay.setWeight(nineteenpayWeight);
		chargeTypes.add(chargeTypeNineteenpay);
		
		ChargeType chargeTypeShenzhoufu = new ChargeType();
		chargeTypeShenzhoufu.setId("shenzhoufu");
		chargeTypeShenzhoufu.setName("shenzhoufu");
		chargeTypeShenzhoufu.setType(BigDecimal.ZERO);
		chargeTypeShenzhoufu.setWeight(shenzhoufuWeight);
		chargeTypes.add(chargeTypeShenzhoufu);		
		
		return chargeTypes;
	}

}
