/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2006 All Rights Reserved.
 */
package com.ruyicai.charge.alipay.client.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;


//import com.alipay.client.util.KeyReader;

/**
 * RSA算法 
 * @author stone.zhangjl
 * @version $Id: RSAEncrypt.java, v 0.1 2008-8-22 下午03:28:13 stone.zhangjl Exp $
 */
public class RSAEncrypt implements Encrypt {

    /** 加密算法 */
    private static final String ALGORITHM   = "RSA";

    /** CIPHER算法名称 */
    private static final String CIPHER_NAME = "RSA/ECB/PKCS1Padding";

    /** 
     * 解密
     * 由于RSA一次只能加密128个字节（加密后也是128个字节），所以将密文按128个字节分割，分块解密，最后将解密后的字节流连接起来。
     * (non-Javadoc)
     * @see com.alipay.api.security.Encrypt#decrypt(java.lang.String, java.lang.String, java.lang.String, com.alipay.api.enums.EncryptStyleEnum)
     */
    public String decrypt(String content, String key) throws Exception {

        PrivateKey prikey = KeyReader.getPrivateKeyFromPKCS8(ALGORITHM, new ByteArrayInputStream(
            key.getBytes()));

        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, prikey);

        InputStream ins = new ByteArrayInputStream(Base64.decodeBase64(content.getBytes()));
        ByteArrayOutputStream writer = new ByteArrayOutputStream();

        byte[] buf = new byte[128];
        int bufl;

        while ((bufl = ins.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(cipher.doFinal(block));
        }

        return new String(writer.toByteArray(), "utf-8");
    }

    /** 
     * 加密
     * 由于RSA一次只能加密128个字节，所以将原始数据按100个字节分割，分块加密，最后将加密后的字节流连接起来。
     * (non-Javadoc)
     * @see com.alipay.api.security.Encrypt#encrypt(java.lang.String, java.lang.String, java.lang.String, com.alipay.api.enums.EncryptStyleEnum)
     */
    public String encrypt(String content, String key) throws Exception {
        PublicKey pubkey = KeyReader.getPublicKeyFromX509(ALGORITHM, new ByteArrayInputStream(key
            .getBytes()));

        Cipher cipher = Cipher.getInstance(CIPHER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, pubkey);

        InputStream inputReader = new ByteArrayInputStream(content.getBytes("utf-8"));
        ByteArrayOutputStream writer = new ByteArrayOutputStream();

        byte[] buf = new byte[100];
        int bufl;

        while ((bufl = inputReader.read(buf)) != -1) {
            byte[] block = null;

            if (buf.length == bufl) {
                block = buf;
            } else {
                block = new byte[bufl];
                for (int i = 0; i < bufl; i++) {
                    block[i] = buf[i];
                }
            }

            writer.write(cipher.doFinal(block));
        }

        return new String(Base64.encodeBase64(writer.toByteArray()));
    }

}
