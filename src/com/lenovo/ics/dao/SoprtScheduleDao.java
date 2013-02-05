package com.lenovo.ics.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SoprtScheduleDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public int findSoprtType(String sourceId) {
		final String sql = "SELECT SPORT_TYPE FROM SPORT_SCHEDULE WHERE SOURCE_ID = ?";
		return jdbcTemplate.queryForInt(sql, sourceId);
	}

}
