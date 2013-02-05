package com.lenovo.ics.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.ConstantValue;
import com.lenovo.ics.common.ImageHelper;
import com.lenovo.ics.common.constants.Constants;
import com.lenovo.ics.common.constants.ErrorCode;
import com.lenovo.ics.model.TvIconSearchResult;
import com.lenovo.ics.service.ServiceException;
import com.lenovo.ics.service.TvIconService;

@Controller
public class TvIconAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(TvIconAction.class);

	@Autowired
	TvIconService tvIconService;

	@Autowired
	ConstantValue constantValue;
	
	/**
	 * 台标识别接口，终端在BODY中上传图片数据
	 */
	@RequestMapping(value = "/1.0/outer/channel/identify")
	public void recognition(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject returnJson = new JSONObject();
		int httpStatusCode = HttpURLConnection.HTTP_OK;
		logger.info("终端开始获取当前频道信息");
		logger.info("终端ip：" + request.getRemoteHost());
		try {
			byte[] iconData = toByteArray(request.getInputStream());
			if (iconData == null || iconData.length > Constants.TV_ICON_SIZE ) {
				throw new ServiceException("请求参数错误", ERROR_CODE_PARAM_ERROR);
			}
			if (iconData.length > 0) {
				InputStream in = new ByteArrayInputStream(iconData);
				logger.info("客户端图片数据大小：" + in.available());
				BufferedImage srcImage = ImageIO.read(in);
				logger.info("本地生成图片数据大小:" + srcImage);
				
				BufferedImage bi = ImageHelper.resize(srcImage, constantValue.getTvIconWidth(), constantValue.getTvIconHeight(), false);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(bi,constantValue.getTvIconType(),out);
				
				iconData = out.toByteArray();
				List<TvIconSearchResult> result = tvIconService.search(iconData);
				String channel = "";
				if (result.size() > 0) {
					channel = result.get(0).getChannelName();
				}
				tvIconService.saveLogo(iconData,channel);
				JSONArray array = (JSONArray) JSON.toJSON(result);
				returnJson.put("list", array);
			} else {
				logger.error("参数错误，无图像数据");
				returnJson.put(ERROR_CODE, ErrorCode.ERROR_CODE_BUSINESS);
				returnJson.put(ERROR_DESC, "参数错误，无图像数据");
				httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
			}
			logger.info("当前频道信息结束，返回终端数据");
		} catch (ServiceException e) {
			logger.error("业务处理异常：", e);
			returnJson.put(ERROR_CODE, e.getErrorCode());
			returnJson.put(ERROR_DESC, e.getMessage());
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		} catch (Exception e) {
			logger.error("未知错误,错误信息为：" , e);
			returnJson.put("errorDesc", "未知错误！");
			returnJson.put(ERROR_CODE, ErrorCode.ERROR_CODE_BUSINESS);
			httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
		}
		returnInfo(request, response, httpStatusCode, returnJson);
	}

	byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] buff = new byte[8192];
		int len = -1;
		while ((len = in.read(buff)) != -1) {
			buffer.write(buff, 0, len);
		}
		return buffer.toByteArray();
	}

}