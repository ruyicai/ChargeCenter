/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruyicai.charge.dna.v2.ca;

import java.io.IOException;

import com.ruyicai.charge.dna.v2.common.Formatter;
import com.ruyicai.charge.dna.v2.common.Strings;
import com.ruyicai.charge.dna.v2.common.ToolKit;
import com.ruyicai.charge.dna.v2.common.encrpt.MD5;
import com.ruyicai.charge.dna.v2.common.encrpt.RSA;
import com.ruyicai.charge.dna.v2.common.encrpt.TripleDes;

public class RSAProvider {

    private String version = "";
    private String cert = "";
    private String keyEncrypt = "";
    private String key = "";
    private String srcEncrypt = "";

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public RSAProvider() {
        this.version = ToolKit.getPropertyFromFile("GDYILIAN_CERT_METHOD");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyEncrypt() {
        return keyEncrypt;
    }

    public void setKeyEncrypt(String keyEncrypt) {
        this.keyEncrypt = keyEncrypt;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSrcEncrypt() {
        return srcEncrypt;
    }

    public void setSrcEncrypt(String srcEncrypt) {
        this.srcEncrypt = srcEncrypt;
    }

    public String getSrcSign() {
        return srcSign;
    }

    public void setSrcSign(String srcSign) {
        this.srcSign = srcSign;
    }
    private String src = "";
    private String srcSign = "";

    /** 密钥加密算法, 报文加密算法, 报文签名算法
     * 
     * @return  RSA.3DES.MD5, RSA.3DES.MD5withRSA
     */
    public String getVersion() {
        return version;
    }

    /** 对报文数据进行加密签名
     *  加密报文体格式：BASE64(版本号))｜BASE64(签名公钥)|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5withRSA(报文原文))
     * @param cert base64公钥，　如果为空，不生成报文加密密钥．
     * @param srcPara 报文原文
     * @param keyPara 动态密钥，　如果为空自动随机生成
     * @return 加密报文体格式：BASE64(版本号))｜BASE64(签名公钥)|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5withRSA(报文原文))
     * @throws Exception 
     */
    public String sign(String keyPara, String srcPara, String cert) throws Exception {
//        ToolKit.writeLog(RSAProvider.class.getName(), "sign.cert", cert);
//        ToolKit.writeLog(RSAProvider.class.getName(), "sign.src", srcPara);

        if (Strings.isNullOrEmpty(keyPara)) {
            keyPara = Strings.random(24);
        }
        this.setKey(keyPara);
        this.setSrc(srcPara);
        ToolKit.writeLog(RSAProvider.class.getName(), "sign.key", key);

        if (!Strings.isNullOrEmpty(cert)) { //公钥为空，不加密密钥
            byte[] keyEnc = RSA.encrypt64(key.getBytes("UTF-8"), cert);
            this.setKeyEncrypt(Formatter.base64Encode(keyEnc));
        }

        byte[] srcEnc = TripleDes.encrypt(key.getBytes("UTF-8"), src.getBytes("UTF-8"));
        this.setSrcEncrypt(Formatter.base64Encode(srcEnc));

        if (this.getVersion().equals("RSA.3DES.MD5withRSA")) {
            byte[] srcSigned = RSA.sign(src.getBytes("UTF-8"), ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX"),
                    ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX_PASSWD"));
            this.setSrcSign(Formatter.base64Encode(srcSigned));
            byte[] pub_key = RSA.getPublicKey(ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX"), ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX_PASSWD")).getEncoded();
            this.setCert(Formatter.base64Encode(pub_key));
            
//            this.setCert(ToolKit.getPropertyFromFile("GDYILIAN_CERT_PUB_64"));
        } else {
            MD5 md5 = new MD5();
            String strSign = md5.getMD5ofByte(src.getBytes("UTF-8"));
            this.setSrcSign(Formatter.base64Encode(strSign.getBytes("UTF-8")));
        }

        String tData = Formatter.base64Encode(this.getVersion().getBytes("UTF-8"));
        tData += "|" + this.getCert();
        tData += "|" + this.getKeyEncrypt();
        tData += "|" + this.getSrcEncrypt();
        tData += "|" + this.getSrcSign();

//        ToolKit.writeLog(RSAProvider.class.getName(), "sign.data", tData);

        return tData;
    }

    /** 对报文数据进行解密验签
     *  加密报文体格式：BASE64(版本号))｜BASE64(签名公钥)|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5withRSA(报文原文))
     * 
     * @param sign 加密报文
     * @param cert 验签公钥，版本为＂RSA.3DES.MD5withRSA＂时验签使用．　
     * @param keyPara 报文密钥, 报文加密密钥为空时使用
     * @return 报文原文
     * @throws Exception 
     */
    public String verify(String keyPara, String sign) throws Exception {
//        ToolKit.writeLog(RSAProvider.class.getName(), "verify.sign", sign);

        String[] values = sign.split("\\|");
        this.version = new String(Formatter.base64Decode(values[0]), "UTF-8");
        this.setCert(values[1]);
        this.setKeyEncrypt(values[2]);
        this.setSrcEncrypt(values[3]);
        this.setSrcSign(values[4]);
        this.setKey(keyPara);

        byte[] keyEnc = Formatter.base64Decode(this.getKeyEncrypt());
        byte[] srcEnc = Formatter.base64Decode(this.getSrcEncrypt());
        byte[] srcSigned = Formatter.base64Decode(this.getSrcSign());

        byte[] keyBt = null;

        if (Strings.isNullOrEmpty(keyPara)) {
            keyBt = this.decryptKey(keyEnc);
            this.setKey(new String(keyBt, "UTF-8"));
        } else {
            keyBt = keyPara.getBytes("UTF-8");
        }

        byte[] srcBt = TripleDes.decrypt(keyBt, srcEnc);

        this.setSrc(new String(srcBt, "UTF-8"));

//        ToolKit.writeLog(RSAProvider.class.getName(), "verify.key", key);
//        ToolKit.writeLog(RSAProvider.class.getName(), "verify.src", src);

        if (this.getVersion().equals("RSA.3DES.MD5withRSA") && !Strings.isNullOrEmpty(cert)) {
            if (!RSA.verify(srcSigned, cert, src)) {
                throw new Exception("fail to verifySignedData");
            }
        } else {
            MD5 md5 = new MD5();
            String strSign = md5.getMD5ofByte(srcBt);
            if (!strSign.equals(new String(srcSigned, "UTF-8"))) {
                throw new Exception("fail to verifySignedData");
            }
        }

        return src;
    }

    /** 解密加密密钥
     * 
     * @param keyEncrypt base64
     * @return
     * @throws IOException 
     */
    public String decryptKey(String keyEncrypt) throws IOException {
        byte[] keyBt = RSA.decrypt(Formatter.base64Decode(keyEncrypt),
                ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX"),
                ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX_PASSWD"));
        return new String(keyBt, "UTF-8");
    }

    public byte[] decryptKey(byte[] keyEncrypt) throws IOException {
        byte[] keyBt = RSA.decrypt(keyEncrypt,
                ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX"),
                ToolKit.getPropertyFromFile("GDYILIAN_CERT_PFX_PASSWD"));
        return keyBt;
    }

    public static void main(String[] args) throws Exception {
        RSAProvider rsa = new RSAProvider();
        String key = Strings.random(24);
        String signData = rsa.sign(key, "123456测试", ToolKit.getPropertyFromFile("GDYILIAN_CERT_PUB_64"));
        System.out.println(signData);
        String Data = rsa.verify(key, signData);
        System.out.println(Data);
    }
}
