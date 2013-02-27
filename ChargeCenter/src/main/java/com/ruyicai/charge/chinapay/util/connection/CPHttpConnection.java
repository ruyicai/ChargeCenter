package com.ruyicai.charge.chinapay.util.connection;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class CPHttpConnection {

    /**
     * 信息编码
     */
    private String msgEncoding;

    /**
     * 对方服务器返回接收成功的响应码
     */
    static final int RESPCODE_SUCCESS = 200;
    /**
     * 接收响应的最大值
     */
    protected static final int MAXLEN = 4096;

    /**
     * 不限制数据流大小
     */
    protected static final int NOLIMITLEN = 0;

    /**
     * 限制数据流大小为4k
     */
    protected static final int LIMITLEN = 1;

    /**
     * 接收响应的长度设置类型<br>
     * 不限制长度大小,根据http返回内容长度获得:0<br>
     * 限制长度大小为MAXLEN:1<br>
     */
    protected int lenType = LIMITLEN;

    /**
     * 接收报文的地址
     */
    protected String URL;

    /**
     * 连接和接收响应的超时时间
     */
    protected String timeOut;

    /**
     * 发送的报文内容
     */
    String sendData;

    /**
     * 接收的报文内容
     */
    protected byte[] receiveData;

    /**
     * 输入流
     */
    protected BufferedInputStream iBufferedInputStream = null;

    /**
     * 发送报文到接收地址，并且接收返回信息
     *
     * @param msgStr 需要发送的数据
     * @return
     */
    public int sendMsg(String msgStr) {
        HttpURLConnection urlCon;
        OutputStream tOut = null;
        int result = -1;

        // 设置连接和超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "" + timeOut);
        System.setProperty("sun.net.client.defaultReadTimeout", "" + timeOut);

        // 建立连接，发送数据
        try {
            // 创建URL对象
            URL tUrl = new URL(URL);
            urlCon = (HttpURLConnection) tUrl.openConnection();

        }
        catch (MalformedURLException mue) {
            System.out.println("连接失败,errCode=[-12];errMsg=[无法连接对方主机]");
            //TraceLog.logStackTrace(this, (Throwable) mue);
            return -12;
        }
        catch (IOException ioe) {
            //TraceLog.logStackTrace(this, (Throwable) ioe);
            System.out.println("连接失败,errCode=[-14];errMsg=[通讯建链失败]");
            return -14;
        }

        try {
        	System.out.println("sendData=[" + msgStr + "]");

            urlCon.setRequestMethod("POST");
            urlCon.setDoOutput(true);
            urlCon.setDoInput(true);
            tOut = urlCon.getOutputStream();

            // 判断字符编码是否为空，不为空，则设置编码
            if (getMsgEncoding() == null || getMsgEncoding().equals(""))
                tOut.write(msgStr.getBytes());
            else
                tOut.write(msgStr.getBytes(getMsgEncoding()));
            tOut.flush();

        }
        catch (IOException ioe) {
            //TraceLog.logStackTrace(this, (Throwable) ioe);
            System.out.println("Http连接失败,errCode=[-52];errMsg=[数据处理超时]");
            return -52;
        }
        catch (Exception e) {
        	System.out.println("数据处理失败");
            //TraceLog.logStackTrace(this, (Throwable) e);
            return -1;
        }
        finally {
            try {
                if (tOut != null)
                    tOut.close();
            }
            catch (IOException e) {
            	System.out.println("输出流关闭失败");
                //TraceLog.logStackTrace(this, (Throwable) e);
                return -1;
            }
        }

        // 以数据流的方式接收响应数据
        InputStream is = null;
        try {
            int respCode = urlCon.getResponseCode();
            System.out.println("http ResponseCode=[" + respCode + "]");
            if (RESPCODE_SUCCESS == respCode) {
                                int contentLen = urlCon.getContentLength();
                is = urlCon.getInputStream();

                /**
                 * 获取 ContentLength失败，则采用ByteArrayOutputStream获取流内容
                 */
                System.out.println("ContentLength=[" + contentLen + "]");
                if (contentLen == -1) {
                	System.out.println("获取ContentLength失败，通过ByteArrayOutputStream方式获取数据");
                    receiveData = getBytes(is, lenType == NOLIMITLEN ? 0 : MAXLEN);
                } else {
                    /**
                     * 获取 ContentLength 成功
                     */
                    int len = lenType == NOLIMITLEN ? contentLen : Math.min(contentLen, MAXLEN);
                    System.out.println("ReceiveData Length=[" + len + "]");
                    receiveData = new byte[len];
                    int ch;
                    int i=0;
                    while((ch = is.read())!=-1){
                        receiveData[i++] = (byte) ch;
                        if(i==len) break;
                    }
//                    is.read(receiveData, 0, len);
                }
                result = 1;
            } else {
            	System.out.println(new StringBuffer("对方返回错误代码=[").append(
                        respCode).append("];respMsg=[").append(
                        urlCon.getResponseMessage()).append("]").toString());
            }

        }
        catch (IOException ioex) {
            //TraceLog.logStackTrace(this, (Throwable) ioex);
        	System.out.println("连接失败,errCode=[-52];errMsg=[数据接收错误]");
            return -54;
        }
        catch (Exception ex) {
        	System.out.println("系统处理异常");
            //TraceLog.logStackTrace(this, (Throwable) ex);
            return -1;
        }
        finally {
            try {
                if (is != null)
                    is.close();
            }
            catch (IOException e) {
            	System.out.println("输入流关闭失败");
                //TraceLog.logStackTrace(this, (Throwable) e);
                return -1;
            }

        }

        return result;

    }

    /**
     * 获得对方返回的报文内容
     *
     * @return
     */
    public abstract byte[] getReceiveData();

    public String getMsgEncoding() {
        return msgEncoding;
    }

    public void setMsgEncoding(String msgEncoding) {
        this.msgEncoding = msgEncoding;
    }

    public int getLenType() {
        return lenType;
    }

    public void setLenType(int lenType) {
        this.lenType = lenType;
    }

    /**
     * 获取流内容
     *
     * @param is        输入流
     * @param limitSize 获取流最大长度，不限制设置为0
     * @return 流内容byte数组
     * @throws IOException 处理失败抛出异常
     */
    static public byte[] getBytes(InputStream is, int limitSize) throws IOException {
        if (is == null) {
            throw new IOException("InputStream is Closed!");
        }
        ByteArrayOutputStream os = null;
        try {
            int ch;
            os = new ByteArrayOutputStream();
            int count = 0;
            while ((ch = is.read()) != -1) {
                if (limitSize != 0 && count >= limitSize) break;
                os.write(ch);
                count++;
            }
            os.flush();
            System.out.println("getBytes:Length=[" + count + "]");
            return os.toByteArray();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            if (os != null) os.close();
        }
    }

}
