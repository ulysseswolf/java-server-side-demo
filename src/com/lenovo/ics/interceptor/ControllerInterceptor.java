package com.lenovo.ics.interceptor;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.constants.ErrorCode;
import com.lenovo.ics.model.ServiceStat;

public class ControllerInterceptor extends HandlerInterceptorAdapter {

//	@Autowired
//	SystemService systemService;
	
	/**
	 * 在Controller方法前进行拦截
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			Date requestDate = Calendar.getInstance().getTime();
			String did = request.getHeader("deviceId");
			String deviceType = request.getHeader("deviceType");
			String deviceModel = request.getHeader("deviceModel");
			String systemType = request.getHeader("systemType");
			String systemVersion = request.getHeader("systemVersion");
			String versionCode = request.getHeader("versionCode");
			String versionName = request.getHeader("versionName");
			String terminalIp = request.getRemoteAddr();
			String timeStamp = request.getHeader("time");
	
			ServiceStat stat = new ServiceStat();
			stat.setRequestTime(requestDate);
			stat.setId(UUID.randomUUID().toString());
			stat.setDeviceId(did);
			stat.setDeviceType(deviceType);
			stat.setDeviceModel(deviceModel);
			stat.setSystemType(systemType);
			stat.setSystemVersion(systemVersion);
			stat.setVersionCode(versionCode);
			stat.setVersionName(versionName);
			stat.setTerminalIp(terminalIp);
			stat.setRequestContent(null);
			stat.setTime(Long.valueOf((timeStamp != null ? timeStamp : "0")));
			
			stat.setResponseTime(new Date());
//			systemService.record(stat);
			return true;
		} catch (Exception e) {
			int httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
			response.setStatus(httpStatusCode);
			//supposed to return error message,but it does not work
			PrintWriter out = response.getWriter();
			JSONObject js = new JSONObject();
			js.put("errorCode",ErrorCode.ERROR_CODE_PARAM_ERROR);
			js.put("errorDesc","请求参数错误！");
			out.write(js.toJSONString());
			out.flush();
            out.close();
            return false;
		}
	}

	/**
	 * This implementation is empty.
	 */
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	/**
	 * 在Controller方法后进行拦截
	 */
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {}
 }