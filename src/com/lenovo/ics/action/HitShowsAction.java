package com.lenovo.ics.action;

import java.net.HttpURLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.model.HitShowsProgram;
import com.lenovo.ics.service.HitShowsService;

/**
 * 热播节目接口
 */
@Controller
public class HitShowsAction extends BaseAction {

	@Autowired
	private HitShowsService hitShowsService;

	@RequestMapping(value = "/1.0/outer/epg/ranking")
	public void rankingEPG(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		JSONObject returnJson = new JSONObject();
		int httpStatusCode = HttpURLConnection.HTTP_OK;
		logger.info("开始热播节目查询处理。");
		try {

			List<HitShowsProgram> epgs = hitShowsService.findRankingEPG();
			JSONArray epgList = (JSONArray) JSON.toJSON(epgs);
			returnJson.put("list", epgList);

			logger.info("热播节目查询处理完成。");
			httpStatusCode = HttpURLConnection.HTTP_OK;
		} catch (Exception e) {// 其他未知异常，如数据库连接失败
			logger.error("未知错误：", e);
			returnJson.put(ERROR_CODE, ERROR_CODE_UNKNOWN);
			returnJson.put(ERROR_DESC, "未知错误");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		}
		returnInfo(req, resp, httpStatusCode, returnJson);
	}

}
