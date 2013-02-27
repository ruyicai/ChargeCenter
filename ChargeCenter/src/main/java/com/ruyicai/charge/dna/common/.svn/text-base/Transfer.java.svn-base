package com.ruyicai.charge.dna.common;

public class Transfer {
	
	public static String transfer(String dn,short ori) {
		
		dn = dn.toLowerCase();
		
		String ret = String.valueOf(ori);
		
		if(dn.equals("paymentmode")) {
			ret = (ori==1?"1":"2");//1-即时 2非即时
		} else if(dn.equals("orderstate")) {
			if(ori == 1) ret = "3";
			else if(ori == 2) ret = "5";
			else if(ori == 3) ret = "5";
			else if(ori == 4) ret = "6";
			else if(ori == 5) ret = "7";
			else if(ori == 6) ret = "4";
			else if(ori == 7) ret = "5";
			else if(ori == 8) ret = "8";
			else if(ori == 9) ret = "9";
			else if(ori == 10) ret = "10";
			else if(ori == 11) ret = "11";
			else if(ori == 12) ret = "5";
		} else if(dn.equals("ordertype")) {
			if(ori == 1) ret = "00";
			else ret = "01";
		} else if(dn.equals("currencytype")) {
			if(ori == 1) ret = "156";
			else ret = "384";
		} else if (dn.equals("channel")) {
			if(ori == 4 || ori == 10 || ori == 11) ret = "IVR";
			else ret = "WEB";
		}
		
		return ret;
	}

}
