package com.ruyicai.charge.dna.common;

public enum OrderState {
	C01("01", "未支付"),
    C02("02", "已支付"),
    C03("03", "已退款"),
    C04("04", "已过期"),
    C05("05", "已作废"),
    C06("06", "支付中"),
    C07("07", "退款中"),
    C08("08", "已被商户撤销"),
    C09("09", "已被持卡人撤销"),
    C10("10", "调账-支付成功"),
    C11("11", "调账-退款成功"),
    C12("12", "已退货");
    
	public String value;
	public String memo;

	OrderState(String value, String memo) {
		this.value = value;
		this.memo = memo;
	}
	
	public static String getMemo(String value) {
		OrderState[] orderStates = values();
		for(OrderState orderState : orderStates) {
			if(orderState.value.equals(value)) {
				return orderState.memo;
			}
		}
		return "";
	}
}
