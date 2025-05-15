package com.example.terravista.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class BaiDuController {
    private static final Logger logger = LoggerFactory.getLogger(BaiDuController.class);

    @Value("${baidu.api.key}")
    private String bochaApiKey;

    // @Value("${baidu.secret.key}")
    // private String baiduSecretKey;

    private final RestTemplate restTemplate;

    public BaiDuController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/baidu")
    public ResponseEntity<?> searchBaidu(@RequestParam String query) {
        try {
            logger.info("Searching Bocha for query: {}", query);

            // 构建请求URL
            String url = "https://api.bocha.io/v1/search";

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + bochaApiKey);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("page", 1);
            requestBody.put("pageSize", 10);

            // 发送请求
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            logger.debug("Sending request to Bocha API with body: {}", requestBody);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class);

            // 处理响应
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> results = new ArrayList<>();

                // 提取搜索结果
                if (responseBody.containsKey("data") && responseBody.get("data") instanceof List) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");
                    for (Map<String, Object> item : data) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("title", item.get("title"));
                        result.put("url", item.get("url"));
                        result.put("description", item.get("description"));
                        results.add(result);
                    }
                }

                logger.info("Successfully retrieved {} results from Bocha", results.size());
                return ResponseEntity.ok(Map.of("results", results));
            }

            logger.error("Failed to get search results from Bocha. Status: {}", response.getStatusCode());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to get search results"));

        } catch (Exception e) {
            logger.error("Error searching Bocha: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
