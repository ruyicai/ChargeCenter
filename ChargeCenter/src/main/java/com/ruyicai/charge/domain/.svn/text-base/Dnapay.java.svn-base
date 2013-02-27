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
@Entity
@RooEntity(versionField = "", table = "DNAPAY", identifierField = "transactionid", persistenceUnit = "persistenceUnitMysql", transactionManager = "transactionManagerMysql")
public class Dnapay {
	@Id
	@Column(name = "TRANSACTIONID")
	private String transactionid;
	
	@Column(name = "USERNO")
	private String userno;
	
	@Column(name = "MOBILEID")
	private String mobileid;
		
	@Column(name = "AMT")
	private String amt;
		
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
	public static Dnapay createDnapay(String transactionid, String userno, String mobileid, String amt) {
		Dnapay dnapay = new Dnapay();
		dnapay.setTransactionid(transactionid);
		dnapay.setUserno(userno);
		dnapay.setMobileid(mobileid);
		dnapay.setAmt(amt);
		dnapay.setChargetime(new Date());
		dnapay.setState(TransactionState.processing.value().toString());
		dnapay.persist();
		return dnapay;
	}
	
	@Transactional("transactionManagerMysql")
	public static Dnapay modifyDnapay(String transactionid, String retcode, String retmemo, String state) {
		Dnapay dnapay = Dnapay.findDnapay(transactionid);
		dnapay.setTransactionid(transactionid);
		dnapay.setRetcode(retcode);
		dnapay.setRetmemo(retmemo);
		dnapay.setNotifytime(new Date());
		dnapay.setState(state);
		dnapay.merge();
		return dnapay;
	}
}
