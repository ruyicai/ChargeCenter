package com.ruyicai.charge.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.consts.SubaccountType;
import com.ruyicai.charge.domain.Taccount;
import com.ruyicai.charge.domain.Tsubaccount;

@Service
public class TsubaccountDao {

	private Logger logger = LoggerFactory.getLogger(TsubaccountDao.class);
	
	@PersistenceContext(unitName="persistenceUnit")
	EntityManager entityManager;
	
	@Autowired
	TaccountDao taccountDao;
	
	@Transactional("transactionManager")
    public void persist(Tsubaccount tsubaccount) {
        this.entityManager.persist(tsubaccount);
        this.entityManager.flush();
        Tsubaccount tsub = findTsubaccount(tsubaccount.getId());
        tsub.setMac(tsub.tsubaccountSign());
        merge(tsub);
    }
	
	@Transactional("transactionManager")
    public Tsubaccount merge(Tsubaccount tsubaccount) {
		tsubaccount.setMac(tsubaccount.tsubaccountSign());
        Tsubaccount merged = this.entityManager.merge(tsubaccount);
        this.entityManager.flush();
        return merged;
    }
	
	public Tsubaccount findTsubaccount(BigDecimal id) {
		return entityManager.find(Tsubaccount.class, id);
	}
	
	public Tsubaccount findTsubaccount(String userno, SubaccountType type) {
		TypedQuery<Tsubaccount> query = entityManager
		.createQuery(
				"select o from Tsubaccount o where o.userno=? and o.type=?",
				Tsubaccount.class).setParameter(1, userno).setParameter(2, type.value());
		List<Tsubaccount> results = query.getResultList();
		if(results.isEmpty()) {
			return null;
		}
		return results.get(0);
	}
	
	public void createTsubaccount(Tsubaccount tsubaccount, String userno, BigDecimal balance, BigDecimal drawbalance, BigDecimal freezebalance, SubaccountType type) {
		tsubaccount.setUserno(userno);
		tsubaccount.setBalance(balance);
		tsubaccount.setDrawbalance(drawbalance);
		tsubaccount.setFreezebalance(freezebalance);
		tsubaccount.setType(type.value());
		persist(tsubaccount);
		logger.info("创建出的子账户, tsubaccount: " + tsubaccount);
	}
	
	public void freeze(Tsubaccount tsubaccount, BigDecimal freezeAmount) {
		logger.info("冻结前子账户, tsubaccount: " + tsubaccount);
		tsubaccount.setFreezebalance(tsubaccount.getFreezebalance().add(freezeAmount));
		merge(tsubaccount);
		logger.info("冻结后子账户, tsubaccount: " + tsubaccount);
	}
	
	public void unfreeze(Tsubaccount tsubaccount, BigDecimal freezeAmount) {
		logger.info("解冻前子账户, tsubaccount: " + tsubaccount);
		tsubaccount.setFreezebalance(tsubaccount.getFreezebalance().add(
				freezeAmount.negate()));
		merge(tsubaccount);
		logger.info("解冻后子账户, tsubaccount: " + tsubaccount);
	}
	
	public void addMoney(Tsubaccount tsubaccount, BigDecimal amount, BigDecimal drawAmount) {
		logger.info("加款前子账户, tsubaccount: " + tsubaccount);
		tsubaccount.setBalance(tsubaccount.getBalance().add(amount.abs()));
		if (new BigDecimal(0).compareTo(drawAmount) < 0) {
			tsubaccount.setDrawbalance(tsubaccount.getDrawbalance().add(drawAmount.abs()));
		}
		merge(tsubaccount);
		logger.info("加款后子账户, tsubaccount: " + tsubaccount);
	}
	
	public void deductMoney(Taccount taccount, SubaccountType subaccountType, BigDecimal amount, BigDecimal drawAmount) {
		if(null == subaccountType) {
			defDeductMoney(taccount, amount, drawAmount);
			return;
		}
		Tsubaccount tsubaccount = taccountDao.findTsubaccount(taccount, subaccountType);
		logger.info("扣款前子账户, tsubaccount: " + tsubaccount);
		tsubaccount.setBalance(tsubaccount.getBalance().subtract(amount.abs()));
		tsubaccount.setDrawbalance(tsubaccount.getDrawbalance().subtract(drawAmount.abs()));
		if (tsubaccount.getBalance().compareTo(tsubaccount.getDrawbalance()) < 0) {
			drawAmount=tsubaccount.getDrawbalance().add(tsubaccount.getBalance().negate());
			tsubaccount.setDrawbalance(tsubaccount.getBalance());
		}
		if(tsubaccount.getDrawbalance().compareTo(BigDecimal.ZERO) < 0) {
			tsubaccount.setDrawbalance(BigDecimal.ZERO);
		}
		merge(tsubaccount);
		logger.info("扣款后子账户, tsubaccount: " + tsubaccount);
	}
	
	private void defDeductMoney(Taccount taccount, BigDecimal amount, BigDecimal drawAmount) {
		Tsubaccount tsubaccountBet = taccountDao.findTsubaccount(taccount, SubaccountType.BET);
		BigDecimal balance = BigDecimal.ZERO;
		BigDecimal drawBalance = BigDecimal.ZERO;
		if(tsubaccountBet.getBalance().compareTo(amount) < 0) {
			balance = amount.subtract(tsubaccountBet.getBalance());
			amount = tsubaccountBet.getBalance();
		}
		if(tsubaccountBet.getDrawbalance().compareTo(drawAmount) < 0) {
			drawBalance = drawAmount.subtract(tsubaccountBet.getDrawbalance());
			drawAmount = tsubaccountBet.getDrawbalance();
		}
		Tsubaccount tsubaccountPrize = taccountDao.findTsubaccount(taccount, SubaccountType.PRIZE);
		if(tsubaccountPrize.getBalance().compareTo(balance) < 0) {
			logger.info("投注账户金额不够扣款, 从奖金账户扣款依然不够");
			//throw new RuyicaiException(ErrorCode.UserRes_DJYC);
		}
		if(tsubaccountPrize.getDrawbalance().compareTo(drawBalance) < 0) {
			logger.info("投注账户可提现金额不够扣可提现金额, 从奖金账户扣可提现金额依然不够");
			//throw new RuyicaiException(ErrorCode.UserRes_DJYC);
		}
		deductMoney(taccount, SubaccountType.BET, amount, drawAmount);
		deductMoney(taccount, SubaccountType.PRIZE, balance, drawBalance);
	}
}
