package com.ruyicai.charge.shenzhoufu;

public enum ShenzhoufuNotifyCode {
	pt01("200", "支付成功"),
	pt02("201", "您输入的充值卡密码错误"),
	pt03("202", "您输入的充值卡已被使用"),
	pt04("203", "您输入的充值卡密码非法"),
	pt1("204", "您输入的卡号或密码错误次数过多"),
	pt2("205", "卡号密码正则不匹配或者被禁止"),
	pt3("206", "本卡之前被提交过，本次订单失败，不再继续处理"),
	pt4("207", "暂不支持该充值卡的支付"),
	pt5("208", "您输入的充值卡卡号错误"),
	pt6("209", "您输入的充值卡未激活（生成卡）"),
	pt7("210", "您输入的充值卡已经作废（能查到有该卡，但是没卡的信息）"),
	pt8("211", "您输入的充值卡已过期"),
	pt9("212", "您选择的卡面额不正确"),
	pt10("213", "该卡为特殊本地业务卡，系统不支持"),
	pt11("214", "该卡为增值业务卡，系统不支持"),
	pt12("215", "新生卡"),
	pt13("216", "系统维护"),
	pt14("217", "接口维护"),
	pt15("218", "运营商系统维护"),
	pt16("219", "系统忙，请稍后再试"),
	pt17("220", "未知错误"),
	pt18("221", "本卡之前被处理完毕，本次订单失败，不再继续处理");

	private String key;
	private String memo;
	
	public String value() {
		return key;
	}
	
	public String memo() {
		return memo;
	}

	ShenzhoufuNotifyCode(String key, String memo) {
		this.key = key;
		this.memo = memo;
	}
	
	public static String getMemo(String value) {
		ShenzhoufuNotifyCode[] shenzhoufuNotifyCodes = values();
		for(ShenzhoufuNotifyCode shenzhoufuNotifyCode : shenzhoufuNotifyCodes) {
			if(shenzhoufuNotifyCode.value().equals(value)) {
				return shenzhoufuNotifyCode.memo();
			}
		}
		//return "";
		return value;
	}
}
