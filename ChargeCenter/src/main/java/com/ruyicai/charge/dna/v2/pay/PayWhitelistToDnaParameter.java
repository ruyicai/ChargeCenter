package com.ruyicai.charge.dna.v2.pay;

/**
 * 易联支付参数类
 * userPhoneNumber 持卡人的手机号码
 * userCardNumber 持卡人的卡号
 * amount 充值金额
 * userName 开户人姓名
 * documentNumber 开户证件号码
 * accountAddress 开户银行所在地
 * ip 支付的IP地址
 * documentAddress 持卡人身份证地址
 * payType 支付类型 0 为白名单支付 1 为灰名单支付
 * userno 客户编号
 * accesstype 接入参数
 * cardType 卡别标识.卡序列标识
 * 
 * @author zsl
 *
 */
public class PayWhitelistToDnaParameter {
	private String userPhoneNumber;
	private String userCardNumber;
	private String amount;
	private String userName;
	private String documentNumber;
	private String accountAddress;
	private String ip;
	private String documentAddress;
	private int payType;
	private String userno;
	private String accesstype;
	private String cardType;
	private String bankId;
	private String type;
	private String amt;
	private String channel;
	private String subchannel;
	private String ladderpresentflag;
	private String continuebettype;
	private String orderid;

	public PayWhitelistToDnaParameter(String userPhoneNumber, String userCardNumber, String amount,
			String userName, String documentNumber, String accountAddress, String ip,
			String documentAddress, int payType, String userno, String accesstype, String cardType,
			String bankId, String type, String amt, String channel, String subchannel,
			String ladderpresentflag, String continuebettype, String orderid) {
		this.userPhoneNumber = userPhoneNumber;
		this.userCardNumber = userCardNumber;
		this.amount = amount;
		this.userName = userName;
		this.documentNumber = documentNumber;
		this.accountAddress = accountAddress;
		this.ip = ip;
		this.documentAddress = documentAddress;
		this.payType = payType;
		this.userno = userno;
		this.accesstype = accesstype;
		this.cardType = cardType;
		this.bankId = bankId;
		this.type = type;
		this.amt = amt;
		this.channel = channel;
		this.subchannel = subchannel;
		this.ladderpresentflag = ladderpresentflag;
		this.continuebettype = continuebettype;
		this.orderid = orderid;
	}

	public String getUserPhoneNumber() {
		return userPhoneNumber;
	}

	public void setUserPhoneNumber(String userPhoneNumber) {
		this.userPhoneNumber = userPhoneNumber;
	}

	public String getUserCardNumber() {
		return userCardNumber;
	}

	public void setUserCardNumber(String userCardNumber) {
		this.userCardNumber = userCardNumber;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getAccountAddress() {
		return accountAddress;
	}

	public void setAccountAddress(String accountAddress) {
		this.accountAddress = accountAddress;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDocumentAddress() {
		return documentAddress;
	}

	public void setDocumentAddress(String documentAddress) {
		this.documentAddress = documentAddress;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public String getUserno() {
		return userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getAccesstype() {
		return accesstype;
	}

	public void setAccesstype(String accesstype) {
		this.accesstype = accesstype;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSubchannel() {
		return subchannel;
	}

	public void setSubchannel(String subchannel) {
		this.subchannel = subchannel;
	}

	public String getLadderpresentflag() {
		return ladderpresentflag;
	}

	public void setLadderpresentflag(String ladderpresentflag) {
		this.ladderpresentflag = ladderpresentflag;
	}

	public String getContinuebettype() {
		return continuebettype;
	}

	public void setContinuebettype(String continuebettype) {
		this.continuebettype = continuebettype;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
}