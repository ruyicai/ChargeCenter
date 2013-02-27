package com.ruyicai.charge.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.consts.Const;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.Page;
import com.ruyicai.charge.util.PaySign;

@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "TACCOUNTDETAIL", schema = "JRTSCH", persistenceUnit = "persistenceUnit", transactionManager = "transactionManager")
public class Taccountdetail {
	
	transient Logger logger = LoggerFactory.getLogger(Taccountdetail.class);
	
	@Id
	@GeneratedValue(generator = Const.IdGeneratorName)
	@GenericGenerator(name = Const.IdGeneratorName, strategy = Const.IdStrategy, //
	parameters = {
			@Parameter(name = Const.Seq_prefix, value = Const.TlotIdPrefix),
			@Parameter(name = Const.Seq_fmtWidth, value = "14"),
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "taccountdetailId"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ") })
	@Column(name = "ID")
	private String id;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "PLATTIME", columnDefinition = "TIMESTAMP(6)")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "S-")
	private Date plattime;

	@Column(name = "AMT")
	@NotNull
	private BigDecimal amt;

	@Column(name = "DRAWAMT")
	@NotNull
	private BigDecimal drawamt;

	@Column(name = "BLSIGN")
	@NotNull
	private BigDecimal blsign;

	@Column(name = "MEMO", length = 256)
	@NotNull
	private String memo;

	@Column(name = "BALANCE")
	@NotNull
	private BigDecimal balance;

	@Column(name = "TACCOUNTTYPE")
	private BigDecimal taccounttype;

	@Column(name = "TTRANSACTIONID", length = 128)
	private String ttransactionid;

	@Column(name = "TTRANSACTIONTYPE")
	private BigDecimal ttransactiontype;

	@Column(name = "MOBILEID", length = 44)
	private String mobileid;

	@Column(name = "USERNO", length = 32)
	@NotNull
	private String userno;

	@Column(name = "DRAWBALANCE")
	@NotNull
	private BigDecimal drawbalance;

	@Column(name = "MAC", length = 256)
	@NotNull
	private String mac = " ";

	@Column(name = "STATE")
	private BigDecimal state;

	@Column(name = "FREEZEAMT")
	@NotNull
	private BigDecimal freezeamt;

	@Column(name = "FREEZEBALANCE")
	@NotNull
	private BigDecimal freezebalance;
	
	@Column(name = "FLOWNO")
	private String flowno;
	
	@Column(name = "OTHERID")
	private String otherid;
	
	@Transactional("transactionManager")
	public Taccountdetail merge() {
		this.setMac(this.taccountDetailSign());
		if (this.entityManager == null)
			this.entityManager = entityManager();
		Taccountdetail merged = this.entityManager.merge(this);
		this.entityManager.flush();
		return merged;
	}

	@Transactional("transactionManager")
	public void persist() {
		if (this.entityManager == null)
			this.entityManager = entityManager();
		this.entityManager.persist(this);
		this.entityManager.flush();
		Taccountdetail detail = Taccountdetail.findTaccountdetail(this.getId());
		this.setMac(detail.taccountDetailSign());
		detail.merge();
	}

	public static List<Taccountdetail> findByTransactionid(String transactionid) {
		TypedQuery<Taccountdetail> query = Taccountdetail
				.entityManager()
				.createQuery(
						"select o from Taccountdetail o where o.ttransactionid=? order by plattime desc",
						Taccountdetail.class);
		query.setParameter(1, transactionid);
		return query.getResultList();
	}

	public static List<Taccountdetail> findByOtherid(String otherid) {
		TypedQuery<Taccountdetail> query = Taccountdetail
				.entityManager()
				.createQuery(
						"select o from Taccountdetail o where o.otherid=? order by plattime desc",
						Taccountdetail.class);
		query.setParameter(1, otherid);
		return query.getResultList();
	}
	
	public static List<Taccountdetail> findByFlowno(String flowno) {
		TypedQuery<Taccountdetail> query = Taccountdetail
				.entityManager()
				.createQuery(
						"select o from Taccountdetail o where o.flowno=? order by plattime desc",
						Taccountdetail.class);
		query.setParameter(1, flowno);
		return query.getResultList();
	}
	
	public static List<Taccountdetail> findByFlownoAndTransactiontype(String flowno, BigDecimal ttransactiontype) {
		TypedQuery<Taccountdetail> query = Taccountdetail
				.entityManager()
				.createQuery(
						"select o from Taccountdetail o where o.flowno=? and o.ttransactiontype=? order by plattime desc",
						Taccountdetail.class);
		query.setParameter(1, flowno);
		query.setParameter(2, ttransactiontype);
		return query.getResultList();
	}
	
