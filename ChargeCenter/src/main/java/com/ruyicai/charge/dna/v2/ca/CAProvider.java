/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruyicai.charge.dna.v2.ca;

import com.ruyicai.charge.dna.v2.common.ToolKit;


/** CA数字证书生成类, 系统具体默认使用北京数字证书认证中心CA证书.
 *  服务器证书公钥可通过systemsetting.properties文件的SERVER_CERT参数获取.
 *  调用商户证书可通过systemsetting.properties文件的BJCA_INSTANCE_NAME指定配置.
 *  商户证书具体配置内容位于在C:/BJCAROOT(windows), /usr/BJCAROOT(linux)目录下BJCA_ServerConfig.xml文件中
 *
 * @author Administrator
 */
public class CAProvider {
    public static void main(String[] args) throws Exception {
        String signData = CAProvider.sign(CAProvider.getCA().getServerCert(), "d乱码dddd");
        System.out.println(signData);
         String Data = CAProvider.verify(signData, true);
          System.out.println(Data);
        
        
    }
    
    private static CA cfca = null;

    /** 调用商户证书, 通过systemsetting.properties文件的BJCA_INSTANCE_NAME指定配置.
     * 
     * @return CA证书实现类
     */
    public static CA getCA() {
        if (cfca == null) {
            try {
                cfca = new SZCA(ToolKit.getPropertyFromFile("BJCA_INSTANCE_NAME"));
            } catch (Exception ex) {
                ToolKit.writeLog(CAProvider.class.getName(), "getCA", ex);
            }
        }
        return cfca;
    }

    /** CA数字证书签名加密
     * 
     * @param cert 服务器证书公钥
     * @param value 需要签名加密的内容
     * @return 签名加密的内容
     * @throws Exception 
     */
    public static String sign(String cert, String value) throws Exception {
        return getCA().sign(cert, value);
    }

    /** 对签名加密数据进行验签。
     * 
     * @param sign 签名加密的内容
     * @return 签名加密的原文
     * @throws Exception 
     */
    public static String verify(String value, boolean checkSign) throws Exception {
        return getCA().verify(value, checkSign);
    }
}

