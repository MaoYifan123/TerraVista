package com.example.terravista.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "poi_info")
public class PoiInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poi_id", unique = true)
    private String poiId;

    @Column(name = "name")
    private String name; // POI名称

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "poi_tags", joinColumns = @JoinColumn(name = "poi_info_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ElementCollection
    @CollectionTable(name = "poi_photos", joinColumns = @JoinColumn(name = "poi_info_id"))
    @Column(name = "photo_url", columnDefinition = "TEXT")
    private List<String> photos;

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
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
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
}
