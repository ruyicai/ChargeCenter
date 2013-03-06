package com.ruyicai.charge.dna.v2.common;

/** 字符，格式转换工具类。
 * 
 * @author Administrator
 */
public class Converter {
	private static char byte2Char(byte no) {

		char[] table = { 0x00 + '0', 0x01 + '0', 0x02 + '0', 0x03 + '0',
				0x04 + '0', 0x05 + '0', 0x06 + '0', 0x07 + '0', 0x08 + '0',
				0x09 + '0', 0x00 + 'A', 0x01 + 'A', 0x02 + 'A', 0x03 + 'A',
				0x04 + 'A', 0x05 + 'A' };
		return table[no];
	}

	private static byte byte2OByte(byte c) {

		if (c >= '0' && c <= '9') {
			c = (byte) (c - '0');
		} else if (c >= 'a' && c <= 'z') {
			c = (byte) (c - 'a' + 0x0A);
		} else if (c >= 'A' && c <= 'Z') {
			c = (byte) (c - 'A' + 0x0A);
		}

		return c;
	}

	/**
	 * 
	 * 字符串转十进制byte，输入：98ABC，输出：00001001 00001000 00001010 00001011 00001100
	 * 输入的必须是0-9 a-f A-F这22个，输出是由字符串转十进制后的字节数组
	 */
	public static byte[] str2OBytes(String str) {

		byte[] result = str.getBytes();

		for (int i = 0; i < result.length; i++) {

			result[i] = byte2OByte(result[i]);
		}
		return result;
	}

	/**
	 * 
	 * bcd str转bytes,输入：86AE，输出：10000110 10011110 输入的必须是0-9 a-f
	 * A-F这22个，输出是由BCD字符串转字节并合并后的字节数组
	 */
	public static byte[] bcdStr2Bytes(String bcd, boolean leftAdd0) {

		if (leftAdd0) {
			while (bcd.length() % 2 != 0)
				bcd = "0" + bcd;
		} else {
			while (bcd.length() % 2 != 0)
				bcd += "0";
		}

		byte[] temp = bcd.getBytes();
		byte[] result = new byte[temp.length / 2];

		for (int i = 0; i < result.length; i++) {

			byte h = byte2OByte(temp[2 * i]);
			byte l = byte2OByte(temp[2 * i + 1]);

			result[i] = (byte) ((h << 4) + l);
		}
		return result;
	}
	

	/**
	 * 
	 * bcd str转bytes,输入：86AE，输出：10000110 10011110 输入的必须是0-9 a-f
	 * A-F这22个，输出是由BCD字符串转字节并合并后的字节数组
	 */
	public static byte[] bcdBytes2Bytes(byte[] bcd) {

		if (bcd.length % 2 != 0)
			return null;

		byte[] temp = bcd;
		byte[] result = new byte[temp.length / 2];

		for (int i = 0; i < result.length; i++) {

			byte h = byte2OByte(temp[2 * i]);
			byte l = byte2OByte(temp[2 * i + 1]);

			result[i] = (byte) ((h << 4) + l);
		}
		return result;
	}


	/**
	 * 
	 * bytes转BCD串，输入：01001100
	 * 00011000(bytes[0]=01001100,bytes[1]=00011000)，输出：4C18
	 */
	public static String bcdBytes2Str(byte[] bytes) {
		return bcdBytes2Str(bytes, false, false);
	}

	public static String bcdBytes2Str(byte[] bytes, boolean cut, boolean cutLeft) {

		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {

			byte h = (byte) ((bytes[i] & 0xf0) >>> 4);
			byte l = (byte) (bytes[i] & 0x0f);

			temp.append(byte2Char(h)).append(byte2Char(l));
		}

		return cut ? (cutLeft ? temp.toString().substring(1) : temp.toString()
				.substring(0, temp.length() - 1)) : temp.toString();
	}

