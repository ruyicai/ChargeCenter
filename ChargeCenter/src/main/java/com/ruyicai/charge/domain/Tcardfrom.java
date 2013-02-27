package com.ruyicai.charge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "Tcardfrom", schema = "JRTSCH", identifierField = "id", identifierType = BigDecimal.class, persistenceUnit = "persistenceUnit",  transactionManager = "transactionManager")
public class Tcardfrom {
	@Id
	@Column(name = "ID")
    private BigDecimal id;

	@Column(name = "NAME")
    private String name;

	@Column(name = "MEMO")
    private String memo;
}
