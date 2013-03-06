package com.ruyicai.charge.dna.v2.thirdpart;

import java.net.Socket;

import com.ruyicai.charge.dna.v2.common.MessageClient;
import com.ruyicai.charge.dna.v2.common.Strings;
import com.ruyicai.charge.dna.v2.common.ToolKit;
import com.ruyicai.charge.dna.v2.common.Utilities;

/**
 * @author Administrator13782693456
 *
 */
public class SocketClient extends MessageClient implements Runnable {

    private int msgLength = 6;

    public int getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }
    private boolean autoReceive = false;

    public boolean isAutoReceive() {
        return autoReceive;
    }

    public void setAutoReceive(boolean autoReceive) {
        this.autoReceive = autoReceive;
    }

    /**
     * @param ip -服务器地址
     * @param port -端口
     * @throws Exception
     */
    public SocketClient(String ip, int port) throws Exception {
        super(ip, port);
    }

    public SocketClient(String ip, int port, int timeout) throws Exception {
        super(ip, port);
        this.mSocket.setSoTimeout(timeout * 1000);
    }

    public SocketClient(Socket s) throws Exception {
        super(s);
    }

    public void Send(PosMessage msg) throws Exception {
        byte[] bts = TransactionUtil.posMessageToXml(msg).getBytes("UTF-8");
        String lens = Strings.padLeft("" + bts.length, '0', this.getMsgLength());
        this.Send(lens.getBytes());
        this.Send(bts);
    }

    public PosMessage ReceiveSocketMessage() throws Exception {
        byte[] headerBytes = this.Receive(this.getMsgLength());

        int len = Integer.parseInt(Utilities.ChangeByteToString(headerBytes));
        byte[] bodyBytes = this.Receive(len);
        
        String xml = Strings.toString(new String(bodyBytes, "UTF-8"));

        ToolKit.writeLog(this.getClass().getName(), "request", xml);
        //String strMsg = SecurityUtil.caVerify(xml); //CA签名校验
        //ToolKit.writeLog(this.getClass().getName(), "requestCaVerify", strMsg);
        PosMessage msg = TransactionUtil.xmlToPosMessage(xml);
        return msg;
    }

    public void start() {
        this.setAutoReceive(true);
        Thread t = new Thread(this);
        t.start();
    }

    /*
     * 启动线程处理
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        ToolKit.writeLog(this.getClass().getName(), this.toString(), "start receiving......");

        try {
            while (this.isAutoReceive()) {
                PosMessage se = this.ReceiveSocketMessage();
                if (se != null) {
                    OnMessage(se);
                }
            }
        } catch (Exception e) {
            ToolKit.writeLog(this.getClass().getName(),
                    this.toString(), e);
            this.Close();
        }

        ToolKit.writeLog(this.getClass().getName(),  this.toString(), " stop receiving......");
    }

    public void OnMessage(PosMessage se) throws Exception {
        ToolKit.writeLog(this.getClass().getName(), this.toString() + " onMessage ", se.toString());
   
    }
}
