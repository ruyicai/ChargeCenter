package com.ruyicai.charge.dna.v2.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * DNA支付
	 * 
	 * @param userPhoneNumber 持卡人的手机号码
	 * @param userCardNumber 持卡人的卡号
	 * @param amount 充值金额
	 * @param userName 开户人姓名
	 * @param documentNumber 开户证件号码
	 * @param accountAddress 开户银行所在地
	 * @param ip 支付的IP地址
	 * @param documentAddress 持卡人身份证地址
	 * @param payType 支付类型 0 为白名单支付 1 为灰名单支付
	 * @param userno 客户编号
	 * @param accesstype 接入参数
	 * @param cardType 卡别标识.卡序列标识
	 * @param bankId
	 * @param type
	 * @param amt
	 * @param channel
	 * @param subchannel
	 * @param ladderpresentflag
	 * @param continuebettype
	 * @param orderid
	 * @return
	 */
	public Map payWhitelistToDna(String userPhoneNumber, String userCardNumber, String amount,
			String userName, String documentNumber, String accountAddress, String ip,
			String documentAddress, int payType, String userno, String accesstype, String cardType,
			String bankId, String type, String amt, String channel, String subchannel,
			String ladderpresentflag, String continuebettype, String orderid) {
		Map result = new HashMap();
		try {
			// 构造TransactionClient，连接方式设置为CA
			TransactionClient tm = createRSATransactionClient();

			String pin = ""; // 和易联确认此项数据我们无需填写
			String accountNum = "14" + userPhoneNumber + "|" + userCardNumber; // 借记卡
			String idCardType = "01"; // 银行开户证件类型，　01:身份证，02:护照，03:军人证，04:台胞证
										// 本系统固定为身份证
			String ipAddress = ip.equals("") ? "127.0.0.1" : ip; // 持卡人登录IP地址．
			String idCardAddress = documentAddress.equals("") ? "身份证地址" : documentAddress; // 身份证地址,截取至街道,
																							// 特殊风控.
			String productPhoneNumber = ""; // 和易联确认此项数据我们无需填写
			String productAddress = ""; // 商品销售地，省市以","号分割，团购商户需填写; 无需填写
			String bankPhoneNumber = "13423105530";
			String extTransData = ""; // 额外交易数据，　Apple appID 手机商城商户确定需要传ID
										// ,和易联确认此项数据我们无需填写

			PosMessage pm = null;

			// 第一次身份验证
			pm = tm.accountQuery(getSerialNO(), accountNum, "||||||||||", Strings.random(24));

			// 根据第一次查卡返回Reference填写业务数据transData进行第二次查卡
			String[] reference = pm.getReference().trim().split("\\|");
			String newuserName = (reference.length > 0 && reference[0].equals("1")) ? userName : "";
			documentNumber = (reference.length > 1 && reference[1].equals("1")) ? documentNumber
					: "";
			accountAddress = (reference.length > 2 && reference[2].equals("1")) ? accountAddress
					: "";
			idCardType = (reference.length > 3 && reference[3].equals("1")) ? idCardType : "";
			String secondUserName = (reference.length > 4 && reference[4].equals("1")) ? userName
					: "";
			ipAddress = (reference.length > 5 && reference[5].equals("1")) ? ipAddress : "";
			idCardAddress = (reference.length > 6 && reference[6].equals("1")) ? idCardAddress : "";
			productPhoneNumber = (reference.length > 7 && reference[7].equals("1")) ? productPhoneNumber
					: "";
			productAddress = (reference.length > 8 && reference[8].equals("1")) ? productAddress
					: "";
			bankPhoneNumber = (reference.length > 9 && reference[9].equals("1")) ? bankPhoneNumber
					: "";
			extTransData = (reference.length > 10 && reference[10].equals("1")) ? extTransData : "";// 要求传ID的手机商城行业商户，不需要看reference填写

			String transData = newuserName + "|" + documentNumber + "|" + accountAddress + "|"
					+ idCardType + "|" + secondUserName + "|" + ipAddress + "|" + idCardAddress
					+ "|" + productPhoneNumber + "|" + productAddress + "|" + bankPhoneNumber + "|"
					+ extTransData;
			LOGGER.info("transData=" + transData);

			// 订单信息，金额，描述，备注，订单号。
			String orderAmount = (Float.parseFloat(amt) / 100) + "";
			String orderDescription = "博雅彩账户充值";
			String orderRemark = "";
			// TODO transactionId订单号需要赋值 目前为null
			String transactionId = "";
			String merOrderNo = "12" + getMerchantOrderNO(); // TODO得修改回去
			// 交易密钥, 随机生成, 用于加密解密报文
			String encryptKey = Strings.random(24);// RSA密钥，区分新旧CA方式，新CA方式就是RSA
			// 交易结果CA证书异步返回测试地址， 请修改为商户服务器提供的地址, 类型＋地址, 请参照<<银联语音支付平台接口规范>>
			String returnUrl = chargeconfigService.getChargeconfig("DNAV2ReturnUrl");
			// 是否即时支付,一线通选非即时支付
			boolean payNow = true;
			String bankaccount = "0";// 银行账户
			StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankId).append("&paytype=").append(cardType)
					.append("&accesstype=").append(accesstype).append("&amt=").append(amt)
					.append("&bankaccount=").append(bankaccount).append("&userno=").append(userno)
					.append("&type=").append(type).append("&channel=").append(channel)
					.append("&subchannel=").append(subchannel).append("&ladderpresentflag=")
					.append(ladderpresentflag).append("&continuebettype=").append(continuebettype)
					.append("&orderid=").append(orderid);
			// 白名单用户开始支付流程
			if (pm.getRespCode().equals("0000")) {
				Map saveResult = saveTransactionRecord(param.toString());
				String errorCode = saveResult.containsKey("errorCode") ? saveResult
						.get("errorCode").toString() : "";
				if (!"0".equals(errorCode)) {
					LOGGER.info("生成交易记录出现错误 errorCode=" + errorCode);
					Map result2 = new HashMap();
					result2.put("pm", new PosMessage());
					result2.put("errorCode", errorCode);
					return result2;
				}

				transactionId = saveResult.get("value").toString();
				LOGGER.info("充值受理成功，平台生成交易记录成功。");
				createDnapay(transactionId, userno, userPhoneNumber, amt);
				// 白名单支付 transdata第一位需要传用户名
				pm = tm.pay(getSerialNO(), accountNum, pin, orderAmount, merOrderNo, "reference",
						orderDescription, orderRemark, payNow, returnUrl, userName + "||||||||||",
						encryptKey);
				orderQuery(transactionId);
			}
			// T438：系统交易过的新卡，需要提供持卡人信息；；T437：系统未交易过的新卡，需要提供持卡人信息；T404系统不支持的卡
			else if (pm.getRespCode().equals("T438") || pm.getRespCode().equals("T437")
					|| pm.getRespCode().equals("T404")) {
				// 为交易过的新卡第二次身份验证
				pm = tm.accountQuery(getSerialNO(), accountNum, transData, Strings.random(24));
				Map saveResult = saveTransactionRecord(param.toString());
				String errorCode = saveResult.containsKey("errorCode") ? saveResult
						.get("errorCode").toString() : "";
				if (!"0".equals(errorCode)) {
					LOGGER.info("生成交易记录出现错误 errorCode=" + errorCode);
					result.put("pm", new PosMessage());
					result.put("errorCode", errorCode);
					return result;
				}

				transactionId = saveResult.get("value").toString();
				LOGGER.info("充值受理成功，平台生成交易记录成功。");
				createDnapay(transactionId, userno, userPhoneNumber, amt);
				pm = tm.pay(getSerialNO(), accountNum, pin, orderAmount, merOrderNo, "reference",
						orderDescription, orderRemark, payNow, returnUrl, transData, encryptKey);
				orderQuery(transactionId);
			}
			// 黑名单T432退出支付流程
			else if (pm.getRespCode().equals("T432")) {
				LOGGER.info("银行卡被列入黑名单，拒绝交易；\r\n");
			} else if (Float.parseFloat(orderAmount) > Float.parseFloat(pm.getAmount())) {
				LOGGER.info("该银行卡超过当日交易金额上限，请明天再交易\r\n");
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
			result.put("errorCode", ErrorCode.ERROR);
			return result;
		}
	}

	public Map<String, String> orderQuery(String transactionId) {
		Map<String, String> map = new HashMap<String, String>();
		String errorCode = ErrorCode.OK.value;
		PosMessage pm = null;
		TransactionClient tm = null;

		try {
			tm = createRSATransactionClient();
			String acqSsn = getSerialNO();
			String orderNo = "12" + transactionId + "|";
			boolean isPay = false;
			pm = tm.orderQuery(acqSsn, "", orderNo, isPay, Strings.random(24));
		} catch (Exception e) {
			errorCode = ErrorCode.ERROR.value;
			e.printStackTrace();
			LOGGER.error("DNA订单查询出错：", e);
		}
		LOGGER.info("DNA订单查询，pm=" + pm.toString());

		map.put("errorCode", errorCode);
		map.put("ProcCode", pm.getProcCode());
		map.put("AccountNum", pm.getAccountNum().substring(2));
		map.put("ProcessCode", pm.getProcessCode());
		map.put("Amount", new BigDecimal(pm.getAmount()).toString());
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
		tm.setMerchantNo("02" + "202020000040"); // 商户类型+商户编号
		tm.setMerchantPassWD("123456"); // 商户Mac密钥
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

	private Map<String, Object> saveTransactionRecord(String param) throws IOException {
		String url = chargeconfigService.getChargeconfig("lotteryReqUrl");
		LOGGER.info("DNA银行卡充值->生成交易记录：url=" + url + " ,param=" + param.toString());
		String result = HttpRequest.doPostRequest(url, param.toString());
		LOGGER.info("返回 return=" + result);
		Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);

		return mapResult;
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
