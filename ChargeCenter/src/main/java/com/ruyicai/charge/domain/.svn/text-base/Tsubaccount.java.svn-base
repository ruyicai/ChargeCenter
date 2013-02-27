package com.ruyicai.charge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.PaySign;

@RooJavaBean
@RooToString
@Entity
@Table(name = "TSUBACCOUNT")
public class Tsubaccount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private BigDecimal id;

	@Column(name = "USERNO")
	private String userno;

	@Column(name = "BALANCE")
	private BigDecimal balance = BigDecimal.ZERO;

	@Column(name = "DRAWBALANCE")
	private BigDecimal drawbalance = BigDecimal.ZERO;

	@Column(name = "FREEZEBALANCE")
	private BigDecimal freezebalance = BigDecimal.ZERO;

	@Column(name = "MAC")
	private String mac;

	@Column(name = "TYPE")
	private BigDecimal type = BigDecimal.ZERO;
	
	private static transient Logger logger = LoggerFactory.getLogger(Tsubaccount.class);
	
	public String tsubaccountSign() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getUserno()).append(this.getBalance())
				.append(this.getDrawbalance()).append(this.getFreezebalance())
				.append(this.getType());
		try {
			return PaySign.Md5(builder.toString());
		} catch (Exception e) {
			logger.error("生成子账户信息校验码出错", e);
			throw new RuyicaiException(ErrorCode.MAC_ERROR);
		}
	}
	
	public boolean isValid(){
		return this.getMac().equals(this.tsubaccountSign());
	}
}
