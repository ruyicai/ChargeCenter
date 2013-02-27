package com.ruyicai.charge.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class HttpRequest {
	/**
	 * POST请求
	 * @param url
	 * @param parameter
	 * @return
	 * @throws IOException 
	 */
	public static String doPostRequest(String url,String parameter) throws IOException{
		
		URL reqUrl = new URL(url);
		HttpURLConnection reqConn = (HttpURLConnection) reqUrl.openConnection();
		reqConn.setDoOutput(true);
		reqConn.setDoInput(true);
		reqConn.setConnectTimeout(300 * 1000);
		reqConn.setReadTimeout(300 * 1000);
		reqConn.setRequestMethod("POST");
		PrintWriter out = new PrintWriter(reqConn.getOutputStream());
		out.print(parameter);
		out.flush();
		out.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(reqConn.getInputStream(), "UTF-8"));
		String retStr = in.readLine();
		return retStr;
	}
	
	/**
	 * POST请求
	 * @param url
	 * @param parameter
	 * @return
	 * @throws IOException 
	 */
	public static String doPostRequest(String url,String parameter, String field) throws IOException{
		
		URL reqUrl = new URL(url);
		HttpURLConnection reqConn = (HttpURLConnection) reqUrl.openConnection();
		reqConn.setDoOutput(true);
		reqConn.setDoInput(true);
		reqConn.setConnectTimeout(300 * 1000);
		reqConn.setReadTimeout(300 * 1000);
		reqConn.setRequestMethod("POST");
		PrintWriter out = new PrintWriter(reqConn.getOutputStream());
		out.print(parameter);
		out.flush();
		out.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(reqConn.getInputStream(), "UTF-8"));
		String retStr = String.valueOf(reqConn.getHeaderFieldInt(field, 0));//in.readLine();
		return retStr;
	}
	
	/**
	 * POST请求
	 * @param url
	 * @param parameter
	 * @return
	 * @throws IOException 
	 */
	public static String doPostRequestGBK(String url,String parameter) throws IOException{
		
		URL reqUrl = new URL(url);
		HttpURLConnection reqConn = (HttpURLConnection) reqUrl.openConnection();
		reqConn.setDoOutput(true);
		reqConn.setDoInput(true);
		reqConn.setConnectTimeout(300 * 1000);
		reqConn.setReadTimeout(300 * 1000);
		reqConn.setRequestMethod("POST");
		PrintWriter out = new PrintWriter(reqConn.getOutputStream());
		out.print(parameter);
		out.flush();
		out.close();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(reqConn.getInputStream(), "GBK"));
		String retStr = in.readLine();
		return retStr;
	}
	
	/**
	 * GET请求
	 * @param urlAndParam
	 * @return
	 * @throws IOException 
	 */
	public static String doGetRequest(String urlAndParam) throws IOException{
		URL reqUrl = new URL(urlAndParam);
		HttpURLConnection reqConn = (HttpURLConnection) reqUrl.openConnection();
		reqConn.setDoOutput(true);
		reqConn.setDoInput(true);
		reqConn.setConnectTimeout(300 * 1000);
		reqConn.setReadTimeout(300 * 1000);
		reqConn.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(reqConn.getInputStream(), "UTF-8"));
		String retStr = in.readLine();
		return retStr;
	}
	
	/**
	 * GET请求
	 * @param urlAndParam
	 * @return
	 * @throws IOException 
	 */
	public static String doGetRequestGBK(String urlAndParam) throws IOException{
		URL reqUrl = new URL(urlAndParam);
		HttpURLConnection reqConn = (HttpURLConnection) reqUrl.openConnection();
		reqConn.setDoOutput(true);
		reqConn.setDoInput(true);
		reqConn.setConnectTimeout(300 * 1000);
		reqConn.setReadTimeout(300 * 1000);
		reqConn.connect();
		BufferedReader in = new BufferedReader(new InputStreamReader(reqConn.getInputStream(), "GBK"));
		String retStr = in.readLine();
		return retStr;
	}
	
//	public static void main(String[] args) throws IOException {
//		System.out.println(doPostRequest("http://219.148.162.70:9080/lottery/sms/send","mobileIds={\"13333\"}&text=哈哈"));
//	}
	public static void main(String[] args) {
		String a = "北京";
		String b = null;
		String c = null;
		try {
			b = URLDecoder.decode(a, "UTF-8");
			c = new String(a.getBytes("utf-8"),"GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(b);
		System.out.println(c);
	}
}
