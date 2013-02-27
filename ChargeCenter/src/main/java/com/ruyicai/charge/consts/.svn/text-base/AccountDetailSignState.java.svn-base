package com.ruyicai.charge.consts;

import java.math.BigDecimal;

public enum AccountDetailSignState {

	// -1：出账，1：进账，-2：解冻，2：冻结
	out(-1), in(1), unfreeze(-2), freeze(2);

	BigDecimal state;

	public int intValue() {

		return state.intValue();
	}

	public BigDecimal value() {
		return state;
	}

	AccountDetailSignState(int val) {
		this.state = new BigDecimal(val);
	}
}
