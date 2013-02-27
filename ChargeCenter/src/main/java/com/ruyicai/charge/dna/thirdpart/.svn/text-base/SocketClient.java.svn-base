package com.ruyicai.charge.dna.thirdpart;

import com.ruyicai.charge.dna.common.*;
import com.ruyicai.charge.dna.thirdpart.PosMessage;

import java.net.*;

/**
 * @author Administrator13782693456
 *
 */
public class SocketClient extends MessageClient implements Runnable {

    private boolean AutoReceive;

    public boolean isAutoReceive() {
        return AutoReceive;
    }

    public void setAutoReceive(boolean autoReceive) {
        AutoReceive = autoReceive;
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
        byte[] bts = TransactionUtil.toBytes(msg);
        String lens = Strings.padLeft("" + bts.length, '0', PosMessage.LENGTH_LENGTH);

        this.Send(lens.getBytes());
        this.Send(bts);
        ToolKit.writeLog(this.getClass().getName(), "Send", lens + new String(bts));
    }

    public PosMessage ReceiveSocketMessage() throws Exception {

        byte[] headerBytes = this.Receive(PosMessage.LENGTH_LENGTH);

        int len = Integer.parseInt(Utilities.ChangeByteToString(headerBytes));
        byte[] bodyBytes = this.Receive(len);
        ToolKit.writeLog(this.getClass().getName(), "Receive", new String(headerBytes) + new String(bodyBytes));
        PosMessage pm = new PosMessage(bodyBytes);
        return pm;
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
        ToolKit.writeLog(this.getClass().getName(),
                this.toString(), "start receiving......");

        try {
            while (this.isAutoReceive()) {
                PosMessage se = this.ReceiveSocketMessage();
                if (se != null) {
                    OnMessage(se);
                }
            }
        } catch (Exception e) {
            ToolKit.writeLog(this.getClass().getName(),
                    this.toString(), " run ", e);
            this.Close();
        }

        ToolKit.writeLog(this.getClass().getName(),
                this.toString(), " stop receiving......");
    }

    public void OnMessage(PosMessage se) throws Exception {

        ToolKit.writeLog(this.getClass().getName(), this.toString() + " onMessage ", se.toString());

    }
}
