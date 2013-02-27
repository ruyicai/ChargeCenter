package com.ruyicai.charge.chinapay.tradequery;

public enum PaymentType {
	pt01("01", "红包结算金预收款"),
	pt02("02", "红包结算金"),
	pt03("03", "提醒收款"),
	pt04("04", "自动发货商品"),
	pt1("1", "商品购买"),
	pt2("2", "服务购买"),
	pt3("3", "网络拍卖"),
	pt4("4", "捐赠"),
	pt5("5", "邮费补偿"),
	pt6("6", "奖金"),
	pt7("7", "基金购买"),
	pt8("8", "机票购买"),
	pt9("9", "收AA款"),
	pt10("10", "团购"),
	pt11("11", "电子客票"),
	pt12("12", "彩票"),
	pt13("13", "拍卖"),
	pt14("14", "手机支付类型"),
	pt15("15", "鲜花礼品"),
	pt16("16", "代理商电子客票"),
	pt17("17", "党费"),
	pt18("18", "外汇"),
	pt19("19", "自动直充"),
	pt20("20", "境外收单退款"),
	pt21("21", "即时到账退款"),
	pt22("22", "业务保证金"),
	pt23("23", "购物车支付"),
	pt24("24", "送礼金"),
	pt25("25", "交房租"),
	pt26("26", "motopay类型"),
	pt27("27", "团购担保交易付款");

	private String type;
	private String memo;
	
	public String value() {
		return type;
	}
	
	public String memo() {
		return memo;
	}

	PaymentType(String type, String memo) {
		this.type = type;
		this.memo = memo;
	}
	
	public static String getMemo(String value) {
		PaymentType[] paymentTypes = values();
		for(PaymentType paymentType : paymentTypes) {
			if(paymentType.value().equals(value)) {
				return paymentType.memo();
			}
		}
		return "";
	}
}
