package com.ruyicai.charge.dna.common;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * ThridDes Encryption
 * @author Jinson Chen
 *
 */
public class DES {

    /**
     * @param args
     */

    private static String strDefaultKey = "0123456789ABCDEFFEDCBA9876543210";

    private Cipher encryptCipher = null;

    private Cipher decryptCipher = null;


    /**
     * 默认构造方法，使用默认密钥
     * @throws Exception
     */
    public DES() throws Exception {
        this(strDefaultKey);
    }

    /**
     * 指定密钥构造方法
     * @param strKey 指定的密钥
     * @throws Exception
     */
    public DES(String strKey) throws Exception {

        //Security.addProvider(new com.sun.crypto.provider.SunJCE());
        //Key key = getKey(strKey.getBytes());


        //encryptCipher = Cipher.getInstance("DES");
        //encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        //decryptCipher = Cipher.getInstance("DES");
        //decryptCipher.init(Cipher.DECRYPT_MODE, key);


        if (strKey.length() == 16)
            strKey += strKey + strKey;
        else if (strKey.length() == 32)
            strKey += strKey.substring(0, 16);

        SecretKey key = new SecretKeySpec(BinConverter.str2Bcd(strKey), "DESede");


        encryptCipher = Cipher.getInstance("DESede/ECB/NoPadding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        decryptCipher = Cipher.getInstance("DESede/ECB/NoPadding");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);


    }

    /**
     * 加密字节数组
     * @param arrB  需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }

    /**
     * 加密字符串
     * @param strIn 需加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public String encrypt(String strIn) throws Exception {
        return byteArr2HexStr(encrypt(strIn.getBytes()));
    }

    public String encryptMQMessage(String mqMessage) throws Exception {

        String hexStr = BinConverter.bytesToHexStr(mqMessage.getBytes());
        //System.out.println("Encrypt BinConverter hexStr:["+hexStr+"]");

        int apdLength = 16- hexStr.getBytes().length%16;
        System.out.println("Original BinConverter hexStr length:"+hexStr.length()+" apdLength:"+apdLength);
        for (int i = 0; i < apdLength && apdLength!=16; i++) {
            hexStr += "0";
        }

        System.out.println("Length:["+hexStr.length()+"] BinConverter hexStr:["+hexStr+"]");

        final byte[] plainTextBytes = BinConverter.str2Bcd(hexStr);
        final byte[] cipherText = encryptCipher.doFinal(plainTextBytes);
        String sendMessage = BinConverter.bytesToHexStr(cipherText);
        System.out.println("Encrypt sendMessage:"+sendMessage);
        return sendMessage;
    }


    public String decryptMQMessage(String strIn) throws Exception {

        String str = new String(decrypt(hexStr2ByteArr(strIn)));

        String hexStr = BinConverter.bytesToHexStr(str.getBytes());
        System.out.println("Decrypt BinConverter hexStr:["+hexStr+"]");
        int subLength = 0;
        while(hexStr.endsWith("00")){
           hexStr = hexStr.substring(0,hexStr.length()-2);
           subLength += 2;
        }
        System.out.println("Decrypt BinConverter hexStr length:"+hexStr.length()+" subLength:"+subLength);

//          if(hexStr.endsWith("0D0A000000000000")){
//              hexStr = hexStr.substring(0,hexStr.length()-6);
//          }
        byte[] byteArr = BinConverter.hexStringToByte(hexStr);
        //System.out.println("decryptMQMessage:["+new String(byteArr)+"]");
        return new String(byteArr);
    }

    /**
     * 解密字节数组
     * @param arrB 需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }

    /**
     * 解密字符串
     * @param strIn 需解密的字符串
     * @return 解密后的字符串
     * @throws Exception
     */
    public String decrypt(String strIn) throws Exception {
        return new String(decrypt(hexStr2ByteArr(strIn)));
    }

    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     * @param arrBTmp  构成该字符串的字节数组
     * @return 生成的密钥
     * @throws java.lang.Exception
     */
    private SecretKey getKey(byte[] arrBTmp) throws Exception {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];
        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        //Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES"); // 生成密钥
        SecretKey key = new SecretKeySpec(arrB, "DESede");

        return key;
    }

