package com.ruyicai.charge.dna.v2.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.dna.common.OrderState;
import com.ruyicai.charge.dna.v2.common.DateFormatter;
import com.ruyicai.charge.dna.v2.common.Strings;
import com.ruyicai.charge.dna.v2.thirdpart.PosMessage;
import com.ruyicai.charge.dna.v2.thirdpart.TransactionClient;
import com.ruyicai.charge.dna.v2.thirdpart.TransactionType;
import com.ruyicai.charge.domain.Dnapay;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;

/**
 * 银联语音支付，第三方商户交易支付客户端例子．
 */
@Service("newwaydna")
public final class DNATransactionClientService {

	@Autowired
	ChargeconfigService chargeconfigService;
	private static final Logger LOGGER = Logger.getLogger(DNATransactionClientService.class);

	private static final String DEFAULT_PARAM_VALUE = StringUtils.EMPTY;
	private static final String ORDER_DESC = "博雅彩账户充值";
	
	private static ResourceBundle systemsetting = ResourceBundle.getBundle("systemsetting");

	// 加载易联支付证书
	static{
		System.setProperty("javax.net.ssl.trustStore", systemsetting.getString("javax.net.ssl.trustStore"));
		System.setProperty("javax.net.ssl.trustStorePassword",systemsetting.getString("javax.net.ssl.trustStorePassword"));
	}
	/**
	 * DNA支付
	 * 
	 * @param payParam
	 * 
	 * @return
	 */
	public Map payWhitelistToDna(PayWhitelistToDnaParameter payParam) {
		Map result = new HashMap();
		try {
			// 构造TransactionClient，连接方式设置为CA
			TransactionClient tm = createRSATransactionClient();
			// 第一次身份验证
			PosMessage pm = tm.accountQuery(getSerialNO(), payParam.getAccountNumber(),
					"||||||||||", Strings.random(24));

			// 订单信息，金额，描述，备注，订单号。
			String transactionId = "";
			if (Float.parseFloat(payParam.getAmount()) > Float.parseFloat(pm.getAmount())) {
				LOGGER.info("该银行卡超过当日交易金额上限，请明天再交易\r\n");
			}
			// 白名单用户开始支付流程
			else if (pm.getRespCode().equals("0000")) {
				transactionId = saveTransactionRecord(payParam);
				// 白名单支付 transdata第一位需要传用户名
				pm = pay(tm, payParam, payParam.getUserName() + "||||||||||", transactionId);
			}
			// T438：系统交易过的新卡，需要提供持卡人信息；；T437：系统未交易过的新卡，需要提供持卡人信息；T404系统不支持的卡
			else if (pm.needSecondAcountQuery()) {
				String transData = getTransData(payParam, pm);
				LOGGER.info("transData=" + transData);
				// 为交易过的新卡第二次身份验证
				tm.accountQuery(getSerialNO(), payParam.getAccountNumber(), transData,
						Strings.random(24));
				transactionId = saveTransactionRecord(payParam);
				pm = pay(tm, payParam, transData, transactionId);
			}
			// 黑名单T432退出支付流程
			else if (pm.getRespCode().equals("T432")) {
				LOGGER.info("银行卡被列入黑名单，拒绝交易；\r\n");
			} else if (pm.getRespCode().equals("T436")) {
				LOGGER.info("该银行卡交易时间受限，请明天八点以后再交易\r\n");
			}
			LOGGER.info("DNA生产服务器处理完成， 返回代码=" + pm.getRespCode() + "，结果=" + pm.getRemark());
			result.put("pm", pm);
			result.put("transactionId", transactionId);
			result.put("errorCode", ErrorCode.OK.value);
			return result;
		} catch (Exception e) {
			LOGGER.error("DNA支付的出现错误:", e);
			result.put("pm", new PosMessage());
			result.put("errorCode", ErrorCode.ERROR.value);
			return result;
		}
	}

