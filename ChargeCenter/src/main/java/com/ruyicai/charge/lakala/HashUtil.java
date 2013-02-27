package com.ruyicai.charge.lakala;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

	public HashUtil() {
	}

	public static String ByteArrayToHexString(byte d[]) {
		if (d == null)
			return "";
		if (d.length == 0)
			return "";
		int len = d.length * 2;
		byte strData[] = new byte[len];
		for (int i = 0; i < strData.length; i++)
			strData[i] = 48;

		byte data[] = new byte[d.length + 1];
		data[0] = 0;
		System.arraycopy(d, 0, data, 1, d.length);
		BigInteger bi = new BigInteger(data);
		byte src[] = bi.toString(16).getBytes();
		int offset = strData.length - src.length;
		len = src.length;
		System.arraycopy(src, 0, strData, offset, len);
		return new String(strData);
	}

	public static byte[] hashData(String algorithm, byte b[])
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(b);
		byte digest[] = md.digest();
		return digest;
	}

	public static String md5(byte b[]) {

		try {
			byte a[];
			a = hashData("MD5", b);
			return ByteArrayToHexString(a);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String md5(String plainTxt) {

		try {
			byte a[];
			a = hashData("MD5", plainTxt.getBytes());
			return ByteArrayToHexString(a);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		String md50 = "20060301|testbillmd5|123456|123456243|123|321621|http://192.168.8.243:8080/lakala2/payInform.jsp|2|9997|";
		String md51 = "20060301|testbillmd5|123456|123456243|123|321621|http://192.168.8.243:8080/lakala2/payInform.jsp|2|9997|";
		try {
			String md55 = HashUtil.md5(md50.getBytes("GBK"));
			String md53 = HashUtil.md5(md51.getBytes("GBK"));
			System.out.println("md50 is...." + md55);
			System.out.println("md51 is........." + md53);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
