package com.ruyicai.charge.dna.thirdpart;

import org.apache.log4j.Logger;

import com.ruyicai.charge.dna.common.*;
import com.ruyicai.charge.dna.common.encrpt.MD5;
import com.ruyicai.charge.dna.thirdpart.jaws.IOrderServerWSProxy;

/**
 * 银联手机支付第三方商户交易客户端，支持Socket，WebService两种接入方式。
 * 请参照“银联手机支付第三方商户系统接入渠道接口.doc”
 */
public final class TransactionClient
{
	private static final Logger logger = Logger.getLogger(TransactionClient.class);
	
    /**
     * 建立一个Socket客户端。
     * 
     * @param IP - 交易网关地址
     * @param port - 端口
     */
    public TransactionClient(String IP, int port)
    {
        transactionIP = IP;
        transactionPort = "" + port;
        this.setTransactionType(TransactionType.Socket);
    }

    /** 建立一个WebService客户端。
     * @param endpoint - WebService 地址。
     * @param nameSpace - WebService 命名空间
     */
    public TransactionClient(String endpoint, String nameSpace)
    {

        transactionIP = endpoint;
        transactionPort = "" + nameSpace;
        this.setTransactionType(TransactionType.WebService);
    }

    /** 定制查询
     * @param acqSsn - 系统跟踪号
     * @param terminalNo - 商户终端编号
     * @param merchantNo - 商户编号
     * @param merchantPWD - 商户密钥
     * @param accountNum - 持卡人帐号
     * @return
     * @throws Exception
     */
    public PosMessage customerQuery(String acqSsn, String terminalNo,
        String merchantNo, String merchantPWD, String accountNum) throws Exception
    {

        PosMessage request = new PosMessage("0100");
        request.setAccountNum(accountNum);
        request.setProcessCode("300000");
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request);
        if (!resMessage.getMac().toUpperCase().equals(
            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
            throw new Exception("返回结果MAC校验失败");

        return resMessage;
    }

    /** 查询银行卡号信息
     * @param acqSsn - 系统跟踪号
     * @param terminalNo - 商户终端编号
     * @param merchantNo - 商户编号
     * @param merchantPWD - 商户密钥
     * @param accountNum - 持卡人帐号
     * @return
     * @throws Exception
     */
    public PosMessage bankCardQuery(String acqSsn, String terminalNo,
        String merchantNo, String merchantPWD, String accountNum, String reference, String orderNo) throws Exception
    {

    	logger.info("查询银行卡号信息开始");
    	logger.info("查询银行卡号信息->" + "acqSsn=" + acqSsn + ";terminalNo=" + terminalNo + ";merchantNo=" + merchantNo
    			+ ";merchantPWD=" + merchantPWD + ";accountNum=" + accountNum+ ";reference=" + reference + ";orderNo="+ orderNo);
        PosMessage request = new PosMessage(CAEnabled?"C100":"0100");
        request.setAccountNum(accountNum);
        request.setProcessCode("300002");
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setReference(reference);
        request.setOrderNo(orderNo);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

         if (CAEnabled && !Strings.isNullOrEmpty(request.getAccountNum()))
        {
            request.setAccountNum(ToolKit.sign(request.getAccountNum()));
        }

        logger.info("查询银行卡号信息->transact开始");
        PosMessage resMessage = this.transact(request);
        logger.info("查询银行卡号信息->transact结束");
        if (CAEnabled && !Strings.isNullOrEmpty(resMessage.getAccountNum()))
        {
            resMessage.setAccountNum(ToolKit.unSign(resMessage.getAccountNum()));
        }
       
        if (!resMessage.getMac().toUpperCase().equals(
            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
            throw new Exception("返回结果MAC校验失败");

        logger.info("查询银行卡号信息结束");
        return resMessage;
    }

    /** 申请短信确认码
     * @param acqSsn - 系统跟踪号
     * @param terminalNo - 终端编号
     * @param merchantNo - 商户编号
     * @param merchantPWD - 商户密钥
     * @param accountNum - 持卡人帐号
     * @return
     * @throws Exception
     */
    public PosMessage smsQuery(String acqSsn, String terminalNo,
        String merchantNo, String merchantPWD, String accountNum) throws Exception
    {

        PosMessage request = new PosMessage("0100");
        request.setAccountNum(accountNum);
        request.setProcessCode("300001");
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request);
        if (!resMessage.getMac().toUpperCase().equals(
            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
            throw new Exception("返回结果MAC校验失败");

        return resMessage;
    }

    /**
     * 订单查询
     * 
     * @param acqSsn
     *            系统跟踪号
     * @param terminalNo
     *            终端编号
     * @param merchantNo
     *            商户编号
     * @param merchantPWD
     *            商户密钥
     * @param accountNum
     *            用户号码
     * @param orderNo
     *            订单号码
     * @param isPay
     *            是否支付, 如果该订单为未支付时是否发起支付。
     * @return
     */
    public PosMessage orderQuery(String acqSsn, String terminalNo,
        String merchantNo, String merchantPWD, String accountNum, String orderNo, String reference,
        boolean isPay) throws Exception
    {

        PosMessage request = new PosMessage("0120");
        request.setAccountNum(accountNum);
        request.setProcessCode(isPay ? "310001" : "310000");
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setReference(reference);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request);
//        if (resMessage != null && !resMessage.getMac().toUpperCase().equals(
//            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
//            throw new Exception("返回结果MAC校验失败");

        return resMessage;
    }

    /**缴费支付
     * @param acqSsn
     *            系统跟踪号
     * @param terminalNo
     *            终端编号
     * @param merchantNo
     *            商户编号
     * @param merchantPWD
     *            商户密钥
     * @param accountNum
     *            用户号码
     * @param passwd
     *            支付密码，填空。
     * @param amount
     *            金额
     * @param orderNo
     *            订单号
     * @param description
     *            订单描述
     * @param remark
     *            备注
     * @param payNow
     *            是否及时支付
     * @param returnUrl
     * 			  异步返回地址，同步交易请填空
     * @param transData
     *              开户姓名+“|”+开户身份证+“|”+开户银行所在省市 
     * @return
     * @throws Exception
     */
    public PosMessage pay(String acqSsn, String terminalNo,
        String merchantNo, String merchantPWD, String accountNum, String passwd,
        String amount, String orderNo, String reference, String description, String remark,
        boolean payNow, String returnUrl, String transData) throws Exception
    {

    	logger.info("缴费支付开始");
    	
    	System.out.println("amount::::::::::::"+amount);
        PosMessage request = new PosMessage(CAEnabled?"C200":"0200");
        request.setAccountNum(accountNum);
        request.setProcessCode("190000");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.yyyyMMdd(new java.util.Date()).substring(4) + Formatter.HHmmss(new java.util.Date()));

        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setMerchantNo(merchantNo);
        request.setTerminalNo(terminalNo);
        request.setOrderNo(orderNo);
        request.setReference(reference);
        request.setCurCode("01");

        request.setDescription(description);
        request.setRemark(remark);
        request.setOrderState("01");
        request.setOrderType(payNow ? "00" : "01");
        request.setTransData(transData);
        request.setReturnAddress(returnUrl);
        System.out.println("TransData::::::::::::"+request.getTransData());
        MD5 md5 = new MD5();

        if (passwd != null && !passwd.equals(""))
            request.setPin(md5.getMD5ofStr(passwd));
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        if (CAEnabled && !Strings.isNullOrEmpty(request.getAccountNum()))
        {
            request.setAccountNum(ToolKit.sign(request.getAccountNum()));
        }

        PosMessage resMessage = this.transact(request);
        if (CAEnabled && !Strings.isNullOrEmpty(resMessage.getAccountNum()))
        {
            resMessage.setAccountNum(ToolKit.unSign(resMessage.getAccountNum()));
        }
        if (!resMessage.getMac().toUpperCase().equals(
            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
            throw new Exception("返回结果校验失败");

        logger.info("缴费支付结束");
        return resMessage;
    }

    /**
     * 缴费撤销，拨打持卡人手机完成交易授权
     * 
     * @param acqSsn
     *            系统跟踪号
     * @param terminalNo
     *            终端编号
     * @param merchantNo
     *            商户编号
     * @param merchantPWD
     *            商户密钥
     * @param accountNum
     *            用户号码
     * @param passwd
     *            交易密码，填空
     * @param amount
     *            金额
     * @param orderNo
     *            订单号
     * @param returnUrl
     * 			  异步返回地址，同步交易请填空
     * @return
     * @throws Exception
     */
    public PosMessage refund(String acqSsn, String terminalNo,
        String merchantNo, String merchantPWD, String accountNum, String passwd,
        String amount, String orderNo, String reference, String returnUrl) throws Exception
    {

        PosMessage request = new PosMessage("0220");
        request.setAccountNum(accountNum);
        request.setProcessCode("290000");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.yyyyMMdd(new java.util.Date()).substring(4) + Formatter.HHmmss(new java.util.Date()));

        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setReference(reference);
        request.setCurCode("01");
        MD5 md5 = new MD5();

        if (passwd != null && !passwd.equals(""))
            request.setPin(md5.getMD5ofStr(passwd));
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request);
        if (!resMessage.getMac().toUpperCase().equals(
            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
            throw new Exception("返回结果校验失败");

        return resMessage;
    }

    /**
     * 缴费冲正，无需拨打持卡人手机完成交易授权
     * @param acqSsn
     *            系统跟踪号
     * @param terminalNo
     *            终端编号
     * @param merchantNo
     *            商户编号
     * @param merchantPWD
     *            商户密钥
     * @param accountNum
     *            用户号码
     * @param passwd
     *            交易密码，填空
     * @param amount
     *            金额
     * @param orderNo
     *            订单号
     * @return
     */
    public PosMessage quash(String acqSsn, String transDateTime,
        String terminalNo, String merchantNo, String merchantPWD, String accountNum,
        String amount, String orderNo, String reference) throws Exception
    {

        PosMessage request = new PosMessage("0220");
        request.setAccountNum(accountNum);
        request.setProcessCode("290001");
        request.setAmount(amount);
        request.setTransDatetime(transDateTime);

        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setReference(reference);
        request.setCurCode("01");

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request);
        if (!resMessage.getMac().toUpperCase().equals(
            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
            throw new Exception("返回结果校验失败");

        return resMessage;
    }

    /**
     * 调账退款
     * 
     * @param acqSsn
     *            系统跟踪号
     * @param terminalNo
     *            终端编号
     * @param merchantNo
     *            商户编号
     * @param merchantPWD
     *            商户密钥
     * @param accountNum
     *            用户号码
     * @param passwd
     *            交易密码，填空
     * @param amount
     *            金额
     * @param orderNo
     *            订单号
     * @return
     */
    public PosMessage adjustOrder(String acqSsn, String transDateTime,
        String terminalNo, String merchantNo, String merchantPWD, String accountNum,
        String passwd, String amount, String orderNo, String reference) throws Exception
    {

        PosMessage request = new PosMessage("0220");
        request.setAccountNum(accountNum);
        request.setProcessCode("290003");
        request.setAmount(amount);
        request.setTransDatetime(transDateTime);

        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setReference(reference);
        request.setCurCode("01");

        MD5 md5 = new MD5();
        if (passwd != null && !passwd.equals(""))
            request.setPin(md5.getMD5ofStr(passwd));
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request);
        if (!resMessage.getMac().toUpperCase().equals(
            md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD)))
            throw new Exception("返回结果MAC校验失败");

        return resMessage;
    }

    public PosMessage transact(PosMessage request) throws Exception
    {
        if (this.getTransactionType().equals(TransactionType.WebService)){
        	logger.info("transact->WebService->TransactionIP=" + this.getTransactionIP() + ";TransactionPort=" 
        			+ this.getTransactionPort() + ";Timeout=" + this.getTimeout());
            return transactXML(request, this.getTransactionIP(), this.getTransactionPort(), this.getTimeout());
        }  else {
        	logger.info("transact->SOCKET->TransactionIP=" + this.getTransactionIP() + ";TransactionPort=" 
        			+ this.getTransactionPort() + ";Timeout=" + this.getTimeout());
            return transactSOCKET(request, this.getTransactionIP(), Integer.parseInt(this.getTransactionPort()), this.getTimeout());
        }
    }

    /** Socket第三方交易接口
     * @param request 请参照“银联手机支付第三方商户系统接入渠道接口.doc”
     * @param ip Socket 地址
     * @param port Socket 端口
     * @param timeout Socket
     * @return
     * @throws Exception
     */
    public static PosMessage transactSOCKET(PosMessage request, String ip, int port, int timeout) throws Exception
    {
        ToolKit.writeLog(TransactionClient.class.getName(), "transactSOCKET.send",
            ip + "|" + port + "|" + timeout + "|" + request.toString());

        SocketClient Client = null;

        try
        {

            Client = new SocketClient(ip, port, timeout);

            Client.Send(request);

            PosMessage payResult = Client.ReceiveSocketMessage();

            ToolKit.writeLog(TransactionClient.class.getName(),
                "transactSOCKET.result", payResult.toString());

            return payResult;

        }
        catch (Exception e)
        {

            ToolKit.writeLog(TransactionClient.class.getName(), "transactSOCKET", e);
            throw e;
        }
        finally
        {
            if (Client != null)
                Client.Close();
        }

    }

    /** WebService第三方交易接口
     * @param request 请参照“银联手机支付第三方商户系统接入渠道接口.doc”
     * @param endPort WebService地址
     * @param nameSpace WebService命名空间
     * @param timeout
     * @return
     * @throws Exception 请参照“银联手机支付第三方商户系统接入渠道接口.doc”
     */
    public static PosMessage transactXML(PosMessage request, String endPoint, String nameSpace, int timeout) throws Exception
    {
    	logger.info("WebService第三方交易接口->transactXML->开始");
    	
    	logger.info("WebService第三方交易接口->transactXML->writeLog->开始");
        ToolKit.writeLog(TransactionClient.class.getName(), "transactXML.send",
            endPoint + "|" + nameSpace + "|" + timeout + "|" + request.toString());
        logger.info("WebService第三方交易接口->transactXML->writeLog->结束");
        
        
        IOrderServerWSProxy Client = null;

        try
        {

            Client = new IOrderServerWSProxy(endPoint, nameSpace);

            logger.info("WebService第三方交易接口->transactXML->transact->开始");
            com.ruyicai.charge.dna.thirdpart.jaws.PosMessage result = Client.transact(TransactionUtil.translate(request));
            logger.info("WebService第三方交易接口->transactXML->transact->结束");

            logger.info("WebService第三方交易接口->transactXML->TransactionUtil.translate->开始");
            PosMessage payResult = TransactionUtil.translate(result);
            logger.info("WebService第三方交易接口->transactXML->TransactionUtil.translate->结束");

            ToolKit.writeLog(TransactionClient.class.getName(), "transactXML.result",
                payResult.toString());

            logger.info("WebService第三方交易接口->transactXML->结束");
            return payResult;

        }
        catch (Exception e)
        {
        	logger.info("WebService第三方交易接口->transactXML->异常");
            e.printStackTrace();
            ToolKit.writeLog(TransactionClient.class.getName(), "transactXML", e);
            throw e;
        }
    }
    private String transactionPort;
    private String transactionIP;
    private int Timeout = 120;
    private boolean CAEnabled;

    public boolean getCAEnabled()
    {
        return CAEnabled;
    }

    public void setCAEnabled(boolean ca)
    {
        CAEnabled = ca;
    }
    
    public int getTimeout()
    {
        return Timeout;
    }

    public void setTimeout(int timeout)
    {
        Timeout = timeout;
    }

    public String getTransactionIP()
    {
        return transactionIP;
    }

    public void setTransactionIP(String transactionIP)
    {
        this.transactionIP = transactionIP;
    }

    public String getTransactionPort()
    {
        return transactionPort;
    }

    public void setTransactionPort(String transactionPort)
    {
        this.transactionPort = transactionPort;
    }
    private TransactionType transactionType;

    public TransactionType getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(TransactionType endPointType)
    {
        this.transactionType = endPointType;
    }
}
