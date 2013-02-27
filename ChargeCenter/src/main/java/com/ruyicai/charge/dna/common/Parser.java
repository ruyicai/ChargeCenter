package com.ruyicai.charge.dna.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Parser {

    private static SimpleDateFormat yyyy_MM_dd_HH_mm_ss = null;
    
    public static Date yyyy_MM_dd_HH_mm_ss(String time) throws Exception {
    	
        if(yyyy_MM_dd_HH_mm_ss == null) {
        	yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return yyyy_MM_dd_HH_mm_ss.parse(time);
    }

}
