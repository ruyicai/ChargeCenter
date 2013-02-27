/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruyicai.charge.dna.ca;

import com.cfca.util.pki.api.KeyUtil;
import com.cfca.util.pki.api.CertUtil;
import com.cfca.util.pki.cert.X509Cert;
import com.cfca.util.pki.cipher.Session;
import com.cfca.util.pki.cipher.JCrypto;
import com.cfca.util.pki.cipher.JKey;
import com.cfca.util.pki.api.SignatureUtil;
import com.ruyicai.charge.dna.common.ToolKit;

/**
 *
 * @author Administrator
 */
public class BeiJingCA implements CFCA
{
    private Session session = null;

    public BeiJingCA()
    {
        try
        {
            //初始化加密库，获得会话session
            //多线程的应用可以共享一个session,不需要重复
            //初始化加密库并获得session。
            //系统退出后要jcrypto.finalize()，释放加密库
            JCrypto jcrypto = JCrypto.getInstance();
            jcrypto.initialize(JCrypto.JSOFT_LIB, null);
            session = jcrypto.openSession(JCrypto.JSOFT_LIB);

        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
        }
    }
    private String certPath;

    public void setCertPath(String certPath)
    {
        this.certPath = certPath;
    }

    public String getCertPath()
    {
        return certPath;
    }
    private String certPass;

    public void setCertPass(String certPass)
    {
        this.certPass = certPass;
    }

    public String getCertPass()
    {
        return certPass;
    }

    public String sign(String src) throws Exception
    {
        ToolKit.writeLog(this.getClass().getName(), "sign", certPath+"|"+certPass+"|"+src);
        //从私钥证书中获得签名者私钥
        //也可以用getPriKey(byte[] pfxData, String pfxPWD)
        JKey priKey = KeyUtil.getPriKey(certPath, certPass);
        //X509Cert cert = 
        X509Cert[] certs = new X509Cert[1];
        certs[0] = CertUtil.getCert(certPath, certPass);
        SignatureUtil signUtil = new SignatureUtil();
        //对消息签名
        byte[] b64SignData = signUtil.p7SignMessage(true, src.getBytes(),
            SignatureUtil.SHA1_RSA, priKey, certs, session);
         ToolKit.writeLog(this.getClass().getName(), "sign.result", new String(b64SignData));
        System.out.println("签名成功,签名结果为:" + new String(b64SignData));
        return new String(b64SignData);
    }

    public String unSign(String sign) throws Exception
    {
         ToolKit.writeLog(this.getClass().getName(), "unSign", certPath+"|"+certPass+"|"+sign);
        SignatureUtil signUtil = new SignatureUtil();
        boolean verify = signUtil.p7VerifySignMessage(sign.getBytes(), session);
        if (verify)
        {
            //获得签名中的证书
            X509Cert[] x509Certs = signUtil.getSigerCert();
            System.out.println("验证消息(非分离式)签名成功, 证书为:");
            for (int i = 0; i < x509Certs.length; i++)
            {
                System.out.println(x509Certs[i].getSubject());
            }
            //获得签名数据中的原文
            byte[] srcData = signUtil.getSignedContent();
            ToolKit.writeLog(this.getClass().getName(), "unSign.result", new String(srcData));
            return new String(srcData);
        }
        else
        {
            System.out.println("验证消息(非分离式)签名失败");
            throw new Exception("验证消息(非分离式)签名失败");
        }
    }
}
