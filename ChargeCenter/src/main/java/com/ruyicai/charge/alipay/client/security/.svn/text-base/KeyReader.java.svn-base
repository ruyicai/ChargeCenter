/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2006 All Rights Reserved.
 */
package com.ruyicai.charge.alipay.client.security;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 密钥读取工具 
 * @author stone.zhangjl
 * @version $Id: KeyReader.java, v 0.1 2008-8-21 下午04:59:42 stone.zhangjl Exp $
 */
public class KeyReader {

    /**
     * 将X509格式的输入流转换成Certificate对象。
     * 
     * @param ins
     * @return
     * @throws AlipayException 
     */
    public static Certificate getCertificateFromX509(InputStream ins) throws Exception {

        Certificate certificate = CertificateFactory.getInstance("X.509").generateCertificate(ins);

        return certificate;
    }

    /**
     * @param algorithm
     * @param ins
     * @return
     * @throws NoSuchAlgorithmException
     * @throws AlipayException 
     */
    public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins)
                                                                                   throws NoSuchAlgorithmException,
                                                                                   Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);

        byte[] encodedKey = writer.toString().getBytes();

        // 先base64解码
        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    /**
     * @param algorithm
     * @param ins
     * @return
     * @throws NoSuchAlgorithmException
     * @throws AlipayException 
     */
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins)
                                                                                      throws NoSuchAlgorithmException,
                                                                                      Exception {
        if (ins == null || StringUtil.isBlank(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

        byte[] encodedKey = StreamUtil.readText(ins).getBytes();

        // 先base64解码
        encodedKey = Base64.decodeBase64(encodedKey);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }
}
