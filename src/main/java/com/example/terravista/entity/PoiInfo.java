package com.example.terravista.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "poi_info")
public class PoiInfo {
    private static final Logger logger = LoggerFactory.getLogger(PoiInfo.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poi_id", unique = true)
    private String poiId;

    @Column(name = "name")
    private String name; // POI名称

    @Column(columnDefinition = "TEXT")
    private String description;

    @Transient
    private List<String> tags = new ArrayList<>();

    @Column(name = "tags", columnDefinition = "JSON")
    private String tagsJson;

    @Transient
    private List<String> photos = new ArrayList<>();

    @Column(name = "photos", columnDefinition = "JSON")
    private String photosJson;

    @Column(name = "website", columnDefinition = "TEXT")
    private String website; // 官网地址

    @Column(name = "rating")
    private Double rating; // 评分

    @Column(name = "price")
    private Double price; // 价格

    @Column(name = "open_time")
    private String openTime; // 开放时间

    @Column(name = "facility")
    private String facility; // 设施服务

    @Column(name = "address")
    private String address; // 地址

    @Column(name = "type")
    private String type; // 类型

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        if (tags == null || tags.isEmpty()) {
            tags = convertJsonToList(tagsJson);
        }
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        this.tagsJson = convertListToJson(tags);
    }

    public List<String> getPhotos() {
        if (photos == null || photos.isEmpty()) {
            photos = convertJsonToList(photosJson);
        }
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
        this.photosJson = convertListToJson(photos);
    }

    public String getTagsJson() {
        return tagsJson;
    }

    public void setTagsJson(String tagsJson) {
        this.tagsJson = tagsJson;
        this.tags = convertJsonToList(tagsJson);
    }

    public String getPhotosJson() {
        return photosJson;
    }

    public void setPhotosJson(String photosJson) {
        this.photosJson = photosJson;
        this.photos = convertJsonToList(photosJson);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Helper methods for JSON conversion
    @PrePersist
    @PreUpdate
    private void beforeSave() {
        if (tags != null) {
            this.tagsJson = convertListToJson(tags);
        }
        if (photos != null) {
            this.photosJson = convertListToJson(photos);
        }
    }

    private String convertListToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            logger.error("Error converting list to JSON", e);
            return "[]";
        }
    }

    private List<String> convertJsonToList(String json) {
        if (json == null || json.isEmpty() || json.equals("null")) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON to list: {}", json, e);
            return new ArrayList<>();
        }
    }
}
