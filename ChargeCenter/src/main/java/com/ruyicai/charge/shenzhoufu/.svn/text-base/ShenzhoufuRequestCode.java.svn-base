package com.ruyicai.charge.shenzhoufu;

public enum ShenzhoufuRequestCode {
	pt01("101", "md5验证失败"),
	pt02("102", "订单号重复"),
	pt03("103", "恶意用户"),
	pt04("104", "序列号，密码简单验证失败"),
	pt1("105", "密码正在处理中"),
	pt2("106", "系统繁忙，暂停提交"),
	pt3("107", "多次支付时卡内余额不足"),
	pt4("109", "des解密失败"),
	pt5("201", "证书验证失败"),
	pt6("501", "插入数据库失败"),
	pt7("502", "插入数据库失败"),
	pt8("200", "请求成功（非订单支付成功）"),
	pt9("902", "商户参数不全"),
	pt10("903", "商户ID不存在"),
	pt11("904", "商户没有激活"),
	pt12("905", "商户没有使用该接口的权限"),
	pt13("906", "商户没有设置密钥（privateKey）"),
	pt14("907", "商户没有设置DES密钥"),
	pt15("908", "该笔订单已经处理完成（状态为确定的：成功或者失败）"),
	pt16("909", "该笔订单不符合重复支付的条件"),
	pt17("910", "服务器返回地址不符合规范"),
	pt18("911", "订单号不符合规范"),
	pt19("912", "非法订单"),
	pt20("913", "该地方卡暂时不支持"),
	pt21("914", "支付金额非法"),
	pt22("915", "卡面额非法"),
	pt23("916", "商户不支持该充值卡的支付"),
	pt24("917", "参数格式不正确"),
	pt25("0", "网络连接失败");

	private String key;
	private String memo;
	
	public String value() {
		return key;
	}
	
	public String memo() {
		return memo;
	}

	ShenzhoufuRequestCode(String key, String memo) {
		this.key = key;
		this.memo = memo;
	}
	
	public static String getMemo(String value) {
		ShenzhoufuRequestCode[] shenzhoufuRequestCodes = values();
		for(ShenzhoufuRequestCode shenzhoufuRequestCode : shenzhoufuRequestCodes) {
			if(shenzhoufuRequestCode.value().equals(value)) {
				return shenzhoufuRequestCode.memo();
			}
		}
		//return "";
		return value;
	}
}
