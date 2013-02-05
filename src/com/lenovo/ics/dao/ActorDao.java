package com.lenovo.ics.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.lenovo.ics.common.PageModel;
import com.lenovo.ics.model.ActorInfo;

@Repository("ActorDao")
public class ActorDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	private final String TABLE = "ACTOR_INFO";

	/**
	 * 根据演员姓名查询演员信息
	 * 
	 * @param actorName
	 * @return
	 */
	public List<ActorInfo> getActorInfoByName(String actorName) {
		String sql = "SELECT ACTOR_ID,NAME FROM " + TABLE + " T1 WHERE T1.NAME LIKE '%" + actorName + "%' ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<ActorInfo>());
	}
	
	/**
	 * 查询单个对象
	 * @param id
	 * @return
	 */
	public ActorInfo getActorInfo(int id){
		String sql = "SELECT ACTOR_ID,NAME FROM " + TABLE + " T1 WHERE T1.ACTOR_ID = ?";
		return (ActorInfo)jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<ActorInfo>(ActorInfo.class), id);
	}

	/**
	 * 根据演员ID删除演员信息
	 */
	public void deleteActor(String actorId) {
		String sql = "DELETE FROM " + TABLE + " T1 WHERE ID = ?";
		jdbcTemplate.update(sql, actorId);
	}

	/**
	 * 根据ID查找演员海报
	 * 
	 * @param actorID
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("rawtypes")
	public List findActorPoster(Integer actorID) {
		String sql = "SELECT * FROM ACTOR_POSTER T1" + " WHERE  T1.ACTOR_ID = ? ";
		return jdbcTemplate.queryForList(sql, new Object[] { actorID });
	}

	public int count(String name) {
		String sql = "SELECT COUNT(1) FROM ACTOR_INFO WHERE NAME LIKE  '%" + name + "%'";
		return jdbcTemplate.queryForInt(sql);
	}

	//分页
	public List<ActorInfo> query(String name) {
		PageModel pageModel = PageModel.get();
		List<ActorInfo> actors;
		String sql;
		if (pageModel != null) {
			int count = count(name);
			pageModel.reInit(count);
			//处理有分页的sql语句查询
			sql = "SELECT ACTOR_ID,NAME FROM ACTOR_INFO WHERE NAME LIKE '%" + name + "%' LIMIT ?,?";
			actors = jdbcTemplate.query(sql,new BeanPropertyRowMapper<ActorInfo>(ActorInfo.class),pageModel.getIndex(),pageModel.getPageSize());
		}
		////处理没有分页的sql语句查询
		sql = "SELECT ACTOR_ID,NAME FROM ACTOR_INFO WHERE NAME LIKE '%" + name + "%' ";
		actors = jdbcTemplate.query(sql, new BeanPropertyRowMapper<ActorInfo>(ActorInfo.class));
		
		return actors;
	}

}
