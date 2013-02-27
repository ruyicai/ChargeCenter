package com.ruyicai.charge.alipay.batchpay;

public enum BatchPayCode {
	pt01("ILLEGAL_SIGN", "签名不正确"),
	pt02("ILLEGAL_DYN_MD5_KEY", "动态密钥信息错误"),
	pt03("ILLEGAL_ENCRYPT", "加密不正确"),
	pt04("ILLEGAL_ARGUMENT", "参数不正确"),
	pt1("ILLEGAL_SERVICE", "非法服务名称"),
	pt2("ILLEGAL_USER", "用户ID不正确"),
	pt3("ILLEGAL_PARTNER", "合作伙伴信息不正确"),
	pt4("ILLEGAL_EXTERFACE", "接口配置不正确"),
	pt5("ILLEGAL_PARTNER_EXTERFACE", "合作伙伴接口信息不正确"),
	pt6("ILLEGAL_SECURITY_PROFILE", "未找到匹配的密钥配置"),
	pt7("ILLEGAL_AGENT", "代理ID不正确"),
	pt8("ILLEGAL_SIGN_TYPE", "签名类型不正确"),
	pt9("ILLEGAL_CHARSET", "字符集不合法"),
	pt10("ILLEGAL_CLIENT_IP", "客户端IP地址无权访问服务"),
	pt11("HAS_NO_PRIVILEGE", "未开通此接口权限"),
	pt12("SYSTEM_ERROR", "支付宝系统错误"),
	pt13("SESSION_TIMEOUT", "session超时"),
	pt14("ILLEGAL_DIGEST_TYPE", "摘要类型不正确"),
	pt15("ILLEGAL_DIGEST", "文件摘要不正确"),
	pt16("ILLEGAL_FILE_FORMAT", "文件格式不正确"),
	pt17("ILLEGAL_TARGET_SERVICE", "错误的target_service"),
	pt18("ILLEGAL_ACCESS_SWITCH_SYSTEM", "partner不允许访问该类型的系统"),
	pt19("ILLEGAL_SWITCH_SYSTEM", "切换系统异常"),
	pt20("ILLEGAL_ENCODING", "不支持该编码类型"),
	pt21("EXTERFACE_IS_CLOSED", "接口已关闭"),
	pt22("ILLEGAL_REQUEST_REFERER", "防钓鱼检查不支持该请求来源"),
	pt23("ILLEGAL_ANTI_PHISHING_KEY", "防钓鱼检查非法时间戳参数"),
	pt24("ANTI_PHISHING_KEY_TIMEOUT", "防钓鱼检查时间戳超时"),
	pt25("ILLEGAL_EXTER_INVOKE_IP", "防钓鱼检查非法调用IP"),
	pt27("RECEIVE_USER_NOT_EXIST", "收款用户不存在"),
	pt28("ACCOUN_NAME_NOT_MATCH", "用户姓名和收款名称不匹配"),
	pt29("ILLEGAL_USER_STATUS", "用户状态不正确");

	private String key;
	private String memo;
	
	public String value() {
		return key;
	}
	
	public String memo() {
		return memo;
	}

	BatchPayCode(String key, String memo) {
		this.key = key;
		this.memo = memo;
	}
	
	public static String getMemo(String value) {
		BatchPayCode[] batchPayCodes = values();
		for(BatchPayCode batchPayCode : batchPayCodes) {
			if(batchPayCode.value().equals(value)) {
				return batchPayCode.memo();
			}
		}
		//return "";
		return value;
	}
}
