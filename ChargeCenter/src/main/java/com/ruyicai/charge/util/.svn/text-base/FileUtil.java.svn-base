package com.ruyicai.charge.util;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class);
	
	public static File create(String filepath) throws IOException {
		
		File f = new File(filepath);
		File ff = f.getParentFile();
		if (!ff.exists()) {
			ff.mkdirs();
		}
		f.createNewFile();
		
		return f;
	}	
}
