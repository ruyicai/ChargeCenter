package com.ruyicai.charge.chinapay.tradequery;

public enum TradeStatus {
	ts1("WAIT_BUYER_PAY", "等待买家付款"),
	ts2("WAIT_SELLER_SEND_GOODS", "买家已付款，等待卖家发货"),
	ts3("WAIT_BUYER_CONFIRM_GOODS", "卖家已发货，等待买家确认"),
	ts4("TRADE_FINISHED", "交易成功结束"),
	ts5("TRADE_CLOSED", "交易中途关闭（已结束，未成功完成）"),
	ts6("WAIT_SYS_CONFIRM_PAY", "支付宝确认买家银行汇款中，暂勿发货"),
	ts7("WAIT_SYS_PAY_SELLER", "买家确认收货，等待支付宝打款给卖家"),
	ts8("TRADE_REFUSE", "立即支付交易拒绝"),
	ts9("TRADE_REFUSE_DEALING", "立即支付交易拒绝中"),
	ts10("TRADE_CANCEL", "立即支付交易取消"),
	ts11("TRADE_PENDING", "等待卖家收款"),
	ts12("TRADE_SUCCESS", "支付成功"),
	ts13("BUYER_PRE_AUTH", "买家已付款（语音支付）"),
	ts14("COD_WAIT_SELLER_SEND_GOODS", "等待卖家发货（货到付款）"),
	ts15("COD_WAIT_BUYER_PAY", "等待买家签收付款（货到付款）"),
	ts16("COD_WAIT_SYS_PAY_SELLER", "签收成功等待系统打款给卖家（货到付款）");

	private String status;
	private String memo;
	
	public String value() {
		return status;
	}
	
	public String memo() {
		return memo;
	}

	TradeStatus(String status, String memo) {
		this.status = status;
		this.memo = memo;
	}
	
	public static String getMemo(String value) {
		TradeStatus[] tradeStatuses = values();
		for(TradeStatus tradeStatus : tradeStatuses) {
			if(tradeStatus.value().equals(value)) {
				return tradeStatus.memo();
			}
		}
		return "";
	}
}
