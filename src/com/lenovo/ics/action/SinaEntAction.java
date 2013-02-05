package com.lenovo.ics.action;

import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.service.ContentService;
import com.lenovo.ics.service.ServiceException;

@Controller
public class SinaEntAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(SinaEntAction.class);

	@Autowired
	ContentService contentService;
	
	@Value("${contentId.default}")
	private String defaultContent;
	
	/**
	 * 新浪娱乐接口
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/1.0/outer/thirdparty/sina/ent")
	public void detail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONObject returnJson = new JSONObject();
		int httpStatusCode = HttpURLConnection.HTTP_OK;
		logger.info("终端开始获取新浪娱乐信息");
		try {
			String requestJson = readRequestBody(request);
			logger.info("内容信息参数为：" + requestJson);
			JSONObject oJSONObject = JSONObject.parseObject(requestJson);
			String contentId = oJSONObject.getString("contentId");
			if (StringUtils.isBlank(contentId)) {
				logger.info("获取参数contentId 为空，设为默认值");
				contentId = defaultContent;
			}
			if (contentId.length() > 36) {
				logger.error("参数contentId过长");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			
			returnJson = contentService.sinaEnt(contentId);
			
			logger.info("终端获取新浪娱乐信息查询结束，返回终端数据；");
			httpStatusCode = HttpURLConnection.HTTP_OK;
		} catch (EOFException eof) {
			logger.error("内容信息获取参数错误:", eof);
			returnJson.put(ERROR_CODE, ERROR_CODE_PARAM_ERROR);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (ServiceException se) {
			returnJson.put(ERROR_CODE, se.getErrorCode());
			returnJson.put(ERROR_DESC, se.getMessage());
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (NullPointerException se) {
			returnJson.put(ERROR_CODE, ERROR_CODE_PARAM_ERROR);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (Exception e) {
			logger.error("未知错误,错误信息为：" ,e);
			returnJson.put("errorDesc", "未知错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		}
		returnInfo(request, response, httpStatusCode, returnJson);
	}
}
