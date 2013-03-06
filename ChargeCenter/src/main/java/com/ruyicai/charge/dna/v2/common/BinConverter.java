package com.ruyicai.charge.dna.v2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * java二进制,字节数组,字符,十六进制,BCD编码转换
 * @author Jinson
 *
 */
public class BinConverter {

    /**
	 * Converts bytes from an array into an integer.
	 *
	 * @param buf
	 *            The source buffer..
	 * @param ofs
	 *            index From where to start reading the data.
	 * @return The 32bit integer value.
	 */
	public final static int byteArrayToInt(byte[] buf, int ofs) {
		return (buf[ofs] << 24) | ((buf[ofs + 1] & 0x0ff) << 16)
				| ((buf[ofs + 2] & 0x0ff) << 8) | (buf[ofs + 3] & 0x0ff);
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Stores an integer into a byte array.
	 *
	 * @param value
	 *            The 32bit integer to store.
	 * @param buf
	 *            The target buffer.
	 * @param ofs
	 *            Where to start writing in the target buffer.
	 */
	public final static void intToByteArray(int value, byte[] buf, int ofs) {
		buf[ofs] = (byte) ((value >>> 24) & 0x0ff);
		buf[ofs + 1] = (byte) ((value >>> 16) & 0x0ff);
		buf[ofs + 2] = (byte) ((value >>> 8) & 0x0ff);
		buf[ofs + 3] = (byte) value;
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Converts bytes from an array into a 64bit integer.
	 *
	 * @param buf
	 *            The source buffer.
	 * @param ofs
	 *            From where to start reading in the source buffer.
	 * @return The 64bit integer.
	 */
	public final static long byteArrayToLong(byte[] buf, int ofs) {
		// (optimized for 32bit platforms)

		return ((long) ((buf[ofs] << 24) | ((buf[ofs + 1] & 0x0ff) << 16)
				| ((buf[ofs + 2] & 0x0ff) << 8) | (buf[ofs + 3] & 0x0ff)) << 32)
				| ((long) ((buf[ofs + 4] << 24)
						| ((buf[ofs + 5] & 0x0ff) << 16)
						| ((buf[ofs + 6] & 0x0ff) << 8) | (buf[ofs + 7] & 0x0ff)) & 0x0ffffffffL);
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Stores a 64bit integer into a byte array.
	 *
	 * @param value
	 *            The 64bit integer to store.
	 * @param buf
	 *            The target buffer.
	 * @param ofs
	 *            Where to start writing in the target buffer.
	 */
	public final static void longToByteArray(long value, byte[] buf, int ofs) {
		int tmp = (int) (value >>> 32);

		buf[ofs] = (byte) (tmp >>> 24);
		buf[ofs + 1] = (byte) ((tmp >>> 16) & 0x0ff);
		buf[ofs + 2] = (byte) ((tmp >>> 8) & 0x0ff);
		buf[ofs + 3] = (byte) tmp;

		tmp = (int) value;

		buf[ofs + 4] = (byte) (tmp >>> 24);
		buf[ofs + 5] = (byte) ((tmp >>> 16) & 0x0ff);
		buf[ofs + 6] = (byte) ((tmp >>> 8) & 0x0ff);
		buf[ofs + 7] = (byte) tmp;
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Converts values from a 32bit integer array to 64bit integer.
	 *
	 * @param buf
	 *            Buffer to read the 32bit integers from.
	 * @param ofs
	 *            Where to start reading in the buffer.
	 * @return The 64bit integer.
	 */
	public final static long intArrayToLong(int[] buf, int ofs) {
		return (((long) buf[ofs]) << 32)
				| (((long) buf[ofs + 1]) & 0x0ffffffffL);
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Stores a 64bit integer into a 32bit integer array.
	 *
	 * @param value
	 *            The 64bit integer to store.
	 * @param buf
	 *            The target buffer.
	 * @param ofs
	 *            Where to start writing in the target buffer.
	 */
	public final static void longToIntArray(long value, int[] buf, int ofs) {
		buf[ofs] = (int) (value >>> 32);
		buf[ofs + 1] = (int) value;
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Makes a 64bit integer out if two 32bit integers.
	 *
	 * @param lo
	 *            The lower 32bits.
	 * @param hi
	 *            The higher 32bits.
	 * @return The 64bit integer.
	 */
	public final static long makeLong(int lo, int hi) {
		return (((long) hi << 32) | ((long) lo & 0x00000000ffffffffL));
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the lower 32 bits of a 64bit integer.
	 *
	 * @param val
	 *            The 64bit integer.
	 * @return The lower 32 bits.
	 */
	public final static int longLo32(long val) {
		return (int) val;
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the higher 32 bits of a 64bit integer.
	 *
	 * @param val
	 *            The 64bit integer.
	 * @return The higher 32 bits.
	 */
	public final static int longHi32(long val) {
		return (int) (val >>> 32);
	}

	// /////////////////////////////////////////////////////////////////////////

	final static char[] HEXTAB = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * Converts a byte array to a hex-string representation.
	 *
	 * @param data
	 *            The byte array.
	 * @return The hex string, twice the length of the byte array.
	 */
	public final static String bytesToHexStr(byte[] data) {
		return bytesToHexStr(data, 0, data.length);
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Converts parts of a byte array to a hex string.
	 *
	 * @param data
	 *            The byte array.
	 * @param ofs
	 *            Where to start reading the bytes.
	 * @param len
	 *            The number of bytes to convert.
	 * @return The hex string, twice the length of the len parameter.
	 */
	public final static String bytesToHexStr(byte[] data, int ofs, int len) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.setLength(len << 1);

		int pos = 0;
		int c = ofs + len;

		while (ofs < c) {
			sbuf.setCharAt(pos++, HEXTAB[(data[ofs] >> 4) & 0x0f]);
			sbuf.setCharAt(pos++, HEXTAB[data[ofs++] & 0x0f]);
		}
		return sbuf.toString().toUpperCase();
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Converts a hex-string into a byte array. Invalid codes will be skipped.
	 * If the target buffer is not large enough the data will be truncated.
	 *
	 * @param hex
	 *            The hex-string.
	 * @param data
	 *            The target buffer.
	 * @param srcofs
	 *            From which character in the string the conversion should
	 *            begin. This is usually an even number, but it doesn't have to.
	 * @param dstofs
	 *            Where to start storing the bytes in the target buffer.
	 * @param len
	 *            The number of bytes to extract.
	 * @return The number of extracted bytes.
	 */
	public final static int hexStrToBytes(String hex, byte[] data, int srcofs,
			int dstofs, int len) {
		// check for correct ranges

		final int strlen = hex.length();

		int availBytes = (strlen - srcofs) >> 1;
		if (availBytes < len) {
			len = availBytes;
		}

		final int outputCapacity = data.length - dstofs;
		if (len > outputCapacity) {
			len = outputCapacity;
		}

		// convert now

		final int dstofsBak = dstofs;

		for (int i = 0; i < len; i++) {
			byte abyte = 0;
			boolean convertOK = true;

			for (int j = 0; j < 2; j++) {
				abyte <<= 4;
				char cActChar = hex.charAt(srcofs++);

				if ((cActChar >= 'a') && (cActChar <= 'f')) {
					abyte |= (byte) (cActChar - 'a') + 10;
				} else {
					if ((cActChar >= '0') && (cActChar <= '9')) {
						abyte |= (byte) (cActChar - '0');
					} else {
						convertOK = false;
					}
				}
			}
			if (convertOK) {
				data[dstofs++] = abyte;
			}
		}

		return (dstofs - dstofsBak);
	}

	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Converts bytes into a string. Two bytes are read in big endian and
	 * converted to one Unicode character.
	 *
	 * @param data
	 *            The source buffer.
	 * @param ofs
	 *            Where to start reading in the source buffer.
	 * @param len
	 *            Number of bytes to convert. It should be an even number, since
	 *            two bytes make one character. If odd the last byte is ignored.
	 * @return The string.
	 */
	public final static String byteArrayToStr(byte[] data, int ofs, int len) {
		// we need two bytes for every character
		len &= ~1;

		// enough bytes in the buffer?
		final int availCapacity = data.length - ofs;

		if (availCapacity < len) {
			len = availCapacity;
		}

		final StringBuffer sbuf = new StringBuffer();
		sbuf.setLength(len >> 1);

		int sbufPos = 0;

		while (0 < len) {
			sbuf.setCharAt(sbufPos++,
					(char) ((data[ofs] << 8) | (data[ofs + 1] & 0x0ff)));
			ofs += 2;
			len -= 2;
		}

		return sbuf.toString();
	}


    /**
        * 把16进制字符串转换成字节数组
        * @param hex
        * @return
        */
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


    /**
        * 把字节数组转换成16进制字符串
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

    /**
        * 把字节数组转换为对象
        * @param bytes
        * @return
        * @throws IOException
        * @throws ClassNotFoundException
        */
    public static final Object bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(in);
        Object o = oi.readObject();
        oi.close();
        return o;
    }

    /**
        * 把可序列化对象转换成字节数组
        * @param s
        * @return
        * @throws IOException
        */
    public static final byte[] objectToBytes(Serializable s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream ot = new ObjectOutputStream(out);
        ot.writeObject(s);
        ot.flush();
        ot.close();
        return out.toByteArray();
    }

    public static final String objectToHexString(Serializable s) throws IOException{
        return bytesToHexString(objectToBytes(s));
    }

    public static final Object hexStringToObject(String hex) throws IOException, ClassNotFoundException{
        return bytesToObject(hexStringToByte(hex));
    }

    /**
        * @函数功能: BCD码转为10进制串(阿拉伯数据)
        * @输入参数: BCD码
        * @输出结果: 10进制串
        */
    public static String bcd2Str(byte[] bytes){
        StringBuffer temp=new StringBuffer(bytes.length*2);

        for(int i=0;i<bytes.length;i++){
         temp.append((byte)((bytes[i]& 0xf0)>>>4));
         temp.append((byte)(bytes[i]& 0x0f));
        }
        return temp.toString().substring(0,1).equalsIgnoreCase("0")?temp.toString().substring(1):temp.toString();
    }

    /**
        * @函数功能: 10进制串转为BCD码
        * @输入参数: 10进制串
        * @输出结果: BCD码
        */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
         asc = "0" + asc;
         len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
         len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length()/2; p++) {
         if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
          j = abt[2 * p] - '0';
         } else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
          j = abt[2 * p] - 'a' + 0x0a;
         } else {
          j = abt[2 * p] - 'A' + 0x0a;
         }

         if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
          k = abt[2 * p + 1] - '0';
         } else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
          k = abt[2 * p + 1] - 'a' + 0x0a;
         }else {
          k = abt[2 * p + 1] - 'A' + 0x0a;
         }

         int a = (j << 4) + k;
         byte b = (byte) a;
         bbt[p] = b;
        }
        return bbt;
    }


}