	private PosMessage pay(TransactionClient tm, PayWhitelistToDnaParameter payParam,
			String transData, String transactionId) throws Exception {
		// 交易密钥, 随机生成, 用于加密解密报文
		String encryptKey = Strings.random(24);// RSA密钥，区分新旧CA方式，新CA方式就是RSA
		// 交易结果CA证书异步返回测试地址， 请修改为商户服务器提供的地址, 类型＋地址, 请参照<<银联语音支付平台接口规范>>
		String returnUrl = chargeconfigService.getChargeconfig("DNAV2ReturnUrl");
		// 是否即时支付,一线通选非即时支付
		boolean payNow = true;
		String merOrderNo = "12" + transactionId;
		PosMessage pm = tm.pay(getSerialNO(), payParam.getAccountNumber(), DEFAULT_PARAM_VALUE,
				payParam.getAmount(), merOrderNo, "reference", ORDER_DESC, DEFAULT_PARAM_VALUE, payNow,
				returnUrl, transData, encryptKey);
		return pm;
	}

	private StringBuffer getSaveTransactionParam(PayWhitelistToDnaParameter payParam) {
		String bankaccount = "0";// 银行账户
		StringBuffer param = new StringBuffer();
		param.append("bankid=").append(payParam.getBankId()).append("&paytype=")
				.append(payParam.getCardType()).append("&accesstype=")
				.append(payParam.getAccesstype()).append("&amt=").append(payParam.getAmt())
				.append("&bankaccount=").append(bankaccount).append("&userno=")
				.append(payParam.getUserno()).append("&type=").append(payParam.getType())
				.append("&channel=").append(payParam.getChannel()).append("&subchannel=")
				.append(payParam.getSubchannel()).append("&ladderpresentflag=")
				.append(payParam.getLadderpresentflag()).append("&continuebettype=")
				.append(payParam.getContinuebettype()).append("&orderid=")
				.append(payParam.getOrderid());
		return param;
	}

	private String getTransData(PayWhitelistToDnaParameter payParam, PosMessage pm) {
		String idCardType = "01"; // 银行开户证件类型，　01:身份证，02:护照，03:军人证，04:台胞证
		// 本系统固定为身份证
		String ipAddress = payParam.getIp().equals("") ? "127.0.0.1" : payParam.getIp(); // 持卡人登录IP地址．
		String idCardAddress = payParam.getDocumentAddress().equals("") ? "身份证地址" : payParam
				.getDocumentAddress(); // 身份证地址,截取至街道
		String bankPhoneNumber = payParam.getUserPhoneNumber();
		// 根据第一次查卡返回Reference填写业务数据transData进行第二次查卡
		String[] reference = pm.getReference().trim().split("\\|");
		String newuserName = (reference.length > 0 && reference[0].equals("1")) ? payParam
				.getUserName() : "";
		String documentNumber = (reference.length > 1 && reference[1].equals("1")) ? payParam
				.getDocumentAddress() : "";
		String accountAddress = (reference.length > 2 && reference[2].equals("1")) ? payParam
				.getAccountAddress() : "";
		idCardType = (reference.length > 3 && reference[3].equals("1")) ? idCardType : "";
		String secondUserName = (reference.length > 4 && reference[4].equals("1")) ? payParam
				.getUserName() : "";
		ipAddress = (reference.length > 5 && reference[5].equals("1")) ? ipAddress : "";
		idCardAddress = (reference.length > 6 && reference[6].equals("1")) ? idCardAddress : "";
		String productPhoneNumber = DEFAULT_PARAM_VALUE; // 无需填写
		String productAddress = DEFAULT_PARAM_VALUE; // 无需填写
		bankPhoneNumber = (reference.length > 9 && reference[9].equals("1")) ? bankPhoneNumber : "";
		// 额外交易数据，Apple appID 手机商城商户确定需要传ID,和易联确认此项数据我们无需填写
		String extTransData = DEFAULT_PARAM_VALUE; // 无需填写

		String transData = newuserName + "|" + documentNumber + "|" + accountAddress + "|"
				+ idCardType + "|" + secondUserName + "|" + ipAddress + "|" + idCardAddress + "|"
				+ productPhoneNumber + "|" + productAddress + "|" + bankPhoneNumber + "|"
				+ extTransData;
		return transData;
	}

