package com.ruyicai.charge.consts;

import java.math.BigDecimal;

public enum TransactionState {

	unfinished(0, "未完成"), ok(1, "成功"), fail(2, "失败"), processing(3, "处理中");

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

	TransactionState(int val, String memo) {
		this.state = new BigDecimal(val);
		this.memo = memo;
	}
}
