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
    @Column(name = "longitude_bd")
    private Double longitudeBd; // 百度坐标系经度
    @Column(name = "latitude_bd")
    private Double latitudeBd; // 百度坐标系纬度
    @Column(name = "longitude_bd2")
    private Double longitudeBd2;
    @Column(name = "latitude_bd2")
    private Double latitudeBd2;
    private String imageUrl; // 扩展信息：图片链接
    private String officialUrl; // 扩展信息：官网地址
    private Double longitudeGcj; // 国测局坐标系经度
    private Double latitudeGcj; // 国测局坐标系纬度

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

    public Double getLongitudeGcj() {
        return longitudeGcj;
    }

    public void setLongitudeGcj(Double longitudeGcj) {
        this.longitudeGcj = longitudeGcj;
    }

    public Double getLatitudeGcj() {
        return latitudeGcj;
    }

    public void setLatitudeGcj(Double latitudeGcj) {
        this.latitudeGcj = latitudeGcj;
    }
}