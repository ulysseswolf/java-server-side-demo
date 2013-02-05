package com.lenovo.ics.action;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.ConstantValue;
import com.lenovo.ics.service.ServiceException;

@Controller
public class SinaEntDetailAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(SinaEntDetailAction.class);

	@Autowired
	ConstantValue constantValue;
	
	/**
	 * 新浪娱乐详情接口
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/1.0/outer/thirdparty/sina/ent/detail")
	public void detail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONObject returnJson = new JSONObject();
		int httpStatusCode = HttpURLConnection.HTTP_OK;
		logger.info("终端开始获取新浪娱乐详细信息");
		try {
			String requestJson = readRequestBody(request);
			logger.info("新浪娱乐信息参数为：" + requestJson);
			JSONObject oJSONObject = JSONObject.parseObject(requestJson);
			String id = oJSONObject.getString("id");
			String title = oJSONObject.getString("title");
			int order = oJSONObject.getIntValue("order");
			if (StringUtils.isBlank(id)) {
				logger.error("获取参数id 为空");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			
			if (id.length() > 36) {
				logger.error("参数id过长");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			if (title != null && title.length() > 128) {
				logger.error("参数title过长");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			if (order > 0x7fffffff || order < 0) {
				logger.error("参数order超过int类型范围或无效");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			String contentId = oJSONObject.getString("contentId");
			if (StringUtils.isBlank(contentId) || contentId.length() > 36) {
				logger.error("参数contentId错误");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			DefaultHttpClient httpclient = null;
			//设置代理
			boolean isProxy = constantValue.getIsProxy();
			if (isProxy == true) {
				HttpParams params = new BasicHttpParams();
				HttpConnectionParams.setSoTimeout(params, 20 * 1000);
				HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
				String proxyHost = constantValue.getProxyHost();
				Integer proxyPort = constantValue.getProxyPort();
				if (proxyHost != null && proxyPort != null) {
					HttpHost host = new HttpHost(proxyHost, proxyPort);
					ConnRouteParams.setDefaultProxy(params, host);
				}
				httpclient = new DefaultHttpClient(params);
			} else {
				httpclient = new DefaultHttpClient();
			}
			HttpPost httpPost = null;
			HttpEntity entity = null;
			
			String sinaEntDetailUrl = constantValue.getSinaEntDetailUrl() + id;
			httpPost = new HttpPost(sinaEntDetailUrl);
			HttpResponse baiduResponse = httpclient.execute(httpPost);
			entity = baiduResponse.getEntity();
			InputStream stream = entity.getContent();
			JSONObject resJson = JSONObject.parseObject(formatResponse(stream));
			if (resJson.getIntValue("status") == 0) {
				JSONObject data =  resJson.getJSONObject("data");
				String title_r = data.getString("title");
				long pubDate = data.getLongValue("pubDate");
				String content = data.getString("content");
				if (!StringUtils.isBlank(content))
					content = content.replaceAll("<!?.*?\\/?>", "");
				JSONArray pics = data.getJSONArray("pics");
				JSONArray picArray = new JSONArray();
				JSONObject jso = new JSONObject();
				String picUrl = null;
				String alt = null;
				if (!pics.isEmpty()) {
					JSONObject pic = pics.getJSONObject(0);
					picUrl = pic.getString("pic");
					alt = pic.getString("alt");
				}
				
				jso.put("pic", picUrl);
				jso.put("alt", alt);
				picArray.add(jso);
				returnJson.put("id",id);
				returnJson.put("title",title_r);
				returnJson.put("pubDate",pubDate);
				returnJson.put("content",content);
				returnJson.put("pic",picArray);
				
			}
			logger.info("终端获取新浪娱乐详细信息查询结束，返回终端数据；");
			httpStatusCode = HttpURLConnection.HTTP_OK;
		} catch (EOFException eof) {
			logger.error("获取参数错误:", eof);
			returnJson.put(ERROR_CODE, ERROR_CODE_PARAM_ERROR);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (ServiceException se) {
			returnJson.put(ERROR_CODE, se.getErrorCode());
			returnJson.put(ERROR_DESC, se.getMessage());
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (NumberFormatException e) {
			returnJson.put(ERROR_CODE, ERROR_CODE_PARAM_ERROR);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			logger.error("获取参数错误:", e);
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (NullPointerException se) {
			returnJson.put(ERROR_CODE, ERROR_CODE_PARAM_ERROR);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			logger.error("获取参数错误:", se);
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (Exception e) {
			logger.error("未知错误,错误信息为：" ,e);
			returnJson.put("errorDesc", "未知错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		}
		returnInfo(request, response, httpStatusCode, returnJson);
	}

	/**
	 * InputStream转换为JSONObject
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	String formatResponse(InputStream stream) throws IOException {
		 StringBuilder buffer = new StringBuilder();
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
         while (true) {
             String line = reader.readLine();
             if (line == null)  break;
             buffer.append(line);
         }
         return buffer.toString();
	}
}
