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


public class FootballOtherMatchTest extends WebTestBase {

	 final String ID="521948d0-7ba2-3a20-9269-a9f83fefffbb"; 
//	 final String ID="c07e5003-d7bb-3e4d-8e4d-7c00ab7c6e8b"; 
	 /**
	  * @title 篮球其他比赛信息查询测试用例 
	  * @step <strong>测试步骤</strong>
	  * <ol type="decimal">
	  * <li>参数初始化 </li>
	  * <li>向服务器发送请求</li>
	  * <li>断言返回的HTTP状态码为200</li>
	  * <li>断言返回数据不为空</li>
	  * </ol>
	  * @throws Exception
	  */
	 @Test
	 public void testOtherMatch() throws Exception {
		 HttpPost request = new HttpPost(toUrl("/outer/sport/basketball/other"));		
		 JSONObject json = new JSONObject();
		 json.put("contentId", ID);
		 
		 String timeStamp = String.valueOf(System.currentTimeMillis());
		 Integer index = Integer.parseInt(String.valueOf(timeStamp.charAt(timeStamp.length()-1))) + 1;
		 String cutOut = timeStamp.substring((timeStamp.length() - index) > 0 ? (timeStamp.length() - index) : 0, timeStamp.length());
		 String tokenS = Util.md5s(timeStamp + cutOut);
		 request.addHeader("time", timeStamp);
		 request.addHeader("token", tokenS);
		 
		 request.setEntity(new ByteArrayEntity(toGzipByte(json)));
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		 httpClient.addResponseInterceptor(new ResponseContentEncoding());
		 HttpResponse response = httpClient().execute(request);
		 Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		 
		 JSONObject json1 = toJson(response.getEntity());
		 System.out.println(json1);
		 
		 
	 }
	 
}
