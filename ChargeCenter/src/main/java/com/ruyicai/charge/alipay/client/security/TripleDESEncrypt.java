/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2006 All Rights Reserved.
 */
package com.ruyicai.charge.alipay.client.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 3DES算法 公钥加密，私钥解密
 * @author stone.zhangjl
 * @version $Id: TripleDESEncrypt.java, v 0.1 2008-8-22 下午03:38:39 stone.zhangjl Exp $
 */
public class TripleDESEncrypt implements Encrypt {

    /** 加密算法 */
    private static final String ALGORITHM_3DES = "DESede";

    /** 算法/加密模式/填充方式 */
    private static final String CIPHER_NAME    = "DESede/ECB/PKCS5Padding";

    /** 
     * 解密
     * (non-Javadoc)
     * @see com.alipay.api.security.Encrypt#decrypt(java.lang.String, java.lang.String, java.lang.String, com.alipay.api.enums.EncryptStyleEnum)
     */
    public String decrypt(String content, String key) throws Exception {
        byte[] cleanBytes = decrypt(Base64.decodeBase64(content.getBytes()), key.getBytes());

        return new String(cleanBytes, "utf-8");
    }

    /** 
     * 加密
     * (non-Javadoc)
     * @see com.alipay.api.security.Encrypt#encrypt(java.lang.String, java.lang.String, java.lang.String, com.alipay.api.enums.EncryptStyleEnum)
     */
    public String encrypt(String content, String key) throws Exception {

        byte[] encryptBytes = encrypt(content.getBytes("utf-8"), key.getBytes());

        return new String(Base64.encodeBase64(encryptBytes));

    }

    /**
     * @param srcBytes 被加密的数据缓冲区
     * @param keyBytes 密钥字节流
     * @return
     */
    private byte[] encrypt(byte[] srcBytes, byte[] keyBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM_3DES));

        return cipher.doFinal(srcBytes);
    }

    /**
     * @param src
     * @param keyBytes
     * @return
     * @throws PaygwException
     */
    private byte[] decrypt(byte[] src, byte[] keyBytes) throws Exception {

        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM_3DES));

        return cipher.doFinal(src);
    }

}
