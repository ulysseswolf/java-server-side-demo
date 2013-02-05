package com.lenovo.ics.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.Util;
import com.lenovo.ics.common.constants.ErrorCode;
import com.lenovo.ics.service.ServiceException;

public class ServiceFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(ServiceFilter.class);
	
	@Override
	public void destroy() {
		this.config = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest hrequest = (HttpServletRequest) request;
		HttpServletResponse hresponse = (HttpServletResponse) response;

		try {
			String did = hrequest.getHeader("deviceId");
			if (did != null && did.length() > 128) {
				logger.error("参数deviceId过长");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			}
			String deviceType = hrequest.getHeader("deviceType");
			if (deviceType != null && deviceType.length() > 128) {
				logger.error("参数deviceType过长");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			} 
			String deviceModel = hrequest.getHeader("deviceModel");
			if (deviceModel != null && deviceModel.length() > 128) {
				logger.error("参数deviceModel过长");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			} 
			String systemType = hrequest.getHeader("systemType");
			if (systemType != null && systemType.length() > 128) {
				logger.error("参数systemType过长");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			} 
			if (StringUtils.isBlank(systemType)) {
				deviceType = "0";
			}
			String systemVersion = hrequest.getHeader("systemVersion");
			if (systemVersion != null && systemVersion.length() > 128) {
				logger.error("参数systemVersion过长");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			} 
			String versionCode = hrequest.getHeader("versionCode");
			if (versionCode != null && versionCode.length() > 128) {
				logger.error("参数versionCode过长");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			} 
			String versionName = hrequest.getHeader("versionName");
			if (versionName != null && versionName.length() > 128) {
				logger.error("参数versionName过长");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			} 
		
			String timeStamp = hrequest.getHeader("time");
			String token = hrequest.getHeader("token");
	
			logger.info("time:{}", timeStamp);
			
			if (StringUtils.isBlank(timeStamp) || timeStamp.length() > 128) {
				logger.error("无效的token，安全认证失败!");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			}
			logger.info("token:{}", token);
			if (StringUtils.isBlank(token) || token.length() > 128) {
				logger.error("无效的token，安全认证失败!");
				throw new ServiceException("请求参数错误", ErrorCode.ERROR_CODE_PARAM_ERROR);
			}
			
			if (!StringUtils.isBlank(timeStamp) && !StringUtils.isBlank(token)) {
				Integer index = Integer.parseInt(String.valueOf(timeStamp.charAt(timeStamp.length()-1))) + 1;
				String cutOut = timeStamp.substring((timeStamp.length() - index) > 0 ? (timeStamp.length() - index) : 0, timeStamp.length());
				String tokenS = Util.md5s(timeStamp + cutOut);
				logger.info("server token:{}",tokenS);
				if (!token.equals(tokenS)) {
					logger.error("请求的token与服务器算出的不一致，安全认证失败!");
					throw new ServiceException("认证失败！", ErrorCode.ERROR_CODE_BAD_REQUEST);
				}
			} else {
				logger.error("无效的认证参数，安全认证失败!");
				throw new ServiceException("认证失败！", ErrorCode.ERROR_CODE_BAD_REQUEST);
			}
			chain.doFilter(hrequest, hresponse);
			
		} catch (ServiceException e) {
			if (e.getErrorCode() == ErrorCode.ERROR_CODE_PARAM_ERROR) {
				hresponse.setStatus(HttpURLConnection.HTTP_PAYMENT_REQUIRED);
			} else if (e.getErrorCode() == ErrorCode.ERROR_CODE_BAD_REQUEST) {
				hresponse.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			}
			PrintWriter out = hresponse.getWriter();
			JSONObject js = new JSONObject();
			js.put("errorCode",e.getErrorCode());
			js.put("errorDesc",e.getMessage());
			String error = js.toJSONString();
			out.write(error);
			out.flush();
			out.close();
			return;
		}
		catch (Throwable e) {
			hresponse.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
			PrintWriter out = hresponse.getWriter();
			JSONObject js = new JSONObject();
			js.put("errorCode",ErrorCode.ERROR_CODE_BAD_REQUEST);
			js.put("errorDesc","认证失败！");
			js.writeJSONString(out);
			out.flush();
            out.close();
            return;
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

	@SuppressWarnings("unused")
	private FilterConfig config;

}
