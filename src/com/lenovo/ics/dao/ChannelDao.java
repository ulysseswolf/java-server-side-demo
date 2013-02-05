package com.lenovo.ics.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.lenovo.ics.dao.ChannelDao;
import com.lenovo.ics.model.ChannelInfo;

@Repository
public class ChannelDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public ChannelInfo findChannel(String channelAlias) {
		final String sql = "SELECT * FROM CHANNEL_INFO WHERE ICON_NAME = ?";
		List<ChannelInfo> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ChannelInfo>(ChannelInfo.class), channelAlias);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public ChannelInfo findByChannelId(String channelId) {
		final String sql = "SELECT * FROM CHANNEL_INFO WHERE CHANNEL_ID = ?";
		List<ChannelInfo> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ChannelInfo>(ChannelInfo.class), channelId);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

}
