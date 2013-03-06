/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruyicai.charge.dna.v2.ca;

import cn.org.bjca.security.SecurityEngineDeal;


/** 北京数字证书中心, 深圳分公司提供的CA证书实现.
 * 
 * @author Administrator
 */
public class SZCA implements CA {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        try {
            //初始化应用,调用BJCAROOT目录下应用中配置的证书
            SecurityEngineDeal sedServer = SecurityEngineDeal.getInstance("SecXV3Default");
            SecurityEngineDeal sedClient = SecurityEngineDeal.getInstance("BJCASecXV2Default");

            sedServer.setSignMethod("RSA-SHA1");
            sedClient.setSignMethod("RSA-SHA1");
            sedServer.setEncryptMethod("T-DES");
            sedClient.setEncryptMethod("T-DES");

            //原文
            String data = "BJCA天的天的天的天天的天的天的天天的天的天的天的天的天的天天的天的天的BJCA";
            System.out.println("原文++++++" + data);

            String certServer = sedServer.getServerCertificate();
            System.out.println("sedServer.getServerCertificate:++++++" + certServer);
            System.out.println("sedServer.validateCert:++++++" + sedServer.validateCert(certServer));

            System.out.println("sedServer.getCertInfo:版本++++++++" + sedServer.getCertInfo(certServer, 1));
            System.out.println("sedServer.getCertInfo:证书序列号++++++++" + sedServer.getCertInfo(certServer, 2));
            System.out.println("sedServer.getCertInfo:证书有效期起始++++++++" + sedServer.getCertInfo(certServer, 11));
            System.out.println("sedServer.getCertInfo:证书有效期截止++++++++" + sedServer.getCertInfo(certServer, 12));
            System.out.println("sedServer.getCertInfo:用户组织名++++++++" + sedServer.getCertInfo(certServer, 14));
            System.out.println("sedServer.getCertInfo:用户通用名++++++++" + sedServer.getCertInfo(certServer, 17));
            System.out.println("sedServer.getCertInfo:证书公钥++++++++" + sedServer.getCertInfo(certServer, 30));

            String certClient = sedClient.getServerCertificate();
            System.out.println("sedClient.getServerCertificate:++++++" + certClient);
            System.out.println("sedClient.validateCert:++++++" + sedClient.validateCert(certClient));

            System.out.println("sedClient.getCertInfo:版本++++++++" + sedClient.getCertInfo(certClient, 1));
            System.out.println("sedClient.getCertInfo:证书序列号++++++++" + sedClient.getCertInfo(certClient, 2));
            System.out.println("sedClient.getCertInfo:证书有效期起始++++++++" + sedClient.getCertInfo(certClient, 11));
            System.out.println("sedClient.getCertInfo:证书有效期截止++++++++" + sedClient.getCertInfo(certClient, 12));
            System.out.println("sedClient.getCertInfo:用户组织名++++++++" + sedClient.getCertInfo(certClient, 14));
            System.out.println("sedClient.getCertInfo:用户通用名++++++++" + sedClient.getCertInfo(certClient, 17));
            System.out.println("sedClient.getCertInfo:证书公钥++++++++" + sedClient.getCertInfo(certClient, 30));


            String signdata = sedClient.signData(data);
            System.out.println("sedClient.signData:++++++" + signdata);
            System.out.println("sedClient.verifySignedData:+++++++" + sedServer.verifySignedData(certClient, data, signdata));

            String key = "1234567890ABCDEFG11111111";

