package com.ruyicai.charge.dna.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import com.ruyicai.charge.dna.ca.*;

/** 系统工具类
 * 日志,培植,监控
 * @author lakey
 * 
 */
public class ToolKit
{
	private static final Logger logger = Logger.getLogger(ToolKit.class);
	
    public static void main(String[] args)
    {
        try
        {
            String str = ToolKit.sign("test test");
            ToolKit.unSign(str);
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
    	logger.info("系统工具类(日志,培植,监控)ToolKit->writeLog->开始");
        writeLog(InfoLevel.INFO, processName, threadName, eventMessage);
        logger.info("系统工具类(日志,培植,监控)ToolKit->writeLog->结束");
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
    private static CFCA cfca = null;

    public static CFCA getCFCA()
    {
        if (cfca == null)
        {
            cfca = new BeiJingCA();
            cfca.setCertPath(ToolKit.getPropertyFromFile("CFCA_CERTPATH"));
            cfca.setCertPass(ToolKit.getPropertyFromFile("CFCA_CERTPASS"));
        }
        return cfca;
    }

    public static String sign(String value)
    {
        try
        {
            return getCFCA().sign(value);
        }
        catch (Exception e)
        {
            ToolKit.writeLog(ToolKit.class.getName(), "sign", e);
        }
        return null;
    }

    public static String unSign(String value) throws Exception
    {
        try
        {
            return getCFCA().unSign(value);
        }
        catch (Exception e)
        {
            ToolKit.writeLog(ToolKit.class.getName(), "unSign", e);
        }
        return null;
    }
}
