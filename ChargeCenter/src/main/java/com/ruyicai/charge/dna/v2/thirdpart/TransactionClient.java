package com.ruyicai.charge.dna.v2.thirdpart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.ruyicai.charge.dna.v2.ca.RSAProvider;
import com.ruyicai.charge.dna.v2.common.Formatter;
import com.ruyicai.charge.dna.v2.common.SslConnection;
import com.ruyicai.charge.dna.v2.common.Strings;
import com.ruyicai.charge.dna.v2.common.ToolKit;
import com.ruyicai.charge.dna.v2.common.encrpt.MD5;
import com.ruyicai.charge.dna.v2.common.encrpt.RSA;
import com.ruyicai.charge.dna.v2.common.encrpt.TripleDes;
import com.ruyicai.charge.dna.v2.thirdpart.jaws.IOrderServerWSProxy;

/**
 * 银联语音支付商户接入客户端，支持WebService, XML,XML CA三种接入方式。
 * 具体说明请参照“银联语音支付平台接口规范.doc”
 */
public final class TransactionClient {

    /** 建立一个银联语音支付客户端实例, 默认是WebService连接, 可以设置TransactionType修改连接方式。
     * 
     * @param url - Url 地址。
     * @param nameSpace - WebService 命名空间, 其他连接方式填空
     */
    public TransactionClient(String url, String nameSpace) {

        this.url = url;
        this.nameSpace = "" + nameSpace;
        this.setTransactionType(TransactionType.WebService);
    }
    private TransactionType transactionType;
    private String serverCert = "";
    private String merchantNo;
    private String terminalNo;
    private String merchantPWD;
    private String nameSpace;
    private String url;
    private String serverEncoding = "GB2312";

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getServerEncoding() {
        return serverEncoding;
    }

    public void setServerEncoding(String serverEncoding) {
        this.serverEncoding = serverEncoding;
    }
    private int timeout = 120000;

    /** 获取交易超时时间,单位:豪秒
     * 
     * @return 交易超时时间, 默认120秒
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置交易超时时间,豪秒, 默认120秒
     * @param timeout 交易超时时间,单位:秒
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /** 获取服务器连接地址,Socket接口时填IP.
     * 
     * @return 服务器连接地址 
     */
    public String getUrl() {
        return url;
    }

    /** 设置服务器连接地址
     * 
     * @param url 服务器连接地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /** 获取服务命名空间, 只针对WebService接入; Socket接口时填端口.
     * 
     * @return 服务命名空间
     */
    public String getNameSapce() {
        return nameSpace;
    }

    /** 设置服务命名空间, 只针对WebService接入; Socket接口时填端口.
     * 
     * @param nameSpace 服务命名空间
     */
    public void setNameSapce(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    /** 获取服务接入方式, 暂时包括WebService, XML, CA, SOCKET
     * 
     * @return 服务接入方式
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /** 设置服务接入方式
     * TransactionType.CA: 加密报文体格式：BASE64(版本号))|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5(报文原文))
     * @param type 暂时包括WebService, XML, CA
     */
    public void setTransactionType(TransactionType type) {
        this.transactionType = type;
    }

    /** 获取服务端CA证书公钥
     * 
     * @return 服务端CA证书公钥 
     */
    public String getServerCert() {
        return serverCert;
    }

    /** 设置服务端CA证书公钥
     * 
     * @param cert 服务端CA证书公钥 
     */
    public void setServerCert(String cert) {
        this.serverCert = cert;
    }

    /** 设置商户编号, 商户编号由银联语音支付平台在商户注册成功后分配 格式如下:
     * “01”	商户编号+子商户编号（例如："01"+"商户编号"+"|"+"子商户编号", 暂不支持）
     * “02”	商户编号 (例如："02"+""+"|"+"")
     *     
     * @return 商户编号
     */
    public String getMerchantNo() {
        return merchantNo;
    }

    /** 设置商户编号，商户编号由银联语音支付平台在商户注册成功后分配
     * “01”	商户编号+子商户编号（例如："01"+"商户编号"+"|"+"子商户编号", 暂不支持）
     * “02”	商户编号 (例如："02"+""+"|"+"")
     * 
     * @param merchantNo 商户编号
     */
    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    /** 获取商户密钥， 商户密钥由银联语音支付平台在商户注册成功后分配
     * 
     * @return 商户密钥
     */
    public String getMerchantPWD() {
        return merchantPWD;
    }

    /** 设置商户密钥，商户密钥由银联语音支付平台在商户注册成功后分配
     * 
     * @param merchantPWD 商户密钥
     */
    public void setMerchantPassWD(String merchantPWD) {
        this.merchantPWD = merchantPWD;
    }

    /** 获取商户终端编号，由银联语音支付平台在商户注册成功后分配
     * 
     * @return 商户终端编号
     */
    public String getTerminalNo() {
        return terminalNo;
    }

    /** 设置商户终端编号，由银联语音支付平台在商户注册成功后分配
     * 
     * @param terminalNo 商户终端编号
     */
    public void setTerminalNo(String terminalNo) {
        this.terminalNo = terminalNo;
    }

    /** 测试连接是否正常
     * 
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @param acqSsn 系统跟踪号
     * @return 根据39域(RespCode)响应码, 0000表示正常
     * @throws Exception 
     */
    public PosMessage connectionTest(String acqSsn, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0800");
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        PosMessage resMessage = this.transact(request, encryptKey);

        return resMessage;
    }

    /** 定制查询，返回手机号在银联语音支付的用户定制情况
     * @param acqSsn - 系统跟踪号
     * @param accountNum - 持卡人帐号，前两位标明类型，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
    
     * @param transData - 业务交换数据
     * @return  根据39域(RespCode)响应码, 判断5.4订单支付48域(TransData)需要填写的内容.
     *           0000：（白名单）系统受信任卡，5.4节48域无需提供持卡人信息；
     *           T437：（新用户）系统未交易过的新卡，5.4节48域需提供持卡人信息包括开户姓名，开户证件号，开户银行所在省市，开户证件号码类型；
     *           T438：（灰名单）系统交易过但未加入白名单的卡，交易时按新用户处理，并要求本人消费；
     *           T436：该卡交易时间受限，拒绝交易；
     *           T432：（黑名单）黑名单银行卡号，拒绝交易；
     *           T404：系统不支持的银行卡号，拒绝交易。
     *           第4域(Amount)返回该用户当日交易金额上限。
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @throws Exception 返回结果MAC校验失败
     */
    public PosMessage bindQuery(String acqSsn, String accountNum, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0100");
        request.setAccountNum(accountNum);
        request.setProcessCode("300002");
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setTransData(transData);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);

        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果MAC校验失败");
        }

