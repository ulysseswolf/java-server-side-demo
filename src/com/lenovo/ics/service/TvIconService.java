package com.lenovo.ics.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lenovo.ics.common.constants.ErrorCode;
import com.lenovo.ics.dao.ChannelDao;
import com.lenovo.ics.dao.ProgramDao;
import com.lenovo.ics.model.ChannelInfo;
import com.lenovo.ics.model.Program;
import com.lenovo.ics.model.TvIconSearchResult;

@Service
public class TvIconService {

	private Logger logger = LoggerFactory.getLogger(TvIconService.class);
	
	@Autowired
	private ChannelDao channelDao;
	@Autowired
	private ProgramDao programDao;
	
	public List<TvIconSearchResult> search(byte[] iconData) throws ServiceException {
		try {
			
			ArrayList<String> aliases = new ArrayList<String>();
			//台标识别
//			String alias = LogoRecognition.search(iconData);
			String alias = "";
			
			
			if (!StringUtils.isBlank(alias)) {
				logger.info(String.format("LogoRecognition识别成功, 识别结果为: %1$s", alias));
				
				String[] items = alias.split(",");
				for (String item : items) {
					String[] values = item.split(":");
					aliases.add(values[0]);
				}
			} else {
				logger.error("LogoRecognition识别结果为空");
			}
			
			ArrayList<TvIconSearchResult> result = new ArrayList<TvIconSearchResult>();
			for (String val : aliases) {
				
				TvIconSearchResult info = new TvIconSearchResult();
				result.add(info);

				info.setChannelAlias(val);
				
				ChannelInfo channel = channelDao.findChannel(val);
				if (channel == null) {
					return result;
				}
				info.setChannelName(channel.getIconName());
				
				info.setChannelId(channel.getChannelId());
				
				Program program = programDao.findCurrentProgram(channel.getChannelId());
				if (program == null || program.getContentId() == null) {
					return result;
				}
				
				info.setProgramId(program.getId());
				
				
			}
			return result;
		} catch (Exception e) {
			logger.error("台标识别异常：", e);
			
			throw new ServiceException("台标识别失败！", ErrorCode.ERROR_CODE_BUSINESS);
		}
	}
	
	/**
	 * 保存上传的台标图片
	 * 路径like:./tvicon/2013-02-05_13/cctv6/uuid_cctv6.jpg
	 * 未识别图片路径:./tvicon/2013-02-05_13/unknown/uuid_.jpg
	 * @param data
	 * @param channel
	 * @throws ServiceException
	 */
	public void saveLogo(byte[] data, String channel) throws ServiceException{
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd_HH");
		Date now = Calendar.getInstance().getTime();
		
		String date = sf.format(now);
		String dir = "./tvicon/" + date + "/";
		
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String subdir = null;
		if (!channel.equals("")) {
			subdir = dir + "/" + channel + "/";
		} else subdir = dir + "/unknown/";
		
		File subF = new File(subdir);
		if (!subF.exists()) {
			subF.mkdirs();
		}
		
		dir = subdir + UUID.randomUUID() + "_" + channel + ".jpg";
		logger.info("图片保存路径:" + dir);

		final byte[] rgbData = data;
		File objFile = new File(dir);
		if (!objFile.exists()) {
			try {
				objFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(objFile);
			os.write(rgbData);
			os.flush();
			
			clean("./tvicon/");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	//删除过期文件
	private void clean(String dir) {
		File f = new File(dir);
		long nowL = System.currentTimeMillis();
		if (f.exists() && f.isDirectory()) {
			String[] a = f.list();
			for (String b : a) {
				File tmp = new File(dir+b);
				long lastModified = tmp.lastModified();
	    		if (nowL-lastModified >= 604800000) {
	    			deleteFile(tmp);
	    		}
			}
		}
	}

	//迭代文件夹删除文件
	private void deleteFile(File f) {
		if (!f.isDirectory()) {
			f.delete();
		} else {
			String[] subFiles = f.list();
			for (String s : subFiles) {
				File subF = new File(f.getAbsolutePath() + "/" + s);
				deleteFile(subF);
			}
		}
		f.delete();
	}


}