	/**
	 * 
	 * 二进制转二进制串。输入：00010000
	 * 00000001(bytes[0]=00010000,bytes[1]=00000001)，输出：0001000000000001
	 */
	public static String bin2BinStr(byte[] bytes) {

		StringBuffer temp = new StringBuffer(bytes.length * 8);

		for (int i = 0; i < bytes.length; i++) {

			temp.append((byte) ((bytes[i] & 0x80) >>> 7));
			temp.append((byte) ((bytes[i] & 0x40) >>> 6));
			temp.append((byte) ((bytes[i] & 0x20) >>> 5));
			temp.append((byte) ((bytes[i] & 0x10) >>> 4));
			temp.append((byte) ((bytes[i] & 0x08) >>> 3));
			temp.append((byte) ((bytes[i] & 0x04) >>> 2));
			temp.append((byte) ((bytes[i] & 0x02) >>> 1));
			temp.append((byte) ((bytes[i] & 0x01)));
		}

		return temp.toString();
	}

	public static byte[] str2Bcd(String str) {
		return str2Bcd(str, true);
	}

	/**
	 * 
	 * 10进制串转为BCD码(字节数组)，输入：0123456789，输出：00000001 00100011 01000101 01100111
	 * 10001001 输入的必须是0-9 a-f A-F这22个字符 如果不是左补‘0’，就是右补‘F’
	 */
	public static byte[] str2Bcd(String str, boolean leftAdd0) {

		if (leftAdd0) {
			while (str.length() % 2 != 0)
				str = "0" + str;
		} else {
			while (str.length() % 2 != 0)
				str += "F";
		}

		byte[] temp = str.getBytes();
		byte[] result = new byte[temp.length / 2];

		for (int i = 0; i < result.length; i++) {

			byte h = byte2OByte(temp[2 * i]);
			byte l = byte2OByte(temp[2 * i + 1]);

			result[i] = (byte) ((h << 4) + l);
		}

		return result;
	}

	/**
	 * 
	 * 二进制串转二进制(字节数组)，输入：00110100 01110001 11000001(24字符串)，输出：00110100 01110001
	 * 11000001(3字节) 输入的字符串职能包含0或1
	 */
	public static byte[] binStr2Bin(String str) {

		byte[] temp = str.getBytes();
		if (str.length() == 0)
			throw new RuntimeException("字符串不能为空，转化二进制失败");
		if (temp.length % 8 != 0)
			throw new RuntimeException("字符串'" + str + "'长度不是8的倍数，转化二进制失败");
		for (int i = 0; i < temp.length; i++) {
			if ((temp[i] - '0') < 0 || (temp[i] - '0') > 1)
				throw new RuntimeException("字符串'" + str
						+ "'包含'0'和'1'以外的字符，转化二进制失败");
		}

		byte[] result = new byte[temp.length / 8];

		int a0, a1, a2, a3, a4, a5, a6, a7;

		for (int i = 0; i < str.length() / 8; i++) {

			a0 = temp[8 * i] - '0';
			a1 = temp[8 * i + 1] - '0';
			a2 = temp[8 * i + 2] - '0';
			a3 = temp[8 * i + 3] - '0';
			a4 = temp[8 * i + 4] - '0';
			a5 = temp[8 * i + 5] - '0';
			a6 = temp[8 * i + 6] - '0';
			a7 = temp[8 * i + 7] - '0';

			result[i] = (byte) ((a0 << 7) + (a1 << 6) + (a2 << 5) + (a3 << 4)
					+ (a4 << 3) + (a5 << 2) + (a6 << 1) + a7);
		}

		return result;
	}

	/**
	 * 输入：1001(4bits)，输出：00000001 00000000 00000000 00000001(4bytes)
	 */
	public static byte[] binBytes2AscBytes(byte[] bin) {

		byte[] result = new byte[bin.length * 8];

		for (int i = 0; i < bin.length; i++) {

			result[8 * i] = (byte) ((bin[i] & 0x80) >>> 7);
			result[8 * i + 1] = (byte) ((bin[i] & 0x40) >>> 6);
			result[8 * i + 2] = (byte) ((bin[i] & 0x20) >>> 5);
			result[8 * i + 3] = (byte) ((bin[i] & 0x10) >>> 4);
			result[8 * i + 4] = (byte) ((bin[i] & 0x08) >>> 3);
			result[8 * i + 5] = (byte) ((bin[i] & 0x04) >>> 2);
			result[8 * i + 6] = (byte) ((bin[i] & 0x02) >>> 1);
			result[8 * i + 7] = (byte) ((bin[i] & 0x01));
		}

		return result;
	}

