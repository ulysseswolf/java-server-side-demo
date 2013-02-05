package com.lenovo.ics.model;

import java.util.Date;

public class Program {

	private String id;
	private String channelId;
	private String contentId;
	private String subContentId;
	private String epgName;
	private String source;
	private Date beginTime;
	private Date endTime;
	private String session;
	private Integer episode;
	private Integer episodeTotal;
	private String channelName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getSubContentId() {
		return subContentId;
	}

	public void setSubContentId(String subContentId) {
		this.subContentId = subContentId;
	}

	public String getEpgName() {
		return epgName;
	}

	public void setEpgName(String epgName) {
		this.epgName = epgName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Integer getEpisode() {
		return episode;
	}

	public void setEpisode(Integer episode) {
		this.episode = episode;
	}

	public Integer getEpisodeTotal() {
		return episodeTotal;
	}

	public void setEpisodeTotal(Integer episodeTotal) {
		this.episodeTotal = episodeTotal;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

}
