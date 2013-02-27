package com.ruyicai.charge.consts;

import java.math.BigDecimal;

public enum TransactionType {

	touzhu(1, false, "投注"),

	yinhangkachongzhi(2, true, "银行卡充值"),

	pingtaikachongzhi(3, true, "平台卡充值"),

	jiesuan(4, false, "结算"),

	tixian(5, false, "提现"),

	duijianghuakuan(6, true, "兑奖划款"),

	fankuan(7, true, "返款"),

	zhuihaotaocan(8, false, "追号套餐"),

	dakehuchongzhi(9, true, "大客户充值"),

	diankachongzhi(10, true, "点卡充值"),

	tiaozhang(11, false, "调账"),

	diyicichongzhizengcaijin(12, true, "第一次充值赠送彩金"),

	jietichongzhizengcaijin(13, true, "阶梯充值赠送彩金"),

	yonghuzhucezengsongcaijin(14, true, "用户注册赠送彩金"),

	hemaijinedongjie(15, false, "合买金额冻结"),

	hemaijinejiedong(16, true, "合买金额解冻"),

	hemaijinefankuan(17, true, "合买金额返款"),

	hemaizhongjiahuakuan(18, true, "合买中奖划款"),

	zhuihaodingzhijinedongjie(20, false, "追号定制金额冻结"),

	zengsongcaijin(23, true, "赠送彩金"),

	yizhifuchexiaokoukuan(24, true, "翼支付撤销扣款"),

	huafeidingzhi(25, false, "话费定制"),
	
	jiangjinzhuangchu(26, false, "奖金转账"),
	
	zhongjintixiankoukuan(27, false, "中金提现扣款");

	private BigDecimal state;

	private String memo;
	
	private boolean isIn;
	
	public int intValue() {
		
		return state.intValue();
	}

	public BigDecimal value() {
		return state;
	}
	
	public static boolean isIn(BigDecimal value) {
		for(TransactionType type : TransactionType.values()) {
			if(type.value().equals(value)) {
				return type.isIn;
			}
		}
		return false;
	}

	public String memo() {
		return memo;
	}

	TransactionType(int val, boolean isIn, String memo) {
		this.state = new BigDecimal(val);
		this.isIn = isIn;
		this.memo = memo;
	}
}
