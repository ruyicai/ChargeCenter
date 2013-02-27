package com.ruyicai.charge.consts;

import java.math.BigDecimal;

public enum SubaccountType {

	BET(1, "投注账户"), PRIZE(2, "奖金账户");
	
	private BigDecimal value;
	
	private String memo;
	
	private SubaccountType(int value, String memo) {
		this.value = new BigDecimal(value);
		this.memo = memo;
	}
	
	public BigDecimal value() {
		return value;
	}
	
	public String memo() {
		return memo;
	}
}
