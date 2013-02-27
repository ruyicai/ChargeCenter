package com.ruyicai.charge.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "Tusercard", schema = "JRTSCH", identifierField = "ttransactionid", identifierType = String.class,  persistenceUnit = "persistenceUnit", transactionManager = "transactionManager")
public class Tusercard {
	@Id
	@Column(name = "TTRANSACTIONID")
    private String ttransactionid;
	
	@Column(name = "AGENCYNO")
    private String agencyno;
	
	@Column(name = "USERNO")
    private String userno;
	
	@Column(name = "TCARDID")
    private String tcardid;
	
	@Column(name = "CARDSTATE")
    private BigDecimal cardstate = new BigDecimal(0);
	
	@Column(name = "GETTIME")
    private Date gettime;
	
}
