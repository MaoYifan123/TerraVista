package com.example.terravista.controller;

import com.example.terravista.entity.PoiInfo;
import com.example.terravista.repository.PoiInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/poiinfo")
public class PoiInfoController {
    private static final Logger logger = LoggerFactory.getLogger(PoiInfoController.class);

    @Autowired
    private PoiInfoRepository poiInfoRepository;

    @GetMapping("/info/{poiId}")
    @RateLimiter(name = "poiInfoRateLimiter", fallbackMethod = "rateLimitFallback")
    public ResponseEntity<PoiInfo> getPoiInfo(@PathVariable String poiId) {
        logger.info("尝试获取POI信息, poiId: {}", poiId);
        PoiInfo poiInfo = poiInfoRepository.findByPoiId(poiId);
        if (poiInfo != null) {
            logger.info("成功从数据库获取POI信息, poiId: {}, name: {}", poiId, poiInfo.getName());
            return ResponseEntity.ok(poiInfo);
        }
        logger.info("数据库中未找到POI信息, poiId: {}", poiId);
        return ResponseEntity.notFound().build();
    }

    // Fallback handler for rate limit exceeded
    public ResponseEntity<PoiInfo> rateLimitFallback(String poiId, RequestNotPermitted ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @PostMapping("/put")
    public ResponseEntity<PoiInfo> savePoiInfo(@RequestBody Map<String, Object> poiInfoData) {
        String poiId = (String) poiInfoData.get("poiId");
        String name = (String) poiInfoData.get("name");
        logger.info("开始保存POI信息, poiId: {}, name: {}", poiId, name);
        System.out.println("正在插入景点POI: " + name);

        try {
            String description = (String) poiInfoData.get("description");
            List<String> tags = (List<String>) poiInfoData.get("tags");
            List<String> photos = (List<String>) poiInfoData.get("photos");
            String website = (String) poiInfoData.get("website");
            Double rating = poiInfoData.get("rating") != null ? Double.parseDouble(poiInfoData.get("rating").toString())
                    : null;
            Double price = poiInfoData.get("price") != null ? Double.parseDouble(poiInfoData.get("price").toString())
                    : null;
            String openTime = (String) poiInfoData.get("openTime");
            String facility = (String) poiInfoData.get("facility");
            String address = (String) poiInfoData.get("address");
            String type = (String) poiInfoData.get("type");

            // 打印接收到的数据详情
            logger.info("接收到的POI数据详情:");
            logger.info("- 描述: {}", description);
            logger.info("- 标签数量: {}", tags != null ? tags.size() : 0);
            logger.info("- 照片数量: {}", photos != null ? photos.size() : 0);
            logger.info("- 官网: {}", website);
            logger.info("- 评分: {}", rating);
            logger.info("- 价格: {}", price);
            logger.info("- 开放时间: {}", openTime);
            logger.info("- 设施: {}", facility);
            logger.info("- 地址: {}", address);
            logger.info("- 类型: {}", type);

            PoiInfo existingPoiInfo = poiInfoRepository.findByPoiId(poiId);
            PoiInfo poiInfo;

            if (existingPoiInfo != null) {
                logger.info("更新已存在的POI信息, poiId: {}, name: {}", poiId, name);
                existingPoiInfo.setName(name);
                existingPoiInfo.setDescription(description);
                existingPoiInfo.setTags(tags);
                existingPoiInfo.setPhotos(photos);
                existingPoiInfo.setWebsite(website);
                existingPoiInfo.setRating(rating);
                existingPoiInfo.setPrice(price);
                existingPoiInfo.setOpenTime(openTime);
                existingPoiInfo.setFacility(facility);
                existingPoiInfo.setAddress(address);
                existingPoiInfo.setType(type);
                poiInfo = existingPoiInfo;
            } else {
                logger.info("创建新的POI信息, poiId: {}, name: {}", poiId, name);
                poiInfo = new PoiInfo();
                poiInfo.setPoiId(poiId);
                poiInfo.setName(name);
                poiInfo.setDescription(description);
                poiInfo.setTags(tags);
                poiInfo.setPhotos(photos);
                poiInfo.setWebsite(website);
                poiInfo.setRating(rating);
                poiInfo.setPrice(price);
                poiInfo.setOpenTime(openTime);
                poiInfo.setFacility(facility);
                poiInfo.setAddress(address);
                poiInfo.setType(type);
            }

            PoiInfo savedPoiInfo = poiInfoRepository.save(poiInfo);
            System.out.println("插入景点POI: " + name + " 成功");

            // 打印保存成功后的详细信息
            logger.info("POI信息保存成功:");
            logger.info("- ID: {}", savedPoiInfo.getId());
            logger.info("- POI ID: {}", savedPoiInfo.getPoiId());
            logger.info("- 名称: {}", savedPoiInfo.getName());
            logger.info("- 描述长度: {}",
                    savedPoiInfo.getDescription() != null ? savedPoiInfo.getDescription().length() : 0);
            logger.info("- 标签数量: {}", savedPoiInfo.getTags() != null ? savedPoiInfo.getTags().size() : 0);
            logger.info("- 照片数量: {}", savedPoiInfo.getPhotos() != null ? savedPoiInfo.getPhotos().size() : 0);
            logger.info("- 官网: {}", savedPoiInfo.getWebsite());
            logger.info("- 评分: {}", savedPoiInfo.getRating());
            logger.info("- 价格: {}", savedPoiInfo.getPrice());
            logger.info("- 开放时间: {}", savedPoiInfo.getOpenTime());
            logger.info("- 设施: {}", savedPoiInfo.getFacility());
            logger.info("- 地址: {}", savedPoiInfo.getAddress());
            logger.info("- 类型: {}", savedPoiInfo.getType());

            return ResponseEntity.ok(savedPoiInfo);
        } catch (Exception e) {
            logger.error("保存POI信息失败, poiId: {}, name: {}, error: {}", poiId, name, e.getMessage());
            logger.error("错误详情:", e);
            System.out.println("插入景点POI: " + name + " 失败: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/info/{poiId}")
    public ResponseEntity<PoiInfo> updatePoiInfo(@PathVariable String poiId,
            @RequestBody Map<String, Object> poiInfoData) {
        logger.info("尝试更新POI信息, poiId: {}", poiId);
        String name = (String) poiInfoData.get("name");
        System.out.println("正在更新景点POI: " + name);

        PoiInfo existingPoiInfo = poiInfoRepository.findByPoiId(poiId);
        if (existingPoiInfo != null) {
            try {
                String description = (String) poiInfoData.get("description");
                List<String> tags = (List<String>) poiInfoData.get("tags");
                List<String> photos = (List<String>) poiInfoData.get("photos");
                String website = (String) poiInfoData.get("website");
                Double rating = poiInfoData.get("rating") != null
                        ? Double.parseDouble(poiInfoData.get("rating").toString())
                        : null;
                Double price = poiInfoData.get("price") != null
                        ? Double.parseDouble(poiInfoData.get("price").toString())
                        : null;
                String openTime = (String) poiInfoData.get("openTime");
                String facility = (String) poiInfoData.get("facility");
                String address = (String) poiInfoData.get("address");
                String type = (String) poiInfoData.get("type");

                existingPoiInfo.setName(name);
                existingPoiInfo.setDescription(description);
                existingPoiInfo.setTags(tags);
                existingPoiInfo.setPhotos(photos);
                existingPoiInfo.setWebsite(website);
                existingPoiInfo.setRating(rating);
                existingPoiInfo.setPrice(price);
                existingPoiInfo.setOpenTime(openTime);
                existingPoiInfo.setFacility(facility);
                existingPoiInfo.setAddress(address);
                existingPoiInfo.setType(type);

                PoiInfo updatedPoiInfo = poiInfoRepository.save(existingPoiInfo);
                logger.info("POI信息更新成功, poiId: {}, name: {}", poiId, name);
                System.out.println("更新景点POI: " + name + " 成功");
                return ResponseEntity.ok(updatedPoiInfo);
            } catch (Exception e) {
                logger.error("更新POI信息失败, poiId: {}, name: {}, error: {}", poiId, name, e.getMessage());
                System.out.println("更新景点POI: " + name + " 失败: " + e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }
        logger.info("未找到要更新的POI信息, poiId: {}", poiId);
        System.out.println("更新景点POI: " + name + " 失败: 未找到此POI");
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all-tags")
    public ResponseEntity<List<String>> getAllTags() {
        logger.info("获取所有POI标签");
        List<String> allTags = poiInfoRepository.findAllTags();
        logger.info("成功获取所有POI标签, 数量: {}", allTags.size());
        return ResponseEntity.ok(allTags);
    }
}
