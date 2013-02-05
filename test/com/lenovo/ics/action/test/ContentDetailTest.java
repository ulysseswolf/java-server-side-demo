package com.lenovo.ics.action.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import junit.framework.Assert;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.Util;

/**
 * 节目详情查询测试用例
 * @author Administrator
 *
 */
public class ContentDetailTest extends WebTestBase {

	 final String ID="be266c47-eb2a-3327-b660-3fbdd418dab5";
	 /**
	  * @title 节目详情查询测试用例 子剧情对应剧情
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
	 public void testDetail() throws Exception {
//		 HttpPost request = new HttpPost(toUrl("/outer/epg/detail"));		
		 HttpPost request = new HttpPost(toUrl("/outer/thirdparty/sina/ent"));
//		 HttpPost request = new HttpPost(toUrl("/outer/thirdparty/sina/ent/detail"));		
		 JSONObject json = new JSONObject();
		 json.put("contentId", null);
		 json.put("id", "64-3246785-ent-cms");
		 
		
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
	 public static void main(String[] string) throws ClientProtocolException, IOException {
		 HttpPost httpPost = new HttpPost("http://r3.sinaimg.cn/28/2013/0130/39/b/49374879/auto.jpg");
		 HttpClient httpclient = new DefaultHttpClient();
         HttpResponse baiduResponse = httpclient.execute(httpPost);
         HttpEntity entity = baiduResponse.getEntity();
         InputStream stream = entity.getContent();
         BufferedImage srcImage = ImageIO.read(stream);
         
	 }
	/**
	 * @title 节目详情查询测试用例
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
	public void testDetail1() throws Exception {
		HttpPost request = new HttpPost(toUrl("/outer/epg/detail"));		
		JSONObject json = new JSONObject();
		json.put("contentId", ID);

		request.setEntity(new ByteArrayEntity(toGzipByte(json)));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		HttpResponse response = httpClient().execute(request);
		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		
		JSONObject json1 = toJson(response.getEntity());
		Assert.assertNotNull(json1.getString("curTime"));
		JSONObject content = (JSONObject) json1.get("contentInfo");
		Assert.assertNotNull(content.getString("id"));
		Assert.assertNotNull(content.getString("name"));
		Assert.assertNotNull(content.getString("tags"));
		Assert.assertNotNull(content.getString("type"));
		Assert.assertNotNull(content.getString("description"));
//		Assert.assertNotNull(content.getString("posterUrl_0"));
//		Assert.assertNotNull(content.getString("posterUrl_1"));
		Assert.assertNotNull(content.getString("language"));
		Assert.assertNotNull(content.getString("grade"));
		JSONArray array = content.getJSONArray("actors");
		int len =0;
		if(array!=null){
			len = array.size();
			for (int i = 0; i < len; i++) {
				JSONObject ele = array.getJSONObject(i);
				Assert.assertNotNull(ele.getString("personId"));
				Assert.assertNotNull(ele.getString("name"));
				Assert.assertNotNull(ele.getInteger("type"));
			}
		}
		
		
		
	}
	
	/**
	 * @title 节目详情查询 节目id为英文 测试用例
	 * @step <strong>测试步骤</strong>
	 * <ol type="decimal">
	 * <li>参数初始化 </li>
	 * <li>向服务器发送请求</li>
	 * <li>断言返回的HTTP状态码为402</li>
	 * </ol>
	 * @throws Exception
	 */
	@Test
	public void testDetail2() throws Exception {
	    HttpPost request = new HttpPost(toUrl("/outer/epg/detail"));		
		JSONObject json = new JSONObject();
		json.put("contentId", "dsgsgerheyhryhrt");
	
		request.setEntity(new ByteArrayEntity(toGzipByte(json)));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		HttpResponse response = httpClient().execute(request);
		Assert.assertEquals(HttpStatus.SC_PAYMENT_REQUIRED, response.getStatusLine().getStatusCode());
		JSONObject json1 = toJson(response.getEntity());
		Assert.assertEquals(ERROR_CODE_BUSINESS, json1.getIntValue("errorCode"));//无查询结果
	}
	
