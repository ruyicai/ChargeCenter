package com.ruyicai.charge.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ruyicai.charge.exception.EncException;

public class StringUtil {
	private static Logger logger = Logger.getLogger(StringUtil.class);
	
			
	/**
	 * 格式化字符串，不足length长度，前面补0
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String format(String str, int length) {
		if (null != str && str.length() > 0) {
			int strLength = str.length();
			if (strLength < length) {
				for (int i = 1; i <= length - strLength; i++) {
					str = "0" + str;
				}
			}
		}
		return str;
	}
	
	public static String getSystemTime()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");//设置日期格式
		String date = sdf.format(new Date());// 获取系统当前时间
		return date;
	}
	
	public static Date getDate(String str) {
		Date date = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = dateFormat.parse(str);			
		} catch (ParseException e) {
			logger.info("日期格式转换错误！str=" + str);
			e.printStackTrace();
		}		
		return date;
	}
	
	public static String desPassword(String key, String cardNo)
	{
		PayEncrypt pe = new PayEncrypt();
		String pass = "";
		try {
			pass = pe.encryptMode1(key, cardNo);// 密钥和被加密的字符串
		} catch (EncException e) {
			logger.info("密码加密发生异常");
			e.printStackTrace();
		}
		return pass; 		
	}
	
	public static String getNowString() {
		String date = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");		
		date = dateFormat.format(new Date());			
		return date;
	}
	
	public static String formatString(String str) {
		if(str == null) {
			return ""; 
		}
		return str;
	}
	
	public static boolean isEmpty(String str) {
		if (StringUtils.isEmpty(str))
			return true;
		if ("".equals(str.trim()))
			return true;
		return false;
	}

	public static boolean isEmpty(Character c) {
		if (null == c)
			return true;
		if ("".equals(c))
			return true;
		return false;
	}

	public static boolean isInt(String str) {
		return str.matches("^[0-9]*$");
	}

	// 嗖付支付传过来的金额是"分"
	public static boolean isFen(String str) {
		return str.matches("^[0-9]+$");
	}
	
	public static String join(String split, String... values) {
		StringBuilder builder = new StringBuilder();
		for(String s : values) {
			builder.append(s).append(split);
		}
		if(!isEmpty(split)) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
	
	public static String fillZero(int num, int width) {
		if (num < 0)
			return "";
		StringBuffer sb = new StringBuffer();
		String s = "" + num;
		if (s.length() < width) {
			int addNum = width - s.length();
			for (int i = 0; i < addNum; i++) {
				sb.append("0");
			}
			sb.append(s);
		} else {
			return s.substring(s.length() - width, s.length());
		}
		return sb.toString();
	}
	
	public static String fillZero(String preix, int num, int width) {
		if (num < 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(preix);
		String s = "" + num;
		if (s.length() < width) {
			int addNum = width - s.length();
			for (int i = 0; i < addNum; i++) {
				sb.append("0");
			}
			sb.append(s);
		} else {
			return s.substring(s.length() - width, s.length());
		}
		return sb.toString();
	}	
}
