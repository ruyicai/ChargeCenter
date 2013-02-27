/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2006 All Rights Reserved.
 */
package com.ruyicai.charge.alipay.client.security;


/**
 * 签名接口 
 * @author jun.huyj
 * @version $Id: Signature.java, v 0.1 2008-8-21 下午02:55:03 stone.zhangjl Exp $
 */
public interface Signature {

    /**
     * 对原始数据进行签名
     * 
     * @param content 原始数据
     * @param key 私钥
     * @return 签名
     * @throws Exception
     */
    public String sign(String content, String key) throws Exception;

    /**
     * 验证签名
     * 
     * @param content 原始数据
     * @param sign 签名
     * @param key 公钥
     * @return 签名验证通过 False 签名验证失败
     * @throws Exception
     */
    public boolean verify(String content, String sign, String key) throws Exception;
}
