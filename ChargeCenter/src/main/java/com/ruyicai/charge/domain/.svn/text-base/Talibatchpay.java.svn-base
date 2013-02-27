package com.ruyicai.charge.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.charge.consts.BatchPayState;
import com.ruyicai.charge.consts.Const;
import com.ruyicai.charge.exception.RuyicaiException;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.StringUtil;


@RooJson
@RooJavaBean
@RooToString
@RooEntity(versionField = "", table = "TALIBATCHPAY", identifierField = "id", persistenceUnit = "persistenceUnitMysql", transactionManager = "transactionManagerMysql")
public class Talibatchpay {
	transient private static Logger logger = Logger.getLogger(Talibatchpay.class);
			
	@Id
	@GeneratedValue(generator = Const.IdGeneratorName)
	@GenericGenerator(name = Const.IdGeneratorName, strategy = Const.IdStrategy, //
	parameters = {
			@Parameter(name = Const.Seq_prefix, value = Const.TlotIdPrefix),
			@Parameter(name = Const.Seq_Date, value = "yyyyMMdd"),
			@Parameter(name = Const.Seq_fmtWidth, value = "22"),
			@Parameter(name = TableGenerator.SEGMENT_COLUMN_PARAM, value = "ID"),
			@Parameter(name = TableGenerator.SEGMENT_VALUE_PARAM, value = "TalibatchpayId"),
			@Parameter(name = TableGenerator.VALUE_COLUMN_PARAM, value = "SEQ"),
			@Parameter(name = TableGenerator.TABLE_PARAM, value = "TSEQ"),
			@Parameter(name=TableGenerator.INCREMENT_PARAM,value="1000")})
	@Column(name = "ID")
	private String id;
	
	@Column(name = "DETAILDATA")
	private String detailData;
	
	@Column(name = "BATCHNUM")
	private String batchNum = BigDecimal.ZERO.toString();
	
	@Column(name = "BATCHFEE")
	private String batchFee = BigDecimal.ZERO.toString();
	
	@Column(name = "PAYDATE")
	private Date payDate;
	
	@Column(name = "NOTIFYTIME")
	private Date notifyTime;
	
	@Column(name = "NOTIFYTYPE")
	private String notifyType;
	
	@Column(name = "NOTIFYID")
	private String notifyID;
	
	@Column(name = "SIGNTYPE")
	private String signType;
	
	@Column(name = "SUCCESSDETAILS")
	private String successDetails;
	
	@Column(name = "FAILDETAILS")
	private String failDetails;
	
	@Column(name = "STATE")
	private BigDecimal state;
	
	@Transactional("transactionManagerMysql")
	public static String checkBatchNo(String batchNo, String detailData, String batchNum, String batchFee, Date date) {	
		boolean isCreate = false;
		Talibatchpay talibatchpay = null;
		
		if (StringUtil.isEmpty(batchNo)) {
			isCreate = true;
			logger.info("batchno=" + batchNo + "为空");			
			talibatchpay = new Talibatchpay();			
		} else {			
			talibatchpay = Talibatchpay.findTalibatchpay(batchNo);			
			if (null == talibatchpay) {
				isCreate = false;
				logger.info("batchno=" + batchNo + "不存在");
				throw new RuyicaiException(ErrorCode.BatchPay_batchnoNotExist);
			}			
		}
		talibatchpay.setDetailData(detailData);
		talibatchpay.setBatchNum(batchNum);
		talibatchpay.setBatchFee(batchFee);
		talibatchpay.setPayDate(date);
		talibatchpay.setNotifyTime(date);
		talibatchpay.setState(BatchPayState.processing.value());
		if(isCreate) {
			talibatchpay.persist();
		} else {
			talibatchpay.merge();
		}
				
		return talibatchpay.getId();
	}
	
	@Transactional("transactionManagerMysql")
	public static void modifyTalibatchpay(String batchNo, Date notifyTime, String notifyType, String notifyID, 
			String signType, String successDetails, String failDetails) {	
		Talibatchpay talibatchpay = Talibatchpay.findTalibatchpay(batchNo);
		if (null == talibatchpay) {
			logger.info("batchno=" + batchNo + "不存在");
			throw new RuyicaiException(ErrorCode.BatchPay_batchnoNotExist);
		}
		
		talibatchpay.setNotifyTime(notifyTime);
		talibatchpay.setNotifyType(notifyType);
		talibatchpay.setNotifyID(notifyID);
		talibatchpay.setSignType(signType);
		talibatchpay.setSuccessDetails(successDetails);
		talibatchpay.setFailDetails(failDetails);
		
		BigDecimal state = BatchPayState.processing.value();
		if (!StringUtil.isEmpty(successDetails) && !StringUtil.isEmpty(failDetails)){
			state = BatchPayState.partsuccess.value();
		} else if (!StringUtil.isEmpty(successDetails) && StringUtil.isEmpty(failDetails)){
			state = BatchPayState.ok.value();
		} else if (StringUtil.isEmpty(successDetails) && !StringUtil.isEmpty(failDetails)){
			state = BatchPayState.fail.value();
		}
		talibatchpay.setState(state);
		talibatchpay.merge();
	}
}
