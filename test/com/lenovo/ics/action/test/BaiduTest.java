package com.lenovo.ics.action.test;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.Util;

public class BaiduTest extends WebTestBase {

	 @Test
	 public void test() throws Exception {
		 HttpPost request = new HttpPost(toUrl("/outer/thirdparty/baike/lemma"));		
		 JSONObject json = new JSONObject();
		 String key = "Tim Burton ";
		 json.put("key", key);
		 request.setEntity(new ByteArrayEntity(toGzipByte(json)));
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		 httpClient.addResponseInterceptor(new ResponseContentEncoding());
		 
		 String timeStamp = String.valueOf(System.currentTimeMillis());
		 Integer index = Integer.parseInt(String.valueOf(timeStamp.charAt(timeStamp.length()-1))) + 1;
		 String cutOut = timeStamp.substring((timeStamp.length() - index) > 0 ? (timeStamp.length() - index) : 0, timeStamp.length());
		 String tokenS = Util.md5s(timeStamp + cutOut);
		 
		 request.addHeader("time", timeStamp);
		 request.addHeader("token", tokenS);
		 
		 HttpResponse response = httpClient().execute(request);
//		 Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		 System.out.println(response.getStatusLine().getStatusCode());
		 
		 JSONObject json1 = toJson(response.getEntity());
		 System.out.println(json1);
		 System.out.println(json1.getString("contentId"));
		 System.out.println(json1.getString("abstract"));
		 
	 }
	 
} 