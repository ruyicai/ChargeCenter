/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2006 All Rights Reserved.
 */
package com.ruyicai.charge.alipay.client.security;

/**
 * 安全管理实现类
 * 
 * @author jun.huyj
 * @version $Id: SecurityManagerImpl.java, v 0.1 Nov 11, 2008 10:03:38 AM jun.huyj Exp $
 */
public class SecurityManagerImpl implements SecurityManager {

    private Encrypt   encrypt;

    private Signature signature;

    public String decrypt(String algoType, String content, String key) throws Exception {
        if (algoType.equals("RSA")) {
            //rsa解密
            encrypt = new RSAEncrypt();
        } else if (algoType.equals("TripleDES")) {
            //
            encrypt = new TripleDESEncrypt();
        } else {
            throw new Exception("本应用不支持的算法");
        }
        return encrypt.decrypt(content, key);
    }

    public String encrypt(String algoType, String content, String key) throws Exception {
        if (algoType.equals("RSA")) {
            //rsa解密
            encrypt = new RSAEncrypt();
        } else if (algoType.equals("TripleDES")) {
            //
            encrypt = new TripleDESEncrypt();
        } else {
            throw new Exception("本应用不支持的算法");
        }
        return encrypt.encrypt(content, key);
    }

    public String sign(String algoType, String content, String key) throws Exception {
        if (algoType.equals("RSA")) {
            //rsa解密
            signature = new RSASignature();
        } else if (algoType.equals("DSA")) {
            //
            signature = new DSASignature();
        } else {
            throw new Exception("本应用不支持的算法");
        }
        return signature.sign(content, key);
    }

    public boolean verify(String algoType, String content, String sign, String key)
                                                                                   throws Exception {
        if (algoType.equals("RSA")) {
            //rsa解密
            signature = new RSASignature();
        } else if (algoType.equals("DSA")) {
            //
            signature = new DSASignature();
        } else {
            throw new Exception("本应用不支持的算法");
        }
        return signature.verify(content, sign, key);
    }
}
