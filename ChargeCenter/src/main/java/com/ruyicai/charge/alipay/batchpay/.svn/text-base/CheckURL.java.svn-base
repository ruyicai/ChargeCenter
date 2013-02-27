package com.ruyicai.charge.alipay.batchpay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class CheckURL {
	private static Logger logger = Logger.getLogger(CheckURL.class);
	   /**
     * 对字符串进行MD5加密
	 * @param myUrl 
     *
     * @param url
     *
     * @return 获取url内容
     */
  public static String check(String urlvalue ) {	 
		String inputLine = "";

		try {
			URL url = new URL(urlvalue);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			inputLine = in.readLine().toString();
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = in.readLine()) != null) {
//				sb.append(line);
//			}
//			inputLine = sb.toString();
			logger.info("urlConnection.getResponseCode()=" + urlConnection.getResponseCode());
			logger.info("urlConnection.getResponseMessage()=" + urlConnection.getResponseMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("CheckURL check error:", e);
		}		

		logger.info("inputLine=" + inputLine);
		return inputLine;
  }
  
	public static void main(String[] args) {
		String url = "https://www.alipay.com/cooperate/gateway.do?service=notify_verify&partner=2088801133080310&notify_id=a8d4bbf1cf06540721badba11089f7af02";
		String ret = check(url);
		System.out.println("ret.length()=" + ret.length());
		System.out.println("ret=" + ret);
	}
}
