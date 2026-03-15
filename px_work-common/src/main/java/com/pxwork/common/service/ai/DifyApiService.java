package com.pxwork.common.service.ai;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class DifyApiService {

    @Value("${ai.dify.base-url}")
    private String baseUrl;

    @Value("${ai.dify.api-key}")
    private String apiKey;

    public Map<String, Object> gradeSubjectiveAnswer(String question, String standardAnswer, String studentAnswer) {
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", question);
        inputs.put("standard_answer", standardAnswer);
        inputs.put("student_answer", studentAnswer);

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", inputs);
        body.put("response_mode", "blocking");
        body.put("user", "px_work_system");

        try {
            HttpResponse response = HttpRequest.post(baseUrl + "/workflows/run")
                    .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                    .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                    .contentType(ContentType.JSON.getValue())
                    .body(JSON.toJSONString(body))
                    .timeout(30000)
                    .execute();
            if (response.getStatus() != 200) {
                throw new RuntimeException("AI判卷服务调用失败");
            }
            String responseBody = response.body();
            log.info("dify grade response: {}", responseBody);
            Map<String, Object> responseMap = JSON.parseObject(responseBody, new TypeReference<Map<String, Object>>() {});
            Object dataObj = responseMap.get("data");
            if (!(dataObj instanceof Map<?, ?> dataMapObj)) {
                throw new RuntimeException("AI判卷服务调用失败");
            }
            Object outputsObj = dataMapObj.get("outputs");
            if (!(outputsObj instanceof Map<?, ?> outputsMapObj)) {
                throw new RuntimeException("AI判卷服务调用失败");
            }
            Map<String, Object> result = new HashMap<>();
            result.put("score", outputsMapObj.get("score"));
            result.put("comment", outputsMapObj.get("comment"));
            return result;
        } catch (Exception e) {
            throw new RuntimeException("AI判卷服务调用失败");
        }
    }
}
