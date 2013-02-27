package com.ruyicai.charge.dna.ca;

import com.cfca.util.pki.api.APIVersion;
import java.util.Arrays;
import com.cfca.util.pki.api.EncryptUtil;
import com.cfca.util.pki.api.KeyUtil;
import java.io.FileInputStream;
import com.cfca.util.pki.PKIException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import com.cfca.util.pki.api.CertUtil;
import com.cfca.util.pki.cert.X509Cert;
import com.cfca.util.pki.cipher.Session;
import com.cfca.util.pki.cipher.JCrypto;
import com.cfca.util.pki.api.EnvelopUtil;
import com.cfca.util.pki.cipher.JKey;
import com.cfca.util.pki.api.SignatureUtil;

public class Bjca {
    private Session session = null;
    public Bjca() {
        try {
            //初始化加密库，获得会话session
            //多线程的应用可以共享一个session,不需要重复
            //初始化加密库并获得session。
            //系统退出后要jcrypto.finalize()，释放加密库
            JCrypto jcrypto = JCrypto.getInstance();
            jcrypto.initialize(JCrypto.JSOFT_LIB, null);
            session = jcrypto.openSession(JCrypto.JSOFT_LIB);

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 证书测试
     */
    public void certTest() {
        try {
            System.out.println("输入证书路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String message = reader.readLine().trim();
            //构造证书
            X509Cert x509Cert = CertUtil.generateCert(message);
            //如果有证书的数据也可以调用
            //CertUtil.generateCert(byte[] certData)方法,来构造证书

            //解析证书
            //如果想获得证书的其他信息和扩展域信息
            //可以调用X509Cert类的方法获得.
            System.out.println("证书主题：" + x509Cert.getSubject());
            System.out.println("证书颁发者：" + x509Cert.getIssuer());
            System.out.println("证书序列号：" + x509Cert.getStringSerialNumber());

            //可以通过此方法同时验证签名、有效期、CRL
            //CertUtil.verifyCert(X509Cert userCert, X509Cert[] caCerts,String crlPath, Session session)
            //也可以根据需要通过如下方式分步验证证书
            //验证有效期
            boolean verifyCertDate = false;
            try {
                verifyCertDate = CertUtil.verifyCertDate(x509Cert);
            } catch (PKIException ex1) {
                if (ex1.getErrCode().equals("850901")) {
                    System.out.println("证书未生效");
                } else if (ex1.getErrCode().equals("850902")) {
                    System.out.println("证书已过期");
                }
            }
            if (verifyCertDate) {
                System.out.println("证书有效期验证通过");
            }

            System.out.println("输入CA证书路径(.cer或.p7b文件):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String caCertPath = reader.readLine().trim();
            //CA证书集合，可以是一张CA证书，也可以是CA证书集合。
            X509Cert[] caCerts = null;
            //一张CA证书
            if (caCertPath.toLowerCase().endsWith(".cer")) {
                caCerts = new X509Cert[1];
                caCerts[0] = new X509Cert(new FileInputStream(caCertPath));
            }
           //CA证书链
            else {
                caCerts = CertUtil.parseP7b(caCertPath);
                //也可以调用CertUtil.parseP7b(byte[] data)获得证书链
            }
            //验证指定的用户证书的签名，同时验证CA证书链
            boolean verifySign = CertUtil.verifyCertSign(x509Cert, caCerts,
                    session);
            if (verifySign) {
                System.out.println("验证证书签名成功");
            } else {
                System.out.println("验证证书签名失败");
            }
            //通过在线查询CRL的方式验证证书
            boolean CRLOnLine = CertUtil.verifyCertByCRLOnLine(x509Cert);
            if (CRLOnLine) {
                System.out.println("在线验证CRL:证书没有被吊销");
            } else {
                System.out.println("在线验证CRL:证书被吊销");
            }
            System.out.println("离线CRL验证证书,输入CRL路径:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            message = reader.readLine().trim();
            //离线方式验证CRL
            //message可以是CRL文件路径，也可以指定一个保存CRL的目录路径。
            //当使用目录路径时要配合CFCA的CRL下载工具使用。
            boolean CRLOutLine = CertUtil.verifyCertByCRLOutLine(x509Cert,
                    message, caCerts, this.session);
            //也可以调用
            //verifyCertByCRLOutLine(X509Cert userCert,byte[] crlData,X509Cert[] caCerts,Session session)

            if (CRLOutLine) {
                System.out.println("离线验证CRL: 证书没有被吊销");
            } else {
                System.out.println("离线验证CRL: 证书被吊销");
            }
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 对文件产生数字信封
     */
    public void envelopFileTest() {
        try {
            System.out.println("输入原文文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String srcFilePath = reader.readLine().trim();

            System.out.println("输入加密证书路径(.cer,多张证书用';'分隔):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();
            String[] certPaths = certPath.split(";");
            EnvelopUtil envUtil = new EnvelopUtil();
            //添加数字信封（加密信息）的接收者公钥证书
            for (int i = 0; i < certPaths.length; i++) {
                X509Cert cert = new X509Cert(new FileInputStream(certPaths[i]));
                envUtil.addRecipient(cert);
            }
            //产生数字信封,EnvelopUtil.RC4比较快
            //使产生的信封带有keyID
            envUtil.setCMSFlag();
            envUtil.envelopeFile(srcFilePath, srcFilePath + ".enc", 1 * 1024 * 1024,
                                 EnvelopUtil.RC4, session);
            System.out.println("产生加密文件成功,路径为:" + srcFilePath + ".enc");
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 对消息产生信封
     */
    public void envelopMsgTest() {
        try {
            System.out.println("输入原文:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String content = reader.readLine().trim();
            System.out.println("输入加密证书路径(.cer,多张证书用';'分隔):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();
            String[] certPaths = certPath.split(";");
            EnvelopUtil envUtil = new EnvelopUtil();
            //添加数字信封(加密信息)接收者公钥证书
            for (int i = 0; i < certPaths.length; i++) {
                X509Cert cert = new X509Cert(new FileInputStream(certPaths[i]));
                envUtil.addRecipient(cert);
            }
            //产生数字信封
            //使产生的信封带有keyID
            envUtil.setCMSFlag();
            byte[] b64MsgEnvelop = envUtil.envelopeMessage(content.getBytes(),
                    EnvelopUtil.DES3_CBC, session);
            System.out.println("产生消息数字信封成功,加密后消息为:" + new String(b64MsgEnvelop));

            System.out.println("输入解密证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            certPath = reader.readLine().trim();

            System.out.println("输入解密证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得数字信封接收者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            //数字信封接收者的公钥证书
            //根据公钥证书解密信息
            X509Cert cert = CertUtil.getCert(certPath, pwd);
           byte[] src = envUtil.openEnvelopedMessage(b64MsgEnvelop, priKey,
                    cert, session);
            System.out.println("解密消息数字信封成功,原文为:" + new String(src));

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     *解密数字信封文件
     */
    public void openEnvelopFileTest() {
        try {
            System.out.println("输入加密文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String envFilePath = reader.readLine().trim();

            System.out.println("输入解密证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入解密证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得数字信封接收者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            //获得公钥证书
            X509Cert cert = CertUtil.getCert(certPath, pwd);

            EnvelopUtil envUtil = new EnvelopUtil();
            int index = envFilePath.lastIndexOf(".");
            String srcPath = envFilePath.substring(0, index);
            //解密信息
            envUtil.openEnvelopedFile(srcPath + ".dec", envFilePath, 1 * 1024 * 1024,
                                      priKey, cert,
                                      session);
            System.out.println("解密文件成功,路径为:" + srcPath + ".dec");
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * P1消息签名验签测试
     */
    public void p1SignMsgTest() {
        try {
            System.out.println("输入原文:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String src = reader.readLine().trim();
            System.out.println("输入签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者的私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            SignatureUtil signUtil = new SignatureUtil();

            //对消息签名
            byte[] b64SignMsg = signUtil.p1SignMessage(src.getBytes(),
                    SignatureUtil.SHA1_RSA, priKey, session);
            System.out.println("签名成功,签名结果为:" + new String(b64SignMsg));

            System.out.println("输入验签证书路径(.cer):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pubCertPath = reader.readLine().trim();
            X509Cert cert = new X509Cert(new FileInputStream(pubCertPath));
            //验签名
            //验证签名
            boolean verify = signUtil.p1VerifySignMessage(src.getBytes(),
                    b64SignMsg, SignatureUtil.SHA1_RSA, cert, session);

            if (verify) {
                System.out.println("验证签名成功");
            } else {
                System.out.println("验证签名失败");
            }
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * P7文件签名
     */
    public void p7SignFileTest() {
        try {
            System.out.println("输入原文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String srcFilePath = reader.readLine().trim();

            System.out.println("输入签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            X509Cert cert = CertUtil.getCert(certPath, pwd);

            SignatureUtil signUtil = new SignatureUtil();
            //对文件签名，并将签名结果输入到指定路径
            signUtil.p7SignFile(true, srcFilePath, srcFilePath + ".sig",
                                1 * 1024 * 1024, SignatureUtil.SHA1_RSA, priKey, cert,
                                session);

            System.out.println("文件签名成功,签名文件路径为:" + srcFilePath + ".sig");
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    /**
     * 对文件 分离式 签名,签名结果输出到文件
     */
    public void p7SignFileDetachedTest() {
        try {
            System.out.println("输入原文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String srcFilePath = reader.readLine().trim();

            System.out.println("输入签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            X509Cert cert = CertUtil.getCert(certPath, pwd);

            SignatureUtil signUtil = new SignatureUtil();
            //对文件分离式签名，并将签名结果输出到指定文件
            signUtil.p7SignFile(false, srcFilePath, srcFilePath + ".nosrc.sig",
                                1 * 1024 * 1024, SignatureUtil.SHA1_RSA, priKey, cert,
                                session);
            System.out.println("文件签名(分离式)成功,签名文件路径为:" + srcFilePath + ".nosrc.sig");
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    /**
     * 对文件 分离式 签名,签名结果以Base64编码的方式输出
     */
    public void p7SignFileDetachedOutMsgTest() {
        try {
            System.out.println("输入原文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String srcFilePath = reader.readLine().trim();

            System.out.println("输入签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            X509Cert cert = CertUtil.getCert(certPath, pwd);

            SignatureUtil signUtil = new SignatureUtil();
            //对文件分离式签名，并将签名结果以Base64编码的方式输出
            byte[] signData = signUtil.p7SignFileDetachedOutMsg(srcFilePath,  1 * 1024 * 1024,
                                SignatureUtil.SHA1_RSA, priKey, cert, session);
            System.out.println("文件签名(分离式)成功,签名结果为:" + new String(signData));
            /**
             * 如果到多人符合签名调用signUtil.p7ReSignFileDetachedOutMsg();
             * 参数的获得请参考其他多人复合签名的例子
             */
            boolean verify = signUtil.p7VerifySignFileDetachedOutMsg(srcFilePath,
                    signData, 1 * 1024 * 1024, session);
            if (verify) {
                //获得签名者证书
                X509Cert[] x509Certs = signUtil.getSigerCert();
                System.out.println("验证文件签名(分离式)成功, 证书为:");
                for (int i = 0; i < x509Certs.length; i++) {
                    System.out.println(x509Certs[i].getSubject());
                }
            } else {
                System.out.println("验证文件签名(分离式)失败");
            }


        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }


    /**
     * 多人 文件 分离式签名
     */
    public void p7ReSignFileDetachedTest() {
        try {
            System.out.println("输入原文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String srcFilePath = reader.readLine().trim();

            System.out.println("输入第一个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            //签名者公钥证书
            X509Cert cert = CertUtil.getCert(certPath, pwd);
            SignatureUtil signUtil = new SignatureUtil();
            //对文件做分离式签名，第一个参数false表示分离式
            //将签名结果输入到指定文件
            signUtil.p7SignFile(false, srcFilePath, srcFilePath + ".nosrc.sig",
                                1 * 1024 * 1024, SignatureUtil.SHA1_RSA, priKey, cert,
                                session);
            System.out.println("输入第二个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            pwd = reader.readLine().trim();
            //下一个签名者的私钥
            priKey = KeyUtil.getPriKey(certPath, pwd);
            //下一个签名者的证书
            cert = CertUtil.getCert(certPath, pwd);
            //在上一次分离式签名的基础上，再做分离式签名
            //将签名结果输出到指定文件
            signUtil.p7ReSignFileDetached(srcFilePath,srcFilePath + ".nosrc.sig",srcFilePath + ".nosrc.resig",
                                          1 * 1024 * 1024,SignatureUtil.SHA1_RSA,priKey,cert,session);

            System.out.println("多人文件签名(分离式)成功,签名文件路径为:" + srcFilePath + ".nosrc.resig");
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }


    /**
     * 多人 文件 非分离式签名
     */
    public void p7ReSignFileTest() {
        try {
            System.out.println("输入原文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String srcFilePath = reader.readLine().trim();

            System.out.println("输入第一个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            //签名者证书
            X509Cert cert = CertUtil.getCert(certPath, pwd);
            SignatureUtil signUtil = new SignatureUtil();
            //对文件做非分离式签名，第一个参数true表示非分离式
            //将签名结果输入到指定文件
            signUtil.p7SignFile(true, srcFilePath, srcFilePath + ".sig",
                                256, SignatureUtil.SHA1_RSA, priKey, cert,
                                session);
            System.out.println("输入第二个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            pwd = reader.readLine().trim();
            //下一个签名者的私钥和证书
            priKey = KeyUtil.getPriKey(certPath, pwd);
            cert = CertUtil.getCert(certPath, pwd);
            //在上一个签名的基础上，再做签名
            //将签名结果输入到指定文件
            signUtil.p7ReSignFile(srcFilePath + ".sig",srcFilePath + ".resig",
                                          256,SignatureUtil.SHA1_RSA,priKey,cert,session);

            System.out.println("多人文件签名(非分离式)成功,签名文件路径为:" + srcFilePath + ".resig");
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 验证P7分离式签名
     */
    public void p7VerifySignFileDetachedTest() {
        try {
            System.out.println("输入原文文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String srcFilePath = reader.readLine().trim();

            System.out.println("输入签名文件路径:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String signFilePath = reader.readLine().trim();
            SignatureUtil signUtil = new SignatureUtil();
            //验证分离式文件签名
            boolean verify = signUtil.p7VerifySignFileDetached(srcFilePath,
                    signFilePath, 1 * 1024 * 1024, session);
            if (verify) {
                //获得签名者证书
                X509Cert[] x509Certs = signUtil.getSigerCert();
                System.out.println("验证文件签名(分离式)成功, 证书为:");
                for (int i = 0; i < x509Certs.length; i++) {
                    System.out.println(x509Certs[i].getSubject());
                }
            } else {
                System.out.println("验证文件签名(分离式)失败" + signFilePath);
            }
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 验证P7非分离式文件签名
     */
    public void p7VerifySignFileTest() {
        try {
            System.out.println("输入签名文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String signFilePath = reader.readLine().trim();

            SignatureUtil signUtil = new SignatureUtil();
            //验证 非分离式 文件签名
            boolean verify = signUtil.p7VerifySignFile(signFilePath+".ver",
                    signFilePath, 1 * 1024 * 1024, session);
            if (verify) {
                //获得文件签名中的证书
                X509Cert[] x509Certs = signUtil.getSigerCert();
                System.out.println("验证文件(分离式)签名成功, 证书为:");
                for (int i = 0; i < x509Certs.length; i++) {
                    System.out.println(x509Certs[i].getSubject());
                }
                System.out.println("验证文件(分离式)签名成功,原文输出路径为:" + signFilePath + ".ver");
            } else {
                System.out.println("验证文件(分离式)签名失败" + signFilePath);
            }
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * P7消息非分离式 签名验签测试
     */
    public void p7SignMsgTest() {
        try {
            System.out.println("输入原文:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String src = reader.readLine().trim();

            System.out.println("输入签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
           
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            //X509Cert cert = 
            X509Cert[] certs = new X509Cert[1];
            certs[0] = CertUtil.getCert(certPath, pwd);

            SignatureUtil signUtil = new SignatureUtil();
            //对消息签名
            byte[] b64SignData = signUtil.p7SignMessage(true, src.getBytes(),
                    SignatureUtil.SHA1_RSA, priKey, certs, session);
            System.out.println("签名成功,签名结果为:" + new String(b64SignData));
            //验证签名
            boolean verify = signUtil.p7VerifySignMessage(b64SignData, session);
            if (verify) {
                //获得签名中的证书
                X509Cert[] x509Certs = signUtil.getSigerCert();
                System.out.println("验证消息(非分离式)签名成功, 证书为:");
                for (int i = 0; i < x509Certs.length; i++) {
                    System.out.println(x509Certs[i].getSubject());
                }
                //获得签名数据中的原文
                byte[] srcData = signUtil.getSignedContent();
                System.out.println("验证消息(非分离式)签名成功,原文为:" + new String(srcData));
            } else {
                System.out.println("验证消息(非分离式)签名失败");
            }

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * P7消息分离式 签名验签测试
     */
    public void p7SignMsgDetachedTest() {
        try {
            System.out.println("输入原文:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String input = reader.readLine().trim();
            String src = new String(input.getBytes());
            System.out.println("输入签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            X509Cert cert = CertUtil.getCert(certPath, pwd);
            X509Cert[] certs = new X509Cert[1];
            certs[0] = cert;

            SignatureUtil signUtil = new SignatureUtil();
            //分离式消息签名
            byte[] b64SignData = signUtil.p7SignMessage(false, src.getBytes(),
                    SignatureUtil.SHA1_RSA, priKey, certs, session);
            System.out.println("签名成功,签名结果为:" + new String(b64SignData));
            //验证分离式签名
            boolean verify = signUtil.p7VerifySignMessageDetached(src.getBytes(),
                    b64SignData, session);
            if (verify) {
                X509Cert[] x509Certs = signUtil.getSigerCert();
                System.out.println("验证消息(分离式)签名成功, 证书为:");
                for (int i = 0; i < x509Certs.length; i++) {
                    System.out.println(x509Certs[i].getSubject());
                }
            } else {
                System.out.println("验证消息(分离式)签名失败");
            }

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * P7多人消息(非分离式)签名验签测试
     */
    public void p7ReSignMsgTest() {
        try {
            System.out.println("输入原文:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String src = reader.readLine().trim();

            System.out.println("输入第一个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            X509Cert cert = CertUtil.getCert(certPath, pwd);
            X509Cert[] certs = new X509Cert[1];
            certs[0] = cert;
            SignatureUtil signUtil = new SignatureUtil();
            //第一次签名
            byte[] b64SignData = signUtil.p7SignMessage(true, src.getBytes(),
                    SignatureUtil.SHA1_RSA, priKey, certs, session);
            System.out.println("第一次签名成功");

            System.out.println("输入第二个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            priKey = KeyUtil.getPriKey(certPath, pwd);
            cert = CertUtil.getCert(certPath, pwd);
            certs = new X509Cert[1];
            certs[0] = cert;
            //第二次签名
            byte[] b64ReSignData = signUtil.p7ReSignMessage(b64SignData,
                    SignatureUtil.SHA1_RSA, priKey, certs, session);

            System.out.println("第二次签名成功,签名结果为:" + new String(b64ReSignData));
            //验证签名
            boolean reVerify = signUtil.p7VerifySignMessage(b64ReSignData,
                    session);
            if (reVerify) {
                //获得签名者证书集合
                X509Cert[] x509Certs = signUtil.getSigerCert();
                System.out.println("验证多人消息(非分离式)签名成功, 证书为:");
                for (int i = 0; i < x509Certs.length; i++) {
                    System.out.println(x509Certs[i].getSubject());
                }
                //获得签名中的原文
                byte[] srcData = signUtil.getSignedContent();
                System.out.println("验证多人消息(非分离式)签名成功,原文为:" + new String(srcData));
            } else {
                System.out.println("验证多人消息(非分离式)签名失败");
            }

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * P7多人消息(分离式)签名验签测试
     */
    public void p7ReSignMsgDetachedTest() {
        try {
            System.out.println("输入原文:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String src = reader.readLine().trim();

            System.out.println("输入第一个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String pwd = reader.readLine().trim();
            //从私钥证书中获得签名者私钥
            //也可以用getPriKey(byte[] pfxData, String pfxPWD)
            JKey priKey = KeyUtil.getPriKey(certPath, pwd);
            //签名者证书
            X509Cert cert = CertUtil.getCert(certPath, pwd);
            X509Cert[] certs = new X509Cert[1];
            certs[0] = cert;

            SignatureUtil signUtil = new SignatureUtil();
            //第一次分离签名
            byte[] b64SignData = signUtil.p7SignMessage(false, src.getBytes(),
                    SignatureUtil.SHA1_RSA, priKey, certs, session);
            System.out.println("第一次签名成功");
            //第二次签名
            System.out.println("输入第二个签名证书路径(.pfx):");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            certPath = reader.readLine().trim();

            System.out.println("输入签名证书口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            pwd = reader.readLine().trim();
            //下一个签名者的证书和私钥
            priKey = KeyUtil.getPriKey(certPath, pwd);
            cert = CertUtil.getCert(certPath, pwd);
            certs = new X509Cert[1];
            certs[0] = cert;
            //再次分离签名
            byte[] b64ReSignData = signUtil.p7ReSignMessageDetached(src.getBytes(),b64SignData,
                    SignatureUtil.SHA1_RSA, priKey, certs, session);

            System.out.println("第二次签名成功,签名结果为:" + new String(b64ReSignData));
            //验证分离式消息签名
            boolean reVerify = signUtil.p7VerifySignMessageDetached(src.getBytes() ,b64ReSignData,
                    session);

            if (reVerify) {
                //验签后，获得签名者证书集合。
                X509Cert[] x509Certs = signUtil.getSigerCert();
                System.out.println("验证多人消息(分离式)签名成功, 证书为:");
                for (int i = 0; i < x509Certs.length; i++) {
                    System.out.println(x509Certs[i].getSubject());
                }
            } else {
                System.out.println("验证多人消息(分离式)签名失败");
            }

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    /**
     * 用密钥和口令对消息做对称加密
     */
    public void encryptMsgTest(){
        try {
            System.out.println("用对称密钥加密解密数据");

            System.out.println("输入原文:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            //原文
            String content = reader.readLine().trim();

            System.out.println("选择产生密钥算法：1-DES,2-DES3,3-RC4");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String encAlg = reader.readLine().trim();
            String keyType = EncryptUtil.RC4;
            if(encAlg.equals("1")){
               keyType = KeyUtil.DES;
            }else if(encAlg.equals("2")){
                keyType = KeyUtil.DES3;
            }else{
                keyType = KeyUtil.RC4;
            }
            //产生密钥
            JKey key = KeyUtil.generateKey(keyType,session);

            System.out.println("选择对称加密模式：1-ECB,2-CBC,3-RC4");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String encMode = reader.readLine().trim();
            if(encMode.equals("1")&& encAlg.equals("1")){
                encMode = EncryptUtil.DES_ECB;
            }else if(encMode.equals("1")&& encAlg.equals("2")){
                encMode = EncryptUtil.DES3_ECB;
            }else if(encMode.equals("2")&& encAlg.equals("1")){
                encMode = EncryptUtil.DES_CBC;
            }else if(encMode.equals("2")&& encAlg.equals("2")){
                encMode = EncryptUtil.DES3_CBC;
            }else{
               encMode =  EncryptUtil.RC4;
            }
            //使用对称密钥加密
            byte[] encData = EncryptUtil.encryptByKey(encMode,key,content.getBytes(),session);
            System.out.println("加密结果数据："+new String(encData));

            //使用对称密钥解密
            byte[] decData = EncryptUtil.decryptByKey(encMode,key,encData,session);

            if(Arrays.equals(content.getBytes(),decData)){
               System.out.println("解密成功，原文为："+new String(decData));
            }else{
                System.out.println("解密失败");
            }

            System.out.println("用口令加密解密数据");
            System.out.println("输入原文:");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
             content = reader.readLine().trim();
             System.out.println("输入加密口令:");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
             //口令
             String pwd = reader.readLine().trim();

             System.out.println("选择产生密钥算法：1-DES,2-DES3,3-RC4");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
             encAlg = reader.readLine().trim();

            if(encAlg.equals("1")){
               encAlg = EncryptUtil.PBE_SHA1_DES;
            }else if(encAlg.equals("2")){
                encAlg = EncryptUtil.PBE_SHA1_3DES;
            }else{
                encAlg = EncryptUtil.PBE_SHA1_RC4;
            }
            //用口令加密数据
            encData = EncryptUtil.encryptByPWD(encAlg,pwd,content.getBytes(),session);
            System.out.println("加密结果数据："+new String(encData));
            System.out.println("输入解密口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
             pwd = reader.readLine().trim();
             //用口令解密
             decData = EncryptUtil.decryptByPWD(encAlg,pwd,encData,session);
             if(Arrays.equals(content.getBytes(),decData)){
               System.out.println("解密成功，原文为："+new String(decData));
            }else{
                System.out.println("解密失败");
            }

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 用对称密钥或口令对文件做对称加密、解密
     */
    public void encryptFileTest(){
        try {
            System.out.println("用对称密钥对文件进行加密和解密");

            System.out.println("输入原文件路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            //原文件
            String srcFile = reader.readLine().trim();

            System.out.println("选择产生密钥算法：1-DES,2-DES3,3-RC4");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String encAlg = reader.readLine().trim();
            String keyType = EncryptUtil.RC4;
            if(encAlg.equals("1")){
               keyType = KeyUtil.DES;
            }else if(encAlg.equals("2")){
                keyType = KeyUtil.DES3;
            }else{
                keyType = KeyUtil.RC4;
            }
            //产生密钥
            JKey key = KeyUtil.generateKey(keyType,session);

            System.out.println("选择对称加密模式：1-ECB,2-CBC,3-RC4");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String encMode = reader.readLine().trim();
            if(encMode.equals("1")&& encAlg.equals("1")){
                encMode = EncryptUtil.DES_ECB;
            }else if(encMode.equals("1")&& encAlg.equals("2")){
                encMode = EncryptUtil.DES3_ECB;
            }else if(encMode.equals("2")&& encAlg.equals("1")){
                encMode = EncryptUtil.DES_CBC;
            }else if(encMode.equals("2")&& encAlg.equals("2")){
                encMode = EncryptUtil.DES3_CBC;
            }else{
               encMode =  EncryptUtil.RC4;
            }
            //使用对称密钥加密
            String encFile = srcFile+".enc";
            EncryptUtil.encryptFileByKey(encMode,key,srcFile,encFile,1024*1024,session);
            System.out.println("加密完成，加密文件路径："+encFile);

            //使用对称密钥解密
            String decFile = srcFile+".dec";
            EncryptUtil.decryptFileByKey(encMode,key,encFile,decFile,1024*1024,session);
            //此处需要比较解密后的文件和原文件数据是否一致
            System.out.println("解密完成，请比较解密文件和原文。解密后路径为："+decFile);

            System.out.println("用口令加密解密文件");
            System.out.println("输入原文件:");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
             srcFile = reader.readLine().trim();
             System.out.println("输入加密口令:");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
             //口令
             String pwd = reader.readLine().trim();

             System.out.println("选择产生密钥算法：1-DES,2-DES3,3-RC4");
             reader = new BufferedReader(new InputStreamReader(
                    System.in));
             encAlg = reader.readLine().trim();

            if(encAlg.equals("1")){
               encAlg = EncryptUtil.PBE_SHA1_DES;
            }else if(encAlg.equals("2")){
                encAlg = EncryptUtil.PBE_SHA1_3DES;
            }else{
                encAlg = EncryptUtil.PBE_SHA1_RC4;
            }
            //用口令加密数据
            encFile = srcFile+".enc";
            EncryptUtil.encryptFileByPWD(encAlg,pwd,srcFile,encFile,1024*1024,session);
            System.out.println("加密完成，加密文件路径："+encFile);

            System.out.println("输入解密口令:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
             pwd = reader.readLine().trim();
             //用口令解密
             decFile = srcFile+".dec";
             EncryptUtil.decryptFileByPWD(encAlg,pwd,srcFile,encFile,1024*1024,session);
             //此处需要比较解密后的文件和原文件数据是否一致
            System.out.println("解密完成，请比较解密文件和原文。解密后路径为："+decFile);

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }


    /**
     * 获得工具包版本号和底层加密编码库的版本号
     * @return String
     */
    public String getVersion() {
        return APIVersion.getVersion();
    }
    /**
     * 对数据做文摘
     * @return String
     */
    public void hashTest() {
        try {
            System.out.println("选择文摘算法：1-MD5,2-SHA1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String hashAlg = reader.readLine().trim();
            if (hashAlg.equals("1")) {
                hashAlg = SignatureUtil.MD5;
            } else {
                hashAlg = SignatureUtil.SHA1;
            }
            System.out.println("对数据做文摘 ...");
            System.out.println("输入原文:");
            reader = new BufferedReader(new InputStreamReader(
                    System.in));
            //原文
            String content = reader.readLine().trim();
            SignatureUtil signUtil = new SignatureUtil();
            byte[] hashBase64 = signUtil.hash(content.getBytes(), hashAlg,
                                              session);
            System.out.println("对数据做文摘成功，文摘值为：" + new String(hashBase64));
            System.out.println("");
            System.out.println("对数据流做文摘 ...");
            System.out.println("输入原文件全路径:");

            String filePath = reader.readLine().trim();
            signUtil = new SignatureUtil();
            hashBase64 = signUtil.hash(filePath, 0, hashAlg, session);
            System.out.println("对数据流做文摘成功，文摘值为：" + new String(hashBase64));

        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * 时间戳测试
     */
    public void timeStampTest() {
        try {
            System.out.println("输入时间戳文件(Base64编码)路径:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String stFile = reader.readLine().trim();
            FileInputStream srcFIS = new FileInputStream(stFile);
            int fileLen = srcFIS.available();
            byte[] data = new byte[fileLen];
            srcFIS.read(data);
            srcFIS.close();

            SignatureUtil signUtil = new SignatureUtil();

            boolean verify = signUtil.verifyTimeStamp(data,session);
            if(verify){
               System.out.println("时间戳验证通过");
               X509Cert[] certs = signUtil.getSigerCert();
               System.out.println("时间戳服务器证书为:"+certs[0].getSubject());

               //时间表示为yyyy-MM-dd HH:mm:ss,可能为null
               String strTime = signUtil.getTimeFromTimeStamp(data);
               System.out.println("时间戳的时间为:"+strTime);
            }else{
                System.out.println("时间戳验证失败");
            }
        } catch (PKIException ex) {
            System.out.println(ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public static String printMessage() {
        System.out.println("*****************************************");
        System.out.println("          CFCA工具包2.0功能测试程序       ");
        System.out.println("*****************************************");
        System.out.println("\t1:\t证书测试");
        System.out.println("\t2:\tPKCS7 消息加密，解密测试");
        System.out.println("\t3:\tPKCS7 文件加密测试");
        System.out.println("\t4:\tPKCS7 文件解密测试");
        System.out.println("\t5:\tPKCS1 消息签名，验签测试");
        System.out.println("\t6:\tPKCS7 消息签名，验签测试");
        System.out.println("\t7:\tPKCS7 分离式消息签名，验签测试");
        System.out.println("\t8:\tPKCS7 多人消息签名，验签测试");
        System.out.println("\t9:\tPKCS7 多人分离式消息签名，验签测试");
        System.out.println("\t10:\tPKCS7 分离式文件签名,验证测试(签名结果是Base64编码字符串)");
        System.out.println("\t11:\tPKCS7 文件签名测试");
        System.out.println("\t12:\tPKCS7 分离式文件签名测试(签名结果是文件)");
        System.out.println("\t13:\tPKCS7 多人非分离式文件签名测试");
        System.out.println("\t14:\tPKCS7 多人分离式文件签名测试(签名结果是文件)");
        System.out.println("\t15:\tPKCS7 验证文件签名测试");
        System.out.println("\t16:\tPKCS7 验证分离式文件签名测试(签名结果是文件)");
        System.out.println("\t17:\t消息对称加密，解密测试");
        System.out.println("\t18:\t文件对称加密，解密测试");
        System.out.println("\t19:\t文摘测试(对消息和文件)");
        System.out.println("\t20:\t时间戳验证测试");
        System.out.println("\t21:\t显示版本号");
        System.out.println("\t22:\t退出测试");
        System.out.println("*****************************************");

        System.out.println("请输入选项:");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.
                in));
        String choose = null;

        try {
            choose = reader.readLine().trim();
        } catch (Exception ex) {

        }
        return choose;
    }

    public static void main(String[] args) {
        try {
            Bjca apiTest = new Bjca();
            String choose = printMessage();

            while (true) {
                //证书测试
                if (choose.equals("1")) {
                    apiTest.certTest();
                }
                //消息数字信封
                else if (choose.equals("2")) {
                    apiTest.envelopMsgTest();
                }
                //文件数字信封
                else if (choose.equals("3")) {
                    apiTest.envelopFileTest();
                }
                //解密文件数字信封
                else if (choose.equals("4")) {
                    apiTest.openEnvelopFileTest();
                }
                //PKCS1消息签名验签
                else if (choose.equals("5")) {
                    apiTest.p1SignMsgTest();
                }
                //PKCS7消息签名验签(非分离)
                else if (choose.equals("6")) {
                    apiTest.p7SignMsgTest();
                }
                //PKCS7消息签名验签(分离)
                else if (choose.equals("7")) {
                    apiTest.p7SignMsgDetachedTest();
                }
                //PKCS7多人消息签名验签(非分离)
                else if (choose.equals("8")) {
                    apiTest.p7ReSignMsgTest();
                }
                //PKCS7多人消息签名验签(分离)
                else if (choose.equals("9")) {
                    apiTest.p7ReSignMsgDetachedTest();
                }
                //分离式文件签名,签名结果是BASE64编码字符串
                else if (choose.equals("10")) {
                    apiTest.p7SignFileDetachedOutMsgTest();
                }
                //文件签名
                else if (choose.equals("11")) {
                    apiTest.p7SignFileTest();
                }
                //文件签名(分离式)
                else if (choose.equals("12")) {
                    apiTest.p7SignFileDetachedTest();
                }
                //多人文件签名(非分离式)
                else if (choose.equals("13")) {
                    apiTest.p7ReSignFileTest();
                }
                //多人文件签名(分离式)
                else if (choose.equals("14")) {
                    apiTest.p7ReSignFileDetachedTest();
                }
                //验证文件签名
                else if (choose.equals("15")) {
                    apiTest.p7VerifySignFileTest();
                }
                //验证文件签名(分离式)
                else if (choose.equals("16")) {
                    apiTest.p7VerifySignFileDetachedTest();
                }

                //消息对称加密解密（用密钥和口令）
                else if (choose.equals("17")) {
                    apiTest.encryptMsgTest();
                }

                //文件对称加密解密（用密钥和口令）
                else if (choose.equals("18")) {
                    apiTest.encryptFileTest();
                }

                //文摘测试
                else if (choose.equals("19")) {
                    apiTest.hashTest();
                }
                //时间戳测试
                else if (choose.equals("20")) {
                    apiTest.timeStampTest();
                }
                //显示版本号
                else if (choose.equals("21")) {
                    System.out.println(apiTest.getVersion());
                }
                if (choose.equals("22")) {
                    break;
                }
                System.out.println("");
                choose = printMessage();
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }
}

