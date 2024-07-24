package com.zkd.demo.datasource.core.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public interface IParser {
    /**
     * post 从HttpServletRequest获取body并转成json格式
     */
    default JSONObject getPostParamByHSR(HttpServletRequest request) {
        StringBuffer data = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            while (null != (line = reader.readLine())) {
                data.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("HttpServletRequest解析异常");
        }
        return !StringUtils.hasLength(data.toString()) ? new JSONObject() : JSONObject.parseObject(data.toString());
    }

    /**
     * datasourceX testConn 异常解析
     */
    default String getTestConnExceptionMessage(Exception throwable) {

        String[] stackFrames = ExceptionUtils.getStackFrames(throwable);

        String other = null;
        //切割Caused by:
        for (String s1 : stackFrames) {
            if (s1.contains("Caused by")) {
                if (s1.contains("com.mysql.cj.exceptions") || s1.contains("java.sql.SQLException")
                        || s1.contains("java.net.UnknownHostException") || s1.contains("java.sql.SQLSyntaxErrorException")) {
                    return s1.substring(s1.indexOf(":", s1.indexOf(":") + 1) + 1).trim();
                }
            }
            other = s1.substring(s1.indexOf(":", s1.indexOf(":") + 1) + 1).trim();
        }
        return other;
    }
}