        return resMessage;
    }

    /** 帐户查询，返回交易帐号在银联语音支付的注册情况
     * @param acqSsn - 系统跟踪号
     * @param accountNum - 持卡人帐号，前两位标明类型，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
    
     * @param transData - 业务交换数据
     * @return  根据39域(RespCode)响应码, 判断5.4订单支付48域(TransData)需要填写的内容.
     *           0000：（白名单）系统受信任卡，5.4节48域无需提供持卡人信息；
     *           T437：（新用户）系统未交易过的新卡，5.4节48域需提供持卡人信息包括开户姓名，开户证件号，开户银行所在省市，开户证件号码类型；
     *           T438：（灰名单）系统交易过但未加入白名单的卡，交易时按新用户处理，并要求本人消费；
     *           T436：该卡交易时间受限，拒绝交易；
     *           T432：（黑名单）黑名单银行卡号，拒绝交易；
     *           T404：系统不支持的银行卡号，拒绝交易。
     *           第4域(Amount)返回该用户当日交易金额上限。
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @throws Exception 返回结果MAC校验失败
     */
    public PosMessage accountQuery(String acqSsn, String accountNum, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0100");
        request.setAccountNum(accountNum);
        request.setProcessCode("300002");
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setTransData(transData);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);

        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果MAC校验失败");
        }

        return resMessage;
    }

    /**
     * 订单查询
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum - 持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param orderNo 订单编号，前两位标明类型， 订单查询建议提供“01”DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示查询成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception 返回结果MAC校验失败
     */
    public PosMessage orderQuery(String acqSsn, String accountNum, String orderNo, boolean isPayNow, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0120");
        request.setAccountNum(accountNum);
        if (isPayNow) {
            request.setProcessCode("310000");
        } else {
            request.setProcessCode("310002");
        }

        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);
        if (resMessage != null && !resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果MAC校验失败");
        }

        return resMessage;
    }

    /** 非即时支付订单发起支付
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum - 持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param orderNo 订单编号，前两位标明类型， 商户提交商户订单编号，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param reference 系统参考号， 原值返回。
     * @param transData
     *          开户姓名	C	C	银行卡开户姓名(老用户本人消费返回给商户)
     *          开户证件号码	C	C	银行卡开户证件号码
     *          开户银行所在省市	C	C	省市已逗号分割，比如“广东省，深圳市”
     *          开户证件类型	C	C	银行卡开户证件类型，参照7.2说明
     *          银行名称	C	C+	310002:非即时订单查询返回银行名称 
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception 返回结果MAC校验失败
     */
    public PosMessage orderPay(String acqSsn, String accountNum, String orderNo, String respCode, String pin, String reference, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0120");
        request.setAccountNum(accountNum);
        request.setProcessCode("310001");
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setReference(reference);
        request.setTransData(transData);
        request.setRespCode(respCode);
        request.setPin(pin);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);
        if (resMessage != null && !resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果MAC校验失败");
        }

        return resMessage;
    }

    /** 非即时支付订单发起支付
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum - 持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param orderNo 订单编号，前两位标明类型， 商户提交商户订单编号，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param reference 系统参考号， 原值返回。
     * @param transData
     *          开户姓名	C	C	银行卡开户姓名(老用户本人消费返回给商户)
     *          开户证件号码	C	C	银行卡开户证件号码
     *          开户银行所在省市	C	C	省市已逗号分割，比如“广东省，深圳市”
     *          开户证件类型	C	C	银行卡开户证件类型，参照7.2说明
     *          银行名称	C	C+	310002:非即时订单查询返回银行名称 
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception 返回结果MAC校验失败
     */
    public PosMessage orderVpc(PosMessage posMsg) throws Exception {

        VpcMessage request = TransactionUtil.toVpcMessage(posMsg);
        request.setMessageType("0500");

        VpcMessage resMessage = this.transactVPC(request);
//        if (resMessage != null && !resMessage.getMac().toUpperCase().equals(
//                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
//            throw new Exception("返回结果MAC校验失败");
//        }

        return TransactionUtil.toXmlMessage(resMessage);
    }

    /** 订单支付
     *  商户在调用5.4订单支付接口前可以先调用5.2帐户查询（处理码300002）接口， 根据5.2帐户查询查询结果正确填写48域(TransData)内容。
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum  持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param amount  订单金额
     * @param orderNo 订单编号， 商户提交商户订单编号，前两位标明类型，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param description 订单描述, 长度不要超过50个字，前两位可以配置语音合成类型。
     *          “00”	中文
     *          “01”	粤语
     *          “02”	英文
     * @param remark 订单备注
     * @param payNow 是否及时支付
     * @param returnUrl 订单支付结果异步返回地址，前两位标明地址类型，同步交易请填空。
     *                   如果该域非空, 手机支付返回交易结果（参照6.2）到该地址, 
     *                   商户收到结果后返回确认信息（参照5.8），如果返回地址为Servlet地址，
     *                   则直接返回“0000”代表商户接收结果成功，无需返回页面.
     *                   任何正常页面返回均表示接收结果成功。
     *
     *           “01”	Socket异步返回接收地址(暂不支持) 内容格式：[IP|PORT]
     *           “02”	Http异步返回接收地址 内容格式：[URL]
     *           “03”	WebService异步返回接收地址,内容格式：[URL|NAMESPACE]
     *           “04”	Http同步/异步返回接收地址,内容格式：[URL|URL]
     *           “05”	CA签名XML异步返回接收地址, 内容格式[URL]
     *           “06”	XML异步返回接收地址, 内容格式[URL]
     *      
     * @param transData 提供业务交换数据包括银行开户姓名，开户证件，开户银行所在省市，开户证件类型等，字段之间以‘|’分割, 无用留空即可
     *          1	开户姓名            银行卡开户姓名
     *          2	开户证件号码        银行卡开户证件号码
     *          3	开户银行所在省市    省市已逗号分割，比如“广东省，深圳市”, ‘北京市’
     *          4	开户证件类型        银行卡开户证件类型，参照7.2说明
     *          5	订单受益人姓名      如机票乘机人, 多个受益人以逗号分割,对于受益人较多的情况,至少填两个,如果受益人包括开户人,需包含在内.
     *          6	持卡人IP地址        持卡人登陆商户网站的IP地址
     *          7	开户证件地址        开户证件地址不需全部提供,截取至街道即可,常见街道关键字包括：路/街道/街/胡同/道/条/里/镇/乡/村/庄/弄/巷/宅/�/屯/巷/寨/组/队/园/院
     *          8	受益人手机号	   受益人手机号(手机充值必填)
     *          9	产品销售地	   省市已逗号分割，比如“广东省，深圳市”, ‘北京市’(团购必填)
     *          10	开户银行登记手机号  (5.2身份验证,交易参考标明要填的必填)
     *          11	其他行业风控数据    例如：
     *                                          Apple:appid
     *                                          代付:银行联行号|银行号
     *                                          信用卡还款:信用卡号|姓名|证件号|证据类型
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception
     */
    public PosMessage pay(String acqSsn, String accountNum, String pin,
            String amount, String orderNo, String reference, String description, String remark,
            boolean payNow, String returnUrl, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0200");
        request.setAccountNum(accountNum);
        request.setProcessCode(payNow ? "190000" : "190001");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setMerchantNo(merchantNo);
        request.setTerminalNo(terminalNo);
        request.setOrderNo(orderNo);
        request.setPin(pin);
        request.setReference(reference);
        request.setCurCode("01");
        request.setDescription(description);
        request.setRemark(remark);
        request.setOrderState("01");
        request.setOrderType(payNow ? "00" : "01");
        request.setTransData(transData);
        request.setReturnAddress(returnUrl);

        MD5 md5 = new MD5();

        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);

        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        return resMessage;
    }

    /** 商户端一线通订单申请, TsNo返回一线通设备支付标识码
     *  商户在调用5.4订单支付接口前可以先调用5.2帐户查询（处理码300002）接口， 根据5.2帐户查询查询结果正确填写48域(TransData)内容。
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum  持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param amount  订单金额
     * @param orderNo 订单编号， 商户提交商户订单编号，前两位标明类型，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param description 订单描述, 长度不要超过50个字，前两位可以配置语音合成类型。
     *          “00”	中文
     *          “01”	粤语
     *          “02”	英文
     * @param remark 订单备注
     * @param payNow 是否及时支付
     * @param returnUrl 订单支付结果异步返回地址，前两位标明地址类型，同步交易请填空。
     *                   如果该域非空, 手机支付返回交易结果（参照6.2）到该地址, 
     *                   商户收到结果后返回确认信息（参照5.8），如果返回地址为Servlet地址，
     *                   则直接返回“0000”代表商户接收结果成功，无需返回页面.
     *                   任何正常页面返回均表示接收结果成功。
     *
     *           “01”	Socket异步返回接收地址(暂不支持) 内容格式：[IP|PORT]
     *           “02”	Http异步返回接收地址 内容格式：[URL]
     *           “03”	WebService异步返回接收地址,内容格式：[URL|NAMESPACE]
     *           “04”	Http同步/异步返回接收地址,内容格式：[URL|URL]
     *           “05”	CA签名XML异步返回接收地址, 内容格式[URL]
     *           “06”	XML异步返回接收地址, 内容格式[URL]
     * @param VpcUrl 一线通设备地址。           
     * @param transData 提供业务交换数据包括银行开户姓名，开户证件，开户银行所在省市，开户证件类型等，字段之间以‘|’分割, 无用留空即可
     *          1	开户姓名            银行卡开户姓名
     *          2	开户证件号码        银行卡开户证件号码
     *          3	开户银行所在省市    省市已逗号分割，比如“广东省，深圳市”, ‘北京市’
     *          4	开户证件类型        银行卡开户证件类型，参照7.2说明
     *          5	订单受益人姓名      如机票乘机人, 多个受益人以逗号分割,对于受益人较多的情况,至少填两个,如果受益人包括开户人,需包含在内.
     *          6	持卡人IP地址        持卡人登陆商户网站的IP地址
     *          7	开户证件地址        开户证件地址不需全部提供,截取至街道即可,常见街道关键字包括：路/街道/街/胡同/道/条/里/镇/乡/村/庄/弄/巷/宅/�/屯/巷/寨/组/队/园/院
     *          8	受益人手机号	   受益人手机号(手机充值必填)
     *          9	产品销售地	   省市已逗号分割，比如“广东省，深圳市”, ‘北京市’(团购必填)
     *          10	开户银行登记手机号  (5.2身份验证,交易参考标明要填的必填)
     *          11	其他行业风控数据    例如：
     *                                          Apple:appid
     *                                          代付:银行联行号|银行号
     *                                          信用卡还款:信用卡号|姓名|证件号|证据类型
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception
     */
    public PosMessage payVPC(String acqSsn, String accountNum, String pin,
            String amount, String orderNo, String reference, String description, String remark,
            String returnUrl, String vpcUrl, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0200");
        request.setAccountNum(accountNum);
        request.setProcessCode("190001");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setMerchantNo(merchantNo);
        request.setTerminalNo(terminalNo);
        request.setOrderNo(orderNo);
        request.setPin(pin);
        request.setReference(reference);
        request.setCurCode("01");
        request.setDescription(description);
        request.setRemark(remark);
        request.setOrderState("01");
        request.setOrderType("01");
        request.setTransData(transData);
        request.setReturnAddress(returnUrl);

        MD5 md5 = new MD5();

        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage pm = this.transact(request, encryptKey);

        if (!pm.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(pm) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        if (pm.getRespCode().equals("0000")) {

            VpcMessage vpcMsg = new VpcMessage();
            vpcMsg.setMessageType("0500"); //一线通
            String accType = accountNum.substring(0, 2);
            String[] acc = accountNum.substring(2).split("\\|");
            if (accType.equals("14")) {
                vpcMsg.setPhoneNum("9" + acc[0]);
                vpcMsg.setPrimaryAcctNum("02|" + acc[1] + "|");
                vpcMsg.setPinType("00");
            } else if (accType.equals("21")) {
                vpcMsg.setPhoneNum("9" + acc[0]);
                vpcMsg.setPrimaryAcctNum("01|" + acc[1] + "|");
                vpcMsg.setPinType("00");
            } else if (accType.equals("01")) {
                vpcMsg.setPrimaryAcctNum("02|" + acc[1] + "|");
                vpcMsg.setPinType("00");
            } else if (accType.equals("02")) {
                vpcMsg.setPrimaryAcctNum("01|" + acc[1] + "|");
                vpcMsg.setPinType("00");
            } else if (accType.equals("04")) {
                vpcMsg.setPhoneNum("9" + acc[0]);
                vpcMsg.setPinType("01");
            }

            vpcMsg.setAmount(pm.getAmount().replace(".", ""));
            vpcMsg.setCurCode("156");
            vpcMsg.setMerchantNo(pm.getMerchantNo().substring(2));
            vpcMsg.setTerminalNo(pm.getTerminalNo());
            vpcMsg.setTransDatetime(Formatter.MMddHHmmss(new Date()));
            vpcMsg.setOrderDesc(pm.getDescription());
            vpcMsg.setOrderExpireDate(Formatter.yyMMddHHmmss(new Date()));
            vpcMsg.setSysTraceID(pm.getAcqSsn());
            vpcMsg.setOrderNo(pm.getOrderNo());

            TransactionClient tmVPC = new TransactionClient(vpcUrl, "");
            tmVPC.setTransactionType(TransactionType.VPC);
            vpcMsg = tmVPC.transactVPC(vpcMsg);
            pm.setTsNo(vpcMsg.getProcessCode()); //支付标识码
        }

        return pm;
    }

    /** 代付交易: 企业委托通过资金代付通道付款
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum  持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param amount  订单金额
     * @param orderNo 订单编号， 商户提交商户订单编号，前两位标明类型，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param description 订单描述, 长度不要超过50个字，前两位可以配置语音合成类型。
     *          “00”	中文
     *          “01”	粤语
     *          “02”	英文
     * @param remark 订单备注
     * @param payNow 是否及时支付
     * @param returnUrl 订单支付结果异步返回地址，前两位标明地址类型，同步交易请填空。
     *                   如果该域非空, 手机支付返回交易结果（参照6.2）到该地址, 
     *                   商户收到结果后返回确认信息（参照5.8），如果返回地址为Servlet地址，
     *                   则直接返回“0000”代表商户接收结果成功，无需返回页面.
     *                   任何正常页面返回均表示接收结果成功。
     *
     *           “01”	Socket异步返回接收地址(暂不支持) 内容格式：[IP|PORT]
     *           “02”	Http异步返回接收地址 内容格式：[URL]
     *           “03”	WebService异步返回接收地址,内容格式：[URL|NAMESPACE]
     *           “04”	Http同步/异步返回接收地址,内容格式：[URL|URL]
     *           “05”	CA签名XML异步返回接收地址, 内容格式[URL]
     *           “06”	XML异步返回接收地址, 内容格式[URL]
     *      
     * @param transData 提供业务交换数据包括银行开户姓名，开户证件，开户银行所在省市，开户证件类型等，字段之间以‘|’分割, 无用留空即可
     *          1	开户姓名            银行卡开户姓名
     *          2	开户证件号码        银行卡开户证件号码
     *          3	开户银行所在省市    省市已逗号分割，比如“广东省，深圳市”, ‘北京市’
     *          4	开户证件类型        银行卡开户证件类型，参照7.2说明
     *          5	订单受益人姓名      如机票乘机人, 多个受益人以逗号分割,对于受益人较多的情况,至少填两个,如果受益人包括开户人,需包含在内.
     *          6	持卡人IP地址        持卡人登陆商户网站的IP地址
     *          7	开户证件地址        开户证件地址不需全部提供,截取至街道即可,常见街道关键字包括：路/街道/街/胡同/道/条/里/镇/乡/村/庄/弄/巷/宅/�/屯/巷/寨/组/队/园/院
     *          8	受益人手机号	   受益人手机号(手机充值必填)
     *          9	产品销售地	   省市已逗号分割，比如“广东省，深圳市”, ‘北京市’(团购必填)
     *          10	开户银行登记手机号  (5.2身份验证,交易参考标明要填的必填)
     *          11	其他行业风控数据    例如：
     *                                          Apple:appid
     *                                          代付:银行联行号|银行号
     *                                          信用卡还款:信用卡号|姓名|证件号|证据类型
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception
     */
    public PosMessage payAgent(String acqSsn, String accountNum, String pin,
            String amount, String orderNo, String reference, String description, String remark,
            boolean payNow, String returnUrl, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0200");
        request.setAccountNum(accountNum);
        request.setProcessCode("190003");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setMerchantNo(merchantNo);
        request.setTerminalNo(terminalNo);
        request.setOrderNo(orderNo);
        request.setPin(pin);
        request.setReference(reference);
        request.setCurCode("01");
        request.setDescription(description);
        request.setRemark(remark);
        request.setOrderState("01");
        request.setOrderType(payNow ? "00" : "01");
        request.setTransData(transData);
        request.setReturnAddress(returnUrl);

        MD5 md5 = new MD5();

        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);

        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        return resMessage;
    }

    /** 代收交易: 企业委托通过资金代收通道收款
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum  持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param amount  订单金额
     * @param orderNo 订单编号， 商户提交商户订单编号，前两位标明类型，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param description 订单描述, 长度不要超过50个字，前两位可以配置语音合成类型。
     *          “00”	中文
     *          “01”	粤语
     *          “02”	英文
     * @param remark 订单备注
     * @param payNow 是否及时支付
     * @param returnUrl 订单支付结果异步返回地址，前两位标明地址类型，同步交易请填空。
     *                   如果该域非空, 手机支付返回交易结果（参照6.2）到该地址, 
     *                   商户收到结果后返回确认信息（参照5.8），如果返回地址为Servlet地址，
     *                   则直接返回“0000”代表商户接收结果成功，无需返回页面.
     *                   任何正常页面返回均表示接收结果成功。
     *
     *           “01”	Socket异步返回接收地址(暂不支持) 内容格式：[IP|PORT]
     *           “02”	Http异步返回接收地址 内容格式：[URL]
     *           “03”	WebService异步返回接收地址,内容格式：[URL|NAMESPACE]
     *           “04”	Http同步/异步返回接收地址,内容格式：[URL|URL]
     *           “05”	CA签名XML异步返回接收地址, 内容格式[URL]
     *           “06”	XML异步返回接收地址, 内容格式[URL]
     *      
     * @param transData 提供业务交换数据包括银行开户姓名，开户证件，开户银行所在省市，开户证件类型等，字段之间以‘|’分割, 无用留空即可
     *          1	开户姓名            银行卡开户姓名
     *          2	开户证件号码        银行卡开户证件号码
     *          3	开户银行所在省市    省市已逗号分割，比如“广东省，深圳市”, ‘北京市’
     *          4	开户证件类型        银行卡开户证件类型，参照7.2说明
     *          5	订单受益人姓名      如机票乘机人, 多个受益人以逗号分割,对于受益人较多的情况,至少填两个,如果受益人包括开户人,需包含在内.
     *          6	持卡人IP地址        持卡人登陆商户网站的IP地址
     *          7	开户证件地址        开户证件地址不需全部提供,截取至街道即可,常见街道关键字包括：路/街道/街/胡同/道/条/里/镇/乡/村/庄/弄/巷/宅/�/屯/巷/寨/组/队/园/院
     *          8	受益人手机号	   受益人手机号(手机充值必填)
     *          9	产品销售地	   省市已逗号分割，比如“广东省，深圳市”, ‘北京市’(团购必填)
     *          10	开户银行登记手机号  (5.2身份验证,交易参考标明要填的必填)
     *          11	其他行业风控数据    例如：
     *                                          Apple:appid
     *                                          代付:银行联行号|银行号
     *                                          信用卡还款:信用卡号|姓名|证件号|证据类型
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception
     */
    public PosMessage PayTrust(String acqSsn, String accountNum, String pin,
            String amount, String orderNo, String reference, String description, String remark,
            boolean payNow, String returnUrl, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0200");
        request.setAccountNum(accountNum);
        request.setProcessCode("190002");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setMerchantNo(merchantNo);
        request.setTerminalNo(terminalNo);
        request.setOrderNo(orderNo);
        request.setPin(pin);
        request.setReference(reference);
        request.setCurCode("01");
        request.setDescription(description);
        request.setRemark(remark);
        request.setOrderState("01");
        request.setOrderType(payNow ? "00" : "01");
        request.setTransData(transData);
        request.setReturnAddress(returnUrl);

        MD5 md5 = new MD5();

        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);

        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        return resMessage;
    }

    /** 代收交易: 企业委托通过资金代收通道收款
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum  持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param amount  订单金额
     * @param orderNo 订单编号， 商户提交商户订单编号，前两位标明类型，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param description 订单描述, 长度不要超过50个字，前两位可以配置语音合成类型。
     *          “00”	中文
     *          “01”	粤语
     *          “02”	英文
     * @param remark 订单备注
     * @param payNow 是否及时支付
     * @param returnUrl 订单支付结果异步返回地址，前两位标明地址类型，同步交易请填空。
     *                   如果该域非空, 手机支付返回交易结果（参照6.2）到该地址, 
     *                   商户收到结果后返回确认信息（参照5.8），如果返回地址为Servlet地址，
     *                   则直接返回“0000”代表商户接收结果成功，无需返回页面.
     *                   任何正常页面返回均表示接收结果成功。
     *
     *           “01”	Socket异步返回接收地址(暂不支持) 内容格式：[IP|PORT]
     *           “02”	Http异步返回接收地址 内容格式：[URL]
     *           “03”	WebService异步返回接收地址,内容格式：[URL|NAMESPACE]
     *           “04”	Http同步/异步返回接收地址,内容格式：[URL|URL]
     *           “05”	CA签名XML异步返回接收地址, 内容格式[URL]
     *           “06”	XML异步返回接收地址, 内容格式[URL]
     *      
     * @param transData 提供业务交换数据包括银行开户姓名，开户证件，开户银行所在省市，开户证件类型等，字段之间以‘|’分割, 无用留空即可
     *          1	开户姓名            银行卡开户姓名
     *          2	开户证件号码        银行卡开户证件号码
     *          3	开户银行所在省市    省市已逗号分割，比如“广东省，深圳市”, ‘北京市’
     *          4	开户证件类型        银行卡开户证件类型，参照7.2说明
     *          5	订单受益人姓名      如机票乘机人, 多个受益人以逗号分割,对于受益人较多的情况,至少填两个,如果受益人包括开户人,需包含在内.
     *          6	持卡人IP地址        持卡人登陆商户网站的IP地址
     *          7	开户证件地址        开户证件地址不需全部提供,截取至街道即可,常见街道关键字包括：路/街道/街/胡同/道/条/里/镇/乡/村/庄/弄/巷/宅/�/屯/巷/寨/组/队/园/院
     *          8	受益人手机号	   受益人手机号(手机充值必填)
     *          9	产品销售地	   省市已逗号分割，比如“广东省，深圳市”, ‘北京市’(团购必填)
     *          10	开户银行登记手机号  (5.2身份验证,交易参考标明要填的必填)
     *          11	其他行业风控数据    例如：
     *                                          Apple:appid
     *                                          代付:银行联行号|银行号
     *                                          信用卡还款:信用卡号|姓名|证件号|证据类型
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception
     */
    public PosMessage payCreditCard(String acqSsn, String accountNum, String pin,
            String amount, String orderNo, String reference, String description, String remark,
            String returnUrl, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0200");
        request.setAccountNum(accountNum);
        request.setProcessCode("190004");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setMerchantNo(merchantNo);
        request.setTerminalNo(terminalNo);
        request.setOrderNo(orderNo);
        request.setPin(pin);
        request.setReference(reference);
        request.setCurCode("01");
        request.setDescription(description);
        request.setRemark(remark);
        request.setOrderState("01");
        request.setOrderType("00");
        request.setTransData(transData);
        request.setReturnAddress(returnUrl);

        MD5 md5 = new MD5();

        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);

        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        return resMessage;
    }

    /** 非即时订单支付:发起非即时订单(190001:非即时下单)的支付流程,适用于语音一线通方案.
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum  持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     *          “24”	手机号码+银行卡号+身份证号
     *          “31”	手机号码+信用卡号+身份证号
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param amount  订单金额
     * @param orderNo 订单编号， 商户提交商户订单编号，前两位标明类型，同一个商户订单编号只能支付成功一次，交易返回时银联语音支付加上DNA订单编号
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param description 订单描述, 长度不要超过50个字，前两位可以配置语音合成类型。
     *          “00”	中文
     *          “01”	粤语
     *          “02”	英文
     * @param remark 订单备注  
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示支付成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception
     */
    public PosMessage payIvr(String acqSsn, String accountNum, String pin,
            String amount, String orderNo, String reference, String description, String remark, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0200");
        request.setAccountNum(accountNum);
        request.setProcessCode("310001");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setMerchantNo(merchantNo);
        request.setTerminalNo(terminalNo);
        request.setOrderNo(orderNo);
        request.setPin(pin);
        request.setReference(reference);
        request.setCurCode("01");
        request.setDescription(description);
        request.setRemark(remark);
        request.setOrderState("01");
        request.setOrderType("00");

        MD5 md5 = new MD5();

        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);

        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        return resMessage;
    }

    /**
     * 订单撤销，拨打持卡人手机完成交易撤销授权
     * 
     * @param acqSsn 系统跟踪号
     * @param accountNum  持卡人帐号，可为空，暂时支持 14，21格式
     *          “01“	借记卡号
     *          “02”	信用卡号
     *          “04”	手机号
     *          “05”	身份证号
     *          “14”	手机号码+银行卡号(举例："14"+"手机号"+"|"+"银行卡号")
     *          “21”	手机号码+信用卡号(举例："14"+"手机号"+"|"+"信用卡号")
     * 
     * @param pin 交易密码，语音插件模式需要填，语音外呼模式填空，需用UPOP公钥加密， 算法请参照<<银联语音支付平台接口规范>>说明
     * @param amount  订单金额
     * @param orderNo 订单编号， 商户提交DNA订单编号/商户订单编号，前两位标明类型.
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param description 订单描述, 长度不要超过50个字，前两位可以配置语音合成类型。
     *          “00”	中文
     *          “01”	粤语
     *          “02”	英文
     * @param remark 订单撤销备注
     * @param returnUrl 订单支付结果异步返回地址，前两位标明地址类型，同步交易请填空。
     *                   如果该域非空, 手机支付返回交易结果（参照6.2）到该地址, 
     *                   商户收到结果后返回确认信息（参照5.8），如果返回地址为Servlet地址，
     *                   则直接返回“0000”代表商户接收结果成功，无需返回页面.
     *                   任何正常页面返回均表示接收结果成功。
     *
     *           “01”	Socket异步返回接收地址(暂不支持) 内容格式：[IP|PORT]
     *           “02”	Http异步返回接收地址 内容格式：[URL]
     *           “03”	WebService异步返回接收地址,内容格式：[URL|NAMESPACE]
     *           “04”	Http同步/异步返回接收地址,内容格式：[URL|URL]
     *           “05”	CA签名XML异步返回接收地址, 内容格式[URL]
     *           “06”	XML异步返回接收地址, 内容格式[URL]
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密     
     * @return RespCode 0000 表示撤销成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception
     */
    public PosMessage refund(String acqSsn, String accountNum, String pin,
            String amount, String orderNo, String reference, String remark, String returnUrl, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0220");
        request.setAccountNum(accountNum);
        request.setProcessCode("290000");
        request.setAmount(amount);
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setPin(pin);
        request.setReference(reference);
        request.setRemark(remark);
        request.setCurCode("01");
        MD5 md5 = new MD5();

        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);
        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        return resMessage;
    }

    /**
     * 订单缴费冲正，无需拨打持卡人手机完成交易授权
     * 
     * @param acqSsn 原交易系统跟踪号
     * @param transDatetime 原交易传输日期
     * @param orderNo 订单编号, 商户提交DNA订单编号/商户订单编号，前两位标明类型.
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param reference 系统参考号， 原值返回。
     * @param remark 订单撤销备注
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示冲正交易成功, 其他错误码请参照“银联语音支付平台错误码列表”
     */
    public PosMessage quash(String acqSsn, String transDatetime, String orderNo, String remark, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0220");
        request.setProcessCode("290001");
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setCurCode("01");
        request.setRemark(remark);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);
        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果校验失败");
        }

        return resMessage;
    }

    /**
     * 调账退货申请,清算日期之外的全额退货或清算日期之内部分退货申请，调帐退货在申请当日可以登录银联手机认证支付后退取消申请，隔日系统将自动处理。
     * 
     * @param acqSsn 系统跟踪号
     * @param orderNo 订单编号， 商户提交DNA订单编号/商户订单编号，前两位标明类型.
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param transData 持卡人开户名称+“|”+持卡人开户银行, 如果为空，DNA自动读取系统记录信息.
     * @param remark 订单调帐备注
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示申请成功, 其他错误码请参照“银联语音支付平台错误码列表”
     */
    public PosMessage adjustApply(String acqSsn, String orderNo, String remark, String transData, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0220");
        request.setProcessCode("290003");
        request.setTransDatetime(Formatter.MMddHHmmss(new java.util.Date()));
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setCurCode("01");
        request.setTransData(transData);

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);
        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果MAC校验失败");
        }

        return resMessage;
    }

    /** 调账退货查询
     * 
     * @param acqSsn 申请时的系统跟踪号
     * @param transDateTime 申请时的传输日期和时间
     * @param orderNo 订单编号， 商户提交DNA订单编号/商户订单编号，前两位标明类型.
     *                “01”	银联手机支付(DNA)订单编号
     *                “02”	商户订单编号，网页自助下单（WEB）
     *                “03”	商户订单编号，客服电话下单（CallCenter）
     *                “04”	商户订单编号，电话自助下单（IVR）
     *                “05”	商户订单编号，手机自助下单（WAP）
     * 
     *                “12”	商户订单编号（WEB）+‘|’+DNA订单编号
     *                “13”	商户订单编号（CallCenter）+‘|’+DNA订单编号
     *                “14”	商户订单编号（IVR）+‘|’+DNA订单编号
     *                “15”	商户订单编号（WAP）+‘|’+DNA订单编号
     * 
     * @param transData 持卡人开户名称+“|”+持卡人开户银行, 如果为空，DNA自动读取系统记录信息.
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return RespCode 0000 表示查询成功, 其他错误码请参照“银联语音支付平台错误码列表”
     * @throws Exception 
     */
    public PosMessage adjustQuery(String acqSsn, String transDateTime, String orderNo, String encryptKey) throws Exception {

        PosMessage request = new PosMessage("0220");
        request.setProcessCode("290004");
        request.setTransDatetime(transDateTime);
        request.setLtime(Formatter.HHmmss(new java.util.Date()));
        request.setLdate(Formatter.yyyyMMdd(new java.util.Date()).substring(4));
        request.setAcqSsn(acqSsn);
        request.setTerminalNo(terminalNo);
        request.setMerchantNo(merchantNo);
        request.setOrderNo(orderNo);
        request.setCurCode("01");

        MD5 md5 = new MD5();
        request.setMac(md5.getMD5ofStr(TransactionUtil.getMacString(request) + " " + merchantPWD));

        PosMessage resMessage = this.transact(request, encryptKey);
        if (!resMessage.getMac().toUpperCase().equals(
                md5.getMD5ofStr(TransactionUtil.getMacString(resMessage) + " " + merchantPWD))) {
            throw new Exception("返回结果MAC校验失败");
        }

        return resMessage;
    }

    /** 交易处理, 根据TransactionType访问相应的服务接口,并返回处理结果.
     * 
     * @param request 请求参数, 请参照<<银联语音支付平台接口规范>>, 4.1交易参数说明.
     * @param encryptKey 加密密钥,24位,每笔交易随机生成, 用于CA接口发送交易报文时加密, 返回报文时解密
     * @return 请参照<<银联语音支付平台接口规范>>, 第5章交易报文说明.
     * @throws Exception 
     */
    public PosMessage transact(PosMessage request, String encryptKey) throws Exception {
        if (this.getTransactionType().equals(TransactionType.CA)) {
            return transactCA(request, encryptKey);
        } else if (this.getTransactionType().equals(TransactionType.XML)) {
            return transactXML(request);
        } else if (this.getTransactionType().equals(TransactionType.SOCKET)) {
            return transactSocketXML(request);
        } else {
            return transactWS(request);
        }
    }

    /** 银联语音支付WebService第三方交易接口请求方法
     * @param request 请参照“银联语音支付平台接口规范.doc”
     * @return PosMessage 请参照“银联语音支付平台接口规范.doc”
     * @throws Exception 
     */
    public PosMessage transactWS(PosMessage request) throws Exception {
        ToolKit.writeLog(TransactionClient.class.getName(), "transactWS.send",
                url + "|" + nameSpace + "|" + timeout + "|" + request.toString());

        IOrderServerWSProxy Client = null;

        try {

            Client = new IOrderServerWSProxy(url, nameSpace);

            com.ruyicai.charge.dna.v2.thirdpart.jaws.PosMessage result = Client.transact(TransactionUtil.translate(request));

            PosMessage payResult = TransactionUtil.translate(result);

            ToolKit.writeLog(TransactionClient.class.getName(), "transactWS.result",
                    payResult.toString());

            return payResult;

        } catch (Exception e) {

            ToolKit.writeLog(TransactionClient.class.getName(), "transactWS", e);
            throw e;
        }
    }

    /** 银联语音支付XML CA签名第三方交易接口请求方法
     * @param request 请参照“银联语音支付平台接口规范.doc”
     * @param encryptKey 加密密钥,24位,每笔交易随机生成
     * @return PosMessage 请参照“银联语音支付平台接口规范.doc”
     * @throws Exception 
     */
    public PosMessage transactCA(PosMessage request, String encryptKey) throws Exception {
        ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.send",
                url + "|" + nameSpace + "|" + timeout + "|" + request.toString());

        try {
            HttpURLConnection connect = null;
            if (!url.contains("https:")) {
                URL urlConnect = new URL(url);
                connect = (HttpURLConnection) urlConnect.openConnection();
            } else {
                SslConnection urlConnect = new SslConnection();
                connect = (HttpURLConnection) urlConnect.openConnection(url);
            }

            connect.setReadTimeout(timeout);
            connect.setConnectTimeout(timeout);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);
            connect.setRequestProperty("content-type", "text/html;charset=utf-8");

            String xml = TransactionUtil.posMessageToXml(request);
            ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.xml", xml);
            RSAProvider rsa = new RSAProvider();
            xml = rsa.sign(encryptKey, xml, this.getServerCert());
            xml = Strings.padLeft(xml.length() + "", 6) + xml;
            ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.sign", xml);

            connect.connect();
            BufferedOutputStream out = new BufferedOutputStream(connect.getOutputStream());

            out.write(xml.getBytes("UTF-8"));
            out.flush();
            out.close();

            BufferedInputStream in = new BufferedInputStream(connect.getInputStream());
            byte[] bts = new byte[10000];
            int totalLen = 0, len = 0;
            while ((len = in.read(bts, totalLen, 1000)) != -1) {
                totalLen += len;
                ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.result", "" + len);
            }

            String result = Strings.toString(new String(bts, "UTF-8"));
            ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.result", result);
            result = rsa.verify(encryptKey, result.substring(6));
            ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.verify", result);
            return TransactionUtil.xmlToPosMessage(result);

        } catch (Exception e) {

            ToolKit.writeLog(TransactionClient.class.getName(), "transactCA", e);
            throw e;
        }
    }

    /** 银联语音支付XML CA签名第三方交易接口请求方法
     * @param request 请参照“银联语音支付平台接口规范.doc”
     * @param encryptKey 加密密钥,24位,每笔交易随机生成
     * @return PosMessage 请参照“银联语音支付平台接口规范.doc”
     * @throws Exception 
     */
    public VpcMessage transactVPC(VpcMessage request) throws Exception {
        ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.send",
                url + "|" + nameSpace + "|" + timeout + "|" + request.toString());

        try {
            HttpURLConnection connect = null;
            if (!url.contains("https:")) {
                URL urlConnect = new URL(url);
                connect = (HttpURLConnection) urlConnect.openConnection();
            } else {
                SslConnection urlConnect = new SslConnection();
                connect = (HttpURLConnection) urlConnect.openConnection(url);
            }

            connect.setReadTimeout(timeout);
            connect.setConnectTimeout(timeout);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);
            connect.setRequestProperty("content-type", "text/html;charset="+this.getServerEncoding());

            String xml = TransactionUtil.toXml(request);
            ToolKit.writeLog(TransactionClient.class.getName(), "transactVPC.xml", xml);

            String key = Strings.random(32);

            xml = Formatter.base64Encode(TripleDes.encrypt(key.substring(0, 24).getBytes(this.getServerEncoding()), xml.getBytes(this.getServerEncoding())));
            key = Formatter.base64Encode(RSA.encrypt64(key.getBytes(this.getServerEncoding()), ToolKit.getPropertyFromFile("VPC_CERT_PUB_64")));//.encryptByPrivateKey(key.getBytes("utf-8"), ToolKit.getPropertyFromFile("VPC_CERT_PFX"),
            xml = xml + "&" + key;
            ToolKit.writeLog(this.getClass().getName(), "encrypt", xml.toString());

            connect.connect();
            BufferedOutputStream out = new BufferedOutputStream(connect.getOutputStream());

            out.write(xml.getBytes(this.getServerEncoding()));
            out.flush();
            out.close();

            BufferedInputStream in = new BufferedInputStream(connect.getInputStream());
            byte[] bts = new byte[10000];
            int totalLen = 0, len = 0;
            while ((len = in.read(bts, totalLen, 1000)) != -1) {
                totalLen += len;
                ToolKit.writeLog(TransactionClient.class.getName(), "transactVPC.result", "" + len);
            }

            String result = Strings.toString(new String(bts, this.getServerEncoding()));
            ToolKit.writeLog(TransactionClient.class.getName(), "transactVPC.result", result);
            String[] encryptValues = result.split("&");
            byte[] keyBt = null;
            if(request.getMessageType().equals("0300")){
                   keyBt = RSA.decrypt(Formatter.base64Decode(encryptValues[1]),
                    ToolKit.getPropertyFromFile("VPC_CERT_PFX"),
                    ToolKit.getPropertyFromFile("VPC_CERT_PFX_PASSWD"));
            } else {
                    keyBt = RSA.decryptByPublicKey(Formatter.base64Decode(encryptValues[1]), ToolKit.getPropertyFromFile("VPC_CERT_PUB_64"));
            }
            byte[] key24 = new byte[24];
            ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.key", new String(keyBt));
            ToolKit.writeLog(TransactionClient.class.getName(), "transactCA.keyBt", keyBt.length + "");
            System.arraycopy(keyBt, 0, key24, 0, 24);

            byte[] srcBt = TripleDes.decrypt(key24, Formatter.base64Decode(encryptValues[0]));
            String strMsg = new String(srcBt, this.getServerEncoding());
            ToolKit.writeLog(this.getClass().getName(), "decrypt", strMsg);

            return TransactionUtil.toVpcMessage(strMsg);

        } catch (Exception e) {

            ToolKit.writeLog(TransactionClient.class.getName(), "transactVPC", e);
            throw e;
        }
    }

    /** 银联语音支付XML第三方交易接口请求方法
     * @param request 请参照“银联语音支付平台接口规范.doc”
     * @return PosMessage 请参照“银联语音支付平台接口规范.doc”
     * @throws Exception 
     */
    public PosMessage transactXML(PosMessage request) throws Exception {
        ToolKit.writeLog(TransactionClient.class.getName(), "transactXml.send",
                url + "|" + nameSpace + "|" + timeout + "|" + request.toString());

        try {
            HttpURLConnection connect = null;
            if (!url.contains("https:")) {
                URL urlConnect = new URL(url);
                connect = (HttpURLConnection) urlConnect.openConnection();
            } else {
                SslConnection urlConnect = new SslConnection();
                connect = (HttpURLConnection) urlConnect.openConnection(url);
            }

            connect.setReadTimeout(this.getTimeout());
            connect.setConnectTimeout(timeout);
            connect.setRequestMethod("POST");
            connect.setDoInput(true);
            connect.setDoOutput(true);
            connect.setRequestProperty("content-type", "text/html;charset=utf-8");

            String xml = TransactionUtil.posMessageToXml(request);
            ToolKit.writeLog(TransactionClient.class.getName(), "transactXml.xml", xml);
            connect.connect();
            BufferedOutputStream out = new BufferedOutputStream(connect.getOutputStream());
            out.write(xml.getBytes("UTF-8"));
            out.flush();
            out.close();

            BufferedInputStream in = new BufferedInputStream(connect.getInputStream());
            byte[] bts = new byte[5000];
            int totalLen = 0, len = 0;
            while ((len = in.read(bts, totalLen, 1000)) != -1) {
                totalLen += len;
                ToolKit.writeLog(TransactionClient.class.getName(), "transactWeb.result", "" + len);
            }

            String result = Strings.toString(new String(bts, "UTF-8"));
            ToolKit.writeLog(TransactionClient.class.getName(), "transactWeb.result", result);
            return TransactionUtil.xmlToPosMessage(result);

        } catch (Exception e) {
            ToolKit.writeLog(TransactionClient.class.getName(), "transactXml", e);
            throw e;
        }
    }

    /** 银联语音支付XML第三方SOCKET交易接口请求方法
     * @param request 请参照“银联语音支付平台接口规范.doc”
     * @return PosMessage 请参照“银联语音支付平台接口规范.doc”
     * @throws Exception 
     */
    public PosMessage transactSocketXML(PosMessage request) throws Exception {
        ToolKit.writeLog(TransactionClient.class.getName(), "transactSocketXML.send",
                url + "|" + nameSpace + "|" + timeout + "|" + request.toString());

        try {
            SocketClient client = new SocketClient(url, Integer.valueOf(nameSpace));
            client.getMSocket().setSoTimeout(timeout);

            client.Send(request);
            PosMessage result = client.ReceiveSocketMessage();
            client.Close();
            return result;

        } catch (Exception e) {
            ToolKit.writeLog(TransactionClient.class.getName(), "transactSocketXML", e);
            throw e;
        }
    }
}
