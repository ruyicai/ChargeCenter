package com.ruyicai.charge.dna.v2.common.encrpt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.ruyicai.charge.dna.v2.common.Converter;
import com.ruyicai.charge.dna.v2.common.InfoLevel;
import com.ruyicai.charge.dna.v2.common.Strings;
import com.ruyicai.charge.dna.v2.common.ToolKit;


/**　　DES 使用一个 56 位的密钥以及附加的 8 位奇偶校验位，产生最大 64 位的分组大小。
 * 这是一个迭代的分组密码，使用称为 Feistel 的技术，其中将加密的文本块分成两半。
 * 使用子密钥对其中一半应用循环功能，然后将输出与另一半进行“异或”运算；
 * 接着交换这两半，这一过程会继续下去，但最后一个循环不交换。DES 使用 16 个循环，
 * 使用异或，置换，代换，移位操作四种基本运算
 *
 * @author: XieminQuan
 * @time  : 2007-11-24 下午04:50:48
 *
 * DNAPAY
 */
public class Des implements IPosDes {

    public static void main(String[] args) throws Exception {
        String contents = "sdfasfdsdfsdfsafd";
        String dd = new String(new Des().encrypt(contents.getBytes()));
        System.out.println();

    }

    private static Cipher cipher;
    private byte[] MK;
    private byte[] PIK;
    private byte[] MAK;
    private byte[] check = Converter.str2Bcd("0000000000000000");//.getBytes();
    private static byte[] MKK = Converter.str2Bcd("1234567890ABCDEF");

