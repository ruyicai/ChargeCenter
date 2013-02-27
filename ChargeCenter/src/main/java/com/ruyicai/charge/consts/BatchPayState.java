package com.ruyicai.charge.consts;

import java.math.BigDecimal;

public enum BatchPayState {

	unfinished(0, "未完成"), ok(1, "成功"), fail(2, "失败"), processing(3, "处理中"), partsuccess(4, "部分成功");

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

	BatchPayState(int val, String memo) {
		this.state = new BigDecimal(val);
		this.memo = memo;
	}
}
