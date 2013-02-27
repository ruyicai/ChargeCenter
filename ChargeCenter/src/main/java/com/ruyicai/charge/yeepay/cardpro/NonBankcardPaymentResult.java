package com.ruyicai.charge.yeepay.cardpro;

public class NonBankcardPaymentResult {
	private String r0_Cmd; // 业务类型
	private String r1_Code; // 消费请求结果(该结果代表请求是否成功，不代表实际扣款结果)
	private String r6_Order; // 商户订单号
	private String rq_ReturnMsg; // 错误信息
	private String hmac; // 签名数据

	public String getR0_Cmd() {
		return r0_Cmd;
	}

	public void setR0_Cmd(String cmd) {
		r0_Cmd = cmd;
	}

	public String getR1_Code() {
		return r1_Code;
	}

	public void setR1_Code(String code) {
		r1_Code = code;
	}

	public String getR6_Order() {
		return r6_Order;
	}

	public void setR6_Order(String order) {
		r6_Order = order;
	}

	public String getRq_ReturnMsg() {
		return rq_ReturnMsg;
	}

	public void setRq_ReturnMsg(String rq_ReturnMsg) {
		this.rq_ReturnMsg = rq_ReturnMsg;
	}

	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
}
