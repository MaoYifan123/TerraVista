package com.example.terravista.service;

import com.example.terravista.entity.Poi;
import com.example.terravista.repository.PoiRepository;
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

    //根据区域寻找景区
    public Page<Poi> search(String name, String province, Double minLon, Double maxLon,
                            Double minLat, Double maxLat, Pageable pageable) {
        Page<Poi> pois = poiRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            if (province != null) predicates.add(cb.equal(root.get("province"), province));
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

        return pois;
    }

    public Poi savePoi(Poi poi) {
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
        return poiRepository.findAll();
    }

    // 添加按ID查找的方法
    public Poi findById(Long id) {
        return poiRepository.findById(id).orElse(null);
    }
}