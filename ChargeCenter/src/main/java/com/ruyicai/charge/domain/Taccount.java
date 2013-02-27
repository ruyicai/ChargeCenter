package com.ruyicai.charge.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.DateUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.PaySign;

@RooJavaBean
@RooToString
@RooJson
@Entity
@Table(name = "TACCOUNT")
public class Taccount implements Serializable {

	private static final long serialVersionUID = 1L;

	transient Logger logger = LoggerFactory.getLogger(Taccount.class);

	@Id
	@Column(name = "USERNO")
	private String userno;

	@Column(name = "BALANCE")
	@NotNull
	private BigDecimal balance = new BigDecimal(0);

	@Column(name = "DRAWBALANCE")
	@NotNull
	private BigDecimal drawbalance = new BigDecimal(0);

	@Column(name = "LASTBETAMT")
	@NotNull
	private BigDecimal lastbetamt = new BigDecimal(0);

	@Column(name = "LASTBETTIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date lastbettime = DateUtil.get1000Date();

	@Column(name = "TOTALBETAMT")
	@NotNull
	private BigDecimal totalbetamt = new BigDecimal(0);

	@Column(name = "LASTPRIZEAMT")
	@NotNull
	private BigDecimal lastprizeamt = new BigDecimal(0);

	@Column(name = "LASTPRIZETIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date lastprizetime = DateUtil.get1000Date();

	@Column(name = "TOTALPRIZEAMT")
	@NotNull
	private BigDecimal totalprizeamt = new BigDecimal(0);

	@Column(name = "LASTDEPOSITAMT")
	@NotNull
	private BigDecimal lastdepositamt = new BigDecimal(0);

	@Column(name = "LASTDEPOSITTIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date lastdeposittime = DateUtil.get1000Date();

	@Column(name = "TOTALDEPOSITAMT")
	@NotNull
	private BigDecimal totaldepositamt = new BigDecimal(0);

	@Column(name = "LASTDRAWAMT")
	@NotNull
	private BigDecimal lastdrawamt = new BigDecimal(0);

	@Column(name = "LASTDRAWTIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date lastdrawtime = DateUtil.get1000Date();

	@Column(name = "TOTALDRAWAMT")
	@NotNull
	private BigDecimal totaldrawamt = new BigDecimal(0);

	@Column(name = "MAC", length = 256)
	@NotNull
	private String mac = " ";

	@Column(name = "FREEZEBALANCE")
	@NotNull
	private BigDecimal freezebalance = new BigDecimal(0);

	public String taccountSign() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer inStr = new StringBuffer();
		inStr.append(this.getUserno()).append(this.getBalance().longValue())
				.append(this.getDrawbalance().longValue())
				.append(this.getLastbetamt().longValue())
				.append(sFormat.format(this.getLastbettime()))
				.append(this.getTotalbetamt().longValue())
				.append(this.getLastprizeamt().longValue())
				.append(sFormat.format(this.getLastprizetime()))
				.append(this.getTotalprizeamt().longValue())
				.append(this.getLastdepositamt().longValue())
				.append(sFormat.format(this.getLastdeposittime()))
				.append(this.getTotaldepositamt().longValue())
				.append(this.getLastdrawamt().longValue())
				.append(sFormat.format(this.getLastdrawtime()))
				.append(this.getTotaldrawamt().longValue());
		try {
			return PaySign.Md5(inStr.toString());
		} catch (Exception e) {
			logger.info("Taccount生成Mac错误");
			throw new RuyicaiException(ErrorCode.Data_MD5);
		}
	}

	public boolean isValid() {
		return this.getMac().equals(this.taccountSign());
	}
}
