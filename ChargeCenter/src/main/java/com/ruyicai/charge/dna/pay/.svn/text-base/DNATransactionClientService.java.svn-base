package com.ruyicai.charge.dna.pay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.charge.dna.common.DateFormatter;
import com.ruyicai.charge.dna.common.OrderState;
import com.ruyicai.charge.dna.thirdpart.PosMessage;
import com.ruyicai.charge.dna.thirdpart.TransactionClient;
import com.ruyicai.charge.domain.Dnapay;
import com.ruyicai.charge.service.ChargeconfigService;
import com.ruyicai.charge.util.ConfigUtil;
import com.ruyicai.charge.util.ErrorCode;
import com.ruyicai.charge.util.HttpRequest;
import com.ruyicai.charge.util.JsonUtil;

/**
 * 第三方商户交易支付客户端
 */
@Service
public final class DNATransactionClientService {

	private static String orderNO = "";
	private static final Logger logger = Logger.getLogger(DNATransactionClientService.class);
	
	public static int  WHITELIST_PAY_TYPE = 0 ; //白名单充值
	public static int  GREYLISTING_PAY_TYPE = 1 ; //灰名单充值
	
	private static ResourceBundle rbint = ResourceBundle.getBundle("charge"); 
	
	@Autowired 
	ChargeconfigService chargeconfigService;
    
//    /**
//     * 测试代码入口
//     * @param args
//     * @throws Exceptions
//     */
//    public static void main(String[] args) throws Exception {
//
//        logger.info("..........................测试开始.........................................");
//      //持卡人信息
//        String phoneNumber = "13510609995";
//        String cardNumber = "6226379118774440009";
//        
//        //订单信息
//        String orderAmount = "1.00";
//
//        
//        logger.info("输入持卡人手机号Enter继续:\r\n");
//        phoneNumber = readLine();
//        logger.info("输入持卡人银行卡号码 Enter继续:\r\n");
//        cardNumber = readLine();
//        logger.info("输入充值金额Enter继续:\r\n");
//        orderAmount = readLine();
//        PosMessage pm = payWhitelistToDna(phoneNumber,cardNumber,orderAmount);
//        while(true){
//
//            
//            if(pm!=null){
//	            if(Float.parseFloat(orderAmount) > Float.parseFloat(pm.getAmount())){
//	            	logger.info("充值失败！！该银行卡余额不足或已超过当日交易金额上限!");
//	            	logger.info("重新输入充值金额Enter继续:\r\n");
//	                orderAmount = readLine();
//	                pm = payWhitelistToDna(phoneNumber,cardNumber,orderAmount);
//	            }else if(pm.getRespCode().equals("0000")){
//	            	logger.info("充值成功 ；充值金额："+pm.getAmount()+"元；手机号|卡号："+pm.getAccountNum());
//	            	break;
//	            }else if(pm.getRespCode().equals("T438") || pm.getRespCode().equals("T437")){
//	            	logger.info("充值失败！！您的卡是第一次进行手机充值，需要填写更详细的信息!");
//	            	String userName = "鞠牧" ; String documentNumber="211021198805030011" ; String accountAddress="广东省深圳市"; String ip="127.0.0.1" ; String documentAddress="身份证地址";
//	            	pm = payWhitelistToDna(phoneNumber,cardNumber,orderAmount,"",userName,documentNumber,accountAddress,ip,accountAddress,GREYLISTING_PAY_TYPE);
//	            	break;
//	            }else if (pm.getRespCode().equals("T436")) {
//	                logger.info(pm.getRemark());
//	                break;
//	            }
//            }
//        }
//
//    }
//    /**
//     * 
//     * DNA白名单支付的重载方法 简便书写  详细逻辑 见
//     * PosMessage payWhitelistToDna(String userPhoneNumber , String userCardNumber,String amount,
//     * String userName, String documentNumber , String accountAddress, String ip , String documentAddress ,int payType)
//     * @param userPhoneNumber 持卡人的手机号码
//     * @param userCardNumber 持卡人的 卡号
//     * @param amount 充值金额
//     */
//    public static PosMessage payWhitelistToDna(String phoneNumber , String cardNumber,String orderAmount){
//    	return payWhitelistToDna(phoneNumber,cardNumber,orderAmount,"","","","","","" ,WHITELIST_PAY_TYPE);
//    }
    /**
     * DNA白名单支付：：
     * @param userPhoneNumber 持卡人的手机号码
     * @param userCardNumber 持卡人的 卡号
     * @param amount 充值金额
     * @param orderId
     * 
     * @param 灰名单需要传入的参数：：（系统交易过的新卡和系统未交易过的新卡）
     * @param userName  开户人姓名
     * @param documentNumber 开户证件号码
     * @param accountAddress 开户银行所在地
     * @param ip 支付的IP地址
     * @param documentAddress 持卡人身份证地址
     * @param 
     * @param payType 支付类型 0 为白名单支付 1 为灰名单支付  
     * @param 返回的 PosMessage 对象中 需要判断 金额是否超额 Float.parseFloat(Amount) > Float.parseFloat(pm.getAmount())  
     * 如果充值金额 没有超出银行卡当日限额或者余额  再判断返回码 RespCode
     * @param RespCode: 
     * 0000 为白名单用户 进行直接支付 无需再做其他操作；
     * T438 和 T437为 系统交易过的新卡和系统未交易过的新卡 需要更详细的用户信息（灰名单需要传入的参数）；
     * T436 该银行卡交易时间受限，请明天八点以后再交易；
     * T432 银行卡被列入黑名单，拒绝交易；
     * 其他 系统不支持的银行卡。
     * @throws Exceptions
     */
	//static
    public Map payWhitelistToDna(String userPhoneNumber , String userCardNumber,String amount,
    		String userName, String documentNumber , String accountAddress, String ip, String documentAddress, int payType, 
    		String userno, String accesstype, String cardType, String bankId, String type, String amt, String channel, 
    		String subchannel, String ladderpresentflag, String continuebettype, String orderid) {
        TransactionClient tm = null;
        Map map = new HashMap();
        String errorCode = ErrorCode.OK.value;
        String transactionId = null;
        try {
            //设置HTTPS连接参数
            // 测试环境，采用WebService连接模式
            //tm = new TransactionClient(rbint.getString("DNAWebServiceAddress"), rbint.getString("DNAWebServiceName"));
        	
        	String merid = chargeconfigService.getChargeconfig("DNAMerchantNo");
        	String requrl = chargeconfigService.getChargeconfig("DNAWebServiceAddress");
        	String bgreturl = chargeconfigService.getChargeconfig("DNAReturnUrl");
            tm = new TransactionClient(requrl, chargeconfigService.getChargeconfig("DNAWebServiceName"));//rbint.getString("DNAWebServiceName")

            //System.setProperty("javax.net.ssl.trustStore", rbint.getString("DnaPayStoreFile"));//加载证书
            //System.setProperty("javax.net.ssl.trustStorePassword", rbint.getString("DnaPayStorePassword"));

            //是否启用CA证书支持
            tm.setCAEnabled(chargeconfigService.getChargeconfig("DnaPayStorePassword").equals("yes")?true:false);//rbint.getString("DnaPayStorePassword")


            //商户信息
            //String merchantNo = rbint.getString("DNAMerNo") + rbint.getString("DNAMerchantNo");//02(商户订单编号，网页自助下单（WEB）)测试商户编号
            String merchantNo = chargeconfigService.getChargeconfig("DNAMerNo") + merid;//02(商户订单编号，网页自助下单（WEB）)测试商户编号   //rbint.getString("DNAMerNo")
            String merchantPW = chargeconfigService.getChargeconfig("DNAMerchantPw");  //测试商户密钥 //rbint.getString("DNAMerchantPw")
            String terminalNo = chargeconfigService.getChargeconfig("DNATerminalNo");	//测试商户终端编号 //rbint.getString("DNATerminalNo")
            //持卡人信息
            String phoneNumber = userPhoneNumber;
            String cardNumber = userCardNumber;           
            
            //1.开户姓名+“|”+2.开户证件号码+“|”+3.开户银行所在省市+“|”+4.开户证件号码类型（参照7.2）+“|”+5.受益人姓名（如乘机人姓名，本人消费时与开户姓名一致）+“|”+6.持卡人IP地址+ “|”+7.开户证件街道地址(不含门牌号)
            String transData = userName + "|" + documentNumber + "|" + accountAddress + "|01|" + userName + "|" + (ip.equals("") ? "127.0.0.1" : ip) + "|"
					+ (documentAddress.equals("") ? "身份证地址" : documentAddress);		
			logger.info("transData=" + transData);
            //订单信息
            String orderAmount = amount;//充值金额
            String orderDescription = "如意彩账户充值";//rbint.getString("DNAOrderDescription");//商品描述
            String orderRemark = "";
			String merOrderNo = "12" + transactionId + "|"; //
            
            String returnUrl = bgreturl;//rbint.getString("DNAReturnUrl");//02http://219.136.132.119:8000/test";  //异步返回地址
            logger.info("异步返回地址DNAReturnUrl=" + returnUrl);
            
            //持卡人银行卡号查询
            PosMessage pm = null;
            logger.info("持卡人银行卡号查询");           
            pm = tm.bankCardQuery(getSerialNO(), terminalNo, merchantNo, merchantPW, "14" + phoneNumber + "|" + cardNumber, "", merOrderNo);           
            logger.info("DNA对账户进行查询返回数据：\n" + pm.toString());

            
//            if (Float.parseFloat(orderAmount) > Float.parseFloat(pm.getAmount())) {
//            	logger.info("该银行卡超过当日交易金额上限或者余额不足以支付！");
//            	//map.put("pm", new PosMessage());//原来的：2011-07-15前的
//            	map.put("pm", pm);//新的：2011-07-15
//            	return map;
//            } else 
            
            
            String bankaccount = "0";//银行账户
    		StringBuffer param = new StringBuffer();
			param.append("bankid=").append(bankId).append("&paytype=").append(cardType).append("&accesstype=").append(accesstype)
					.append("&amt=").append(amt).append("&bankaccount=").append(bankaccount).append("&userno=").append(userno)
					.append("&type=").append(type).append("&channel=").append(channel).append("&subchannel=").append(subchannel)
					.append("&ladderpresentflag=").append(ladderpresentflag).append("&continuebettype=").append(continuebettype)
					.append("&orderid=").append(orderid);
			String url = chargeconfigService.getChargeconfig("lotteryReqUrl");//ConfigUtil.getConfig("charge.properties", "lotteryReqUrl");
            if (pm.getRespCode().equals("0000")) {
            	logger.info("该银行卡为白名单用户，开始传输订单到DNA生产服务器");
                //0000：系统受信任卡，无需提供持卡人信息;//从11月2日起姓名必须传     	
            	
    			logger.info("DNA银行卡充值->生成交易记录：url=" + url + " ,param=" + param.toString());    		
    			String result = HttpRequest.doPostRequest(url, param.toString());
    			logger.info("返回 return=" + result);        			
    			Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
    			errorCode = mapResult.containsKey("errorCode")? mapResult.get("errorCode").toString() : "";
    			if (!"0".equals(errorCode)) {
    				logger.info("生成交易记录出现错误 errorCode=" + errorCode); 			
    				map.put("pm", new PosMessage());
    				map.put("errorCode", errorCode);
                	return map;
    			} 
    			
    			transactionId = mapResult.get("value").toString();
    			logger.info("充值受理成功，平台生成交易记录成功。");
    			createDnapay(transactionId, userno, phoneNumber, amt);
    			
                pm = tm.pay(getSerialNO(), terminalNo, merchantNo, merchantPW, "14" + phoneNumber + "|" + cardNumber, "", orderAmount,
                        "12"+transactionId+"|", "reference", orderDescription, orderRemark, true, returnUrl, transData);//以前白名单transData传的是空""
            } else if (pm.getRespCode().equals("T438") || pm.getRespCode().equals("T437")) {
                //T438：系统交易过的新卡，需要提供持卡人信息；；T437：系统未交易过的新卡，需要提供持卡人信息；
                //如果订单类型要求本人消费，或开户证件非身份证则必须提供受益人姓名，且与持卡人姓名相同。
                //开户姓名+“|”+开户证件号码+“|”+开户银行所在省市+“|”+开户证件号码类型（参照7.2）+“|”+收益人姓名（如乘机人姓名，本人消费时与开户姓名一致） 				
				logger.info("DNA银行卡充值->生成交易记录：url=" + url + " ,param=" + param.toString());				
				String result = HttpRequest.doPostRequest(url, param.toString());
				logger.info("返回 return=" + result);
				Map<String, Object> mapResult = JsonUtil.transferJson2Map(result);
				errorCode = mapResult.containsKey("errorCode") ? mapResult.get("errorCode").toString() : "";
				if (!"0".equals(errorCode)) {
					logger.info("生成交易记录出现错误 errorCode=" + errorCode);				
					map.put("pm", new PosMessage());
					map.put("errorCode", errorCode);
					return map;
				}

				transactionId = mapResult.get("value").toString();
				logger.info("充值受理成功，平台生成交易记录成功。");
				createDnapay(transactionId, userno, phoneNumber, amt);

				logger.info("灰名单银行卡获得更多数据后，开始传输订单到DNA生产服务器");

				pm = tm.pay(getSerialNO(), terminalNo, merchantNo, merchantPW, "14" + phoneNumber + "|" + cardNumber, "", orderAmount, 
						"12" + transactionId + "|", "reference", orderDescription, orderRemark, true, returnUrl, transData);        
            	
            } else {
            	logger.info("其他返回码");            	
            }
            
			logger.info("DNA生产服务器处理完成， 返回代码=" + pm.getRespCode() + "，结果="+ pm.getRemark());
            map.put("pm", pm);
            map.put("transactionId", transactionId);
        	map.put("errorCode", errorCode);
            return map;
        } catch (Exception e) {
        	logger.error("DNA支付的出现错误:", e); 
        	errorCode = ErrorCode.ERROR.value;
        	map.put("pm", new PosMessage());
        	map.put("errorCode", errorCode);
        	return map;
        }
    }

