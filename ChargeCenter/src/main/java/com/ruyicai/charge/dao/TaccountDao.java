package com.ruyicai.charge.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.consts.AccountDetailSignState;
import com.ruyicai.charge.consts.SubaccountType;
import com.ruyicai.charge.consts.TransactionType;
import com.ruyicai.charge.domain.Taccount;
import com.ruyicai.charge.domain.Taccountdetail;
import com.ruyicai.charge.domain.Tsubaccount;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;

@Service
public class TaccountDao {

	private Logger logger = LoggerFactory.getLogger(TaccountDao.class);
	
	@PersistenceContext(unitName="persistenceUnit")
	EntityManager entityManager;
	
	@Autowired
	TsubaccountDao tsubaccountDao;
	
	@Autowired
	TtransactionDao ttransactionDao;

	// 扣款
	public Taccountdetail deductMoney(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, 
			BigDecimal amount, String memo, SubaccountType type) {
		return deductMoneyWithDrawAmount(taccount, flowno, otherid, transactionId, ttransactiontype, amount, BigDecimal.ZERO, memo, type);
	}
	
	// 扣款
	public Taccountdetail deductMoney(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, 
			BigDecimal amount, BigDecimal drawAmount, String memo, SubaccountType type) {
		return deductMoneyWithDrawAmount(taccount, flowno, otherid, transactionId, ttransactiontype, amount, drawAmount, memo, type);
	}

	public Taccountdetail deductMoneyWithDrawAmount(Taccount taccount, String flowno, String otherid, String transactionId,
			BigDecimal ttransactiontype, BigDecimal amount, BigDecimal drawAmount, String memo, SubaccountType type) {
		checkSubaccount(taccount, type, amount);
		checkaccount(taccount, amount);
		taccount.setBalance(taccount.getBalance().subtract(amount.abs()));
		taccount.setDrawbalance(taccount.getDrawbalance().subtract(drawAmount.abs()));
		if (taccount.getBalance().compareTo(taccount.getDrawbalance()) < 0) {
			drawAmount = taccount.getDrawbalance().add(taccount.getBalance().negate());
			taccount.setDrawbalance(taccount.getBalance());
		}
		merge(taccount);
		tsubaccountDao.deductMoney(taccount, type, amount, drawAmount);
		return createAccountDetail(taccount, flowno, otherid, transactionId, ttransactiontype, amount, drawAmount,
				BigDecimal.ZERO, AccountDetailSignState.out.value(), memo);
	}

	public void checkaccount(Taccount taccount, BigDecimal amount) {
		if (taccount.getBalance().compareTo(new BigDecimal(0)) < 0) {
			logger.info("用户 账户余额小于0 ,扣款失败,帐户编号为 taccountId="
					+ taccount.getUserno());
			throw new RuyicaiException(ErrorCode.UserRes_YEYC);
		}
		if (taccount.getBalance().add(taccount.getFreezebalance().negate())
				.compareTo(amount) < 0) { // 判断用户余额减去冻结金额是否小于投注金额
			logger.info("用户账户余额减去冻结金额小于投注金额  : " + taccount);
			throw new RuyicaiException(ErrorCode.UserRes_YEYC);
		}
	}

	// 加款
	public void addMoney(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, 
			BigDecimal amount, BigDecimal drawAmount, String memo, SubaccountType type) {
		Tsubaccount tsubaccount = findTsubaccount(taccount, type);
		taccount.setBalance(taccount.getBalance().add(amount.abs()));
		if (new BigDecimal(0).compareTo(drawAmount) < 0) {
			taccount.setDrawbalance(taccount.getDrawbalance().add(drawAmount.abs()));
		}
		createAccountDetail(taccount, flowno, otherid, transactionId, ttransactiontype, amount, drawAmount, BigDecimal.ZERO,
				AccountDetailSignState.in.value(), memo);
		tsubaccountDao.addMoney(tsubaccount, amount, drawAmount);
		merge(taccount);
	}
	
