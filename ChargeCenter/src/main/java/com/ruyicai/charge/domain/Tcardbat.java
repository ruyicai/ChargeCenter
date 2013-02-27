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
@RooEntity(versionField = "", table = "Tcardbat", schema = "JRTSCH", identifierField = "id", identifierType = String.class,  persistenceUnit = "persistenceUnit", transactionManager = "transactionManager")
public class Tcardbat {
	@Id
	@Column(name = "ID")
    private String id;
    	
	@Column(name = "CARDMANAGER")
    private String cardmanager;
    
	@Column(name = "BATCHNO")
    private String batchno;
   
	@Column(name = "BEGINNUMBER")
    private long beginnumber;
    
	@Column(name = "ENDNUMBER")
    private long endnumber;
   
	@Column(name = "STARTTIME")
    private Date starttime;
   
	@Column(name = "ENDTIME")
    private Date endtime;
	
	public static BigDecimal findMaxId() {
		Query query = Tcardbat.entityManager().createNativeQuery("select max(cast(id as int)) from Tcardbat");
		return (BigDecimal) query.getSingleResult();
	}
	
	public static BigDecimal findLastBatchNo() {
		Query query = Tcardbat.entityManager().createNativeQuery("select max(cast(batchno as int)) from Tcardbat");
		return (BigDecimal) query.getSingleResult();
	}
	
	public static Long findLastCardNo() {
		Query query = Tcardbat.entityManager().createQuery("select max(endnumber) from Tcardbat");
		return (Long) query.getSingleResult();
	}
}
