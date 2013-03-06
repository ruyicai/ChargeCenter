package com.ruyicai.charge.dna.v2.common;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ruyicai.charge.dna.v2.common.encrpt.IPosDes;

/** 类型转换工具类
 * 
 * @author Administrator
 */
public class Utilities {


	/**
	 * 输入：1001(4bits)，输出：00000001 00000000 00000000 00000001(4bytes)
	 */
	public static byte[] binBytes2AscBytes(byte[] bin) {
		
		byte[] result = new byte[bin.length*8];
		
		for(int i = 0;i < bin.length;i++) {
			
			result[8*i]     = (byte)((bin[i]&0x80) >>> 7);
			result[8*i + 1] = (byte)((bin[i]&0x40) >>> 6);
			result[8*i + 2] = (byte)((bin[i]&0x20) >>> 5);
			result[8*i + 3] = (byte)((bin[i]&0x10) >>> 4);
			result[8*i + 4] = (byte)((bin[i]&0x08) >>> 3);
			result[8*i + 5] = (byte)((bin[i]&0x04) >>> 2);
			result[8*i + 6] = (byte)((bin[i]&0x02) >>> 1);
			result[8*i + 7] = (byte)((bin[i]&0x01));
		}
		
		return result;
	}
	
	/**
	 * 输入：00000001 00000000 00000000 00000001(4bytes)，输出：1001(4bits)
	 */
	public static byte[] ascBytes2BinBytes(byte[] asc) {
		
		byte[] result = new byte[asc.length/8];
		
		int a0,a1,a2,a3,a4,a5,a6,a7;

		for(int i=0;i < asc.length/8;i++) {
			
			a0 = asc[8*i];
			a1 = asc[8*i + 1];
			a2 = asc[8*i + 2];
			a3 = asc[8*i + 3];
			a4 = asc[8*i + 4];
			a5 = asc[8*i + 5];
			a6 = asc[8*i + 6];
			a7 = asc[8*i + 7];

			result[i] = (byte)((a0<<7) + (a1<<6) + (a2<<5) + (a3<<4) + (a4<<3) + (a5<<2) + (a6<<1) + a7);
		}
		
		return result;
	}
	
	public static byte[] to64MacBytes(IPosDes ClientDes, byte[] src) {

		try {

			int le = 8 - src.length % 8;
			if (le == 8)
				le = 0;
			byte[] temp = new byte[le];
			for (int i = 0; i < le; i++)
				temp[i] = 0x00;

			byte[] max = new byte[src.length + le];
			System.arraycopy(src, 0, max, 0, src.length);
			System.arraycopy(temp, 0, max, src.length, temp.length);

			byte[] bi = new byte[8];
			byte[] result = new byte[8];
			for (int i = 0; i < max.length / 8; i++) {

				System.arraycopy(max, 8 * i, bi, 0, 8);

				for (int j = 0; j < 8; j++) {
					if (i == 0) {
						result[j] = (byte) (bi[j] ^ 0x00);
					} else {
						result[j] = (byte) (result[j] ^ bi[j]);
					}
				}
				result = ClientDes.encryptMac(result);
			}
			return result;
			// byte[] re = new byte[4];
			// System.arraycopy(result,0,re,0,4);
			//			
			// return re;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	public static byte[] Change2bitIntToByte(short integer) {
		ByteBuffer buffer = null;
		buffer = ByteBuffer.allocate(2);
		buffer.putShort(integer);
		return buffer.array();
	}

	public static byte[] Change4bitIntToByte(int integer) {
		ByteBuffer buffer = null;
		buffer = ByteBuffer.allocate(4);
		buffer.putInt(integer);
		return buffer.array();
	}

	public static byte[] Change8bitIntToByte(long integer) {
		ByteBuffer buffer = null;
		buffer = ByteBuffer.allocate(8);
		buffer.putLong(integer);
		return buffer.array();
	}

	public static short ChangeByteTo2Int(byte[] bytes) {
		ByteBuffer buffer = null;
		buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		return buffer.getShort(0);
	}

	public static int ChangeByteTo4Int(byte[] bytes) {
		ByteBuffer buffer = null;
		buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		return buffer.getInt(0);
	}

	public static long ChangeByteTo8Int(byte[] bytes) {
		ByteBuffer buffer = null;
		buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		return buffer.getLong(0);
	}

	public static String ChangeByteToString(byte[] bytes) throws Exception {
		return new String(bytes, "ASCII");
	}

	public static String ChangeGBKByteToString(byte[] bytes) throws Exception {
		return new String(bytes, "GBK");

	}

	public static String ChangeGB2312ByteToString(byte[] bytes) throws Exception {
		return new String(bytes, "gb2312");

	}

	public static String ChangeUTFByteToString(byte[] bytes) throws Exception {
		return new String(bytes, "UTF-16");
	}

	public static String FormatConnectDate(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	public static Date ParseDateStr(String dateStr, String dateFormat)
			throws Exception {
		SimpleDateFormat parser = new SimpleDateFormat(dateFormat);
		Date date = parser.parse(dateStr);

		return date;
	}

	public static String getStackTrace(Exception e) {
		StackTraceElement[] stackTraceElement = e.getStackTrace();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < stackTraceElement.length; i++) {
			buffer.append(stackTraceElement[i].toString() + "\r\n");
		}
		return buffer.toString();
	}
}
