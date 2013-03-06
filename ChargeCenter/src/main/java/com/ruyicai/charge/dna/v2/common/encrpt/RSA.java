package com.ruyicai.charge.dna.v2.common.encrpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import javax.crypto.Cipher;

import com.ruyicai.charge.dna.v2.common.Formatter;
import com.ruyicai.charge.dna.v2.common.ToolKit;

public class RSA {

    public static void main(String[] args) throws Exception {

//        final String KEYSTORE_FILE = "E:\\Project\\svn\\api\\conf\\gdyilian.pfx";
//        final String KEYSTORE_PASSWORD = "42124114";
//        final String KEYSTORE_FILE_PUB = "E:\\我的微盘\\文档\\深圳东进\\djPublicKeyFile.key";
//        final String PUB_KEY_64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc+L2JGUKlGtsFm2f/wuF2T6/8mc6yrN8tLPgsx7sxAatvMvunHLXKC8xjkChHqVfJgohV4OIWe8zCw7jPsJMiPvrNnFHJ2Mumg/zQ8eZOnzMA0LDqBNFvZnOpy2XtagQn4yxxzG9+9h4P5eNojC3vD2t3H/6q5V3Cd022/egIZQIDAQAB";
//       System.out.println(Formatter.base64Encode(getCertFromFile(KEYSTORE_FILE_PUB).getBytes()));
//        
//        System.out.println("加密解密====================================================================");
//        System.out.println(Formatter.base64Encode(RSA.getPublicKey(KEYSTORE_FILE, KEYSTORE_PASSWORD).getEncoded()));
//        
//        
//        System.out.println("加密解密====================================================================");
//        //加密解密
//        String value = "1234|567";
//        String pinStr = value;
//        String pinLen = String.valueOf(pinStr.length());
//        pinStr = Strings.bytePadLeft(pinLen, '0', 2) + pinStr;
//        byte[] pin = Util.str2Bcd(pinStr);
//
//        byte[] pinblock = new byte[8];
//        System.arraycopy(pin, 0, pinblock, 0, pin.length);
//        for (int i = pin.length; i < 8; i++) {
//            pinblock[i] = (byte) 0xFF;
//        }
//
//        System.out.println("value=" + Strings.toHexString(pinblock));
//        System.out.println("value2=" + Util.bcdBytes2Str(pinblock));
//
//        String secDate = Formatter.base64Encode(RSA.encrypt64(value.getBytes(), PUB_KEY_64));
//        System.out.println("encrypt64=" + secDate);
//        byte[] secDate2 = RSA.decrypt(Formatter.base64Decode(secDate), KEYSTORE_FILE, KEYSTORE_PASSWORD);
//        System.out.println("decrypt=" + new String(secDate2));
//
//        System.out.println("签名验签====================================================================");
//        //签名验签
//        value = "123456";
////        secDate = RSA.sign(value.getBytes(), KEYSTORE_FILE_PUB);
////        System.out.println("encrypt64=" + secDate);
//
//        secDate2 = RSA.sign(value.getBytes(), KEYSTORE_FILE, KEYSTORE_PASSWORD);
//        //System.out.println("sign=" + new String(secDate));
//        System.out.println("checkSign=" + RSA.verify(secDate2, PUB_KEY_64, value));
//
//        System.out.println("签名验签====================================================================");
//        //签名验签
//        value = "123456";
//
//        secDate2 = RSA.encryptByPrivateKey(value.getBytes(), KEYSTORE_FILE, KEYSTORE_PASSWORD);
//        System.out.println("encryptByPrivateKey=" + secDate);
//        System.out.println("decryptByPublicKey=" + RSA.decryptByPublicKey(secDate2, KEYSTORE_FILE_PUB, value));
        
        String sign = "ef6dUd31M9SDUhAJdagRYHlBZrZw379TdfHZ4CuLEgOYexQyy3BvyPS9dncCpBO8RhPbf17SkoXr" +
"kC9/B9qLdMv+mv6jHc1yEP2mpmfrXgZRHvI3/GDlv1oiwMRYisaVew0iT9ESzoxHmNd7uYzJ5Zpg" +
"qY8zVzEmyVZG84qMz5GILgQQ4I0oqDiVSQYOF0ooLweus46hXpYzbg8/2TvReqJR4WCNXT0JkBwg" +
"6aK5AK5oqdNLvAC5ODQBCZw+GgtfYCRy+BIFOSZMLxojIZzvneJLtyueeuu+2fj2QAmYjJYsP/Sq" +
"l+cmyaD3e64XmL28ANk4l/fo8H0jjIe+f+CJSQ==";
        String value = "UZ0AZzony1brw6DZlVExIK/4y8P5aX1+Nl87K/0sMSFS5gXfmCVyYynzf2czBhAR" +
"iUzHrFuUZLqmz3JnV5ypy34NrlEhl9vaOn2q4hKF40kwSJT4nUy5JiN9jAhDrrAT" +
"PehFuPYSyp0ANo9+tXBUFQ7zTntRymZjIvQfZQKerSA=1220120803072644|602012080300004289602020000001";
        
        byte[] dts = RSA.decryptByPublicKey(Formatter.base64Decode(sign), ToolKit.getPropertyFromFile("VPC_CERT_PUB_64"));
        ToolKit.writeLog(RSA.class.getName(), "decryptByPublicKey.data", new String(dts));
        RSA.verify(Formatter.base64Decode(sign), ToolKit.getPropertyFromFile("VPC_CERT_PUB_64"), value);
    }

    
    public static RSAPublicKey getPublicKey(String keyPath) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream in1 = new FileInputStream(keyPath);
        java.security.cert.Certificate cert = cf.generateCertificate(in1);
        in1.close();
        java.security.interfaces.RSAPublicKey pbk = (java.security.interfaces.RSAPublicKey) cert.getPublicKey();

