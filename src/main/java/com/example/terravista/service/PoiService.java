package com.example.terravista.service;

import com.example.terravista.entity.Poi;
import com.example.terravista.repository.PoiRepository;
import com.example.terravista.util.CoordinateConverter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PoiService {
    private final PoiRepository poiRepository;
    private final AmapService amapService;

    @Autowired
    public PoiService(PoiRepository poiRepository, AmapService amapService) {
        this.poiRepository = poiRepository;
        this.amapService = amapService;
    }

    // 根据区域寻找景区
    public Page<Poi> search(String name, String province, Double minLon, Double maxLon,
                            Double minLat, Double maxLat, Pageable pageable) {
        Page<Poi> pois = poiRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null)
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            if (province != null)
                predicates.add(cb.equal(root.get("province"), province));
            if (minLon != null && maxLon != null)
                predicates.add(cb.between(root.get("longitudeBd"), minLon, maxLon));
            if (minLat != null && maxLat != null)
                predicates.add(cb.between(root.get("latitudeBd"), minLat, maxLat));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        // 为每个POI获取图片
        pois.getContent().forEach(poi -> {
            if (poi.getImageUrl() == null || poi.getImageUrl().isEmpty()) {
                String imageUrl = amapService.searchPoiImage(poi.getName(), poi.getProvince());
                if (imageUrl != null) {
                    poi.setImageUrl(imageUrl);
                    poiRepository.save(poi);
                }
            }
        });

        // 确保所有POI都有GCJ-02坐标
        pois.getContent().forEach(this::ensureGcjCoordinates);

        return pois;
    }

    public Poi savePoi(Poi poi) {
        // 如果只有百度坐标，计算GCJ-02坐标
        if (poi.getLongitudeBd() != null && poi.getLatitudeBd() != null) {
            double[] gcj02 = CoordinateConverter.bd09ToGcj02(poi.getLongitudeBd(), poi.getLatitudeBd());
            poi.setLongitudeGcj(gcj02[0]);
            poi.setLatitudeGcj(gcj02[1]);
        }
        // 如果POI没有图片，尝试从高德地图获取
        if (poi.getImageUrl() == null || poi.getImageUrl().isEmpty()) {
            String imageUrl = amapService.searchPoiImage(poi.getName(), poi.getProvince());
            if (imageUrl != null) {
                poi.setImageUrl(imageUrl);
            }
        }
        return poiRepository.save(poi);
    }

    public void deletePoi(Long id) {
        poiRepository.deleteById(id);
    }

    // 添加获取所有POI的方法
    public List<Poi> findAll() {
        List<Poi> pois = poiRepository.findAll();
        // 确保所有POI都有GCJ-02坐标
        pois.forEach(this::ensureGcjCoordinates);
        return pois;
    }

    // 添加按ID查找的方法
    public Poi findById(Long id) {
        Poi poi = poiRepository.findById(id).orElse(null);
        if (poi != null) {
            ensureGcjCoordinates(poi);
        }
        return poi;
    }

    /**
     * 确保POI有GCJ-02坐标，如果没有则从百度坐标转换
     */
    private void ensureGcjCoordinates(Poi poi) {
        if (poi.getLongitudeGcj() == null || poi.getLatitudeGcj() == null) {
            if (poi.getLongitudeBd() != null && poi.getLatitudeBd() != null) {
                double[] gcj02 = CoordinateConverter.bd09ToGcj02(poi.getLongitudeBd(), poi.getLatitudeBd());
                poi.setLongitudeGcj(gcj02[0]);
                poi.setLatitudeGcj(gcj02[1]);
                // 保存更新后的坐标
                poiRepository.save(poi);
            }
        }
    }
}