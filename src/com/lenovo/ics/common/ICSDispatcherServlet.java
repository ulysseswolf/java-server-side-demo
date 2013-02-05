package com.lenovo.ics.common;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

@SuppressWarnings("serial")
public class ICSDispatcherServlet extends DispatcherServlet {

	static final Logger logger = LoggerFactory.getLogger(ICSDispatcherServlet.class);
	
	@Override
	public final void init(ServletConfig config) throws ServletException {
		super.init(config);

		init(getWebApplicationContext());
	}

	/**
	 * 系统参数初始化
	 */
	private void init(ApplicationContext applicationContext) {

		// 设置json格式中为null的值返回null
		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteMapNullValue.getMask();
		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteEmptyListAsNull.getMask();
		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteEmptyMapAsNull.getMask();
		
		/* 初始化台标识别样本 */
		ConstantValue val = applicationContext.getBean(ConstantValue.class);
//		logger.info("LogoRecognition样本初始化");
//		boolean result = LogoRecognition.init(val.getSamplePath());
//		if (result) {
//			logger.info("LogoRecognition样本初始化成功！");
//		} else {
//			logger.error("LogoRecognition样本初始化失败！");
//			throw new RuntimeException("LogoRecognition样本初始化失败！");
//		}
	}

	@Override
	public void destroy() {
		super.destroy();
//		LogoRecognition.unInit();
	}

}
