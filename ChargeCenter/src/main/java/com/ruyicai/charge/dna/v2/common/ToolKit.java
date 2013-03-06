package com.ruyicai.charge.dna.v2.common;

import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.ruyicai.charge.dna.v2.ca.CAProvider;

/** 系统工具类，包括日志,培植,监控等。
 * @author lakey
 * 
 */
public class ToolKit
{

    public static void main(String[] args)
    {
        try
        {
            ToolKit.writeLog(ToolKit.class.getName(), "main", "test");
//            String str = ToolKit.sign("test test");
//            ToolKit.unSign(str);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static SimpleDateFormat DateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    /**
     * @param logLevel      日志等级，例如：InfoLevel.info
     * @param processName   日志名称，一般可这样获得： this.getClass().getPackage().getName()   或者 this.getClass().getName()。另参见 log4j.properties
     * @param threadName    线程名称， 这样获取：Thread.currentThread().getName()
     * @param eventMessage  与流程相关的消息
     */
    public synchronized static void writeLog(InfoLevel logLevel, String processName,
        String threadName, String eventMessage)
    {
        System.out.println(processName + " " + threadName + " " + eventMessage);

        Logger logger = Logger.getLogger(processName);
        if (processName != null)
            processName = processName.replace(" ", ",");
        if (threadName != null)
            threadName = threadName.replace(" ", ",");

        eventMessage = threadName + " " + eventMessage;

        switch (logLevel)
        {
            case DEBUG:
                logger.debug(eventMessage);
                break;
            case INFO:
                logger.info(eventMessage);
                break;
            case WARN:
                logger.warn(eventMessage);
                break;
            case EXCEPTION:
                logger.error(eventMessage);
                break;
            case ERROR:
                logger.fatal(eventMessage);
                break;
            default:
                logger.info(eventMessage);
        }
    }

    public synchronized static void writeLog(String processName,
        String threadName, String message, Exception eventMessage)
    {
        String exStr = "";
        if (eventMessage != null)
        {
            exStr = "" + eventMessage.getMessage();
            for (StackTraceElement ste : eventMessage.getStackTrace())
                exStr = exStr + " " + ste.toString();
        }
        writeLog(InfoLevel.EXCEPTION, processName, threadName, exStr);
    }

    public synchronized static void writeLog(String processName, String message,
        Exception eventMessage)
    {
        String exStr = "";
        if (eventMessage != null)
        {
            exStr = "" + eventMessage.getMessage();
            for (StackTraceElement ste : eventMessage.getStackTrace())
                exStr = exStr + " " + ste.toString();
        }
        writeLog(InfoLevel.EXCEPTION, processName, Thread.currentThread().getName(), exStr);
    }

    public synchronized static void writeLog(InfoLevel logLevel, String processName, String eventMessage)
    {
        writeLog(logLevel, processName, Thread.currentThread().getName(), eventMessage);
    }

    public synchronized static void writeLog(String processName, String eventMessage)
    {
        writeLog(InfoLevel.INFO, processName, Thread.currentThread().getName(), eventMessage);
    }

    public synchronized static void writeLog(String processName, String threadName, String eventMessage)
    {
        writeLog(InfoLevel.INFO, processName, threadName, eventMessage);
    }

    public static Integer toInt(InfoLevel level)
    {
        if (level == InfoLevel.DEBUG)
            return 0;
        else if (level == InfoLevel.INFO)
            return 1;
        else if (level == InfoLevel.WARN)
            return 2;
        else if (level == InfoLevel.EXCEPTION)
            return 3;
        else
            return 4;


    }

    public synchronized static String getPropertyFromFile(String filename, String key)
    {
        ResourceBundle rb = ResourceBundle.getBundle(filename);
        return rb.getString(key).trim();
    }

    public synchronized static String getPropertyFromFile(String key)
    {
        ResourceBundle rb = ResourceBundle.getBundle("systemsetting");
        return rb.getString(key).trim();
    }

    public static String sign(String cert, String value)
    {
        try
        {
            return CAProvider.sign(cert, value);
        }
        catch (Exception e)
        {
            ToolKit.writeLog(ToolKit.class.getName(), "sign", e);
        }
        return null;
    }

    public static String verify(String value, boolean checkSign) throws Exception
    {
        try
        {
            return CAProvider.verify(value, checkSign);
        }
        catch (Exception e)
        {
            ToolKit.writeLog(ToolKit.class.getName(), "unSign", e);
        }
        return null;
    }
}
