package com.example.terravista.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class OpenAIController {

        @Value("${ai.config.deepseek.apiKey}")
        private String apiKey;

        @Value("${ai.config.deepseek.baseUrl}")
        private String baseUrl;

        private final RestTemplate restTemplate = new RestTemplate();

        @GetMapping("/tags")
        public ResponseEntity<Map<String, List<String>>> getTags() {
                Map<String, List<String>> tags = new HashMap<>();

                // 省份标签
                tags.put("provinces", Arrays.asList(
                                "北京", "上海", "广东", "江苏", "浙江", "四川", "云南", "西藏", "新疆", "内蒙古"));

                // 山水标签
                tags.put("landscape", Arrays.asList(
                                "山川", "湖泊", "海洋", "森林", "草原", "沙漠", "峡谷", "瀑布", "温泉"));

                // 人文标签
                tags.put("culture", Arrays.asList(
                                "历史古迹", "宗教寺庙", "民俗文化", "美食", "艺术", "博物馆", "古镇", "现代都市"));

                return ResponseEntity.ok(tags);
        }

        @PostMapping("/recommend")
        public ResponseEntity<?> getRecommendations(@RequestBody Map<String, Object> request) {
                try {
                        List<String> provinces = (List<String>) request.get("province");
                        List<String> landscapeTags = (List<String>) request.get("landscape");
                        List<String> cultureTags = (List<String>) request.get("culture");
                        String additionalInfo = (String) request.get("additionalInfo");

                        if (provinces == null || provinces.isEmpty()) {
                                return ResponseEntity.badRequest().body(Map.of("error", "请至少选择一个省份"));
                        }

                        // 构建提示词
                        StringBuilder prompt = new StringBuilder();
                        prompt.append("作为一个旅游推荐专家，请根据以下条件推荐3个最适合的旅游景点：\n");
                        prompt.append("省份：").append(String.join("、", provinces)).append("\n");

                        if (landscapeTags != null && !landscapeTags.isEmpty()) {
                                prompt.append("景观偏好：").append(String.join("、", landscapeTags)).append("\n");
                        }

                        if (cultureTags != null && !cultureTags.isEmpty()) {
                                prompt.append("文化偏好：").append(String.join("、", cultureTags)).append("\n");
                        }

                        if (additionalInfo != null && !additionalInfo.trim().isEmpty()) {
                                prompt.append("其他需求：").append(additionalInfo).append("\n");
                        }

                        prompt.append("请以JSON格式返回推荐结果，包含景点名称、推荐理由、最佳游玩时间、交通建议等信息。");

                        // 构建请求体
                        Map<String, Object> requestBody = new HashMap<>();
                        requestBody.put("model", "deepseek-chat");
                        requestBody.put("messages", Arrays.asList(
                                        Map.of("role", "system", "content", "你是一个专业的旅游推荐助手，擅长根据用户偏好推荐合适的旅游景点。"),
                                        Map.of("role", "user", "content", prompt.toString())));
                        requestBody.put("temperature", 0.7);
                        requestBody.put("max_tokens", 1000);

                        // 设置请求头
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set("Authorization", "Bearer " + apiKey);

                        // 发送请求
                        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                        ResponseEntity<Map> response = restTemplate.exchange(
                                        baseUrl,
                                        HttpMethod.POST,
                                        entity,
                                        Map.class);

                        return ResponseEntity.ok(response.getBody());
                } catch (Exception e) {
                        e.printStackTrace(); // 添加这行来打印详细错误信息
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "获取推荐失败：" + e.getMessage()));
                }
        }
}
