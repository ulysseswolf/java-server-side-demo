package com.lenovo.ics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.ConstantValue;

@Service
public class ContentService {

	@Autowired
	ConstantValue constantValue;
	
	@Value("${sina.title.length}")
	int titleLength;
	
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	

	public JSONObject sinaEnt(String contentId) throws ServiceException {
		JSONObject returnJ = new JSONObject();
		String json = stringRedisTemplate.opsForValue().get("contentEnt:" + contentId);
		JSONArray list = new JSONArray();
		if (json != null) {
			JSONArray titles = JSONArray.parseArray(json);
			for (int i=0;i<titles.size();i++) {
				JSONObject j = titles.getJSONObject(i);
				JSONObject tmp = new JSONObject();
				tmp.put("id", j.get("id"));
				tmp.put("order", i);
				String title = j.getString("title");
				if (title != null && title.length() > titleLength) {
					title = title.substring(0,titleLength);
				}
				tmp.put("title", title);
				tmp.put("pubDate", j.get("pubDate"));
				list.add(tmp);
			}
		}
		returnJ.put("list", list);
		return returnJ;
	}

	
}
