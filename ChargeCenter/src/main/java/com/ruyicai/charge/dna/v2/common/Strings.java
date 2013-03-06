package com.ruyicai.charge.dna.v2.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**　字符窜工具类
 * 
 * @author Administrator
 */
public class Strings {

    public static String format(String str, int beginSize, int endSize, String leftFill, String rightFill) throws Exception {

        return format(str, beginSize, endSize, leftFill, rightFill, false);
    }

    public static boolean isValidIdCard(String idNo) {
        if (idNo == null) {
            return false;
        }
        for (int i = 0; i < idNo.length(); i++) {
            char c = idNo.charAt(i);
            if (c != '1' && c != '2' && c != '3' && c != '4' && c != '5' && c != '6' && c != '7' && c != '8' && c != '9' && c != '0' && c != 'x' && c != 'X') {
                return false;
            }

        }
        return true;
    }

    public static String format(String str, int beginSize, int endSize, String leftFill, String rightFill, boolean cutLeft) throws Exception {

        while (beginSize > 0) {
            str = leftFill + str;
            beginSize--;
        }
        if (str.getBytes("gbk").length > endSize) {

            byte[] temp = str.getBytes();
            byte[] newbyte = new byte[endSize];
            if (cutLeft) {
                for (int i = newbyte.length - 1, j = temp.length - 1; i >= 0; i--, j--) {
                    newbyte[i] = temp[j];
                }
            } else {
                for (int i = 0; i < newbyte.length; i++) {
                    newbyte[i] = temp[i];
                }
            }
            str = new String(newbyte);
        }

        while (str.getBytes("gbk").length < endSize) {
            str += rightFill;
        }
        return str;
    }

    public static String format(String str, int endSize) {
        try {
            return format(str, 0, endSize, " ", " ");
        } catch (Exception e) {
            return "格式化字符串出现异常:" + e;
        }
    }

    public static String getStackTrace(Throwable e) {
        StringBuffer stack = new StringBuffer();
        stack.append(e);
        stack.append("\r\n");

        Throwable rootCause = e.getCause();

        while (rootCause != null) {
            stack.append("Root Cause:\r\n");
            stack.append(rootCause);
            stack.append("\r\n");
            stack.append(rootCause.getMessage());
            stack.append("\r\n");
            stack.append("StackTrace:\r\n");
            stack.append(rootCause);
            stack.append("\r\n");
            rootCause = rootCause.getCause();
        }


        for (int i = 0; i < e.getStackTrace().length; i++) {
            stack.append(e.getStackTrace()[i].toString());
            stack.append("\r\n");
        }
        return stack.toString();
    }

    public static String padLeft(String input, char c, int length) {
        String output = input;
        while (output.length() < length) {
            output = c + output;
        }
        return output;
    }

    public static String padRight(String input, char c, int length) {
        String output = input;
        while (output.length() < length) {
            output = output + c;
        }
        return output;
    }

    public static String padRight(String input, int length) {
        return padRight(input, ' ', length);
    }

    public static String padLeft(String input, int length) {
        return padLeft(input, '0', length);
    }

    public static String bytePadLeft(String input, char c, int length) {
        String output = input;
        while (output.getBytes().length < length) {
            output = c + output;
        }
        return output;
    }

    public static String bytePadRight(String input, char c, int length) {
        String output = input;
        while (output.getBytes().length < length) {
            output = output + c;
        }
        return output;
    }

    public static String formatNumber(double in, int sf) {

        DecimalFormat formater = new DecimalFormat();
        String pattern = "#########";

        if (sf > 0) {
            pattern = pattern + ".#";
        }

        for (int i = 1; i < sf; i++) {
            pattern = pattern + "#";
        }

        formater.applyPattern(pattern);

        String tmp = formater.format(in);

        if (sf > 0) {
            if (tmp.indexOf(".") == -1) {
                tmp = tmp + ".";
            }

            int zeros = (sf + 1) - (tmp.length() - tmp.indexOf("."));

            for (int i = 0; i < zeros; i++) {
                tmp += "0";
            }
        }

        return tmp;
    }

    public static String formatPassword(String pan, int showStartLen, int showEndLen) {

        String temp = "";
        for (int i = 0; i < pan.length(); i++) {
            if (i < showStartLen || i >= pan.length() - showEndLen) {
                temp = temp + pan.charAt(i);
            } else {
                temp = temp + "*";
            }
        }
        return temp;

    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.equals("");
    }

    public static boolean isNullOrEmpty(Object str) {
        return str == null || str.toString().equals("");
    }

    public static String toXmlString(Object o, String append) {
        StringBuilder sb = new StringBuilder(append);
        sb.append("<" + o.getClass().getName() + ">" + append);
        Object[] agrs = new Object[]{};
        for (Method m : o.getClass().getDeclaredMethods()) {
            if (m.getDeclaringClass().getName().equals(o.getClass().getName()) && m.getName().startsWith("get")) {
                sb.append("  <" + m.getName().substring(3) + ">");
                try {
                    Object obj = m.invoke(o, agrs);
                    sb.append(obj == null ? "" : obj);
                } catch (Exception e) {
                    sb.append(toXmlString(e, ""));
                }
                sb.append("</" + m.getName().substring(3) + ">" + append);

            }
        }

        sb.append("</" + o.getClass().getName() + ">" + append);
        return sb.toString();
    }

    public static String toString(Object o) {
        if (o == null) {
            return "";
        }
        return o.toString().trim();
    }

    public static String toXmlString(Object o) {
        if (o == null) {
            return "<object></object>";
        }
        return toXmlString(o, "\n");
    }
    private static int num = 0;

    public synchronized static String getNextNum4() {

        if (num > 9999) {
            num = 0;
        }

        String back = num + "";

        while (back.length() < 4) {
            back = "0" + back;
        }

        num++;

        return back;
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]);
            sb.append(HEXCHAR[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static byte[] toBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bytes;
    }
    
    public static String random(int len) {
        String str = "";
        java.util.Random rander = new java.util.Random(System.currentTimeMillis());
        for(int i=0; i<len; i++) {
            str+= HEXCHAR[rander.nextInt(16)];
        }
        return str;
    }
    
    private static char[] HEXCHAR = {'0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
        /**
     * copy the value of declaredMethod just like: oClone.setXXXX(o.getXXXX());
     * @param o
     * @param oClone
     * @return 
     */
    public static Object clone(Object o, Object oClone) {
        for (Field f : o.getClass().getDeclaredFields()) {
            String fieldName = f.getName();
            String suffixName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            if (!fieldName.equalsIgnoreCase("serialVersionUID")) {

                try {
                    Method m = o.getClass().getDeclaredMethod("get" + suffixName, new Class[]{});

                    if (null != m && !m.getReturnType().getName().startsWith("dna")
                            && (!m.getReturnType().getName().startsWith("java.util") || m.getReturnType().getName().startsWith("java.util.Date"))) {

                        Object obj = m.invoke(o, new Object[]{});
                        if (!isNullOrEmpty(obj)) {
                            m = oClone.getClass().getDeclaredMethod("set" + suffixName, new Class[]{obj.getClass()});
                            m.invoke(oClone, new Object[]{obj});
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return oClone;
    }

}