    private static void createDnapay(String transactionid, String userno, String mobileid, String amt) {
    	try {
    		Dnapay dnapay = Dnapay.createDnapay(transactionid, userno, mobileid, amt);
    		logger.info("createDnapay:" + dnapay.toString());
    	} catch (Exception e) {
			e.printStackTrace();
			logger.error("createDnapay error:", e);
		}
    }
    
    /**
     * 
     * 商户测试示例
     * 
     * @param tm
     * @throws Exception
     * @throws InterruptedException
     */
    private static void testMerchant(TransactionClient tm, String accountNo, String userPasswd, String merchantNo, String termNo, String merchantPasswd, String amount, String description) throws Exception,
            InterruptedException {
    }

    
    /**
     * 获取系统跟踪号
     * @return
     */
    public static String getSerialNO() {
        return DateFormatter.HHmmss(new java.util.Date());
    }

    public static void setOrderNO(String orderNo){
    	orderNO = orderNo;
    }
    /**
     * 生成商户订单编号
     * @return
     */
    public static String getMerchantOrderNO() {
        return (orderNO.equals("")?DateFormatter.yyyyMMddHHmmss(new java.util.Date()):orderNO) ;
    }

    public static String readLine() {

        int ch;
        String r = "";
        boolean done = false;
        while (!done) {
            try {
                ch = System.in.read();
                if (ch < 0 || (char) ch == '\n') {
                    done = true;
                } else if ((char) ch != '\r') {
                    r = r + (char) ch;
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
                done = true;
            }

        }
        return r;
    }
    
    //static
    public Map<String, String> orderQuery(String transactionId){
        Map<String, String> map = new HashMap<String, String>();
    	String errorCode = ErrorCode.OK.value;
        PosMessage pm = null;
        TransactionClient tm = null;
        
        try {
        	//tm = new TransactionClient(rbint.getString("DNAWebServiceAddress"), rbint.getString("DNAWebServiceName"));
        	tm = new TransactionClient(chargeconfigService.getChargeconfig("DNAWebServiceAddress"), chargeconfigService.getChargeconfig("DNAWebServiceName"));

            //System.setProperty("javax.net.ssl.trustStore", rbint.getString("DnaPayStoreFile"));//加载证书
           // System.setProperty("javax.net.ssl.trustStore", "F:\\temp\\dnapay\\GlobalSignRootCA.jks");//加载证书
            //System.setProperty("javax.net.ssl.trustStorePassword", rbint.getString("DnaPayStorePassword"));

            //是否启用CA证书支持
            tm.setCAEnabled(chargeconfigService.getChargeconfig("DnaPayStorePassword").equals("yes")?true:false);//rbint.getString("DnaPayStorePassword")
            
        	String acqSsn = getSerialNO();
        	//String terminalNo = rbint.getString("DNATerminalNo");	//商户终端编号
        	//String merchantNo = rbint.getString("DNAMerNo") + rbint.getString("DNAMerchantNo");//02(商户订单编号，网页自助下单（WEB）)测试商户编号
            //String merchantPWD = rbint.getString("DNAMerchantPw");  //商户密钥
            String merchantNo = chargeconfigService.getChargeconfig("DNAMerNo") + chargeconfigService.getChargeconfig("DNAMerchantNo");//02(商户订单编号，网页自助下单（WEB）)测试商户编号   //rbint.getString("DNAMerNo")
            String merchantPWD = chargeconfigService.getChargeconfig("DNAMerchantPw");  //测试商户密钥 //rbint.getString("DNAMerchantPw")
            String terminalNo = chargeconfigService.getChargeconfig("DNATerminalNo");	//测试商户终端编号 //rbint.getString("DNATerminalNo")
            String accountNum = "";
            String orderNo = "12" + transactionId + "|";
            String reference = "reference";
            boolean isPay = false;
        	pm = tm.orderQuery(acqSsn, terminalNo, merchantNo, merchantPWD, accountNum, orderNo, reference, isPay);
        } catch(Exception e) {
        	errorCode = ErrorCode.ERROR.value;
        	e.printStackTrace();
        	logger.error("DNA订单查询出错：", e);
        }
        logger.info("DNA订单查询，pm=" + pm.toString());

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
        map.put("OrderNo", pm.getOrderNo().substring(2, pm.getOrderNo().length()-1));
        map.put("OrderState", OrderState.getMemo(pm.getOrderState()));
        map.put("ValidTime", pm.getValidTime());
        map.put("OrderType", pm.getOrderType());
        map.put("Mac", pm.getMac());
        
    	return map;
    }
}

