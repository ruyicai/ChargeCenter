package com.ruyicai.charge.alipay.client.security;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("notify")
public class DirectTradeCreateResNotify {

	 /**
     * 用户支付方式
     */
    @XNode("payment_type")
    private String paymentType;//

	public String getPaymentType() {
		return paymentType;
	}
	/**
	 * 交易商品名称
	 */
	 @XNode("subject")
	public String subject;//

	public String getSubject() {
		return subject;
	}
	/**
	 * 外部交易号
	 */
	@XNode("out_trade_no")
	public String outTradeNo;//

	public String getOutTradeNo() {
		return outTradeNo;
	}
    /**
     * 支付宝的交易号
     */
	@XNode("trade_no")
	public String tradeNo;//

	public String getTradeNo() {
		return tradeNo;
	}
	 /**
     * 买家账号
     */
	@XNode("buyer_email")
	public String buyerEmail;

	public String getBuyerEmail() {
		return buyerEmail;
	}
	/**
	 * 交易创建时间
	 */
	@XNode("gmt_create")
	public String gmtCreate;

	public String getGmtCreate() {
		return gmtCreate;
	}
	
	/**
	 * 通知类型
	 */
	@XNode("notify_type")
	public String notifyType;

	public String getNotifyType() {
		return notifyType;
	}
	/**
	 * 通知类型
	 */
	@XNode("quantity")
    public String quantity;

	public String getQuantity() {
		return quantity;
	}
	
	/**
	 * 支付宝发送通知时间
	 */
	@XNode("notify_time")
	public String notifyTime;

	public String getNotifyTime() {
		return notifyTime;
	}
	/**
	 * 卖家的支付宝的账号Id
	 */
	@XNode("seller_id")
	public String sellerId;

	public String getSellerId() {
		return sellerId;
	}
	/**
	 * 交易状态
	 */
	@XNode("trade_status")
	public String tradeStatus;

	public String getTradeStatus() {
		return tradeStatus;
	}
	/**
	 * 总价是否被修改
	 */
	@XNode("is_total_fee_adjust")
	public String isTotalFeeAdjust;

	public String getIsTotalFeeAdjust() {
		return isTotalFeeAdjust;
	}
	/**
	 * 交易总价
	 */
	@XNode("total_fee")
	public String totalFee;

	public String getTotalFee() {
		return totalFee;
	}
	
	/**
	 * 付款时间
	 */
	@XNode("gmt_payment")
	public String gmtPayment;

	public String getGmtPayment() {
		return gmtPayment;
	}
	
	/**
	 * 卖家账号
	 */
	@XNode("seller_email")
	public String sellerEmail;

	public String getSellerEmail() {
		return sellerEmail;
	}
	/**
	 * 交易结束时间
	 */
	@XNode("gmt_close")
	public String gmtClose;

	public String getGmtClose() {
		return gmtClose;
	}
	/**
	 * 单个商品价格
	 */
	@XNode("price")
	public String price;

	public String getPrice() {
		return price;
	}
	
//	public static void main(String []args){
//		String businessResult="<?xml version=\"1.0\" encoding=\"utf-8\"?><notify><trade_status>TRADE_FINISHED</trade_status>" +
//		        "<total_fee>20.22</total_fee>"+
//				"</notify>";
//		DirectTradeCreateResNotify directTradeCreateRes = null;
//		XMapUtil.register(DirectTradeCreateResNotify.class);
//
//		try {
//			directTradeCreateRes = (DirectTradeCreateResNotify) XMapUtil.load(new ByteArrayInputStream(
//					businessResult.getBytes("UTF-8")));
//		} catch (UnsupportedEncodingException e) {
//		} catch (Exception e) {
//		}
//		
//		String tradeStatusString=directTradeCreateRes.getTradeStatus();
//		System.out.println(tradeStatusString+" "+directTradeCreateRes.getTotalFee());
//		
//		Map<String, String > map=new HashMap<String, String>();
//		map.put("dd","dd");
//		map.put("dd", "22");
//		System.out.println(map.toString());
//		
//	}
    
}