	/**
	 * @title 节目详情查询 节目id为中文 测试用例
	 * @step <strong>测试步骤</strong>
	 * <ol type="decimal">
	 * <li>参数初始化 </li>
	 * <li>向服务器发送请求</li>
	 * <li>断言返回的HTTP状态码为402</li>
	 * </ol>
	 * @throws Exception
	 */
	@Test
	public void testDetail3() throws Exception {
		HttpPost request = new HttpPost(toUrl("/outer/epg/detail"));		
		JSONObject json = new JSONObject();
		json.put("contentId", "中文字");

		request.setEntity(new ByteArrayEntity(toGzipByte(json)));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		HttpResponse response = httpClient().execute(request);
		Assert.assertEquals(HttpStatus.SC_PAYMENT_REQUIRED, response.getStatusLine().getStatusCode());
	}
	
	/**
	 * @title 节目详情查询 节目id为空 测试用例
	 * @step <strong>测试步骤</strong>
	 * <ol type="decimal">
	 * <li>参数初始化 </li>
	 * <li>向服务器发送请求</li>
	 * <li>断言返回的HTTP状态码为402</li>
	 * </ol>
	 * @throws Exception
	 */
	@Test
	public void testDetail4() throws Exception {
		HttpPost request = new HttpPost(toUrl("/outer/epg/detail"));		
		JSONObject json = new JSONObject();
	json.put("contentId", "");

		request.setEntity(new ByteArrayEntity(toGzipByte(json)));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		HttpResponse response = httpClient().execute(request);
		Assert.assertEquals(HttpStatus.SC_PAYMENT_REQUIRED, response.getStatusLine().getStatusCode());
		
	}

	/**
	 * @title 节目详情查询 未找到相关服务 返回404  测试用例
	 * @step <strong>测试步骤</strong>
	 * <ol type="decimal">
	 * <li>参数初始化 </li>
	 * <li>向服务器发送请求</li>
	 * <li>断言返回的HTTP状态码为200</li>
	 * </ol>
	 * @throws Exception
	 */
	@Test
	public void testDetail5() throws Exception {
		HttpPost request = new HttpPost(toUrl("/outer/epg/dddd"));		
		JSONObject json = new JSONObject();
		json.put("contentId", ID);

		request.setEntity(new ByteArrayEntity(toGzipByte(json)));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		HttpResponse response = httpClient().execute(request);
		Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    }
	
	/**
	 * @title 节目详情查询   不传参数数据   测试用例
	 * @step <strong>测试步骤</strong>
	 * <ol type="decimal">
	 * <li>参数初始化 </li>
	 * <li>向服务器发送请求</li>
	 * <li>断言返回的HTTP状态码为402</li>
	 * </ol>
	 * @throws Exception
	 */
	@Test
	public void testGetTibits6() throws Exception {
        HttpPost post = new HttpPost(toUrl("/outer/epg/detail"));
		
		HttpResponse response = httpClient().execute(post);
		
		Assert.assertEquals(HttpStatus.SC_PAYMENT_REQUIRED, response.getStatusLine().getStatusCode());
		
	}
	
	
	/**
	 * @title 节目详情查询  参数数据为空Json对象   测试用例
	 * @step <strong>测试步骤</strong>
	 * <ol type="decimal">
	 * <li>参数初始化 </li>
	 * <li>向服务器发送请求</li>
	 * <li>断言返回的HTTP状态码为402</li>
	 * </ol>
	 * @throws Exception
	 */
	@Test
	public void testGetTibits7() throws Exception {		
        HttpPost post = new HttpPost(toUrl("/outer/epg/detail"));
		
		JSONObject param = new JSONObject();
		post.setEntity(new ByteArrayEntity(toGzipByte(param)));
		DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		HttpResponse response = httpClient().execute(post);
		
		Assert.assertEquals(HttpStatus.SC_PAYMENT_REQUIRED, response.getStatusLine().getStatusCode());
		
	}
	
	
	/**
	 * @title 节目详情查询   get方式请求   测试用例
	 * @step <strong>测试步骤</strong>
	 * <ol type="decimal">
	 * <li>参数初始化 </li>
	 * <li>向服务器发送请求</li>
	 * <li>断言返回的HTTP状态码为402</li>
	 * </ol>
	 * @throws Exception
	 */
	@Test
	public void testGetTibits8() throws Exception {
        HttpGet post = new HttpGet(toUrl("/outer/epg/detail"));
        DefaultHttpClient httpClient = new DefaultHttpClient();
		 httpClient.addRequestInterceptor(new RequestAcceptEncoding());
		httpClient.addResponseInterceptor(new ResponseContentEncoding());
		HttpResponse response = httpClient().execute(post);
		
		Assert.assertEquals(HttpStatus.SC_PAYMENT_REQUIRED, response.getStatusLine().getStatusCode());
		
	}
	
	

	
}