        System.out.println("GetPublicKey base64 ==== " + Formatter.base64Encode(pbk.getEncoded()));
        return pbk;
    }

    /**
     * 
     * @param pubKey base64 
     * @return
     * @throws Exception 
     */
    public static RSAPublicKey toPublicKey(String pubKey64) throws Exception {
        byte[] key = Formatter.base64Decode(pubKey64);
        KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        RSAPublicKey pbk = (RSAPublicKey) rsaKeyFac.generatePublic(keySpec);
        System.out.println("toPublicKey base64 ==== " + Formatter.base64Encode(pbk.getEncoded()));
        return pbk;
    }

    public static RSAPrivateKey getPrivateKey(String keyPath, String passwd) throws Exception {

        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(keyPath);
            // If the keystore password is empty(""), then we have to set           
            // to null, otherwise it won't work!!!            
            char[] nPassword = null;
            if ((passwd == null) || passwd.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = passwd.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();
            ToolKit.writeLog(RSA.class.getName(), "getPrivateKey.getType", ks.getType());

            // Now we loop all the aliases, we need the alias to get keys.      
            // It seems that this value is the "Friendly name" field in the        
            // detals tab <-- Certificate window <-- view <-- Certificate        
            // Button <-- Content tab <-- Internet Options <-- Tools menu            //
            //In MS IE 6.            
            Enumeration enumq = ks.aliases();
            String keyAlias = null;
            if (enumq.hasMoreElements()) // we are readin just one certificate.           
            {
                keyAlias = (String) enumq.nextElement();
                ToolKit.writeLog(RSA.class.getName(), "getPrivateKey.keyAlias", keyAlias);
            }
            // Now once we know the alias, we could get the keys.            
//            ToolKit.writeLog(RSA.class.getName(), "getPrivateKey.isKeyEntry", ks.isKeyEntry(keyAlias) + "");
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
//            ToolKit.writeLog(RSA.class.getName(), "getPrivateKey.base64", Formatter.base64Encode(prikey.getEncoded()));
            return (RSAPrivateKey) prikey;
        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "getPrivateKey", e);
            return null;
        }
    }

    /**  从公钥证书文件读取公钥
     * 
     * @param certPath 证书路径
     * @return 
     */
    public static String getCertFromFile(String certPath) {
        File file = new File(certPath);
        StringBuilder sb = new StringBuilder();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                if (!tempString.contains("-----")) {// && !tempString.contains("-----END CERTIFICATE-----")) {
                    sb.append(tempString);
                }
            }
            reader.close();
        } catch (IOException e) {
            ToolKit.writeLog(RSA.class.getName(), "getCertFromFile", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    ToolKit.writeLog(RSA.class.getName(), "getCertFromFile", e1);
                }
            }
        }
        return sb.toString();
    }

    /**
     *  从签名证书读取公钥信息
     * @param keyPath
     * @param passwd
     * @return
     * @throws Exception 
     */
    public static RSAPublicKey getPublicKey(String keyPath, String passwd) throws Exception {

        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(keyPath);
            // If the keystore password is empty(""), then we have to set           
            // to null, otherwise it won't work!!!            
            char[] nPassword = null;
            if ((passwd == null) || passwd.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = passwd.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();
//            ToolKit.writeLog(RSA.class.getName(), "getPublicKey.getType", ks.getType());

            // Now we loop all the aliases, we need the alias to get keys.      
            // It seems that this value is the "Friendly name" field in the        
            // detals tab <-- Certificate window <-- view <-- Certificate        
            // Button <-- Content tab <-- Internet Options <-- Tools menu            //
            //In MS IE 6.            
            Enumeration enumq = ks.aliases();
            String keyAlias = null;
            if (enumq.hasMoreElements()) // we are readin just one certificate.           
            {
                keyAlias = (String) enumq.nextElement();
//                ToolKit.writeLog(RSA.class.getName(), "getPublicKey.keyAlias", keyAlias);
            }
            // Now once we know the alias, we could get the keys.            
//            ToolKit.writeLog(RSA.class.getName(), "getPublicKey.isKeyEntry", ks.isKeyEntry(keyAlias) + "");
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();

//            ToolKit.writeLog(RSA.class.getName(), "getPublicKey.base64", Formatter.base64Encode(pubkey.getEncoded()));

            return (RSAPublicKey) pubkey;
        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "getPublicKey", e);
            return null;
        }
    }
    
    

