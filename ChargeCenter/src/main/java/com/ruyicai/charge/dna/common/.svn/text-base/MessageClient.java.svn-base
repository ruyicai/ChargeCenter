package com.ruyicai.charge.dna.common;

import java.net.*;

/**
 * @author Administrator
 * 
 */
public class MessageClient {

	/**
	 * @param ip
	 *            -地址
	 * @param port
	 *            -端口
	 * @throws Exception
	 */
	public MessageClient(String ip, int port) throws Exception {
		ToolKit.writeLog(this.getClass().getName(),
				"Constructing",  ip + ":" + port);
		this.mSocket = new Socket(ip, port);
	}

	public MessageClient(Socket s) {
		this.mSocket = s;
	}
	
	public Socket getMSocket() {
		return mSocket;
	}

	public void setMSocket(Socket socket) {
		mSocket = socket;
	}

	public MessageClient(){}

	protected Socket mSocket;

	public boolean isConnected()
	{
		return this.mSocket != null && this.mSocket.isConnected() && !this.mSocket.isClosed();
	}
	
	/**
	 * @param length
	 *            -长度
	 * @return 数据流
	 * @throws Exception
	 */
	public byte[] Receive(int length) throws Exception {
		byte[] bts = new byte[length];
		int btsLength = mSocket.getInputStream().read(bts);
		ToolKit.writeLog(InfoLevel.DEBUG, this.getClass().getName(),
				this.toString() + " Received ", "[" + new String(bts, "GB2312") + "]");
		if (btsLength != length)
			throw new RuntimeException("Receive Length " + btsLength + "!="
					+ length + "]");
		else
			return bts;
	}

	/**
	 * @param msg
	 *            -数据
	 * @throws Exception
	 */
	public void Send(byte[] msg) throws Exception {

		ToolKit.writeLog(InfoLevel.DEBUG, this.getClass().getName(),
				this.toString() + " Send ", "[" + new String(msg, "GB2312") + "]");
		this.mSocket.getOutputStream().write(msg); 
		this.mSocket.getOutputStream().flush();
	}

	/**
	 * @throws Exception
	 */
	public void Close() {
		try {
			ToolKit.writeLog(this.getClass().getName(),
					this.toString(), " Closing");
			
			this.mSocket.close();
		}
		catch(Exception e){
			ToolKit.writeLog(this.getClass().getName(),
					this.toString(), e);
		}
	}
	

	public String toString(){
		return this.mSocket.getRemoteSocketAddress().toString();
	}
}
