package com.ruyicai.charge.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class KeyedDigestMD5 {

	public static byte[] getKeyedDigest(byte[] buffer, byte[] key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(buffer);
			return md5.digest(key);
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static String getKeyedDigest(String strSrc, String key) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(strSrc.getBytes("UTF8"));

			String result = "";
			byte[] temp;
			temp = md5.digest(key.getBytes("UTF8"));
			for (int i = 0; i < temp.length; i++) {
				result += Integer.toHexString(
						(0x000000ff & temp[i]) | 0xffffff00).substring(6);
			}

			return result;

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param args
	 */
	// public static void main(String[] args) {
	// // TODO Auto-generated method stub
	// KeyedDigestMD5 md5 = new KeyedDigestMD5();
	// String mi;
	// String s =
	// "version_id=1.1&merchant_id=6250&order_date=20070327&order_id=4057&amount=0.01&currency=RMB&returl=http://test.wangpiao.net/payment/19Pay_Return.aspx&pm_id=&pc_id=&merchant_key=123456789";
	// mi = md5.getKeyedDigest(s, "");
	//
	// System.out.println("mi:" + mi);
	//
	// }

}