    /**
     * 加密MK(主密钥的密钥)
     */
    public static byte[] encrypt(byte[] data) throws Exception {

        if (cipher == null) {
            cipher = Cipher.getInstance("DES/ECB/NoPadding", "SunJCE");
        }
        SecretKey key = new SecretKeySpec(MKK, "DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    /**
     * 解密MK(主密钥的密钥)
     */
    public static byte[] decrypt(byte[] data) throws Exception {

        if (cipher == null) {
            cipher = Cipher.getInstance("DES/ECB/NoPadding", "SunJCE");
        }
        SecretKey key = new SecretKeySpec(MKK, "DES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    /* (non-Javadoc)
     */
    public void setKeys(byte[] key) throws Exception {
        ToolKit.writeLog(InfoLevel.INFO, this.getClass().getName(), "setKeys", "[" + key.length + "/" + Converter.bytesToHexString(key) + "]");

        if (key.length == 24) {


            byte[] pikSec = new byte[8];
            byte[] pikCheck = new byte[4];
            byte[] makSec = new byte[8];
            byte[] makCheck = new byte[4];

            System.arraycopy(key, 0, pikSec, 0, 8);
            System.arraycopy(key, 8, pikCheck, 0, 4);
            System.arraycopy(key, 12, makSec, 0, 8);
            System.arraycopy(key, 20, makCheck, 0, 4);


            this.decryptPIK(pikSec);
            ToolKit.writeLog(this.getClass().getName(),
                    "PinKey", "[" + Converter.bytesToHexString(PIK) + "]");

            byte[] PAKcheckvalue = this.encryptPIKCheck();
            ToolKit.writeLog(this.getClass().getName(),
                    "PiKCheck", "[" + Converter.bytesToHexString(PAKcheckvalue) + "]");

            if (new String(PAKcheckvalue).indexOf(new String(pikCheck)) != 0) {
                throw new RuntimeException("PIK的checkvalue匹配失败");
            }

            this.decryptMAK(makSec);
            ToolKit.writeLog(this.getClass().getName(),
                    "MacKey", "[" + Converter.bytesToHexString(MAK) + "]");

            byte[] MAKcheckvalue = this.encryptMAKCheck();
            ToolKit.writeLog(this.getClass().getName(),
                    "MacCheck", "[" + Converter.bytesToHexString(MAKcheckvalue) + "]");

            if (new String(MAKcheckvalue).indexOf(new String(makCheck)) != 0) {
                throw new RuntimeException("MAK的checkvalue匹配失败");
            }
        } else {
            throw new RuntimeException("只支持单倍长加密算法");
        }
    }

    /* (non-Javadoc)
     */
    public byte[] encryptPin(byte[] data) throws Exception {
        String pin = new String(data);
        pin = Strings.padRight(pin, 'F', 16);
        data = Converter.str2Bcd(pin);

        SecretKey key = new SecretKeySpec(PIK, "DES");
        Des.getCipher().init(Cipher.ENCRYPT_MODE, key);

        return Des.getCipher().doFinal(data);
    }

    /* (non-Javadoc)
     */
    public byte[] encryptPIK() throws Exception {
        SecretKey key = new SecretKeySpec(MK, "DES");
        Des.getCipher().init(Cipher.ENCRYPT_MODE, key);

        return Des.getCipher().doFinal(PIK);
    }

    /* (non-Javadoc)
     */
    public byte[] encryptMAK() throws Exception {
        SecretKey key = new SecretKeySpec(MK, "DES");
        Des.getCipher().init(Cipher.ENCRYPT_MODE, key);

        return Des.getCipher().doFinal(MAK);
    }

    public byte[] decryptPIK(byte[] pik) throws Exception {
        SecretKey mainKey = new SecretKeySpec(MK, "DES");

        Des.getCipher().init(Cipher.DECRYPT_MODE, mainKey);
        PIK = Des.getCipher().doFinal(pik);
        return PIK;
    }

    public byte[] decryptMAK(byte[] mak) throws Exception {
        SecretKey mainKey = new SecretKeySpec(MK, "DES");

        Des.getCipher().init(Cipher.DECRYPT_MODE, mainKey);
        MAK = Des.getCipher().doFinal(mak);
        return mak;
    }

    /* (non-Javadoc)
     */
    public byte[] encryptPIKCheck() throws Exception {
        SecretKey pinKey = new SecretKeySpec(PIK, "DES");
        Des.getCipher().init(Cipher.ENCRYPT_MODE, pinKey);
        byte[] PAKcheckvalue = Des.getCipher().doFinal(check);
        byte[] check = new byte[4];
        System.arraycopy(PAKcheckvalue, 0, check, 0, check.length);

        return check;
    }

    /* (non-Javadoc)
     */
    public byte[] encryptMAKCheck() throws Exception {
        SecretKey pinKey = new SecretKeySpec(MAK, "DES");
        Des.getCipher().init(Cipher.ENCRYPT_MODE, pinKey);
        byte[] PAKcheckvalue = cipher.doFinal(check);
        byte[] check = new byte[4];
        System.arraycopy(PAKcheckvalue, 0, check, 0, check.length);

        return check;
    }

    /* (non-Javadoc)
     */
    public byte[] decryptPin(byte[] data) throws Exception {
        SecretKey key = new SecretKeySpec(PIK, "DES");
        Des.getCipher().init(Cipher.DECRYPT_MODE, key);

        byte[] pin = Des.getCipher().doFinal(data);
        return Converter.bcdBytes2AscBytes(pin);
    }

    /* (non-Javadoc)
     */
    public byte[] encryptMac(byte[] src) throws Exception {

        SecretKey key = new SecretKeySpec(MAK, "DES");
        Des.getCipher().init(Cipher.ENCRYPT_MODE, key);


        int le = 8 - src.length % 8;
        if (le == 8) {
            le = 0;
        }

        byte[] temp = new byte[le];
        for (int i = 0; i < le; i++) {
            temp[i] = 0x00;
        }

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
            result = Des.getCipher().doFinal(result);
        }
        return result;
    }

    /* (non-Javadoc)
     */
    public byte[] decryptMac(byte[] data) throws Exception {
        SecretKey key = new SecretKeySpec(MAK, "DES");
        Des.getCipher().init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    /* (non-Javadoc)
     */
    public void setMK(String data) {
        ToolKit.writeLog(InfoLevel.INFO, this.getClass().getName(), "setMK", "[" + data.length() + "/" + data + "]");

        this.MK = Converter.str2Bcd(data);
    }

    public byte[] getMacKey() {
        return this.MAK;
    }

    public void setMacKey(byte[] macKey) {
        this.MAK = macKey;
    }

    public byte[] getPinKey() {
        return this.PIK;
    }

    public void setPinKey(byte[] pinKey) {
        this.PIK = pinKey;
    }

    public static Cipher getCipher() {
        if (cipher == null) {
            try {
                cipher = Cipher.getInstance("DES/ECB/NoPadding", "SunJCE");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cipher;
    }
}
