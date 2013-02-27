package com.ruyicai.charge.consts;

import java.math.BigDecimal;

public enum CashDetailState {

	Tixian(1, "待审核"),
	Shenghezhong(103,"已审核"),
	Bohui(104, "驳回"),
	Chenggong(105, "成功"),
	Quxiao(106, "取消"),
	TiaoJiao(107, "提交");

	BigDecimal state;

	String memo;

	public int intValue() {
		
		return state.intValue();
	}
	
	public BigDecimal value() {
		return state;
	}

	public String memo() {
		return memo;
	}

	CashDetailState(int val, String memo) {
		this.state = new BigDecimal(val);
		this.memo = memo;
	}
}
