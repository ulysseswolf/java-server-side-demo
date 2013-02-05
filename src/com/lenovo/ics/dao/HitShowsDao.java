package com.lenovo.ics.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.lenovo.ics.common.constants.Constants;
import com.lenovo.ics.model.ProgramDetail;

@Repository
public class HitShowsDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${ics.hitshows.max}")
	private int maxValue;
	
	@Value("${ics.hitshows.page}")
	private int page;

	public List<ProgramDetail> findRankingEPG() {
		final long now = System.currentTimeMillis() / 1000;
		String sql = "SELECT DISTINCT p.* FROM `PROGRAM_DETAIL` p JOIN `CONTENT_IMG` m ON p.CONTENT_ID = m.CONTENT_ID " +
			"WHERE p.CONTENT_ID IS NOT NULL AND p.HEAT_DEGREE > 0 AND UNIX_TIMESTAMP(p.END_TIME) >= ? " +
			"AND p.END_TIME <= DATE_FORMAT(DATE_ADD(p.END_TIME,INTERVAL 2 DAY),'%Y-%m-%d') " +
			"AND m.IMG_TYPE = ? " +
			"GROUP BY p.CONTENT_ID ORDER BY BEGIN_TIME ASC,HEAT_DEGREE DESC LIMIT ?";
		List<ProgramDetail> programs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ProgramDetail>(ProgramDetail.class), now, Constants.POSTER_NORMAL, maxValue);
		if (programs.size() <= page || programs.size() == maxValue) {
			return programs;
		} else if (programs.size() % page == 1) {
			programs.remove(programs.size()-1);
		}
		return programs;
	}

}
