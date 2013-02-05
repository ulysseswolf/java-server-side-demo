package com.lenovo.ics.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.lenovo.ics.model.Program;

@Repository
public class ProgramDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Program findCurrentProgram(String channelId) {
		long now = System.currentTimeMillis() / 1000;
		final String sql = "SELECT * FROM PROGRAM_INFO " +
				"WHERE CHANNEL_ID = ? " +
				"AND UNIX_TIMESTAMP(BEGIN_TIME) <= ? " +
				"AND UNIX_TIMESTAMP(END_TIME) > ?";
		List<Program> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Program>(Program.class), channelId, now, now);
		if (list.isEmpty()) return null;
		return list.get(0);
	}

	public Program findByContentId(String contentId) {
		String sql = "SELECT p.*,c.DISPLAY_NAME AS CHANNEL_NAME FROM PROGRAM_INFO p JOIN CHANNEL_INFO c ON p.CHANNEL_ID = c.CHANNEL_ID WHERE p.CONTENT_ID = ? ";
		List<Program> programs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Program>(Program.class),contentId);
		if (programs.isEmpty()) return null;
		else return programs.get(0);
	}
}
