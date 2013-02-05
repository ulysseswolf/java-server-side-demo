package com.lenovo.ics.action;

import java.io.EOFException;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.service.ServiceException;
import com.lenovo.ics.service.SportService;

@Controller
public class NbaOtherMatchAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(NbaOtherMatchAction.class);

	@Autowired
	SportService sportService;
	
	@RequestMapping(value = "/1.0/outer/sport/basketball/other")
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject returnJson = new JSONObject();
		int httpStatusCode = HttpURLConnection.HTTP_OK;
		logger.info("终端开始获取其他篮球比赛信息");
		try{
			String requestJson = readRequestBody(request);
			logger.info("contentId：" + requestJson);

			JSONObject oJSONObject = JSON.parseObject(requestJson);
			String contentId = oJSONObject.getString("contentId");
			if (StringUtils.isBlank(contentId)) { 
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			if (contentId.length() > 36) {
				logger.error("参数contentId过长");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			JSONArray matches = sportService.otherMatch(contentId, "nba",-1);
			returnJson.put("list", matches);
			logger.info("其他篮球比赛信息查询结束，返回终端数据");
		} catch (EOFException eof) {
			logger.error("节目获取参数错误:", eof);
			returnJson.put(ERROR_CODE, ERROR_CODE_PARAM_ERROR);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (ServiceException se) {
			logger.error("业务逻辑错误,错误信息为：" ,se);
			returnJson.put(ERROR_CODE, se.getErrorCode());
			returnJson.put(ERROR_DESC, se.getMessage());
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (Exception e) {
			logger.error("未知错误,错误信息为：",e);
			returnJson.put(ERROR_CODE, ERROR_CODE_BUSINESS);
			returnJson.put("ERROR_DESC", "未知错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		}
		returnInfo(request, response, httpStatusCode, returnJson);
	}
	
}
