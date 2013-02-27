package com.ruyicai.charge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Query;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "Tcardsnstate", schema = "JRTSCH", identifierField = "id", identifierType = String.class, persistenceUnit = "persistenceUnit", transactionManager = "transactionManager")
public class Tcardsnstate {
	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "BEGINNO")
	private long beginno;

	@Column(name = "ENDNO")
	private long endno;

	@Column(name = "STATE")
	private BigDecimal state;
	
	public static BigDecimal findMaxId() {
		Query query = Tcardsnstate.entityManager().createNativeQuery("select max(cast(ID as int)) from Tcardsnstate");
		return (BigDecimal) query.getSingleResult();
	}
}