    /**
     * 将byte数组转换为表示16进制值的字符串，
     * 如：byte[]{8,18}转换为：0813，
     * 和public static byte[] hexStr2ByteArr(String strIn) 互为可逆的转换过程
     * @param arrB 需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception  本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组，
     *  和public static String byteArr2HexStr(byte[] arrB) 互为可逆的转换过程
     *
     * @param strIn 需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception
     *  本方法不处理任何异常，所有异常全部抛出
     */
    public static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen/2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            //System.out.println(i+ " strTmp:"+strTmp);
            arrOut[i/2] = (byte)Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }


    /**
     * BCD code to string
     * @param bytes
     * @return
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer outputStr = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            int unsignedInt = bytes[i] & 0xFF;
            outputStr.append(Integer.toHexString(unsignedInt));
        }

        return outputStr.toString().toUpperCase();
    }


//    public static void main(String[] args) throws Exception{
//
//        String text = FileUtil.fileReader("D:\\MqFormatData\\mq_message.xml");
//        //String text = FileUtil.fileReader("D:\\MqFormatData\\test115.xml");
//        System.out.println("Length["+text.length()+"] entext:\n["+text+"]");
//
//        DES des = new DES();
//        String encryptResult = des.encryptMQMessage(text);
//        System.out.println("Encrypt result:["+encryptResult+"]");
//
//
//        System.out.println("==============================================");
//
//        String desc = "5BF1B18713ACE7BB3C044E81976B85A447FCE88DECB8401FE7B3A2A3962F8E6E0DBEB29B494DEC99F9B924D9918603689A183B60EAF3034E8D1B38A1B2D9FFC3696BEF6BE872E938F6CFD55FECD53E660785F31E776B4475BF1B59876D317D71E7CE488F34F55A383A71F8AF2C60132A2247FB4115A24D54AC63DADF1CC688ACFD3F819EFB5FDBC78D2F4B8B1FACD739C82246893C3C33A60D7541B1827CD3D2F2700F68A54641400C776D0A8A7825C99A4F93829C6C35CD58811BBED025EEBC9050381EA0120051C36B1BFA1E684035D8544FC357BE4D10CED1DF845C36098BEDB4FED4811F458158811BBED025EEBCC19E4AD5AA07A61E7EA4241E68AAF179B7B5339425816DE1B317D74221469E86B0CD64C5EC938040E998C514245A4AABAE9597F53B656D093351771649B1F30C979085FF7C2D21989AF5E5448398B4103CC2F315F6CBCDD9F964DFE64DF198510D6A5F8C089BE6AF1D29B52BB139316F82D5D3BB9809A9237601FA0A3809F5EEAD0CCFA4C8A4187558D717BC4146C5A55707E73437D8BF77B6B600160FD08B3B97F2D22CA769D237EC804E507EC45CAD119E156649200FA2B6DAD6705CD1DE35F8BD3AF57965AB920A92AF9DF31F17E35AC4675B5F47E050C50610D79E0500043A7DE9D6110890261C9031F1477F67A50CBE7D8F619DD0AC58E7AB2E0C0FFD83DF974BB6A535091ACBC8A09E3F5DAAD27F02764D1C1C696FE0F24A5B46B14FB1533A480FC441FE6BF99578A937D39F99A223C3C81A2DCF75B9DE4E52F88EBAE42D1C6EB7D56D05951780E18C05935764C0C54C407F7C3867FB17F89ADA156FA3CDF85A16584B136937EE673BAB684842888D6BBA556715E4F839FAA0A7BF2967E108BFB315199A381446270445DC2536E125B52D81211F8AA917F775041C9133156F54D51B61086CD635E50E3268EFD8780F35F9443AF42D0CBE7D8F619DD0AC58E7AB2E0C0FFD83DF974BB6A535091A88F01EDAC12AC0D728A3BF5BB4E97EB78F625E6FE3FAEDACFE39AF15E16847593D37D758AD1740A7EED3930DDEC6666AD8CAABE958370EAF9D07AB2861F6466430F7BAE4E0DC55FA1AEBF7A03B641F190BEBEAC477B27EF0AE6C7A09906CAEFB6764CC1B92682A6BE31D189C3A1957A154FDD99EAC171BEE23919FF5F2CC3D187BD7D54B4740355DE8D50A95F1DCE6F8578087533B39B8FECAD8E92AE2D3B81B9FEAF76F3C5AE0F31E5E22ECF84247E90CA8FCD585D73458AC2854F244649CF2F1F01A35D45B85F39D411EF980EBCE24875DE3E90BE993B6D82E69902079DC2961BC0F998550B45732BB05270D56E19D6A0338EB6B8ADEE22A16802EBE080C3CDD0C0D3DB2E180CA8884CE536ED83A9C527A315F2F9058018AD2763F8E0E7A71873F7D582488837558811BBED025EEBC9050381EA0120051C36B1BFA1E684035D8544FC357BE4D10CED1DF845C36098BEDB4FED4811F458158811BBED025EEBC352FDCDEF51C217533266B1BBC26E3C249F05F044EEBB8F035B5B34EA617310D7E9BBACB553A79E2FE39AF15E1684759CF2C04E0B8A3426310EC6DF929EE05193B8897D267FC157D7B6082C4FE600F34976A21A8CB72BDE17CD1A464DB465E5B9E210EEE9B28D92D11ACBFCCD7BC54BC27E6415AAE248015DA5BA9712D4BDB63385C209215C8BABF4FE95A578C4BCF9B0021D1A1747B91C896ED87CCF53CFAA4BB5F1B3AD41BD72E2D7D64C325D9F3947EE44F2DB11F6762681C782BBC5A07B1CECAC989EE2E8CACA0EBBE202F9C407681B272440399D61639018C73F591E2A479807F70AB7AAA399E2E55ED8EADEC848ACA95B9C5FB0B8D4BD46A09831E791A4B084CCDC0632AAF151CB49A0C48582E3EA2423E2EE2C785B9E3EDFD94AF47820BAE9129E554A9C100DBF83D58AB2E78DCE0939EACB748E06B87AFEEB9DF823ED099BCEA871811D9538D141D29B803A370BFC93E59AF9A703D6EA8F1BAABF9B75EE03BA5CE293A6A49C7CEFE56D5DC555CCBFE2F1D95C5D0852689E77192618F83CD81DE7703AB66B270E284B0F506C01D61849BE2D33D7F866C84CF87245AFDF19F05A68E22DF38421BA2F7864BB2D5701211371BDE33D1421BA2F7864BB2D554330B4BF2BBD9CAF8EC60C09D9FAF564D8221CD121ADC15224C77A48769E55E8C41925F06023D708DD4B4C86363298646213FE3FD89726CD981479ADA118EA21CFFB46B99C2D1981BBF11660A54D0B9";
//        String descStr = des.decryptMQMessage(desc);
//        System.out.println("Length["+descStr.length()+"] DES des:\n["+descStr+"]");
//
//        String hexStr = BinConverter.bytesToHexStr(descStr.getBytes());
//        System.out.println("Decrypt BinConverter hexStr:["+hexStr+"]");
//
////        if(hexStr.endsWith("0D0A0000")){
////            descStr = descStr.substring(0,descStr.length()-2);
////        }
////        System.out.println("Length["+descStr.length()+"] Decrypt result:\n["+descStr+"]");
//
//
//        hexStr = "3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D2249534F2D383835392D31223F3E0A3C783A4F726465725265706F6E736520786D6C6E733A783D22687474703A2F2F7777772E73706563747261746563682E636F6D2220786D6C6E733A7873693D22687474703A2F2F7777772E77332E6F7267223E0A20203C66313E313030373C2F66313E0A20203C66323E3C636172643E3632323538383738303031303030323C2F636172643E3C6976725F6C616E67756167653E30303C2F6976725F6C616E67756167653E3C736D735F6C616E67756167653E30303C2F736D735F6C616E67756167653E3C636F756E7472793E3C2F636F756E7472793E3C617265613E3C2F617265613E3C6D6F62696C653E3031333533383131353030313C2F6D6F62696C653E3C74696D65733E303C2F74696D65733E3C696E74657276616C3E303C2F696E74657276616C3E3C7375626D69745F74696D653E3137313933343C2F7375626D69745F74696D653E3C69735F62696E64696E675F6163636F756E743E4E3C2F69735F62696E64696E675F6163636F756E743E3C2F66323E0A20203C66333E5031303037613C2F66333E0A20203C66343E323230303C2F66343E0A20203C66353E3135363C2F66353E0A20203C66373E3C6D6F6E74683E30313C2F6D6F6E74683E3C6461793E31353C2F6461793E3C686F75723E31373C2F686F75723E3C6D696E7574653E31393C2F6D696E7574653E3C7365636F6E643E33343C2F7365636F6E643E3C2F66373E0A20203C6631313E3137313931393C2F6631313E0A20203C6631323E3C686F75723E31373C2F686F75723E3C6D696E7574653E31393C2F6D696E7574653E3C7365636F6E643E31393C2F7365636F6E643E3C2F6631323E0A20203C6631333E3C796561723E323031303C2F796561723E3C6D6F6E74683E30313C2F6D6F6E74683E3C6461793E31353C2F6461793E3C2F6631333E0A20203C6631353E3C6D6F6E74683E20203C2F6D6F6E74683E3C6461793E20203C2F6461793E3C2F6631353E0A20203C6631393E30353C2F6631393E0A20203C6632393E3C2F6632393E0A20203C6633393E543230313C2F6633393E0A20203C6634303EB6A9B5A5B9DCC0EDC6F7A3BAC9CCBBA7B2BBB4E6D4DABBF2C9CCBBA7D7B4CCACCEB4BCA4BBEE3C2F6634303E0A20203C6634313E30323032313632353C2F6634313E0A20203C6634323E3037353531323334353637383930313C2F6634323E0A20203C6634333E3230313030313135313731393039333C2F6634333E0A20203C6634353E3C70726F647563745F6465736372697074696F6E3EB2E2CAD4BDBBD2D73C2F70726F647563745F6465736372697074696F6E3E3C6976725F6C616E67756167653E30303C2F6976725F6C616E67756167653E3C736D735F6C616E67756167653E30303C2F736D735F6C616E67756167653E3C2F6634353E0A20203C6634363E3C796561723E323031303C2F796561723E3C6D6F6E74683E30313C2F6D6F6E74683E3C6461793E31353C2F6461793E3C686F75723E32333C2F686F75723E3C6D696E7574653E35393C2F6D696E7574653E3C7365636F6E643E35393C2F7365636F6E643E3C2F6634363E0A20203C6634373E30303C2F6634373E0A20203C6634383E3C62616E6B5F616464723EB9E3B6ABB9E3D6DD3C2F62616E6B5F616464723E3C637573746F6D65725F6E616D653ED0BBC3F4C8A83C2F637573746F6D65725F6E616D653E3C6964636172645F6E6F3E30313C2F6964636172645F6E6F3E3C62656E65666963696172795F6E616D653ED0BBC3F4C8A8323C2F62656E65666963696172795F6E616D653E3C6D715F73746172745F74696D653E32303130303131353039313932303C2F6D715F73746172745F74696D653E3C6D715F656E645F74696D653E32303130303131353039323232303C2F6D715F656E645F74696D653E3C2F6634383E0A20203C6636303E3C6375705F7465726D696E616C5F7365716E6F3E3C2F6375705F7465726D696E616C5F7365716E6F3E3C6375725F6375705F7365716E6F3E3C2F6375725F6375705F7365716E6F3E3C2F6636303E0A20203C6636343E35333444343941363039384432374239414141304131433037433544313238303C2F6636343E0A3C2F783A4F726465725265706F6E73653E0A00000000";
//        int apdLength = 16- hexStr.getBytes().length%16;
//        System.out.println("Original BinConverter hexStr length:"+hexStr.length()+" apdLength:"+apdLength);
//        for (int i = 0; i < apdLength && apdLength!=16; i++) {
//            hexStr += "0";
//        }
//        byte[] byteArr = BinConverter.hexStringToByte(hexStr);
//        System.out.println("bt:["+new String(byteArr)+"]");
//
//    }

}
