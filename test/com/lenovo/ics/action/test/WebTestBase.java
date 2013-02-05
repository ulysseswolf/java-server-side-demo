package com.lenovo.ics.action.test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import jetty.MyServer;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;

import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.constants.ErrorCode;

/**
 * 单元测试类的基类，主要用于启动Jetty服务器，测试用例资源的初始化和释放
 */
public class WebTestBase implements ErrorCode {

	public static final String JSON_ARRAY_KEY = "list";
	
	public static final String sUrlPrefix = "http://127.0.0.1:8080/ics/1.0";
//	public static final String sUrlPrefix = "http://10.100.1.125:8090/ics/1.0";
//	public static final String sUrlPrefix = "http://10.100.1.47:8088/ics/1.0";
//	public static final String sUrlPrefix = "http://10.100.1.213:8090/ics/1.0";
//	public static final String sUrlPrefix = "http://10.100.9.53:8090/ics/1.0";
	
	protected HttpClient httpClient;
	
	static DefaultHttpClient httpClient() {
		HttpParams params = new BasicHttpParams();
//		HttpConnectionParams.setSoTimeout(params, 30 * 60 * 1000);
//		HttpConnectionParams.setConnectionTimeout(params, 30 * 60 * 1000);
		
		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		
		httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		
		return httpClient;
	}
	
	/**
	 * 启动Jetty服务
	 */
	static {
		try {
			MyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 用例执行前的资源初始化，子类在需要的情况下调用
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		httpClient = httpClient();
	}

	/**
	 * 用例执行之后的资源释放，子类在需要的情况下调用
	 * 
	 * @throws Exception
	 */
	@After
	public void tearsDown() throws Exception {
		httpClient.getConnectionManager().shutdown();
	}

    protected static byte[] toGzipByte(JSONObject json) throws IOException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        GZIPOutputStream out = new GZIPOutputStream(buff);
        out.write(json.toString().getBytes("utf-8"));
        out.flush();
        out.close();
        return buff.toByteArray();
    }
	
    protected static JSONObject toJson(HttpEntity entity) throws Exception {
    	String text = EntityUtils.toString(entity, HTTP.UTF_8);
    	return JSONObject.parseObject(text);
    }
    
    protected static String toUrl(String segment) {
    	return sUrlPrefix.concat(segment);
    }
}
