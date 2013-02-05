package com.lenovo.ics.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lenovo.ics.common.ConstantValue;
import com.lenovo.ics.dao.HitShowsDao;
import com.lenovo.ics.model.ContentImg;
import com.lenovo.ics.model.ProgramDetail;
import com.lenovo.ics.model.HitShowsProgram;

@Service
public class HitShowsService {

	static final Logger logger = LoggerFactory.getLogger(HitShowsService.class);

	@Autowired
	private HitShowsDao hitShowsDao;
	
	@Autowired
	ConstantValue constantValue;
	
	@Value("${channelName.maxLength}")
	private int channelNameMaxLength;

	public List<HitShowsProgram> findRankingEPG() {

		List<ProgramDetail> epgs = hitShowsDao.findRankingEPG();
		Collections.sort(epgs);//排序

		// 查询节目相关的海报信息
		Map<String, ContentImg> images = null;

		ArrayList<HitShowsProgram> result = new ArrayList<HitShowsProgram>();
		for (ProgramDetail epg : epgs) {
			
			HitShowsProgram program = new HitShowsProgram();
			
			program.setChannelId(epg.getChannelId());
			String channelName = epg.getChannelDisplayName();
			if (channelName!=null && channelName.length()>channelNameMaxLength) {
				channelName = channelName.substring(0,channelNameMaxLength);
			}
			program.setChannelName(channelName);
			program.setContentId(epg.getContentId());
			program.setContentName(epg.getContentName());
			program.setGrade(epg.getGrade());
			program.setHeatDegree(epg.getHeatDegree());
			program.setProgramId(epg.getProgramId());
			
			if (images.containsKey(epg.getContentId())) {
				ContentImg image = images.get(epg.getContentId());
				if (image.getImgUrl().startsWith("http://")){
					program.setPosterUrl_0(image.getImgUrl());
					program.setPosterUrl_1(image.getImgUrl());
				}
			} else {
				//如果此节目无图片，过滤掉
				continue;
			}
			
			program.setStartTime(epg.getBeginTime().getTime());
			program.setEndTime(epg.getEndTime().getTime());
			
			result.add(program);
		}
		return result;
	}
}
