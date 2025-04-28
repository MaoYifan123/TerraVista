package com.example.terravista.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pois")
public class Poi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String province;
    private String category; // 5A景区
    private Double longitudeBd;
    private Double latitudeBd;
    private Double longitudeBd2;
    private Double latitudeBd2;
    private String imageUrl;  // 扩展信息：图片链接
    private String officialUrl; // 扩展信息：官网地址

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getLongitudeBd() {
        return longitudeBd;
    }

    public void setLongitudeBd(Double longitudeBd) {
        this.longitudeBd = longitudeBd;
    }

    public Double getLatitudeBd() {
        return latitudeBd;
    }

    public void setLatitudeBd(Double latitudeBd) {
        this.latitudeBd = latitudeBd;
    }

    public Double getLongitudeBd2() {
        return longitudeBd2;
    }

    public void setLongitudeBd2(Double longitudeBd2) {
        this.longitudeBd2 = longitudeBd2;
    }

    public Double getLatitudeBd2() {
        return latitudeBd2;
    }

    public void setLatitudeBd2(Double latitudeBd2) {
        this.latitudeBd2 = latitudeBd2;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOfficialUrl() {
        return officialUrl;
    }

    public void setOfficialUrl(String officialUrl) {
        this.officialUrl = officialUrl;
    }
}