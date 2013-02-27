package com.ruyicai.charge.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * 热部署，读取配置文件，不用重启服务器
 * 
 * */
public class ConfigUtil {
    private static Properties props = null;    
    private static File configFile = null; 
    private static long fileLastModified = 0L; 
    
    private static String configFileName ="charge.properties";
    
    public ConfigUtil(String fileurl){
    	configFileName=fileurl;
    }
    public ConfigUtil(){}
    
    private static void init() { 
        URL url = ConfigUtil.class.getClassLoader().getResource(configFileName); 
        configFile = new File(url.getFile()); 
        fileLastModified = configFile.lastModified();      
        props = new Properties(); 
        load(); 
    } 
    
    private static void load() { 
        try { 
            props.load(new FileInputStream(configFile)); 
            fileLastModified = configFile.lastModified(); 
        } catch (IOException e) {            
            throw new RuntimeException(e); 
        } 
    } 

    public static String getConfig(String key) { 
        if ((configFile == null) || (props == null)) init(); 
        if (configFile.lastModified() > fileLastModified) load(); 
        return props.getProperty(key); 
    } 
    
    public static String getConfig(String configFileName,String key){
        URL url = ConfigUtil.class.getClassLoader().getResource(configFileName); 
        File config = new File(url.getFile()); 
        Properties properties = new Properties();
        try{
            properties.load(new InputStreamReader(new FileInputStream(config), "UTF-8")); 
        }catch (IOException e) {            
            throw new RuntimeException(e); 
        } 
        return properties.getProperty(key); 
    }

    public static void main(String[] args) {
//    	String str = ConfigUtil.getConfig("jrtLot.properties", "ms_switch");
//    	System.out.println(str);
	}
}