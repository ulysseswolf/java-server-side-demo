package com.lenovo.ics.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.dao.ProgramDao;
import com.lenovo.ics.dao.SportDao;
import com.lenovo.ics.model.BasketballMatch;
import com.lenovo.ics.model.NbaPlayerLiveInfo;

@Service
public class SportService {
	
	@Autowired
	SportDao sportDao;
	
	@Autowired
	ProgramDao programDao;
	
	@Value("${sport.team.length}")
	private int teamLength;
	
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
			
	public JSONArray otherMatch(String contentId,String sportType,int league) throws ServiceException {
        List<HashMap<String,String>> otherMatches = null;
        if (sportType.equals("nba")) {
        	otherMatches = sportDao.getOtherMatches(contentId);
        } 
        BoundHashOperations<String, String, String> userOps = null;
        Map<String,String> ball = null;
        JSONArray matchArray = new JSONArray();
        for (HashMap<String,String> matchMap : otherMatches) {
        	String sourceId = matchMap.get("source_id");
        	userOps = stringRedisTemplate.boundHashOps(sportType + ":" + sourceId);
        	ball = userOps.entries();
        	if (ball.isEmpty()) continue; 
        	BasketballMatch match = new BasketballMatch();
        	
        	String homeTeamId = ball.get("homeTeamId");
        	String visitTeamId = ball.get("visitTeamId");
        	String homeTeam = sportDao.getTeamName(homeTeamId);
        	String visitTeam = sportDao.getTeamName(visitTeamId);
        	if (homeTeam == null) {
        		homeTeam = matchMap.get("homeTeam");
        	} 
        	//截取球队名称长度
        	if (homeTeam.length() > teamLength) {
    			homeTeam = homeTeam.substring(0,teamLength);
        	}
        	match.fteam = homeTeam;
        	
        	if (visitTeam == null) {
        		visitTeam = matchMap.get("visitTeam");
        	}
        	//截取球队名称长度
        	if (visitTeam.length() > teamLength) {
        		visitTeam = visitTeam.substring(0,teamLength);
        	}
        	match.steam = visitTeam;
        	long begin = Long.valueOf(matchMap.get("date"));
        	long now = System.currentTimeMillis();
    		if (now > begin) {
    			match.status = 1;
    		} else {
    			//不返回未开始的比赛
    			match.status = 0;
    			continue;
    		}
        	
        	String score = ball.get("score");
        	if (sportType.intern() == "football") {
        		if (score != null && score.contains("-")) {
        			match.fscore = score.trim().substring(0,score.indexOf("-"));
                	match.sscore = score.trim().substring(score.indexOf("-") + 1);
        		} else {
        			match.fscore = null;
                	match.sscore = null;
        		}
        	} else {
        		JSONObject scoreJ = JSON.parseObject(score);
        		match.fscore = scoreJ.get("team1Score").toString();
            	match.sscore = scoreJ.get("team2Score").toString();
        	}
        	
        	match.contentId = matchMap.get("content_id");
        	matchArray.add(match);
        }
		return matchArray;
	}
	

	
	 
	/**
	 * 计算各队各项指标比率
	 * @param item
	 * @param team
	 * @return
	*/ 
	public static String ratingPerItem(String item, List<NbaPlayerLiveInfo> team) {
		Double made = 0d;
		Double att = 0d;
		DecimalFormat formater = null;
		formater= (DecimalFormat)NumberFormat.getPercentInstance();
		formater.applyPattern("##.0%"); 
		for (NbaPlayerLiveInfo t : team) {
			if (item.intern() == "attackRating") {
				made += Double.parseDouble(t.getFieldMade());
				att += Double.parseDouble(t.getFieldAtt());
			} else if (item.intern() == "freeThrowRating") {
				made += Double.parseDouble(t.getFreeMade());
				att += Double.parseDouble(t.getFreeAtt());
			} else if (item.intern() == "threePointRating") {
				made += Double.parseDouble(t.getThreeMade());
				att += Double.parseDouble(t.getThreeAtt());
			}
		}	
		if (att.equals(0d)) return "0.0%";
		if (made.equals(0d)) return "0.0%";
		
		return formater.format(made / att);
	}
	
	
}
