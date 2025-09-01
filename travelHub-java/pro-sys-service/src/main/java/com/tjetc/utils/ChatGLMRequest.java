package com.tjetc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjetc.common.guide.vo.param.GuideParam;
import com.tjetc.entity.Guide;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatGLMRequest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Value("${url}")  // 必须的参数准备
    private String url;
    @Value("${token}")
    private String token;
    @Value("${aiNo}")
    private String aiNo;

    public Boolean LinkAi(Guide guide) {
        // 1. 构建标准JSON请求体（基于MyBatis参数绑定原理[4](@ref)）
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("assistant_id", aiNo);
        requestMap.put("prompt", buildPrompt(guide));  // 根据Guide对象生成内容

        try {
            HttpURLConnection con = buildConnection();
            sendRequest(con, mapper.writeValueAsString(requestMap));  // 自动处理JSON转义[7](@ref)
            return processResponse(con);
        } catch (IOException e) {
            logError("请求异常: " + e.getMessage());
            return true;  // 失败策略根据业务需求调整
        }
    }

    private String buildPrompt(Guide guide) {
        // 2. 内容清洗（参考JSON存储规范[6,7](@ref)）
        String processedTitle = Optional.ofNullable(guide.getTitle())
                .orElse("")
                .replace("\"", "\\\"")
                .replace("\n", " ");

        String processedContent = Optional.ofNullable(guide.getContent())
                .orElse("")
                .replace("\n", "\\n")    // 转义换行符
                .replace("\"", "\\\"")    // 转义双引号
                .replaceAll("<[^>]+>", "");  // 过滤HTML标签[6](@ref)

        return String.format("帖子审核：%s%n%s",
                processedTitle,
                processedContent);
    }

    private HttpURLConnection buildConnection() throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // 3. 完善请求头配置[7](@ref)
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setRequestProperty("Authorization", token);
        con.setDoOutput(true);
        con.setConnectTimeout(5000);  // 添加超时设置
        con.setReadTimeout(10000);
        return con;
    }

    private void sendRequest(HttpURLConnection con, String jsonBody) throws IOException {
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(jsonBody.getBytes("UTF-8"));  // 指定编码[7](@ref)
            wr.flush();
            logDebug("请求体：" + jsonBody);  // 敏感信息需脱敏
        }
    }

    private Boolean processResponse(HttpURLConnection con) throws IOException {
        int code = con.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            return parseSuccessResponse(con);
        } else {
            logError("请求失败，状态码：" + code + "，错误详情：" + readErrorStream(con));
            return true;  // 失败默认通过策略
        }
    }

    private Boolean parseSuccessResponse(HttpURLConnection con) throws IOException {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            logDebug("AI审核结果：" + response);
            boolean contains = response.toString().contains("AI备注：审核未通过");
            if (contains){
                return false;
            }else {
                return true;
            }
        }
    }

    private String readErrorStream(HttpURLConnection con) {
        try (BufferedReader err = new BufferedReader(
                new InputStreamReader(con.getErrorStream(), "UTF-8"))) {
            return String.join("\n", err.lines().toArray(String[]::new));
        } catch (IOException e) {
            return "无法读取错误流";
        }
    }

    private void logDebug(String message) {
        System.out.println("[DEBUG] " + message);  // 建议替换为Logger
    }

    private void logError(String message) {
        System.err.println("[ERROR] " + message);
    }
}