            String certCopy = "MIIE9TCCA92gAwIBAgIKIAAAAAAAARJTczANBgkqhkiG9w0BAQUFADA6MQswCQYDVQQGEwJDTjENMAsGA1UECgwEQkpDQTENMAsGA1UECwwEQkpDQTENMAsGA1UEAwwEQkpDQTAeFw0wOTEwMjExNjAwMDBaFw0xMTEwMDExNTU5NTlaMEgxCzAJBgNVBAYTAkNOMQ0wCwYDVQQKDARCSkNBMQ0wCwYDVQQLDARCSkNBMRswGQYDVQQDDBLmnI3liqHlmajor4HkuabkuIAwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBAInLo4VW6RLWDkzxgn3gkYzvzPWOgG0JHVG1KWb18oGd+lo5RNOERn6rj6qmso7M1C5El5Zsu6jPjz0EL7/XmfUKhFhv7raii+MOlM89PFe/8ifd0e5FoW7733pGua3MDUGxySdD73rU9JHnr60FRTuyz2zus0vForyWZumznyS/AgMBAAGjggJxMIICbTAfBgNVHSMEGDAWgBTBzihoGF2OgzPxlaoIwz2KCJqddjAMBgNVHQ8EBQMDB/gAMCsGA1UdEAQkMCKADzIwMDkxMDIyMDAwMDAwWoEPMjAxMTEwMDEyMzU5NTlaMAkGA1UdEwQCMAAwgZkGA1UdHwSBkTCBjjBWoFSgUqRQME4xCzAJBgNVBAYTAkNOMQ0wCwYDVQQKDARCSkNBMQ0wCwYDVQQLDARCSkNBMQ0wCwYDVQQDDARCSkNBMRIwEAYDVQQDEwljYTJjcmwzNDkwNKAyoDCGLmh0dHA6Ly9sZGFwLmJqY2Eub3JnLmNuL2NybC9iamNhL2NhMmNybDM0OS5jcmwwEQYJYIZIAYb4QgEBBAQDAgD/MCoGC2CGSAFlAwIBMAkKBBtodHRwOi8vYmpjYS5vcmcuY24vYmpjYS5jcnQwGgYFKlYLBwkEEUpKMDExMDAwMTAwMDE1MTExMB0GCGCGSAGG+EQCBBFKSjAxMTAwMDEwMDAxNTExMTAbBggqVoZIAYEwAQQPMDExMDAwMTAwMDE1MTExMB4GBipWCwcBCAQUMUJASkowMTEwMDAxMDAwMTUxMTEwgbAGA1UdIASBqDCBpTA1BgkqgRwBxTiBFQEwKDAmBggrBgEFBQcCARYaaHR0cDovL3d3dy5iamNhLm9yZy5jbi9jcHMwNQYJKoEcAcU4gRUCMCgwJgYIKwYBBQUHAgEWGmh0dHA6Ly93d3cuYmpjYS5vcmcuY24vY3BzMDUGCSqBHAHFOIEVAzAoMCYGCCsGAQUFBwIBFhpodHRwOi8vd3d3LmJqY2Eub3JnLmNuL2NwczANBgkqhkiG9w0BAQUFAAOCAQEAgjF3O4M0Ztt1ykvbpMLWtCMlJ4sXZh6ABh/XW3tC72wldX9KwJZIHG1lLYgumWHJdZjKL1AzayUntl7ifjkNEIMjU1oqajAAMXSPIPV84hgRCVCx54ue+udknVFQhO1dfAl5cdc4SU2rftIUmx8FG0BF9qwNwE7GdIx8cdYusdXFivYKcExypBQrRS284QOlB1a4GEoU1Pf7OvZ/86wzMRwic3DxM4iZjvJv+G4okR0w2HvRaYO7fsY4H2yBvjtiKoiuryA6pqHIRtcgQvpxOdlRmxZgL5x4Ss3aJcIxUSJ7SF6w/nF5ywD/VSZZ8/jo7/avISOEuqDmvkqDYn0Txw==";
            System.out.println("key++++++" + key);
            String keyEncrypt = sedClient.pubKeyEncrypt(certCopy, key);
            System.out.println("sedClient.pubKeyEncrypt++++++" + keyEncrypt);
            System.out.println("sedServer.priKeyDecrypt++++++" + sedServer.priKeyDecrypt(keyEncrypt));

            String encryptData = sedClient.encryptData(sedServer.base64Encode(key.getBytes()), data);
            System.out.println("sedClient.encryptData++++++" + encryptData);
            System.out.println("sedServer.decryptData++++++" + sedServer.decryptData(sedServer.base64Encode(key.getBytes()), encryptData));


            String signData = sedClient.signDataPkcs7(data);
            System.out.println("sedClient.ignDataPkcs7++++++" + signData);
            System.out.println("sedServer.verifySignedDataPkcs7++++++" + sedServer.verifySignedDataPkcs7(signData));
            System.out.println("sedServer.getP7SignDataInfo:原文++++++" + sedServer.getP7SignDataInfo(signData, 1));
            String certData = sedServer.getP7SignDataInfo(signData, 2);
            System.out.println("sedServer.getP7SignDataInfo:签名者证书++++++" + sedServer.getP7SignDataInfo(signData, 2));
            System.out.println("sedServer.signData:签名值++++++" + sedServer.getP7SignDataInfo(signData, 3));

            String envelopData = sedClient.encodeP7SignAndEnvelopData(certServer, data);
            System.out.println("sedClient.encodeP7SignAndEnvelopData++++++" + envelopData);
            System.out.println("sedServer.decodeP7SignAndEnvelopData++++++" + sedServer.decodeP7SignAndEnvelopData(envelopData));



        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    SecurityEngineDeal engine = null;

    public SZCA(String instance) throws Exception {
        engine = SecurityEngineDeal.getInstance(instance);
        engine.setSignMethod("RSA-SHA1");
        engine.setEncryptMethod("T-DES");
    }

    public SZCA() throws Exception {
        engine = SecurityEngineDeal.getInstance("SecXV3Default");
        engine.setSignMethod("RSA-SHA1");
        engine.setEncryptMethod("T-DES");
    }

    /** 对字符串数据进行数字签名，签名格式为Pkcs7
     * 
     * @param instance
     * @throws Exception 
     */
    public String sign(String cert, String src) throws Exception {
        String certClient = engine.getServerCertificate();
        String key = engine.genRandom(24);
        String keyEncrypt = engine.pubKeyEncrypt(cert, key);
        String srcEncrypt = engine.encryptData(key, src);
        String srcSign = engine.signData(srcEncrypt);
        String tData = certClient + "&" + keyEncrypt + "&" + srcEncrypt + "&" + srcSign;
        return tData;
    }

    /** 对签名加密数据进行验签，解密。
     * 
     * @param sign 签名加密的内容
     * @return 签名加密的原文
     * @throws Exception 
     */
    public String verify(String sign, boolean checkSign) throws Exception {
        String[] values = sign.split("&");
        String cert = values[0];
        String key = engine.priKeyDecrypt(values[1]);
        String src = engine.decryptData(key, values[2]);
        if (checkSign) {
            if (!engine.verifySignedData(cert, values[3], values[4])) {
                throw new Exception("fail to verifySignedData");
            }
        }
        return src;
    }

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
    public String getCertInfo(String sign, int type)
            throws Exception {

        return engine.getCertInfo(sign, type);

    }

    /** 获取服务器证书
     * 
     * @return
     * @throws Exception 
     */
    public String getServerCert() throws Exception {
        return engine.getServerCertificate();
    }
}
