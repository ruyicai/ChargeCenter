package com.ruyicai.charge.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "Tcard", schema = "JRTSCH", identifierField = "id", identifierType = String.class, persistenceUnit = "persistenceUnit",  transactionManager = "transactionManager")
public class Tcard {
	@Id
	@Column(name = "ID")
	private String id;

	@Column(name = "AMT")
	private BigDecimal amt = new BigDecimal(0);

	@Column(name = "TYPE")
	private BigDecimal type = new BigDecimal(0);

	@Column(name = "CARDFROM")
	private BigDecimal cardfrom = new BigDecimal(0);

	@Column(name = "CARDSN")
	private String cardsn;

	@Column(name = "STATE")
	private BigDecimal state = new BigDecimal(0);

	@Column(name = "STARTTIME")
	private Date starttime;

	@Column(name = "ENDTIME")
	private Date endtime;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "MANAGER")
	private String manager;

	@Column(name = "BATCHNO")
	private String batchno;

	@Column(name = "AGENCYNO")
	private String agencyno;

	@Column(name = "SELLTIME")
	private Date selltime;

	@Column(name = "DECRPTKEY")
	private String decrptkey;

	@Column(name = "CHANNEL")
	private String channel;

	public static Long findCountByAmtTypeCardfromChannel(int amt, int type,
			int cardfrom, String channel, BigDecimal state) {
		Query query = Tcard
				.entityManager()
				.createQuery(
						"select count(id) from Tcard WHERE amt=? AND type=? AND cardfrom=? And channel=?  And state=?")
				.setParameter(1, new BigDecimal(amt))
				.setParameter(2, new BigDecimal(type))
				.setParameter(3, new BigDecimal(cardfrom))
				.setParameter(4, channel).setParameter(5, state);
		return (Long) query.getSingleResult();
	}

	public static List<Tcard> findTcardsByAmtTypeCardfromChannel(int amt,
			int type, int cardfrom, String channel, int sellamt,
			BigDecimal state) {
		TypedQuery<Tcard> query = Tcard
				.entityManager()
				.createQuery(
						"select o from Tcard as o WHERE amt=? AND type=? AND cardfrom=? And channel=?  And state=? And rownum <=?",
						Tcard.class).setParameter(1, new BigDecimal(amt))
				.setParameter(2, new BigDecimal(type))
				.setParameter(3, new BigDecimal(cardfrom))
				.setParameter(4, channel).setParameter(5, state)
				.setParameter(6, (long) sellamt);
		return query.getResultList();
	}
}
