package com.lenovo.ics.action;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.ConstantValue;
import com.lenovo.ics.common.HTMLDecoder;
import com.lenovo.ics.dao.BaiduTokenDao;
import com.lenovo.ics.service.ServiceException;

@Controller
public class BaiduBaikeAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(BaiduBaikeAction.class);
	
	@Resource
	ConstantValue constantValue;
	
	@Resource
	BaiduTokenDao baiduTokenDao;
	
	/**
	 * 百度百科接口
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/1.0/outer/thirdparty/baike/lemma")
	public void baike(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		JSONObject returnJson = new JSONObject();
		int httpStatusCode = HttpURLConnection.HTTP_OK;
		logger.info("终端开始获取百度百科信息");
		try {
			String requestJson = readRequestBody(request);
			logger.info("查询百科词条参数：" + requestJson);
			JSONObject oJSONObject = JSONObject.parseObject(requestJson);
			String key = null;
			try {
				key = oJSONObject.getString("key");
			} catch (JSONException e) {
				logger.error("查询词条参数错误！",e);
				returnJson.put(ERROR_CODE, 102);
				returnJson.put(ERROR_DESC, "请求参数错误！");
				httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
			} 
			if (StringUtils.isBlank(key)) {
				logger.error("获取查询词条参数为空");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			} else if (key.length() > 38){
				logger.error("查询词条参数过长");
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			
			Pattern p = Pattern.compile("([\\u4e00-\\u9fa5]|\\w)*");
			Matcher m = p.matcher(key);
			if (m.find())
				if (!key.equals(m.group())) 
					 key = URLEncoder.encode(key,"utf8");
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
			//读取百度百科请求参数
			String appId = constantValue.getAppId();
			HttpPost httpPost = null;
			HttpEntity entity = null;
			String accessToken = baiduTokenDao.getToken();
			//当前日期减去数据库中的token生效日期，如果大于30天，更新token，否则读取数据库中token
			long now = System.currentTimeMillis();
			long effectiveDate = baiduTokenDao.getTokenEffectiveDate();
			if (now - effectiveDate > 2592000000L || accessToken == null) {
	            String apiKey = constantValue.getApiKey();
	            String secretKey = constantValue.getSecretKey();
	            String accessTokenUrl = constantValue.getAccessTokenUrl();
	            accessTokenUrl += String.format("client_id=%1$s&client_secret=%2$s", apiKey, secretKey);
	            //获得access token
		    	httpPost = new HttpPost(accessTokenUrl);
	            HttpResponse baiduResponse = httpclient.execute(httpPost);
	            entity = baiduResponse.getEntity();
	            InputStream stream = entity.getContent();
	            JSONObject oAuthJson = JSONObject.parseObject(formatResponse(stream));
	            logger.info("response:{}",oAuthJson);
	            accessToken = oAuthJson.getString("access_token");
	            baiduTokenDao.updateToken(accessToken);
	            
			} 
            logger.info("access token:{}",accessToken);
            //根据token和词条调百科api
            String query = constantValue.getQueryUrl();
            String bkLength = constantValue.getBkLength();
			query =  query + String.format("access_token=%1$s&format=json&appid=%2$s&bk_key=%3$s&bk_length=%4$s", accessToken,appId,key,bkLength);
			logger.info("query url:{}",query);
            httpPost = new HttpPost(query);
            //解析返回json数据
            HttpResponse dataResponse = httpclient.execute(httpPost);
            entity = dataResponse.getEntity();
            InputStream dataStream = entity.getContent();
            String responseStr = formatResponse(dataStream);
            logger.info("response:{}",responseStr);
            JSONObject data = JSONObject.parseObject(responseStr);
            String _abstract = data.getString("abstract");
            //再查询一遍
            if (_abstract == null) {
            	dataResponse = httpclient.execute(httpPost);
            	 entity = dataResponse.getEntity();
                 dataStream = entity.getContent();
                 responseStr = formatResponse(dataStream);
                 logger.info("response:{}",responseStr);
                 data = JSONObject.parseObject(responseStr);
                 _abstract = data.getString("abstract");
            }
            //过滤html标记
            if (_abstract != null){
            	_abstract = filte(_abstract);
            }
            String url = data.getString("url");
            returnJson.put("abstract",_abstract);
            returnJson.put("url", url);
            
			logger.info("百度百科信息查询结束，返回终端数据；");
			httpStatusCode = HttpURLConnection.HTTP_OK;
		} catch (EOFException eof) {
			logger.error("内容信息获取参数错误:", eof);
			returnJson.put(ERROR_CODE, 102);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (HttpHostConnectException e) {
			logger.error("百度服务器连接错误:", e);
			returnJson.put(ERROR_CODE, 118);
			returnJson.put(ERROR_DESC, "百度服务器连接错误，请检查网络！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (ServiceException e) {
			returnJson.put(ERROR_CODE, e.getErrorCode());
			returnJson.put(ERROR_DESC, e.getMessage());
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (JSONException e) {
			logger.error("访问百度百科接口异常：{}",e);
			returnJson.put("errorDesc", "网络异常！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (NullPointerException e) {
			logger.error("空指针异常:{}", e);
			returnJson.put(ERROR_CODE, 102);
			returnJson.put(ERROR_DESC, "请求参数错误！");
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (Exception e) {
			logger.error("未知错误,错误信息为：{}",e);
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
	
	private String filte(String str) {
		str = str.replaceAll("</?(a)(.*?>|.*>?)", "");
    	str = str.replaceAll("</?", "");
    	str = str.replaceAll("\\[/?.*?\\……", "...");
    	str = HTMLDecoder.decode(str);
    	str = HTMLDecoder.decode(str);
		return str;
	}

}
