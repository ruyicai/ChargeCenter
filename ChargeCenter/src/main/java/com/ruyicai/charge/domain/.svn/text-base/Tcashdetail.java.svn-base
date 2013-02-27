package com.ruyicai.charge.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.charge.consts.Const;
import com.ruyicai.charge.consts.CashDetailState;
import com.ruyicai.charge.domain.Tcashdetail;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.Page;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "TCASHDETAIL", identifierField = "id", schema = "JRTSCH", persistenceUnit = "persistenceUnit", transactionManager = "transactionManager")
@RooJson
public class Tcashdetail {
	@Id
	@GeneratedValue(generator = Const.IdGeneratorName)
	@GenericGenerator(name = Const.IdGeneratorName, strategy = Const.IdStrategy, 
	parameters = {
			@Parameter(name = Const.Seq_fmtWidth, value = "8"),
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "TcashdetailID"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ") })
	private String id;

	@Column(name = "USERNO")
	private String userno;

	@Column(name = "TTRANSACTIONID")
	private String ttransactionid;

	@Column(name = "NAME")
	private String name;

	@Column(name = "PROVCODE")
	private String provcode;

	@Column(name = "AREACODE")
	private String areacode;

	@Column(name = "PROVNAME")
	private String provname;

	@Column(name = "AREANAME")
	private String areaname;

	@Column(name = "AMT")
	private BigDecimal amt;

	@Column(name = "FEE")
	private BigDecimal fee;

	@Column(name = "PLATTIME")
	private Date plattime;

	@Column(name = "BANKNAME")
	private String bankname;

	@Column(name = "BANKACCOUNT")
	private String bankaccount;

	@Column(name = "STATE")
	private BigDecimal state;

	@Column(name = "SUBBANKNAME")
	private String subbankname;
	
	@Column(name = "REJECTREASON")
	private String rejectreason;
	
	@Column(name = "TYPE")
	private BigDecimal type;
	
	@Column(name = "BATCHNO")
	private String batchno;
	
	@Column(name = "MODIFYTIME")
	private Date modifytime;
	
	public static List<Tcashdetail> getTcashdetailByUserNo(String userno) {
		List<Tcashdetail> resultList = Tcashdetail.entityManager().createQuery(
						"select o from Tcashdetail o where o.userno=?  order by o.plattime desc ", Tcashdetail.class)
				.setParameter(1, userno).getResultList();
		return resultList;
	}
	
	public static void getTcashdetailByUsernoAndPage(String userno, Page<Tcashdetail> page) {		
		String sql = "select o from Tcashdetail o where o.userno=?  order by o.plattime desc ";		
		TypedQuery<Tcashdetail> query = Tcashdetail.entityManager().createQuery(sql, Tcashdetail.class).setParameter(1, userno);		
		query.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		List<Tcashdetail> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			throw new RuyicaiException(ErrorCode.Taccountdetail_TcashDetailEmpty);
		}		
		page.setList(resultList);
		
		String totalSql = "select count(o) from Tcashdetail o where o.userno=? ";
		TypedQuery<Long> totalQuery = Tcashdetail.entityManager().createQuery(totalSql, Long.class).setParameter(1, userno);
		page.setTotalResult(totalQuery.getSingleResult().intValue());
	}
	
	public static Tcashdetail getTcashdetailByTransactionId(String ttransactionid) {
		List<Tcashdetail> resultList = Tcashdetail.entityManager().createQuery(
				"select o from Tcashdetail o where o.ttransactionid=? ", Tcashdetail.class)
				.setParameter(1, ttransactionid).getResultList();
		
		return resultList.get(0);
	}

	public static void findAllCashDetail(String userno, String beginTime, String endTime,Page<Tcashdetail> page) {
		page.setList(Tcashdetail.entityManager().createQuery(
				"select o from Tcashdetail o where to_char(o.plattime,'yyyymmdd')>=? and to_char(o.plattime,'yyyymmdd')<=?  and userno=?",
				Tcashdetail.class).setParameter(1, beginTime).setParameter(2, endTime).setParameter(3, userno)
				.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult()).getResultList());
		
		TypedQuery<Long> total = Tcashdetail.entityManager().createQuery(
				"select count(o) from Tcashdetail o where to_char(o.plattime,'yyyymmdd')>=? and to_char(o.plattime,'yyyymmdd')<=?  and userno=?"
				, Long.class).setParameter(1, beginTime).setParameter(2, endTime).setParameter(3, userno);
		Integer totalRes = total.getSingleResult().intValue();
		page.setTotalResult(totalRes);
		Tcashdetail.entityManager().clear();
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object[]> findSuccess(String userno) {
		Query query = Tcashdetail.entityManager().createQuery(
				"select o.name,o.provname,o.areaname,o.bankname,o.bankaccount from Tcashdetail o where o.userno=? and o.state=? group by o.name,o.provname,o.areaname,o.bankname,o.bankaccount")
				.setParameter(1, userno).setParameter(2, CashDetailState.Chenggong.value());
		return query.getResultList();
	}
}
