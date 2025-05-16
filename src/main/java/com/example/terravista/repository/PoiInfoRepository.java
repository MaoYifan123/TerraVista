package com.example.terravista.repository;

import com.example.terravista.entity.PoiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoiInfoRepository extends JpaRepository<PoiInfo, Long> {
    PoiInfo findByPoiId(String poiId);

    @Query(value = "SELECT DISTINCT json_unquote(json_extract(tags, '$[*]')) FROM poi_info WHERE tags IS NOT NULL", nativeQuery = true)
    List<String> findAllTags();
}
