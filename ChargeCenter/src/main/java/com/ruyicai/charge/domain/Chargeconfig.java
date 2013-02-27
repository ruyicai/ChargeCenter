package com.ruyicai.charge.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.consts.Const;
import com.ruyicai.charge.consts.TransactionState;

@RooJson
@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "CHARGECONFIG", identifierField = "id", persistenceUnit = "persistenceUnitMysql", transactionManager = "transactionManagerMysql")
public class Chargeconfig implements Serializable {
	private static final long serialVersionUID = 1L;

	transient private static Logger logger = Logger.getLogger(Chargeconfig.class);
	
	@Id
	@Column(name = "ID")
	private String id;
	
	@Column(name = "memo")
	private String memo;
	
	@Column(name = "value")
	private String value;
	
	@Column(name = "CREATETIME")
	private Date createtime;
	
	@Column(name = "MODIFYTIME")
	private Date modifytime;

	
	@Transactional("transactionManagerMysql")
	public static Chargeconfig createOrModifyChargeconfig(String id, String memo, String value) {
		boolean isExist = false;
		Chargeconfig cc = Chargeconfig.findChargeconfig(id);
		if (null != cc) {
			isExist = true;
		} else {
			isExist = false;
			cc = new Chargeconfig();
			cc.setId(id);
		}		
		cc.setMemo(memo);
		cc.setValue(value);		
		Date date = new Date();
		cc.setModifytime(date);
		if(isExist){
			cc.merge();
		} else {
			cc.setCreatetime(date);
			cc.persist();
		}
		return cc;
	}

	@Transactional("transactionManagerMysql")
	public static Chargeconfig modifyChargeconfig(String id, String memo, String value) {
		Chargeconfig cc = Chargeconfig.findChargeconfig(id);		
		cc.setMemo(memo);
		cc.setValue(value);		
		Date date = new Date();
		cc.setModifytime(date);
		cc.merge();		
		return cc;
	}
	
	@Transactional("transactionManagerMysql")
	public static Chargeconfig modifyChargeconfig(String id, String value) {			
		Chargeconfig cc = Chargeconfig.findChargeconfig(id);
		cc.setValue(value);	
		Date date = new Date();
		cc.setModifytime(date);
		cc.merge();
		return cc;
	}
}