	public String taccountDetailSign() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer inStr = new StringBuffer();
		inStr.append(this.getId()).append(sFormat.format(this.getPlattime()))
				.append(this.getAmt().longValue())
				.append(this.getDrawamt().longValue()).append(this.getBlsign())
				.append(this.getMemo()).append(this.getBalance().longValue())
				.append(this.getTaccounttype())
				.append(this.getTtransactionid())
				.append(this.getTtransactiontype()).append(this.getMobileid())
				.append(this.getUserno())
				.append(this.getDrawbalance().longValue());
		try {
			return PaySign.Md5(inStr.toString());
		} catch (Exception e) {

			logger.error("为taccountdetail生成加密时出错", e);
			throw new RuyicaiException(ErrorCode.Data_MD5);
		}
	}

	public static void findByUsernoAndDate(String userno, String begin,
			String end, BigDecimal[] type, Page<Taccountdetail> page) {
		StringBuilder types = new StringBuilder("");
		if (type != null && type.length > 0) {
			types.append(" AND (");
			int index = 1;
			for (BigDecimal t : type) {
				types.append(" o.ttransactiontype=:type" + index + " or");
				index += 1;
			}
			types.delete(types.length() - 2, types.length());
			types.append(")");
		}
		String sql = "select o from Taccountdetail o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ?"
			+types.toString()+" order by o.plattime desc";
		String totalSql = "select count(o) from Taccountdetail o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ?"
			+types.toString();
		TypedQuery<Taccountdetail> query = Taccountdetail.entityManager().createQuery(sql, Taccountdetail.class).setParameter(1, userno)
							.setParameter(2, begin).setParameter(3, end);
		TypedQuery<Long> totalQuery = Taccountdetail.entityManager().createQuery(totalSql, Long.class).setParameter(1, userno).setParameter(2, begin).setParameter(3, end);
		if (null != type && type.length > 0) {
			int index = 1;
			for (BigDecimal t : type) {
				query.setParameter("type" + index, t);
				totalQuery.setParameter("type" + index, t);
				index += 1;
			}
		}
		query.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		/*TypedQuery<Taccountdetail> query = Taccountdetail
				.entityManager()
				.createQuery(
						"select o from Taccountdetail o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ? order by o.plattime desc",
						Taccountdetail.class).setParameter(1, userno)
				.setParameter(2, begin).setParameter(3, end)
				.setFirstResult(page.getPageIndex() * page.getMaxResult())
				.setMaxResults(page.getMaxResult());*/
		List<Taccountdetail> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			throw new RuyicaiException(ErrorCode.Taccountdetail_Empty);
		}
		page.setList(resultList);

		/*TypedQuery<Long> totalQuery = Taccountdetail
				.entityManager()
				.createQuery(
						"select count(o) from Taccountdetail o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ?",
						Long.class).setParameter(1, userno)
				.setParameter(2, begin).setParameter(3, end);*/
		page.setTotalResult(totalQuery.getSingleResult().intValue());
	}

	public static Taccountdetail findbyTranIdAndType(String ttransactionid,
			BigDecimal type) {
		TypedQuery<Taccountdetail> query = Taccountdetail
				.entityManager()
				.createQuery(
						"select o from Taccountdetail o where o.ttransactionid=? and o.ttransactiontype=? order by plattime desc",
						Taccountdetail.class);
		query.setParameter(1, ttransactionid).setParameter(2, type);
		List<Taccountdetail> results = query.getResultList();
		if (results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}
	
	
	public static List<Taccountdetail> findbyTranIdAndTypeList(String ttransactionid,
			BigDecimal type) {
		TypedQuery<Taccountdetail> query = Taccountdetail
				.entityManager()
				.createQuery(
						"select o from Taccountdetail o where o.ttransactionid=? and o.ttransactiontype=? order by plattime desc",
						Taccountdetail.class);
		query.setParameter(1, ttransactionid).setParameter(2, type);
		List<Taccountdetail> results = query.getResultList();
		
		return results;
	}

	public static List<Taccountdetail> findByFlownoAndType(String flowno,
			BigDecimal type) {
		return entityManager().createQuery("select o from Taccountdetail o where o.flowno=? and o.ttransactiontype=?", Taccountdetail.class)
				.setParameter(1, flowno).setParameter(2, type)
				.getResultList();
	}
}
