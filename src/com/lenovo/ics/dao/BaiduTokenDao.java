package com.lenovo.ics.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BaiduTokenDao {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public String getToken() {
		String sql = "SELECT ACCESS_TOKEN FROM BAIDU_TOKEN";
		List<String> tokens = jdbcTemplate.query(sql,new RowMapper<String>(){
			public String mapRow(ResultSet rs,int rowNum)throws SQLException{
				return rs.getString("ACCESS_TOKEN");
			}
		});
		if (tokens.size() > 0) return tokens.get(0);
		else return null;
	}

	public long getTokenEffectiveDate() {
		String sql = "SELECT EFFECTIVE_DATE FROM BAIDU_TOKEN";
		return jdbcTemplate.queryForLong(sql);
	}

	public void updateToken(String token) {
		String sql = "UPDATE BAIDU_TOKEN SET ACCESS_TOKEN = ?, EFFECTIVE_DATE = ?";
		long now = System.currentTimeMillis();
		jdbcTemplate.update(sql,token,now);
	}

}
