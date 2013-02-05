package com.lenovo.ics.action.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static junit.framework.Assert.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.Util;

public class TvIconActionTest extends WebTestBase {

//	public static final String sUrlPrefix = "http://127.0.0.1:8080/ics/1.0";
//	public static final String sUrlPrefix = "http://10.100.1.86:8090/ics/1.0";
//	public static final String sUrlPrefix = "http://10.100.1.125:8090/ics/1.0";
	public static final String sUrlPrefix = "http://10.100.1.47:8088/ics/1.0";
//	public static final String sUrlPrefix = "http://10.100.9.53:8090/ics/1.0";

	protected static String toUrl(String segment) {
		return sUrlPrefix.concat(segment);
	}

	protected static byte[] toGzipByteArray(File file) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		GZIPOutputStream stream = new GZIPOutputStream(buffer);
		FileInputStream fis = new FileInputStream(file);
		byte[] buff = new byte[8192];
		int len = -1;
		while ((len = fis.read(buff)) != -1) {
			stream.write(buff, 0, len);
		}
		fis.close();
		stream.flush();
		stream.close();
		return buffer.toByteArray();
	}

	/**
	 * 正常流程测试用例
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRecognition() throws IOException {
		String url = toUrl("/outer/channel/identify/hk");

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(params, 30 * 60 * 1000);
		HttpConnectionParams.setConnectionTimeout(params, 30 * 60 * 1000);

		DefaultHttpClient httpclient = new DefaultHttpClient(params);
		httpclient.addRequestInterceptor(new RequestAcceptEncoding());
		httpclient.addResponseInterceptor(new ResponseContentEncoding());
		HttpPost httppost = new HttpPost(url);
		String timeStamp = String.valueOf(System.currentTimeMillis());
		Integer index = Integer.parseInt(String.valueOf(timeStamp.charAt(timeStamp.length() - 1))) + 1;
		String cutOut = timeStamp.substring((timeStamp.length() - index) > 0 ? (timeStamp.length() - index) : 0, timeStamp.length());
		String tokenS = Util.md5s(timeStamp + cutOut);
		httppost.addHeader("time", timeStamp);
		httppost.addHeader("token", tokenS);
		httppost.setEntity(new ByteArrayEntity(toGzipByteArray(new File("sample/cctv8.jpg"))));
		 
		HttpResponse response = httpclient.execute(httppost);

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		HttpEntity httpEntity = response.getEntity();
		JSONObject json = JSON.parseObject(EntityUtils.toString(httpEntity, "UTF-8"));
		System.out.println(json.toJSONString());
		assertTrue(json.containsKey("list"));
		JSONArray list = json.getJSONArray("list");
		if (list != null) {
			final int len = list.size();
			for (int i = 0; i < len; i++) {
				JSONObject ele = list.getJSONObject(i);
				assertTrue(ele.containsKey("channelAlias"));
				assertTrue(ele.containsKey("programId"));
				assertTrue(ele.containsKey("channelId"));
				assertTrue(ele.containsKey("contentId"));
				assertTrue(ele.containsKey("subContentId"));
				assertTrue(ele.containsKey("contentType"));
				assertTrue(ele.containsKey("sportType"));
				assertTrue(ele.containsKey("voteNumber"));

			}
		}
	}

}
