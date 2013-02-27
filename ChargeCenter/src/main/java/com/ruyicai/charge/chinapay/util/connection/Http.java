package com.ruyicai.charge.chinapay.util.connection;

public class Http extends CPHttpConnection
{
	
	/**
	 * 构造方法，获得接收请求的地址，端口号和超时时间
	 * 
	 * @param URL
	 *            http地址，以http://开始
	 * @param Port
	 *            http端口号
	 * @param timeOut
	 *            http超时时间
	 */
	public Http(String httpURL, String timeOut)
	{
		URL = httpURL;
		
		this.timeOut = timeOut;
	}
	
	
	public byte[] getReceiveData()
	{
		return receiveData;
	}
}
