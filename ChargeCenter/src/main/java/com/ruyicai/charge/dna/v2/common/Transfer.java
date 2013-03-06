package com.ruyicai.charge.dna.v2.common;

/** 接口类型转换类
 * 
 * @author Administrator
 */
public class Transfer {
	
	public static String transfer(String dn,short ori) {
		
		dn = dn.toLowerCase();
		
		String ret = String.valueOf(ori);
		
		if(dn.equals("paymentmode")) {
			ret = (ori==1?"0":"1");//0-即时 1非即时
		} else if(dn.equals("orderstate")) {
			if(ori == 1) ret = "3";
			else if(ori == 2) ret = "5";
			else if(ori == 3) ret = "10";
			else if(ori == 4) ret = "6";
			else if(ori == 5) ret = "7";
			else if(ori == 6) ret = "4";
			else if(ori == 7) ret = "5";
			else if(ori == 8) ret = "8";
			else if(ori == 9) ret = "9";
			else if(ori == 10) ret = "13";
			else if(ori == 11) ret = "10";
			else if(ori == 12) ret = "11";
		} else if(dn.equals("ordertype")) {
			if(ori == 1) ret = "00";
			else ret = "01";
		} else if(dn.equals("cardtype")) {
			if(ori == 1) ret = "01";
			else ret = "0" + ori;
		} else if(dn.equals("currencytype")) {
			if(ori == 1) ret = "156";
			else ret = "384";
		} else if (dn.equals("channel")) {
			if(ori == 4 || ori == 10 || ori == 11) ret = "IVR";
			else ret = "WEB";
		}
		
		return ret;
	}

	public static short reTransfer(String dn,String ori) {
		
		dn = dn.toLowerCase();
		
		short ret = 1;
		
		if(dn.equals("paymentmode")) {
			ret = (short)(ori.equals("1")?0:1);//0-即时 1非即时
		} else if(dn.equals("orderstate")) {

			if(ori.equals("1")) ret = 5;
			else if(ori.equals("2")) ret = 1;
			else if(ori.equals("3")) ret = 1;
			else if(ori.equals("5")) ret = 2;
			else if(ori.equals("10")) ret = 3;
			else if(ori.equals("6")) ret = 4;
			else if(ori.equals("7")) ret = 5;
			else if(ori.equals("4")) ret = 6;
			else if(ori.equals("8")) ret = 8;
			else if(ori.equals("9")) ret = 9;
			else if(ori.equals("13")) ret = 10;
			else if(ori.equals("12")) ret = 11;
			else if(ori.equals("11")) ret = 12;
			
		}
		
		return ret;
	}
}