//    public static byte[] encrypt(byte[] data, String keyPath) {
//
//        try {
//            ToolKit.writeLog(RSA.class.getName(), "decrypt.data", data.length + "|" + keyPath);
//            RSAPublicKey pbk = getPublicKey(keyPath);
//
//            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
//            cipher.init(Cipher.ENCRYPT_MODE, pbk);
//
//            byte[] encDate = cipher.doFinal(data);
//            ToolKit.writeLog(RSA.class.getName(), "encrypt.data", data.length + "");
//            return encDate;
//        } catch (Exception e) {
//            ToolKit.writeLog(RSA.class.getName(), "encrypt", e);
//            return null;
//        }
//    }
    
        public static String encrypt(byte[] data, String keyPath) {

        try {

            RSAPublicKey pbk = getPublicKey(keyPath);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pbk);

            byte[] encDate = cipher.doFinal(data);

            return Formatter.base64Encode(encDate);
        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "", e);
            return "";
        }
    }

     public static byte[] encrypt64(byte[] data, String pubKey64) {

        try {
//            ToolKit.writeLog(RSA.class.getName(), "encrypt64.data", Formatter.base64Encode(data));
//            ToolKit.writeLog(RSA.class.getName(), "encrypt64.pubkey", pubKey64);
            RSAPublicKey pbk = toPublicKey(pubKey64);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pbk);

            byte[] encDate = cipher.doFinal(data);
//            ToolKit.writeLog(RSA.class.getName(), "encrypt64.result", Formatter.base64Encode(encDate));
            return encDate;
        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "encrypt", e);
            return null;
        }
    }

    public static byte[] decrypt(byte[] data, String keyPath, String keyPasswd) {

        try {
            ToolKit.writeLog(RSA.class.getName(), "decrypt.data", Formatter.base64Encode(data));
            ToolKit.writeLog(RSA.class.getName(), "decrypt.keyPath", keyPath);
            RSAPrivateKey pbk = getPrivateKey(keyPath, keyPasswd);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, pbk);

            byte[] btSrc = cipher.doFinal(data);
            ToolKit.writeLog(RSA.class.getName(), "decrypt.result", Formatter.base64Encode(btSrc));
            return btSrc;

        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "decrypt", e);
            return null;
        }
    }

    public static byte[] sign(byte[] data, String keyPath, String keyPasswd) {

        try {
//            ToolKit.writeLog(RSA.class.getName(), "sign.data", Formatter.base64Encode(data));
//            ToolKit.writeLog(RSA.class.getName(), "sign.keyPath", keyPath);

            RSAPrivateKey pbk = getPrivateKey(keyPath, keyPasswd);

            // 用私钥对信息生成数字签名
            java.security.Signature signet = java.security.Signature.getInstance("MD5withRSA");
            signet.initSign(pbk);
            signet.update(data);
            byte[] signed = signet.sign(); // 对信息的数字签名           
//            ToolKit.writeLog(RSA.class.getName(), "sign.result", Formatter.base64Encode(signed));
            return signed;

        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "", e);
            return null;
        }
    }

    public static boolean verify(byte[] data, String pubKey64, String value) {
        try {
//            ToolKit.writeLog(RSA.class.getName(), "verify.data", Formatter.base64Encode(data));
//            ToolKit.writeLog(RSA.class.getName(), "verify.pubKey64", pubKey64);
//            ToolKit.writeLog(RSA.class.getName(), "verify.value", value);

            RSAPublicKey pbk = toPublicKey(pubKey64);
            java.security.Signature signetcheck = java.security.Signature.getInstance("MD5withRSA");
            signetcheck.initVerify(pbk);
            signetcheck.update(value.getBytes("UTF-8"));
            if (signetcheck.verify(data)) {
                ToolKit.writeLog(RSA.class.getName(), "verify.result", "签名正常");
                return true;
            } else {
               ToolKit.writeLog(RSA.class.getName(), "verify.result", "签名异常");
                return false;
            }


        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "", e);
            return false;
        }
    }
    
            public static boolean verifySha1(byte[] data, String pubKey64, String value) {
        try {
//            ToolKit.writeLog(RSA.class.getName(), "verify.data", Formatter.base64Encode(data));
//            ToolKit.writeLog(RSA.class.getName(), "verify.pubKey64", pubKey64);
//            ToolKit.writeLog(RSA.class.getName(), "verify.value", value);

            RSAPublicKey pbk = toPublicKey(pubKey64);
            java.security.MessageDigest signetcheck = java.security.MessageDigest.getInstance("SHA-1");
            //signetcheck.initVerify(pbk);
            signetcheck.update(value.getBytes("UTF-8"));
            if (signetcheck.isEqual(data, signetcheck.digest())) {
                ToolKit.writeLog(RSA.class.getName(), "verifySha1.result", "签名正常");
                return true;
            } else {
                ToolKit.writeLog(RSA.class.getName(), "verifySha1.result", "签名异常");
                return false;
            }


        } catch (Exception e) {
            ToolKit.writeLog(RSA.class.getName(), "", e);
            return false;
        }
    }

    public static byte[] encryptByPrivateKey(byte[] data, String keyPath, String keyPasswd)
            throws Exception {
//        ToolKit.writeLog(RSA.class.getName(), "encryptByPrivateKey.data", data.length + "|" + keyPath + "|" + keyPasswd);
        RSAPrivateKey pbk = getPrivateKey(keyPath, keyPasswd);

        // 对数据加密   
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, pbk);
        return cipher.doFinal(data);
    }

    public static byte[] decryptByPublicKey(byte[] data, String pubKey64)
            throws Exception {
//        ToolKit.writeLog(RSA.class.getName(), "decryptByPublicKey.data", data.length + "|" + keyPath);
        //RSAPublicKey pbk = getPublicKey(keyPath);
        RSAPublicKey pbk = toPublicKey(pubKey64);
        // 对数据加密   
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
        cipher.init(Cipher.DECRYPT_MODE, pbk);

        return cipher.doFinal(data);
    }
}