	public Map<String, String> orderQuery(String transactionId) throws InterruptedException {
		Thread.sleep(1000);
		Map<String, String> map = new HashMap<String, String>();
		String errorCode = ErrorCode.OK.value;
		PosMessage pm = null;
		TransactionClient tm = null;

		try {
			tm = createRSATransactionClient();
			String acqSsn = getSerialNO();
			boolean isPay = false;
			pm = tm.orderQuery(acqSsn, "", transactionId, isPay, Strings.random(24));
		} catch (Exception e) {
			errorCode = ErrorCode.ERROR.value;
			e.printStackTrace();
			LOGGER.error("DNA订单查询出错：", e);
		}
		LOGGER.info("DNA订单查询，pm=" + pm.toString());

		map.put("errorCode", errorCode);
		map.put("ProcCode", pm.getProcCode());
		map.put("AccountNum", pm.getAccountNum());
		map.put("ProcessCode", pm.getProcessCode());
		map.put("Amount", pm.getAmount().isEmpty() ? "" : new BigDecimal(pm.getAmount()).toString());
		map.put("AcqSsn", pm.getAcqSsn());
		map.put("Ltime", pm.getLtime());
		map.put("Ldate", pm.getLdate());
		map.put("SettleDate", pm.getSettleDate());
		map.put("UpsNo", pm.getUpsNo());
		map.put("TsNo", pm.getTsNo());
		map.put("Reference", pm.getReference());
		map.put("RespCode", pm.getRespCode());
		map.put("Remark", pm.getRemark());
		map.put("TerminalNo", pm.getTerminalNo());
		map.put("MerchantNo", pm.getMerchantNo());
		map.put("OrderNo", pm.getOrderNo().substring(2, pm.getOrderNo().length() - 1));
		map.put("OrderState", OrderState.getMemo(pm.getOrderState()));
		map.put("ValidTime", pm.getValidTime());
		map.put("OrderType", pm.getOrderType());
		map.put("Mac", pm.getMac());
		return map;
	}

	private TransactionClient createRSATransactionClient() {
		String nameSpace = chargeconfigService.getChargeconfig("DNANameSpace");
		String rsapayUrl = chargeconfigService.getChargeconfig("DNARSAPayAddress");
		TransactionClient tm = new TransactionClient(rsapayUrl, nameSpace);
		tm.setTransactionType(TransactionType.CA);
		tm.setServerCert(chargeconfigService.getChargeconfig("GDYILIAN_CERT_PUB_64"));
		tm.setMerchantNo("02" + chargeconfigService.getChargeconfig("DNAMerchantNo")); // 商户类型+商户编号
		tm.setMerchantPassWD(chargeconfigService.getChargeconfig("DNAMerchantPw")); // 商户Mac密钥
		tm.setTerminalNo(chargeconfigService.getChargeconfig("DNATerminalNo")); // 商户终端编号
		return tm;
	}

	private static void createDnapay(String transactionid, String userno, String mobileid,
			String amt) {
		try {
			Dnapay dnapay = Dnapay.createDnapay(transactionid, userno, mobileid, amt);
			LOGGER.info("createDnapay:" + dnapay.toString());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("createDnapay error:", e);
		}
	}

	private String saveTransactionRecord(PayWhitelistToDnaParameter payParam) throws Exception {
		StringBuffer param = getSaveTransactionParam(payParam);
		String url = chargeconfigService.getChargeconfig("lotteryReqUrl");
		LOGGER.info("DNA银行卡充值->生成交易记录：url=" + url + " ,param=" + param.toString());
		String result = HttpRequest.doPostRequest(url, param.toString());
		LOGGER.info("返回 return=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
		String errorCode = mapResult.containsKey("errorCode") ? mapResult.get("errorCode")
				.toString() : "";
		if (!"0".equals(errorCode)) {
			throw new Exception("生成交易记录出现错误 errorCode=" + errorCode);
		}

		String transactionId = mapResult.get("value").toString();
		LOGGER.info("充值受理成功，平台生成交易记录成功。");
		createDnapay(transactionId, payParam.getUserno(), payParam.getUserPhoneNumber(),
				payParam.getAmt());
		return transactionId;
	}

	/**
	 * 根据当前时间生成模拟系统跟踪号
	 * 
	 * @return
	 */
	public static String getSerialNO() {
		return DateFormatter.HHmmss(new java.util.Date());
	}

	/**
	 * 根据当前时间生成模拟商户订单编号
	 * 
	 * @return
	 */
	public static String getMerchantOrderNO() {
		return DateFormatter.yyyyMMddHHmmss(new java.util.Date());
	}
}
