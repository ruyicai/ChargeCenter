package com.ruyicai.charge.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Query;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "Tchannelcard", schema = "JRTSCH", identifierField = "id", identifierType = String.class, persistenceUnit = "persistenceUnit", transactionManager = "transactionManager")
public class Tchannelcard {
	@Id
	@Column(name = "ID")
	private String id;
    
	@Column(name = "AGENCYNO")
    private String agencyno;
    
	@Column(name = "CARDCODE")
    private String cardcode;
    
	@Column(name = "CARDAMT")
    private BigDecimal cardamt;
	
	@Column(name = "CARDCOUNT")
    private BigDecimal cardcount;
   
	@Column(name = "GETTIME")
    private Date gettime;
    
	@Column(name = "DECRPTKEY")
    private String decrptkey;
	
	public static BigDecimal findMaxId() {
		Query query = Tchannelcard.entityManager().createNativeQuery("select max(cast(ID as int)) from Tchannelcard");
		return (BigDecimal) query.getSingleResult();
	}
}
