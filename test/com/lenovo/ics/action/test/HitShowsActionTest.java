package com.lenovo.ics.action.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.Util;

/**
 * 热播节目测试用例
 */
public class HitShowsActionTest extends WebTestBase {

	@Test
	public void testHitShows() throws Exception {

		HttpPost req = new HttpPost(toUrl("/outer/epg/ranking"));

		String timeStamp = String.valueOf(System.currentTimeMillis());
		 Integer index = Integer.parseInt(String.valueOf(timeStamp.charAt(timeStamp.length()-1))) + 1;
		 String cutOut = timeStamp.substring((timeStamp.length() - index) > 0 ? (timeStamp.length() - index) : 0, timeStamp.length());
		 String tokenS = Util.md5s(timeStamp + cutOut);
		 req.addHeader("time", timeStamp);
		 req.addHeader("token", tokenS);
		 
		HttpResponse resp = httpClient.execute(req);

		assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());

		HttpEntity entity = resp.getEntity();
		
		JSONObject json = JSON.parseObject(EntityUtils.toString(entity, "UTF-8"));
		System.out.println(json);

		assertTrue(json.containsKey("list"));

		JSONArray list = json.getJSONArray("list");
		System.out.println(list.size());
		if (list != null) {
			final int len = list.size();
			for (int i = 0; i < len; i++) {
				JSONObject ele = list.getJSONObject(i);
				assertTrue(ele.containsKey("channelId"));
				assertTrue(ele.containsKey("channelName"));
				assertTrue(ele.containsKey("programId"));
				assertTrue(ele.containsKey("startTime"));
				assertTrue(ele.containsKey("endTime"));
				assertTrue(ele.containsKey("contentName"));
				assertTrue(ele.containsKey("heatDegree"));
				assertTrue(ele.containsKey("grade"));
				assertTrue(ele.containsKey("posterUrl_0"));
				assertTrue(ele.containsKey("posterUrl_1"));
				SimpleDateFormat dateformat2=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E "); 
				System.out.println("name: "+ele.getString("contentName"));
		        String startTime=dateformat2.format(ele.getLong("startTime"));
		        String endTime=dateformat2.format(ele.getLong("endTime"));
				System.out.println("startTime: "+startTime);
				System.out.println("endTime :"+endTime);
				System.out.println("海报缩略图 :"+ele.getString("posterUrl_0"));
				System.out.println("heatDegree :"+ele.getString("heatDegree"));
			}
		}

	}

}