	public void addDrawAmount(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, BigDecimal drawAmount) {
		taccount.setDrawbalance(taccount.getDrawbalance().add(drawAmount));
		merge(taccount);
		
		createAccountDetail(taccount, flowno, otherid, transactionId, ttransactiontype, BigDecimal.ZERO, drawAmount, BigDecimal.ZERO,
				AccountDetailSignState.in.value(), "增加可提现金额");
	}
	
	public void deductDrawAmount(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, BigDecimal drawAmount) {
		taccount.setDrawbalance(taccount.getDrawbalance().subtract(drawAmount));
		merge(taccount);
		
		createAccountDetail(taccount, flowno, otherid, transactionId, ttransactiontype, BigDecimal.ZERO, drawAmount, BigDecimal.ZERO,
				AccountDetailSignState.out.value(), "减少可提现金额");
	}

	// 兑奖
	@Transactional("transactionManager")
	public void encash(Taccount taccount, String flowno, String otherid, BigDecimal amount) {
		addMoney(taccount, flowno, otherid, "", TransactionType.duijianghuakuan.value(), amount, amount, "奖金", SubaccountType.PRIZE);
		taccount.setLastprizeamt(amount);
		taccount.setTotalprizeamt(taccount.getTotalprizeamt().add(amount));
		taccount.setLastprizetime(new Date());
		merge(taccount);
	}

	// 冻结
	public void freeze(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype,
			BigDecimal freezeAmount, String memo, SubaccountType subaccountType) {
		checkSubaccount(taccount, subaccountType, freezeAmount);
		if (taccount.getBalance().compareTo(freezeAmount) < 0) {// 判断用户账户余额是否小于投注金额
			logger.error("用户投注金额大于用户账户余额 : " + taccount);
			throw new RuyicaiException(ErrorCode.UserRes_YEYC);
		}
		if (taccount.getBalance().add(taccount.getFreezebalance().negate())
				.compareTo(freezeAmount) < 0) { // 判断用户余额减去冻结金额是否小于投注金额
			logger.error("用户账户余额减去冻结金额小于投注金额  : " + taccount);
			throw new RuyicaiException(ErrorCode.UserRes_YEYC);
		}
		Tsubaccount tsubaccount = findTsubaccount(taccount, subaccountType);
		taccount.setFreezebalance(taccount.getFreezebalance().add(freezeAmount));
		merge(taccount);
		createAccountDetail(taccount, flowno, otherid, transactionId, ttransactiontype, BigDecimal.ZERO,
				BigDecimal.ZERO, freezeAmount, AccountDetailSignState.freeze.value(), memo);
		tsubaccountDao.freeze(tsubaccount, freezeAmount);
	}

	public void checkSubaccount(Taccount taccount, SubaccountType subaccountType, BigDecimal amount) {
		if (null == subaccountType) {
			return;
		}
		Tsubaccount tsubaccount = findTsubaccount(taccount, subaccountType);
		if (tsubaccount.getBalance().compareTo(amount) < 0) {
			logger.error("用户投注金额大于用户子账户金额: " + tsubaccount);
			throw new RuyicaiException(ErrorCode.UserRes_DJYC);
		}
		if (tsubaccount.getBalance()
				.add(tsubaccount.getFreezebalance().negate()).compareTo(amount) < 0) {
			logger.error("用户子账户余额减去冻结金额小于投注金额  : " + tsubaccount);
			throw new RuyicaiException(ErrorCode.UserRes_DJYC);
		}
	}

	public void unfreeze(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, BigDecimal freezeAmount,
			String memo, SubaccountType subaccountType) {
		if (taccount.getFreezebalance().intValue() < freezeAmount.intValue()) {
			throw new RuyicaiException(ErrorCode.UserRes_JDJEBZ);
		}
		Tsubaccount tsubaccount = findTsubaccount(taccount, subaccountType);
		taccount.setFreezebalance(taccount.getFreezebalance()
				.add(freezeAmount.negate()));
		merge(taccount);
		createAccountDetail(taccount, flowno, otherid, transactionId, ttransactiontype, BigDecimal.ZERO,
				BigDecimal.ZERO, freezeAmount, AccountDetailSignState.unfreeze.value(), memo);
		tsubaccountDao.unfreeze(tsubaccount, freezeAmount);
	}

