package com.lenovo.ics.model;

import java.util.Date;

public class ProgramDetail implements Comparable<ProgramDetail> {

	/* channel part */
	private String channelId;
	private String channelDisplayName;
	private String channelIcon;

	/* program part */
	private String programId;
	private Date beginTime;
	private Date endTime;

	/* content part */
	private String contentId;
	private String contentName;
	private Integer grade;
	private Integer heatDegree;
	private Integer episode;

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelIcon() {
		return channelIcon;
	}

	public void setChannelIcon(String channelIcon) {
		this.channelIcon = channelIcon;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
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

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Integer getHeatDegree() {
		return heatDegree;
	}

	public void setHeatDegree(Integer heatDegree) {
		this.heatDegree = heatDegree;
	}

	public String getChannelDisplayName() {
		return channelDisplayName;
	}

	public void setChannelDisplayName(String channelDisplayName) {
		this.channelDisplayName = channelDisplayName;
	}

	public String getContentName() {
		if (episode>0) {
			return contentName + "(" + episode + ")";
		}
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public Integer getEpisode() {
		return episode;
	}

	public void setEpisode(Integer episode) {
		this.episode = episode;
	}

	@Override
	public int compareTo(ProgramDetail pd) {
		if (beginTime.before(pd.beginTime)) {
			return -1;
		} else if (beginTime.after(pd.beginTime)) {
			return 1;
		}
		return 0;
	}

}