	/**
	 * 输入：00000001 00000000 00000000 00000001(4bytes)，输出：1001(4bits)
	 */
	public static byte[] ascBytes2BinBytes(byte[] asc) {

		byte[] result = new byte[asc.length / 8];

		int a0, a1, a2, a3, a4, a5, a6, a7;

		for (int i = 0; i < asc.length / 8; i++) {

			a0 = asc[8 * i];
			a1 = asc[8 * i + 1];
			a2 = asc[8 * i + 2];
			a3 = asc[8 * i + 3];
			a4 = asc[8 * i + 4];
			a5 = asc[8 * i + 5];
			a6 = asc[8 * i + 6];
			a7 = asc[8 * i + 7];

			result[i] = (byte) ((a0 << 7) + (a1 << 6) + (a2 << 5) + (a3 << 4)
					+ (a4 << 3) + (a5 << 2) + (a6 << 1) + a7);
		}

		return result;
	}

	/**
	 * 输入：95BE5779 04DC9CF7，输出：39 35 42 45 35 37 37 39 30 34 44 43 39 43 46 37
	 */
	public static byte[] bcdBytes2AscBytes(byte[] bcd) {

		byte[] result = new byte[bcd.length * 2];

		for (int i = 0; i < bcd.length; i++) {

			byte h = (byte) ((bcd[i] & 0xf0) >>> 4);
			byte l = (byte) (bcd[i] & 0x0f);

			result[2 * i] = (byte) byte2Char(h);
			result[2 * i + 1] = (byte) byte2Char(l);
		}

		return result;
	}

	/**
	 * 数字转2进制，右靠，左补'0'(bit) 输入：60，2 输出：00000000 00111100 (0x00,0x3C)
	 */
	public static byte[] int2BcdBytes(int num, int length) {

		String hex = Integer.toBinaryString(num);

		try {
			hex = Strings.format(hex, length * 8 - hex.length(), length * 8,
					"0", "0", true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return binStr2Bin(hex);
	}

	/**
	 * 输入：00000000 00111100 (0x00,0x3C) 输出：60
	 */
	public static int bcdBytes2Int(byte[] bcd) {

		return Integer.valueOf(bcdBytes2Str(bcd), 16);
	}

	/**
	 * 输入字节数组，返回该字节数组的字符串
	 */
	public static String getBcdString(byte[] src, int srcPos, int length) {
		return getBcdString(src, srcPos, 0, length, false, false);
	}

	public static String getBcdString(byte[] src, int srcPos, int destPos,
			int length, boolean cut, boolean cutLeft) {

		byte[] temp = new byte[length];
		System.arraycopy(src, srcPos, temp, destPos, length);
		return bcdBytes2Str(temp, cut, cutLeft);
	}

	public static String getAscString(byte[] src, int srcPos, int length) {
		return getAscString(src, srcPos, 0, length, false, false);
	}

	public static String getAscString(byte[] src, int srcPos, int destPos,
			int length, boolean cut, boolean cutLeft) {

		byte[] temp = new byte[length];
		System.arraycopy(src, srcPos, temp, destPos, length);
		String result = "";
		try {
			result = new String(temp, "ASCII");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static String getGB2312String(byte[] src, int srcPos, int length) throws Exception{

		byte[] temp = new byte[length];
		System.arraycopy(src, srcPos, temp, 0, length);
		return new String(temp, "GB2312");
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/** */
	/**
	 * 把字节数组转换成16进制字符串
	 * 
	 * @param bArray
	 * @return
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

}
