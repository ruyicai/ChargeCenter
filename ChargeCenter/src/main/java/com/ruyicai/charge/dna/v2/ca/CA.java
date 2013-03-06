/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruyicai.charge.dna.v2.ca;

/** CA证书接口
 *
 * @author Administrator
 */
public interface CA {

    /** 对字符串数据进行数字签名加密.
     * 
     * @param cert 服务器证书公钥
     * @param src 需要签名加密的内容
     * @return 签名加密的内容
     * @throws Exception 
     */
    String sign(String cert, String src) throws Exception;

    /** 对签名加密数据进行验签解密。
     * 
     * @param sign 签名加密的内容
     * @return 签名加密的原文
     * @throws Exception 
     */
    String verify(String sign, boolean checkSign) throws Exception;

    /** 获取本地服务器证书
     * 
     * @return 本地服务器证书
     * @throws Exception 
     */
    String getServerCert() throws Exception;

    /** 获取证书信息
     * 
     * @param sign Base64编码的X.509数字证书
     * @param Type type	意义
    1	证书版本
    2	证书序列号
    8	证书发放者通用名
    11	证书有效期起始
    12	证书有效期截止
    13	用户国家名
    14	用户组织名
    15	用户部门名
    16	用户省州名
    17	用户通用名
    18	用户城市名
    19	用户EMAIL地址
    20	证书颁发者DN
    21	证书主题（DN）
    23	用户国家名(备用名C)
    24	用户组织名(备用名O)
    25	用户部门名(备用名OU)
    26	用户省州名(备用名S)
    27	用户通用名(备用名CN)
    28	用户城市名(备用名L)
    29	用户EMAIL地址(备用名E)
    30	证书公钥（base64）    
     * @return CertInfo
     */
    String getCertInfo(String cert, int Type) throws Exception;
}
