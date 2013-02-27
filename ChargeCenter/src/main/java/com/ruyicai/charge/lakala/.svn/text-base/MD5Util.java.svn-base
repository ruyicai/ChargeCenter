package com.ruyicai.charge.lakala;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public static String signBySec(String content, String mackey) {
		String orgcontent = content + mackey;
		System.out.println("orgcontent is........" + orgcontent);
		return md5(orgcontent.getBytes());
	}

	public boolean verify(String content, String sign, String publicKey) {
		return true;
	}

	public static boolean verifysign(String orgdata, String sign, String mackey) {
		try {
			String md5ret = md5((orgdata + mackey).getBytes());
			if (md5ret.equals(sign))
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将byte数组转换成16进制AscII字符串
	 * 
	 * @param d
	 *            byte[]
	 * @return String
	 */
	public static String ByteArrayToHexString(byte[] d) {
		if (d == null)
			return "";
		if (d.length == 0)
			return "";

		int len = d.length * 2;
		byte[] strData = new byte[len];
		for (int i = 0; i < strData.length; i++)
			strData[i] = (byte) '0';
		byte[] data = new byte[d.length + 1];
		data[0] = 0;
		System.arraycopy(d, 0, data, 1, d.length);
		BigInteger bi = new BigInteger(data);
		byte[] src = bi.toString(16).getBytes();
		int offset = strData.length - src.length;
		len = src.length;
		System.arraycopy(src, 0, strData, offset, len);
		return new String(strData);
	}

	/**
	 * 哈希函数
	 * 
	 * @param algorithm
	 *            String MD5/SHA
	 * @param b
	 *            byte[]
	 * @return byte[]
	 */
	public static byte[] hashData(String algorithm, byte[] b)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(b);
		byte[] digest = md.digest();
		return digest;
	}

	/**
	 * 制作MD5
	 * 
	 * @param algorithm
	 *            String MD5/SHA
	 * @param b
	 *            byte[]
	 * @return byte[]
	 */
	public static String md5(byte[] b) {
		try {
			byte[] a = hashData("MD5", b);
			return ByteArrayToHexString(a);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	public static String sign(String content, String mackey) {
		String orgcontent = content + mackey;
		return md5(orgcontent.getBytes());
	}

	public static void main(String[] args) {
		String md50 = "20060301|testbillmd5|123456|123456243|123|321621|http://192.168.8.243:8080/lakala2/payInform.jsp|2|9997|";
		String md51 = "20060301|testbillmd5|123456|123456243|123|321621|http://192.168.8.243:8080/lakala2/payInform.jsp|2|9997|";
		String str = "20060301|3F010001601|lkl88uh67ij|go-29-1-aews|39900|512332|http://1234.lakala.com/order/lakala/notify.php|2|811002|";
		System.out.println("md50 is...." + MD5Util.md5(md50.getBytes()));
		System.out.println("md51 is........." + MD5Util.md5(md51.getBytes()));
		String md52 = "amount=0.01&can_pay=y&mer_id=312901&partner_bill_no=6842&partner_extendinfo=ydDC8834&partner_query_time=110819 062050&req_id=26824&sec_id=MD5&service=lakala.agency.tradePayBalance&v=1.1";
		String md53 = "amount=0.01&can_pay=y&mer_id=312901&partner_bill_no=6842&partner_extendinfo=ydDC8834&partner_query_time=110819 065341&req_id=26824&sec_id=MD5&service=lakala.agency.tradePayBalance&v=1.1123456";
		String md34 = "101720111228深圳vlp\\$!~$)>6/*8k^";
		// String ii="\";
		String dd = "20060301|3F070001001|clxfuh4jt52uhj|20111229-45427-21536506-100730|51900|588884264| http://store.kidulty.com/cart/lakala_r|2|312895|";

		String al = "3G020000301312967120331000002TR4567JGFDW49OLNBV";
		System.out.println("md51 is........." + MD5Util.md5(al.getBytes()));
	}
}
