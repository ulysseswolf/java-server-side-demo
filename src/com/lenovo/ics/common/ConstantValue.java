package com.lenovo.ics.common;

import org.springframework.beans.factory.annotation.Value;

public class ConstantValue {

	@Value("${ics.search.samplePath}")
	private String samplePath;
	@Value("${ics.search.HKsamplePath}")
	private String HKsamplePath;
	
	@Value("${isProxy}")
	private boolean isProxy;
	@Value("${proxy.host}")
	private String proxyHost;
	@Value("${proxy.port}")
	private Integer proxyPort;
	
	@Value("${tvIcon.scale.width}")
	private int width;
	@Value("${tvIcon.scale.height}")
	private int height;
	@Value("${tvIcon.type}")
	private String tvIconType;
	
	@Value("${appid}")
	private String appId;
	@Value("${apiKey}")
	private String apiKey;
	@Value("${secretKey}")
	private String secretKey;
	@Value("${accessTokenUrl}")
	private String accessTokenUrl;
	@Value("${queryUrl}")
	private String queryUrl;
	@Value("${bkLength}")
	private String bkLength;
	
	@Value("${sina.ent.url}")
	private String sinaEntUrl;
	@Value("${sina.ent.detail.url}")
	private String sinaEntDetailUrl;
	
	public String getSamplePath() {
		return samplePath;
	}
	public String getHKSamplePath() {
		return HKsamplePath;
	}
	
	public String getProxyHost() {
		return proxyHost;
	}
	
	public Integer getProxyPort() {
		return proxyPort;
	}
	
	public int getTvIconWidth() {
		return width;
	}
	
	public int getTvIconHeight() {
		return height;
	}

	public String getTvIconType() {
		return tvIconType;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getAppId() {
		return appId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public String getQueryUrl() {
		return queryUrl;
	}

	public String getBkLength() {
		return bkLength;
	}
	public boolean getIsProxy() {
		return isProxy;
	}

	public String getSinaEntUrl() {
		return sinaEntUrl;
	}
	public String getSinaEntDetailUrl() {
		return sinaEntDetailUrl;
	}
}
