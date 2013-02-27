package com.ruyicai.charge.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

public class Weight { 
	private Logger logger = Logger.getLogger(Weight.class);

	private int currentWeight = 0;
	private int maxWeight = 0;
	private int weightgcd = 0;
	private int chargeTypeNum = 0;
	private int currentChargeType = -1;
	List<ChargeType> chargeTypes = null;

	private ReadWriteLock lock = new ReentrantReadWriteLock();

	private static class WeightSingletonHolder {
		private static final Weight INSTANCE = new Weight();		
	}

	private Weight() {	
		init(null, null);
	}

	public static final Weight getInstance() {
		return WeightSingletonHolder.INSTANCE;
	}

	public void init(BigDecimal nineteenpayWeight, BigDecimal shenzhoufuWeight) {		
		resetWRR(ChargeType.findAllChargeType(nineteenpayWeight, shenzhoufuWeight));
	}

	protected void resetWRR(List<ChargeType> chargeTypes) {
		lock.writeLock().lock();
		try {
			this.chargeTypes = chargeTypes;
			maxWeight = 0;
			weightgcd = 0;
			chargeTypeNum = 0;
			currentChargeType = -1;
			currentWeight = 0;
			
			for (ChargeType chargeType : chargeTypes) {
				chargeTypeNum++;
				int weight = chargeType.getWeight().intValue();				
				if (weight == 0) {
					continue;
				}
				if (weightgcd == 0) {
					weightgcd = weight;
				} else {
					weightgcd = GCD.getGCD(weight, weightgcd);
				}
				if (maxWeight < weight) {
					maxWeight = Math.max(maxWeight, weight);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("resetWRR error:", e);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public String getNextChargeType() {
		lock.readLock().lock();

		try {
			int index = 0;
			while (true) {
				index += 1;
				if (index >= 1000) {
					return "";
				}

				currentChargeType = (currentChargeType + 1) % chargeTypeNum;
				if (currentChargeType == 0) {
					currentWeight = currentWeight - weightgcd;
					if (currentWeight < 0) {
						currentWeight = maxWeight;
						if (currentWeight == 0) {
							return "";
						}
					}
				}
				ChargeType chargeType = chargeTypes.get(currentChargeType);

				if (chargeType.getWeight().intValue() > 0 && chargeType.getWeight().intValue() >= currentWeight) {
					return chargeType.getId();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getNextChargeType error:", e);
		} finally {
			lock.readLock().unlock();
		}
		
		return "";
	}	
	
}
