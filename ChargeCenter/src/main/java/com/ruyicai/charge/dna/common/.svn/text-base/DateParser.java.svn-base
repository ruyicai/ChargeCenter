package com.ruyicai.charge.dna.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

 /**
 *
 * @author: XieminQuan
 * @time  : 2008-1-3 下午02:56:50
 *
 * DNAPAY
 */

public class DateParser {
	public static Date yyyyMMdd(String date) throws ParseException {
		return new SimpleDateFormat("yyyyMMdd").parse(date);
	}
	
	public static Date yyyy_MM_dd_HH_mm_ss(String date) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
	}
        
        public static Date yyyyMMddHHmmss(String date) throws ParseException {
		return new SimpleDateFormat("yyyyMMddHHmmss").parse(date);
	}
        
        public static Date MMddHHmmss(String date) throws ParseException {
		return yyyyMMddHHmmss(Calendar.getInstance().get(Calendar.YEAR)+date);
	}
        
        
    	public static Date yyyy_MM_dd(String date) throws ParseException {
    		return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    	}
    	
    	public static Date hh_mm_ss(String date) throws ParseException {
    		return new SimpleDateFormat("hh:mm:ss").parse(date);
    	}
}
