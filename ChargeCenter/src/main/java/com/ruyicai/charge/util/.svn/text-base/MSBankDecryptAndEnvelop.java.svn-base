package com.ruyicai.charge.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.umpay.api.log.SysOutLogger;

import Union.JnkyServer;

public class MSBankDecryptAndEnvelop {
	private static final Logger logger = Logger
			.getLogger(MSBankDecryptAndEnvelop.class);

	public MSBankDecryptAndEnvelop() {

	}

	/**
	 * 加密
	 * 
	 * @param
	 * @return
	 */
	public static String Envelop(String content, String keyMiMa) {
		try {
			logger.info("开始加密");
			// String msBank_pubKey =
			// ConfigUtil.getConfig("charge.properties","msBank_pubKey");//
			// 民生银行公钥
			// logger.info("民生银行公钥msBank_pubKey=" + msBank_pubKey);
			// msBank_pubKey = new String(msBank_pubKey.toString().trim()
			// .getBytes("ISO-8859-1"), "UTF-8");
			// logger.info("编码后的银行公钥msBank_pubKey=" + msBank_pubKey);
			// String msShangHu_preKey =
			// ConfigUtil.getConfig("charge.properties","msShangHu_preKey");//
			// 民生银行的商户私钥
			// logger.info("民生银行的商户私钥msShangHu_preKey=" + msShangHu_preKey);
			//
			// msShangHu_preKey = new String(msShangHu_preKey.toString().trim()
			// .getBytes("ISO-8859-1"), "UTF-8");
			// logger.info("编码后的民生银行的商户私钥msShangHu_preKey=" + msShangHu_preKey);

			// msShangHu_preKey=D:/cvswork/umpay_wap/ruyicai.pfx
			// ###民生银行充值民生公钥
			// msBank_pubKey=D:/cvswork/umpay_wap/cmbc-base64.cer
			try {
				logger.info("keyMiMa: " + keyMiMa);
			} catch (Exception e) {
				logger.error(e);
			}
			JnkyServer my = new JnkyServer(
					MSBankDecryptAndEnvelop.readFile(MSBankDecryptAndEnvelop.class
							.getResourceAsStream("/cmbc-base64.cer")),
					MSBankDecryptAndEnvelop
							.readFile(MSBankDecryptAndEnvelop.class
									.getResourceAsStream("/ruyicai.pfx")),
					keyMiMa);
			logger.info("获得加密");
			String envolopData = my.EnvelopData(content);// 加密
			logger.info("加密结束");
			return envolopData;
		} catch (Exception e) {
			logger.error("民生银行请求参数加密出现异常   Exception:" + e.toString()
					+ ",Exception2:" + e.getMessage(), e);
			return "";
		}

	}

	/**
	 * 解密
	 * 
	 * @param content
	 * @param keyMiMa
	 * @return
	 */
	public static String Decrypt(String content, String keyMiMa) {
		try {
			logger.info("解密开始");
			// String msBank_pubKey =
			// ConfigUtil.getConfig("charge.properties","msBank_pubKey");//
			// 民生银行公钥
			// logger.info("民生银行公钥msBank_pubKey=" + msBank_pubKey);
			// msBank_pubKey = new String(msBank_pubKey.toString().trim()
			// .getBytes("ISO-8859-1"), "UTF-8");
			// logger.info("编码后的银行公钥msBank_pubKey=" + msBank_pubKey);

			// String msShangHu_preKey =
			// ConfigUtil.getConfig("charge.properties","msShangHu_preKey");//
			// 民生银行的商户私钥
			// logger.info("民生银行的商户私钥msShangHu_preKey=" + msShangHu_preKey);

			// msShangHu_preKey = new String(msShangHu_preKey.toString().trim()
			// .getBytes("ISO-8859-1"), "UTF-8");
			// logger.info("编码后的民生银行的商户私钥msShangHu_preKey=" + msShangHu_preKey);
			JnkyServer my1 = new JnkyServer(
					MSBankDecryptAndEnvelop.readFile(MSBankDecryptAndEnvelop.class
							.getResourceAsStream("/ruyicai.pfx")), keyMiMa);
			logger.info("开始解密");
			String decryptData = my1.DecryptData(content);// 加密
			logger.info("解密decryptData=" + decryptData);
			logger.info("解密结束");
			return decryptData;
		} catch (Exception e) {
			logger.error("民生银行返回参数加密出现异常   Exception:" + e.toString()
					+ ",Exception2:" + e.getMessage());
			return "";
		}

	}

	private static byte[] readFile(InputStream in) {
		try {
			// FileInputStream fileInStream = new FileInputStream(filename);
			byte[] buf = new byte[in.available()];
			in.read(buf);
			in.close();
			return buf;
		} catch (Exception e) {
			logger.error("读取文件民生银行密钥时出现异常   Exception :" + e);
		}

		return null;
	}

	private static void writeFile(String fileName, byte[] data) {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			fos.write(data);
			fos.close();
		} catch (Exception e) {
			logger.error(e);
		}

	}

	public static void main(String[] args) throws Exception {
		System.out.print(new String(MSBankDecryptAndEnvelop
				.readFile(new FileInputStream(MSBankDecryptAndEnvelop.class
						.getResource("/struts.xml").getFile()))));
	}
}
