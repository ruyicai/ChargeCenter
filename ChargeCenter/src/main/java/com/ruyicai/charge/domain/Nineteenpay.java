package com.ruyicai.charge.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.consts.TransactionState;

@RooJson
@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "NINETEENPAY", identifierField = "transactionid", persistenceUnit = "persistenceUnitMysql", transactionManager = "transactionManagerMysql")
public class Nineteenpay {
	@Id
	@Column(name = "TRANSACTIONID")
	private String transactionid;

	@Column(name = "USERNO")
	private String userno;

	@Column(name = "CARDNO")
	private String cardno;

	@Column(name = "CARDPWD")
	private String cardpwd;

	@Column(name = "AMT")
	private String amt;

	@Column(name = "TOTALAMT")
	private String totalamt;

	@Column(name = "balance")
	private String balance;

	@Column(name = "CARDTYPE")
	private String cardtype;

	@Column(name = "CHARGETIME")
	private Date chargetime;

	@Column(name = "NOTIFYTIME")
	private Date notifytime;

	@Column(name = "STATE")
	private String state;

	@Column(name = "RETCODE")
	private String retcode;

	@Column(name = "RETMEMO")
	private String retmemo;

	@Column(name = "MEMO")
	private String memo;

	@Transactional("transactionManagerMysql")
	public static Nineteenpay createNineteenpay(String transactionid,
			String userno, String cardno, String cardpwd, String amt,
			String totalamt, String cardtype) {
		Nineteenpay nineteenpay = new Nineteenpay();
		nineteenpay.setTransactionid(transactionid);
		nineteenpay.setUserno(userno);
		nineteenpay.setCardno(cardno);
		nineteenpay.setCardpwd(cardpwd);
		nineteenpay.setAmt(amt);
		nineteenpay.setTotalamt(totalamt);
		nineteenpay.setBalance("0");
		nineteenpay.setCardtype(cardtype);
		nineteenpay.setChargetime(new Date());
		nineteenpay.setState(TransactionState.processing.value().toString());
		nineteenpay.persist();
		return nineteenpay;
	}

	@Transactional("transactionManagerMysql")
	public static Nineteenpay modifyNineteenpay(String transactionid,
			String balance, String retcode, String retmemo, String memo,
			String state) {
		Nineteenpay nineteenpay = Nineteenpay.findNineteenpay(transactionid);
		nineteenpay.setBalance(balance);
		nineteenpay.setNotifytime(new Date());
		nineteenpay.setRetcode(retcode);
		nineteenpay.setRetmemo(retmemo);
		nineteenpay.setMemo(memo);
		nineteenpay.setState(state);
		nineteenpay.merge();
		return nineteenpay;
	}
}
