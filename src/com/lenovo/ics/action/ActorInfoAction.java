package com.lenovo.ics.action;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import javax.servlet.ServletException;
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
import com.lenovo.ics.common.PageModel;
import com.lenovo.ics.dao.ActorDao;
import com.lenovo.ics.model.ActorInfo;


@Controller
public class ActorInfoAction extends BaseAction {
    
    private static Logger logger = LoggerFactory.getLogger(ActorInfoAction.class);
    
    @Autowired
    private ActorDao actorDao;
    
    @RequestMapping(value = "/actor/search")
    public void search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        JSONObject returnJson = new JSONObject();
        JSONArray array = new JSONArray();
        int httpStatusCode = HttpURLConnection.HTTP_OK;
        logger.info("终端开始获取演员信息内容列表");
        try {
            String requestJson = readRequestBody(request);
            logger.info("演员信息参数为：" + requestJson);
            
            JSONObject oJSONObject = JSON.parseObject(requestJson);
            
            //封装分页参数，供DAO查询使用
            PageModel.readFromJson(oJSONObject);

            String actorName = oJSONObject.getString("actorName");
//          int id = oJSONObject.getIntValue("actorId");
            List<ActorInfo> list = actorDao.getActorInfoByName(actorName);
//            ActorInfo actorInfoList = actorDao.getActorInfo(id);
            array = (JSONArray)JSON.toJSON(list);
//            returnJson = (JSONObject) JSON.toJSON(actorInfoList);
            returnJson.put("list", array);
            //获取分页参数
            PageModel.writeToJson(returnJson);
            
            logger.info("演员信息查询结束，返回终端数据；");
            httpStatusCode = HttpURLConnection.HTTP_OK;
        }catch(NumberFormatException ne) {
            logger.error("演员信息获取参数错误,错误信息为：" + ne.toString());
            returnJson.put("errorDesc","演员信息获取参数错误！");
            httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
        } catch(Exception e) {
            logger.error("未知错误,错误信息为：" + e.toString());
            returnJson.put("errorDesc","未知错误！");
            httpStatusCode = HttpURLConnection.HTTP_PAYMENT_REQUIRED;
        }
        returnInfo(request, response, httpStatusCode, returnJson);
    }
    
}