	public void bet(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, BigDecimal amount,
			SubaccountType subaccountType) {
		deductMoney(taccount, flowno, otherid, transactionId, ttransactiontype, amount, "用户投注扣款", subaccountType);
		taccount.setLastbettime(new Date());
		taccount.setLastbetamt(amount);
		taccount.setTotalbetamt(taccount.getTotalbetamt().add(amount));
		merge(taccount);
	}
	
	private Taccountdetail createAccountDetail(Taccount taccount, String flowno, String otherid, String transactionId, BigDecimal ttransactiontype, 
			BigDecimal amount, BigDecimal drawAmount, BigDecimal freeAmount, BigDecimal sign, String memo) {		
		Taccountdetail detail = new Taccountdetail();
		detail.setAmt(amount);
		detail.setFreezeamt(freeAmount);
		detail.setDrawamt(drawAmount);// 可提现变动金额，单位：分
		detail.setTtransactionid(transactionId);// 交易标识
		detail.setUserno(taccount.getUserno());
		detail.setBlsign(sign);// -1：出账，1：进账，-2：解冻，2：冻结
		detail.setMemo(memo);// 账户科目明细描述
		detail.setBalance(taccount.getBalance());// 账户总余额
		detail.setDrawbalance(taccount.getDrawbalance());
		detail.setState(new BigDecimal(1));
		detail.setFreezebalance(taccount.getFreezebalance());
		detail.setPlattime(new Date());
		detail.setTtransactiontype(ttransactiontype);
		detail.setFlowno(flowno);
		detail.setOtherid(otherid);
		detail.persist();
		return detail;
	}

	@Transactional("transactionManager")
	public Taccount merge(Taccount taccount) {
		taccount.setMac(taccount.taccountSign());
		Taccount merged = this.entityManager.merge(taccount);
		this.entityManager.flush();
		return merged;
	}

	@Transactional("transactionManager")
	public void persist(Taccount taccount) {
		this.entityManager.persist(taccount);
		this.entityManager.flush();
		Taccount account = findTaccount(taccount.getUserno());
		merge(account);
	}
	
	//修改最后充值时间，最后充值金额，累计充值金额
	public void updateChargeInfo(Taccount taccount, BigDecimal amt) {
		taccount.setLastdepositamt(amt);
		taccount.setLastdeposittime(new Date());
		taccount.setTotaldepositamt(taccount.getTotaldepositamt().add(amt));
		merge(taccount);
	}
	
	public Tsubaccount findTsubaccount(Taccount taccount, SubaccountType type) {
		type = null == type ? SubaccountType.BET : type;
		Tsubaccount tsubaccount = tsubaccountDao.findTsubaccount(taccount.getUserno(),
				type);
		if (null == tsubaccount) {
			tsubaccount = new Tsubaccount();
			switch (type) {
			case BET:
				tsubaccountDao.createTsubaccount(tsubaccount, taccount.getUserno(),
						taccount.getBalance(), taccount.getDrawbalance(),
						taccount.getFreezebalance(), type);
				break;
			case PRIZE:
				tsubaccountDao
						.createTsubaccount(tsubaccount, taccount.getUserno(), BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, type);
				break;
			}
		}
		return tsubaccount;
	}
	
	public Taccount findTaccount(String id, boolean lock) {
		if (id == null || 0 == id.length())
			return null;
		Taccount account = entityManager.find(Taccount.class, id,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return account;
	}

	public Taccount findTaccount(String id) {
		return findTaccount(id, false);
	}
	
    @Transactional("transactionManager")
    public void remove(Taccount taccount) {
        if (this.entityManager.contains(taccount)) {
            this.entityManager.remove(taccount);
        } else {
            Taccount attached = findTaccount(taccount.getUserno());
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
    
    public long countTaccounts() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Taccount o", Long.class).getSingleResult();
    }
    
    public List<Taccount> findAllTaccounts() {
        return entityManager.createQuery("SELECT o FROM Taccount o", Taccount.class).getResultList();
    }
    
    public List<Taccount> findTaccountEntries(int firstResult, int maxResults) {
        return entityManager.createQuery("SELECT o FROM Taccount o", Taccount.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
}
