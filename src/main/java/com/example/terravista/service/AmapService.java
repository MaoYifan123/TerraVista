package com.example.terravista.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AmapService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${amap.api.key}")
    private String apiKey;

    @Value("${amap.api.url:https://restapi.amap.com/v3}")
    private String baseUrl;

    public AmapService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String searchPoiImage(String name, String province) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/place/text")
                    .queryParam("key", apiKey)
                    .queryParam("keywords", name)
                    .queryParam("city", province)
                    .queryParam("citylimit", "true")
                    .queryParam("output", "json")
                    .build()
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            if (root.has("pois") && root.get("pois").isArray() && root.get("pois").size() > 0) {
                JsonNode firstPoi = root.get("pois").get(0);
                if (firstPoi.has("photos") && firstPoi.get("photos").isArray() && firstPoi.get("photos").size() > 0) {
                    return firstPoi.get("photos").get(0).get("url").asText();
                }
            }
            return null;
        } catch (Exception e) {
            // 记录错误但不中断程序
            return null;
        }
    }
} 