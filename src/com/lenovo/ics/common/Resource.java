package com.lenovo.ics.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Resource {
	
	private static final Logger sLogger = LoggerFactory.getLogger(Resource.class);

	private static int pageSize;
	
	 static {
	        InputStream mStream = null;
	        try {
	            mStream = new FileInputStream(new File("conf/value.properties"));
	            Properties prop = new Properties();
	            prop.load(mStream);
	            
	            pageSize = Integer.valueOf(prop.getProperty("pageSize"));
	        } catch (IOException e) {
	            sLogger.error("读取配置文件失败！", e);
	        } finally {
	            if (mStream != null) {
	                try {
	                    mStream.close();
	                } catch (IOException e) {
	                }
	            }
	        }
	    }
	 
	 public static final int pageSize(){
		 return pageSize;
	 }
}
