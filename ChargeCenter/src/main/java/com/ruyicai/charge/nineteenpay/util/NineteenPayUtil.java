package com.ruyicai.charge.nineteenpay.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
/**
 * 19Pay-支付-工具类
 * @author shenpenglan
 * 现财付通wap充值支付也使用此工具类，修改时请注意
 */
public class NineteenPayUtil {
	/**
	 * 提交19pay支付请求
	 * @param geturl 支付地址
	 * @param paramContent 支付请求参数
	 * @return
	 * @throws Exception
	 */
	public static String http(final String geturl, final String paramContent)throws Exception {
		StringBuffer responseMessage = new StringBuffer();
		URL reqUrl = new URL(geturl);
		final URLConnection connection = reqUrl.openConnection();
		connection.setDoOutput(true);
		connection.setConnectTimeout(300 * 1000);
		connection.setReadTimeout(300 * 1000);
		OutputStreamWriter reqOut = null;
		if (paramContent != null) {
			reqOut = new OutputStreamWriter(connection.getOutputStream());
			reqOut.write(paramContent);
			reqOut.flush();
		}
		int charCount = -1;
		InputStream in = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "GBK"));
		while ((charCount = br.read()) != -1) {
			responseMessage.append((char) charCount);
		}
		in.close();
		if (reqOut != null)
			reqOut.close();
		return responseMessage.toString();
	}
}
