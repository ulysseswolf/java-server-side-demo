package com.lenovo.ics.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.lenovo.ics.model.PlayerBaseInfo;

@Repository
public class SportDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public List<PlayerBaseInfo> getPlayers(String matchId, int type,String team, String exclude) {
		String sql;
		if (StringUtils.isBlank(exclude)) {
			sql = "SELECT h.SOURCE_ID AS PLAYER_ID,h.CN_FIRST_NAME,h.DISPLAY_NAME,t.NAME AS TEAM,h.IS_STAR AS STAR,h.GRADE " +
					"FROM SPORT_PLAYER h JOIN SPORT_SCHEDULE s ON s.HOME_TEAM_ID = h.TEAM_ID " +
					"JOIN SPORT_TEAM t ON h.TEAM_ID = t.ID " +
					"WHERE s.SOURCE_ID = ? AND h.PLAYER_TYPE = ? " +
					"AND t.TYPE = ? " +
					"AND h.SOURCE_ID NOT IN (?) " +
					"ORDER BY h.IS_STAR DESC,h.GRADE DESC,h.CN_FIRST_NAME " ;
		} else {
			sql = "SELECT h.SOURCE_ID AS PLAYER_ID,h.CN_FIRST_NAME,h.DISPLAY_NAME,t.NAME AS TEAM,h.IS_STAR AS STAR,h.GRADE " +
					"FROM SPORT_PLAYER h JOIN SPORT_SCHEDULE s ON s.HOME_TEAM_ID = h.TEAM_ID " +
					"JOIN SPORT_TEAM t ON h.TEAM_ID = t.ID " +
					"WHERE s.SOURCE_ID = ? AND h.PLAYER_TYPE = ? " +
					"AND t.TYPE = ? " +
					"ORDER BY h.IS_STAR DESC,h.GRADE DESC,h.CN_FIRST_NAME " ;
		}
		
		if (team.intern() == "term2"){
			sql = sql.replace("HOME_TEAM_ID", "VISIT_TEAM_ID");
		}
		List<PlayerBaseInfo> players = jdbcTemplate.query(sql,new BeanPropertyRowMapper<PlayerBaseInfo>(PlayerBaseInfo.class), matchId, type, type,exclude);
		return players;
	}

	public String getSourceId(String contentId) {
		String sql = "SELECT s.SOURCE_ID FROM SPORT_SCHEDULE s " +
				"WHERE s.CONTENT_ID = ?";
		
		List<String> urls = jdbcTemplate.query(sql,new Object[]{contentId},new RowMapper<String>(){
			public String mapRow(ResultSet rs,int rowNum)throws SQLException{
				return rs.getString("SOURCE_ID");
			}
		});
		
		if (urls.size() > 0) 
			return urls.get(0);
		else return null;
	}

	public List<HashMap<String, String>> getOtherMatches(String contentId) {
		//根据content_id取出比赛的league_id,date,然后以之作为查询条件获取联赛当天其他比赛source_id
		String sql = "SELECT a.SOURCE_ID,a.STATUS,a.CONTENT_ID,a.HOME_TEAM_ID,a.VISIT_TEAM_ID,UNIX_TIMESTAMP(a.DATE)*1000 AS DATE FROM SPORT_SCHEDULE a LEFT JOIN ( " +
				"SELECT (@league:=LEAGUE_NAME) AS LEAGUE_NAME,(@date:=DATE_FORMAT(DATE,'%Y-%m-%d')) AS DATE " +
				"FROM SPORT_SCHEDULE s JOIN CONTENT_INFO c ON s.CONTENT_ID = c.ID," +
				"(SELECT @league:='',@date:='') AS t " +
				"WHERE c.ID = ? " +
				") AS b ON a.LEAGUE_NAME = b.LEAGUE_NAME " +
				"WHERE a.DATE >= b.DATE AND a.DATE < DATE_ADD(b.DATE,INTERVAL 1 DAY) " +
				"ORDER BY a.DATE";
		return jdbcTemplate.query(sql, new Object[]{contentId},new RowMapper<HashMap<String,String>>(){
			public HashMap<String,String> mapRow(ResultSet rs,int rowNum) throws SQLException {
				String sourceId = rs.getString("SOURCE_ID");
//				String status = rs.getString("STATUS");
				String contentId = rs.getString("CONTENT_ID");
				String homeTeamId = rs.getString("HOME_TEAM_ID");
				String visitTeamId = rs.getString("VISIT_TEAM_ID");
				String date = rs.getString("DATE");
				HashMap<String,String> map = new HashMap<String,String>();
				map.put("source_id", sourceId);
//				map.put("status", status);
				map.put("content_id", contentId);
				map.put("homeTeam", getTeamName(homeTeamId));
				map.put("visitTeam", getTeamName(visitTeamId));
				map.put("date", date);
				return map;
			}
		});
	}
	
	public String getTeamName(String teamId) {
		String sql = "SELECT t.NAME,t.DISPLAY_NAME FROM SPORT_TEAM t WHERE t.ID = ?";
		
		List<String> teams = jdbcTemplate.query(sql,new Object[]{teamId},new RowMapper<String>(){
			public String mapRow(ResultSet rs,int rowNum)throws SQLException{
				String displayName = rs.getString("DISPLAY_NAME");
				if (!StringUtils.isBlank(displayName)) {
					return displayName;
				}
				return rs.getString("NAME");
			}
		});
		if (teams.size() > 0) 
			return teams.get(0);
		else return null;
	}
	
	public int getRound(String league) {
		//两周之内的最大轮次
		long nowL = System.currentTimeMillis();
		String sql = "SELECT MAX(a.ROUND) FROM SPORT_SCHEDULE a " +
				"WHERE a.LEAGUE_NAME = ? AND a.DATE <= ? " +
				"AND ?-UNIX_TIMESTAMP(a.DATE)*1000 <= 1209600000 ";
		Date now = Calendar.getInstance().getTime();
		return jdbcTemplate.queryForInt(sql,league,now,nowL);
	}
}
