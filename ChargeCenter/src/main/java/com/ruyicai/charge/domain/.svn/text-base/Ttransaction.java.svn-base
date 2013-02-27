package com.ruyicai.charge.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.charge.consts.Const;

@RooJavaBean
@RooToString
@Entity
@Table(name = "ttransaction")
public class Ttransaction {
	@Id
	@GeneratedValue(generator = Const.IdGeneratorName)
	@GenericGenerator(name = Const.IdGeneratorName, strategy = Const.IdStrategy, //
	parameters = {
			@Parameter(name = Const.Seq_prefix, value = Const.TlotIdPrefix),
			@Parameter(name = Const.Seq_Date, value = "yyyyMMdd"),
			@Parameter(name = Const.Seq_fmtWidth, value = "22"),
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "ttransactionId"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ"),
			@Parameter(name=TableGenerator.INCREMENT_PARAM,value="1000")})
	@Column(name = "ID")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "TYPE")
	@NotNull
	private BigDecimal type;

	@Column(name = "USERNO", length = 32)
	@NotNull
	private String userno;

	@Column(name = "ACCEPTNO", length = 32)
	@NotNull
	private String acceptno;

	@Column(name = "FLOWNO", length = 64)
	@NotNull
	private String flowno;

	@Column(name = "PLATTIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date plattime;

	@Column(name = "AMT")
	@NotNull
	private BigDecimal amt;

	@Column(name = "FEE")
	@NotNull
	private BigDecimal fee;

	@Column(name = "BANKID", length = 32)
	private String bankid;

	@Column(name = "BANKACCOUNT", length = 128)
	@NotNull
	private String bankaccount;

	@Column(name = "BANKORDERID", length = 128)
	@NotNull
	private String bankorderid;

	@Column(name = "BANKORDERTIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date bankordertime;

	@Column(name = "BANKTRACE", length = 128)
	@NotNull
	private String banktrace;

	@Column(name = "BANKRETTIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date bankrettime;

	@Column(name = "STATE")
	@NotNull
	private BigDecimal state;

	@Column(name = "RETCODE", length = 32)
	@NotNull
	private String retcode;

	@Column(name = "RETMEMO", length = 256)
	@NotNull
	private String retmemo;

	@Column(name = "MEMO", length = 512)
	private String memo;

	@Column(name = "BANKCHECK")
	@NotNull
	private BigDecimal bankcheck;

	@Column(name = "PAYTYPE", length = 16)
	@NotNull
	private String paytype;

	@Column(name = "ACCESSTYPE")
	@NotNull
	private Character accesstype;
	
	@Column(name = "CHANNEL")
	private String channel;

	@Column(name = "SUBCHANNEL")
	private String subchannel;
	
	@Column(name = "PRESENTAMT")
	private BigDecimal presentamt = new BigDecimal(0);
	
	@Column(name = "LADDERPRESENTFLAG")
	private BigDecimal ladderpresentflag = new BigDecimal(0);

	@Column(name = "CONTINUEBETTYPE")
	private BigDecimal continuebettype;
	
	@Column(name = "ORDERID")
	private String orderid;
}
