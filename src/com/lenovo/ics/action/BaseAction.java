package com.lenovo.ics.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lenovo.ics.common.constants.ErrorCode;

public class BaseAction implements ErrorCode {

    protected final static Logger logger = Logger.getLogger(BaseAction.class);
    
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_DESC = "errorDesc";

    public static final String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            buffer.append(line);
        }
        return buffer.toString();
    }

    protected void returnInfo(HttpServletRequest request, HttpServletResponse response, int httpStatusCode, String returnInfo) throws IOException {
        response.setStatus(httpStatusCode);
        if (returnInfo != null && returnInfo.trim().length() > 0) {
            PrintWriter out = response.getWriter();
            logger.info("返回终端数据：" + returnInfo);
            out.print(returnInfo);
            out.flush();
            out.close();
        } else {
            logger.info("本次请求无返回数据！");
        }
    }

    protected void returnInfo(HttpServletRequest request, HttpServletResponse response, int httpStatusCode) throws IOException {
        response.setStatus(httpStatusCode);
        logger.info(httpStatusCode + "本次请求无返回数据！");
    }

    protected void returnInfo(HttpServletRequest request, HttpServletResponse response, int httpStatusCode, JSONObject returnJson) throws IOException {
        if (returnJson == null || returnJson.isEmpty()) {
            returnInfo(request, response, httpStatusCode);
        } else {
            returnInfo(request, response, httpStatusCode, returnJson.toString());
        }
    }

    protected void returnInfo(HttpServletRequest request, HttpServletResponse response, int httpStatusCode, JSONArray returnJsonArray) throws IOException {
        logger.info("本次请求结果状态码：" + httpStatusCode);
        if (returnJsonArray == null || returnJsonArray.isEmpty()) {
            returnInfo(request, response, httpStatusCode);
        } else {
            returnInfo(request, response, httpStatusCode, returnJsonArray.toString());
        }
    }

}
