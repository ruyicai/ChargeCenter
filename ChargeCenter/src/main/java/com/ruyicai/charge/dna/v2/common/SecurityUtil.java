/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruyicai.charge.dna.v2.common;

import com.ruyicai.charge.dna.v2.ca.CAProvider;
import com.ruyicai.charge.dna.v2.ca.RSAProvider;

/** 安全工具类。
 *
 * @author Administrator
 */
public class SecurityUtil {

    /** 公钥加密，私钥签名
     * 
     * @param cert 证书公钥
     * @param value 需签名加密内容
     * @return 签名加密后内容
     * @throws Exception 
     */
    public static String caSign(String cert, String value) throws Exception {
        String result = CAProvider.sign(cert, value);
        return result;
    }

    /** 公钥验签，私钥解密
     * 
     * @param value 签名加密后内容
     * @return 签名加密前内容
     * @throws Exception 
     */
    public static String caVerify(String value, boolean checkSign) throws Exception {
        String result = CAProvider.verify(value, checkSign);
        return result;
    }

    /** 对报文数据进行加密签名
     *  加密报文体格式：BASE64(版本号))|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5(报文原文))
     * @param cert base64公钥
     * @param src 报文原文
     * @param key 动态密钥，　如果为空自动随机生成
     * @return 加密报文体格式：BASE64(版本号))|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5(报文原文))
     * @throws Exception 
     */
    public static String rsaSign(String key, String src, String cert) throws Exception {
        String result = new RSAProvider().sign(key, src, cert);
        return result;
    }

    /** 对报文数据进行解密验签
     *  加密报文体格式：BASE64(版本号))|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5(报文原文))
     * 
     * @param sign 加密报文
     * @param cert 验签公钥，版本为＂RSA.3DES.MD5withRSA＂时验签使用．　
     * @param key 报文密钥, 报文加密密钥为空时使用
     * @return 报文原文
     * @throws Exception 
     */
    public static String rsaVerify(String key, String sign, String cert) throws Exception {
        String result = new RSAProvider().verify(key, sign);
        return result;
    }
}
