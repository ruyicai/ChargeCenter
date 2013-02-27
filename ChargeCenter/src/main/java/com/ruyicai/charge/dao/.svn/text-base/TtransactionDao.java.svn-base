package com.ruyicai.charge.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.consts.TransactionState;
import com.ruyicai.charge.consts.TransactionType;
import com.ruyicai.charge.domain.Ttransaction;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.Page;

@Service
public class TtransactionDao {
	
    @PersistenceContext(unitName="persistenceUnit")
    EntityManager entityManager;
    
    @Transactional("transactionManager")
    public void persist(Ttransaction ttransaction) {
        this.entityManager.persist(ttransaction);
    }
    
    @Transactional("transactionManager")
    public void remove(Ttransaction ttransaction) {
        if (this.entityManager.contains(ttransaction)) {
            this.entityManager.remove(ttransaction);
        } else {
            Ttransaction attached = findTtransaction(ttransaction.getId());
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional("transactionManager")
    public void flush() {
        this.entityManager.flush();
    }
    
    @Transactional("transactionManager")
    public void clear() {
        this.entityManager.clear();
    }
    
    @Transactional("transactionManager")
    public Ttransaction merge(Ttransaction ttransaction) {
        Ttransaction merged = this.entityManager.merge(ttransaction);
        this.entityManager.flush();
        return merged;
    }

    public long countTtransactions() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Ttransaction o", Long.class).getSingleResult();
    }
    
    public List<Ttransaction> findAllTtransactions() {
        return entityManager.createQuery("SELECT o FROM Ttransaction o", Ttransaction.class).getResultList();
    }
    
    public Ttransaction findTtransaction(String id) {
        if (id == null || id.length() == 0) return null;
        return entityManager.find(Ttransaction.class, id);
    }
    
    public Ttransaction findTtransaction(String id, boolean lock) {
        if (id == null || id.length() == 0) return null;
        return entityManager.find(Ttransaction.class, id, lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
    }
    
    public List<Ttransaction> findTtransactionEntries(int firstResult, int maxResults) {
        return entityManager.createQuery("SELECT o FROM Ttransaction o", Ttransaction.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public Ttransaction findTtransactionByFlowno(String flowno,
			String userno) {
		List<Ttransaction> resultList = entityManager
				.createQuery(
						"select o from Ttransaction o where flowno=? and userno =?",
						Ttransaction.class).setParameter(1, flowno)
				.setParameter(2, userno).getResultList();
		if (resultList.size() > 0) {
			return resultList.get(0);
		}
		return null;
	}

	public List<Ttransaction> findTtransactionByUsernoAndBankIdCurrentMonth(
			String userno, String bankId) {

		List<Ttransaction> resultList = entityManager
				.createQuery(
						"select o from Ttransaction o where o.userno=? and o.bankid=? and to_char(o.plattime, 'yyyy-mm') = ? and (o.state=1 or o.state=3) and (o.type=2 or o.type=3 or o.type=10)",
						Ttransaction.class)
				.setParameter(1, userno)
				.setParameter(2, bankId)
				.setParameter(3,
						new SimpleDateFormat("yyyy-MM").format(new Date()))
				.getResultList();

		return resultList;
	}

	public void findByUsernoAndDate(String userno, String begin,
			String end, BigDecimal state, Page<Ttransaction> page) {
		String append = null == state ? "" : " and state=?";
		TypedQuery<Ttransaction> query = entityManager
				.createQuery(
						"select o from Ttransaction o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ? "
								+ append + " order by o.plattime desc",
						Ttransaction.class).setParameter(1, userno)
				.setParameter(2, begin).setParameter(3, end);
		if (null != state) {
			query.setParameter(4, state);
		}
		query.setFirstResult(page.getPageIndex() * page.getMaxResult())
				.setMaxResults(page.getMaxResult());
		List<Ttransaction> resultList = query.getResultList();
		if (resultList.isEmpty()) {

			throw new RuyicaiException(ErrorCode.Ttransaction_Empty);
		}
		page.setList(resultList);

		TypedQuery<Long> totalQuery = entityManager
				.createQuery(
						"select count(o) from Ttransaction o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ?"
								+ append, Long.class).setParameter(1, userno)
				.setParameter(2, begin).setParameter(3, end);
		if (null != state) {
			totalQuery.setParameter(4, state);
		}
		page.setTotalResult(totalQuery.getSingleResult().intValue());
	}

	public List<Ttransaction> getByFlowno(String flowno) {
		List<Ttransaction> resultList = entityManager
				.createQuery("select o from Ttransaction o where o.flowno=?",
						Ttransaction.class).setParameter(1, flowno)
				.getResultList();

		return resultList;
	}

	public List<Ttransaction> getByType(BigDecimal type) {
		List<Ttransaction> resultList = entityManager
				.createQuery("select o from Ttransaction o where o.type=?",
						Ttransaction.class).setParameter(1, type)
				.getResultList();

		return resultList;
	}

	public Long checkOutMoney(String userno, String accessType,
			String startTime, String endTime, String state, String type,
			String bankid) {
		Long resLong = null;
		TypedQuery<BigDecimal> totalQuery = entityManager
				.createQuery(
						"select sum(amt) from Ttransaction o where o.type=? "
								+ "and  to_char(o.plattime, 'YYYY-MM-DD HH24:Mi:SS') >= ? and to_char(o.plattime, 'YYYY-MM-DD HH24:Mi:SS') <= ?"
								+ " and o.bankid=? and o.state=? and (o.accesstype='B' or o.accesstype='W' or o.accesstype='Y') and o.userno=?",
						BigDecimal.class)
				.setParameter(1, BigDecimal.valueOf(Long.parseLong(type)))
				.setParameter(2, startTime).setParameter(3, endTime)
				.setParameter(4, bankid)
				.setParameter(5, BigDecimal.valueOf(Long.parseLong(state)))
				.setParameter(6, userno);
		BigDecimal decimal = totalQuery.getSingleResult();
		if (decimal != null) {
			resLong = decimal.longValue();
		} else {
			resLong = new Long(0);
		}

		return resLong;
	}

	@SuppressWarnings("unused")
	public void findByTypesWithTongji(String userno, String begin,
			String end, BigDecimal[] types, Page<Ttransaction> page) {
		StringBuilder builder = new StringBuilder();
		if (null != types && types.length > 0) {
			builder.append(" and (");
		}
		if (null != types) {
			for (BigDecimal type : types) {
				builder.append(" o.type=? or");
			}
		}
		if (null != types && types.length > 0) {
			builder.delete(builder.length() - 2, builder.length());
			builder.append(")");
		}
		TypedQuery<Ttransaction> query = entityManager
				.createQuery(
						"select o from Ttransaction o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ? "
								+ builder.toString()
								+ " order by o.plattime desc",
						Ttransaction.class).setParameter(1, userno)
				.setParameter(2, begin).setParameter(3, end);
		int index = 4;
		if (null != types) {
			for (BigDecimal type : types) {
				query.setParameter(index, type);
				index = index + 1;
			}
		}
		query.setFirstResult(page.getPageIndex() * page.getMaxResult())
				.setMaxResults(page.getMaxResult());
		List<Ttransaction> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			throw new RuyicaiException(ErrorCode.Ttransaction_Empty);
		}
		page.setList(resultList);

		TypedQuery<Long> totalQuery = entityManager
				.createQuery(
						"select count(o) from Ttransaction o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ?"
								+ builder.toString(), Long.class)
				.setParameter(1, userno).setParameter(2, begin)
				.setParameter(3, end);
		index = 4;
		if (null != types) {
			for (BigDecimal type : types) {
				totalQuery.setParameter(index, type);
				index = index + 1;
			}
		}
		page.setTotalResult(totalQuery.getSingleResult().intValue());
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public List<Object[]> findByTypesWithTongji(String userno,
			String begin, String end, BigDecimal[] types) {
		StringBuilder builder = new StringBuilder();
		if (null != types && types.length > 0) {
			builder.append(" and (");
		}
		if (null != types) {
			for (BigDecimal type : types) {
				builder.append(" o.type=? or");
			}
		}
		if (null != types && types.length > 0) {
			builder.delete(builder.length() - 2, builder.length());
			builder.append(")");
		}
		Query query = entityManager
				.createQuery(
						"select o.type, nvl(count(o),0), nvl(sum(o.amt),0) from Ttransaction o where userno=? and to_char(o.plattime, 'yyyymmdd') >= ? and to_char(o.plattime, 'yyyymmdd') <= ?"
								+ builder.toString() + " group by o.type").setParameter(1, userno)
				.setParameter(2, begin).setParameter(3, end);
		int index = 4;
		if (null != types) {
			for (BigDecimal type : types) {
				query.setParameter(index, type);
				index = index + 1;
			}
		}
		return query.getResultList();
	}
	
	public List<Ttransaction> getById(String id) {
		List<Ttransaction> resultList = entityManager
				.createQuery("select o from Ttransaction o where o.id=?",
						Ttransaction.class).setParameter(1, id)
				.getResultList();

		return resultList;
	}
	
	public List<Ttransaction> findByFlownoAndType(String flowno, BigDecimal type) {
		return entityManager.createQuery("select o from Ttransaction o where o.state=1 and o.flowno=? and o.type=?", Ttransaction.class)
			.setParameter(1, flowno).setParameter(2, type)
			.getResultList();
	}
	
	public void mergeWithOk(Ttransaction ttransaction){
		ttransaction.setState(TransactionState.ok.value());
		merge(ttransaction);
    }
	
	public BigDecimal findByTypesWithAction(String userno, String date) {
		return entityManager
				.createQuery(
						"select nvl(sum(o.amt),0) from Ttransaction o where o.userno=? and o.plattime >= to_date(?,'YYYY-MM-DD HH24:Mi:SS') and o.type in ('"
								+ TransactionType.touzhu.value()
								+ "','"
								+ TransactionType.hemaijinedongjie.value()
								+ "') ", BigDecimal.class).setParameter(1, userno).setParameter(2, date)
				.getSingleResult();
	}
}
