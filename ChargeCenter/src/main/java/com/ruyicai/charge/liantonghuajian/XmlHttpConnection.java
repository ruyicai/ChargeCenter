package com.ruyicai.charge.liantonghuajian;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

public class XmlHttpConnection implements Serializable {
	private static final long serialVersionUID = -5200002229125738107L;
	private static final int RESPCODE_SUCCESS = 200;
	private Logger logger = Logger.getLogger(XmlHttpConnection.class);	
	private String url;
	private String recvMsg;
	private HttpURLConnection urlCon;
	private InputStream in;

	public XmlHttpConnection(String url, int timeOut) {
		this(url, timeOut + "");
	}

	public XmlHttpConnection(String url, String timeOut) {
		this.url = url;
		System.setProperty("sun.net.client.defaultConnectTimeout", timeOut);
		System.setProperty("sun.net.client.defaultReadTimeout", timeOut);
	}

	private boolean open() {
		try {
			urlCon = (HttpURLConnection) new URL(url).openConnection();
			return true;
		} catch (Exception e) {
			logger.error("open error:", e);
		}
		return false;
	}

	public boolean sendMsg(String msgStr) {
		if (!open()) {
			return false;
		}

		OutputStream os = null;
		InputStream is = null;
		try {
			try {
				urlCon.setRequestMethod("POST");
				urlCon.setRequestProperty("content-type", "text/plain");
				urlCon.setDoOutput(true);
				urlCon.setDoInput(true);
				os = urlCon.getOutputStream();
				// os.write(msgStr.getBytes("utf-8"));
				// os.flush();
				OutputStreamWriter writer = new OutputStreamWriter(os);
				writer.write(URLEncoder.encode(msgStr, "utf-8"));
				writer.flush();
			} catch (Exception e) {
				logger.error("sendMsg error:", e);
				return false;
			}
			
			try {
				int respCode = urlCon.getResponseCode();
				if (RESPCODE_SUCCESS != respCode) {
					logger.info("respCode=" + respCode);
				}
				is = urlCon.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
				StringBuilder responseBuilder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					responseBuilder.append(line);
				}
				recvMsg = URLDecoder.decode(responseBuilder.toString(), "utf-8");
				in = StringToInputStream(recvMsg);
			} catch (Exception e) {
				logger.error("sendMsg error:", e);
				return false;
			}
			
		} finally {
			close(is);
			close(os);
			urlCon.disconnect();
			urlCon = null;
		}
		return true;
	}

	private void close(InputStream stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (Exception e) {
			logger.error("close error:", e);
		}
		stream = null;
	}

	private void close(OutputStream stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (Exception e) {
			logger.error("close error:", e);
		}
		stream = null;
	}

	public InputStream getRecvMsg() {
		return in;
	}

	public String getReMeg() {
		return recvMsg;
	}

	InputStream StringToInputStream(String recvMsg) {
		ByteArrayInputStream stream = new ByteArrayInputStream(recvMsg.getBytes());
		return stream;
	}
}
