package com.ruyicai.charge.dna.v2.thirdpart;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import com.ruyicai.charge.dna.v2.common.Strings;


/** 支付通第三方商户接入交易报文
 * @author Administrator
 *
 * 位	数据元名称	类型	说明
    1	MessageType	ans	报文类型
    2	PrimaryAcctNum	ans	用户账号
    3	ProcessCode	ans	处理码
    4	Amount	ans	交易金额，如100表示1元
    5	CurCode	ans	交易币种  156：人民币. 默认
    7	TransDatetime	ans	交易日期和时间[MMDDhhmmss]
    11	SysTraceID	ans	系统跟踪号，与其域一起保持唯一。
    12	TransLocalTime	ans	本地交易时间[HHmmss]
    13	TransLocalDate	ans	本地交易日期[MMDD]
    15	SettleDate	ans	清算日期[MMDD]
    16	SequenceNum	ans	银联流水号
    17	TermSeqNum	ans	终端流水号
    18	Reference	ans	交易参考，原值返回
    20	IdType	ans	证件类型01身份证
    21	IdNum	ans	证件号
    22	PhoneNum	ans	手机号
    39	RspCode	ans	响应码
    40	PrivateData	ans	备注 GB2312
    41	TerminalNo	ans	终端号
    42	MerchantNo	ans	商户号
    43	OrderNo	ans	订单编号
    44	OrderState	ans	订单状态
    45	OrderDesc	ans	订单描述
    46	OrderExpireDate	ans	订单有效期
    47	OrderType	ans	订单类型
    48	TransData	ans	业务交换数据
    49	MerchantName	ans	商户名称
    50	CallBackUrl	Ans	交易结果通知url
    52	Pin		ans	持卡人支付码
    64	Mac	ans	校验码
    65	Sign	ans	签名
 * 
 */
public final class VpcMessage implements Serializable {

    private static final long serialVersionUID = -7784871050544876882L;
    private String MessageType;
    private String PrimaryAcctNum;
    private String ProcessCode;
    private String Amount;
    private String CurCode;
    private String TransDatetime;
    private String SysTraceID;
    private String TransLocalTime;
    private String TransLocalDate;
    private String SettleDate;
    private String SequenceNum;
    private String TermSeqNum;
    private String Reference;
    private String IdType;
    private String IdNum;
    private String PhoneNum;
    private String RspCode;
    private String PrivateData;
    private String TerminalNo;
    private String MerchantNo;
    private String OrderNo;
    private String OrderState;
    private String OrderDesc;
    private String OrderExpireDate;
    private String OrderType;
    private String TransData;
    private String MerchantName;
    private String CallBackUrl;
    private String Pin;
    private String PinType;
    private String Mac;
    private String Sign;

    public String getPinType() {
        return PinType;
    }

    public void setPinType(String PinType) {
        this.PinType = PinType;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getCallBackUrl() {
        return CallBackUrl;
    }

    public void setCallBackUrl(String CallBackUrl) {
        this.CallBackUrl = CallBackUrl;
    }

    public String getCurCode() {
        return CurCode;
    }

    public void setCurCode(String CurCode) {
        this.CurCode = CurCode;
    }

    public String getIdNum() {
        return IdNum;
    }

    public void setIdNum(String IdNum) {
        this.IdNum = IdNum;
    }

    public String getIdType() {
        return IdType;
    }

    public void setIdType(String IdType) {
        this.IdType = IdType;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String Mac) {
        this.Mac = Mac;
    }

    public String getMerchantName() {
        return MerchantName;
    }

    public void setMerchantName(String MerchantName) {
        this.MerchantName = MerchantName;
    }

    public String getMerchantNo() {
        return MerchantNo;
    }

    public void setMerchantNo(String MerchantNo) {
        this.MerchantNo = MerchantNo;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public String getOrderDesc() {
        return OrderDesc;
    }

    public void setOrderDesc(String OrderDesc) {
        this.OrderDesc = OrderDesc;
    }

    public String getOrderExpireDate() {
        return OrderExpireDate;
    }

    public void setOrderExpireDate(String OrderExpireDate) {
        this.OrderExpireDate = OrderExpireDate;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String getOrderState() {
        return OrderState;
    }

    public void setOrderState(String OrderState) {
        this.OrderState = OrderState;
    }

    public String getOrderType() {
        return OrderType;
    }

    public void setOrderType(String OrderType) {
        this.OrderType = OrderType;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String PhoneNum) {
        this.PhoneNum = PhoneNum;
    }

    public String getPin() {
        return Pin;
    }

    public void setPin(String Pin) {
        this.Pin = Pin;
    }

    public String getPrimaryAcctNum() {
        return PrimaryAcctNum;
    }

    public void setPrimaryAcctNum(String PrimaryAcctNum) {
        this.PrimaryAcctNum = PrimaryAcctNum;
    }

    public String getPrivateData() {
        return PrivateData;
    }

    public void setPrivateData(String PrivateData) {
        this.PrivateData = PrivateData;
    }

    public String getProcessCode() {
        return ProcessCode;
    }

    public void setProcessCode(String ProcessCode) {
        this.ProcessCode = ProcessCode;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String Reference) {
        this.Reference = Reference;
    }

    public String getRspCode() {
        return RspCode;
    }

    public void setRspCode(String RspCode) {
        this.RspCode = RspCode;
    }

    public String getSequenceNum() {
        return SequenceNum;
    }

    public void setSequenceNum(String SequenceNum) {
        this.SequenceNum = SequenceNum;
    }

    public String getSettleDate() {
        return SettleDate;
    }

    public void setSettleDate(String SettleDate) {
        this.SettleDate = SettleDate;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String Sign) {
        this.Sign = Sign;
    }

    public String getSysTraceID() {
        return SysTraceID;
    }

    public void setSysTraceID(String SysTraceID) {
        this.SysTraceID = SysTraceID;
    }

    public String getTermSeqNum() {
        return TermSeqNum;
    }

    public void setTermSeqNum(String TermSeqNum) {
        this.TermSeqNum = TermSeqNum;
    }

    public String getTerminalNo() {
        return TerminalNo;
    }

    public void setTerminalNo(String TerminalNo) {
        this.TerminalNo = TerminalNo;
    }

    public String getTransData() {
        return TransData;
    }

    public void setTransData(String TransData) {
        this.TransData = TransData;
    }

    public String getTransDatetime() {
        return TransDatetime;
    }

    public void setTransDatetime(String TransDatetime) {
        this.TransDatetime = TransDatetime;
    }

    public String getTransLocalDate() {
        return TransLocalDate;
    }

    public void setTransLocalDate(String TransLocalDate) {
        this.TransLocalDate = TransLocalDate;
    }

    public String getTransLocalTime() {
        return TransLocalTime;
    }

    public void setTransLocalTime(String TransLocalTime) {
        this.TransLocalTime = TransLocalTime;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public VpcMessage clone() {
        VpcMessage msg = new VpcMessage();
        Strings.clone(this, msg);
        return msg;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        try {
            return TransactionUtil.toXml(this);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(VpcMessage.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
