package com.example.terravista.repository;

import com.example.terravista.entity.PoiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoiInfoRepository extends JpaRepository<PoiInfo, Long> {
    PoiInfo findByPoiId(String poiId);

    @Query("SELECT DISTINCT t FROM PoiInfo p JOIN p.tags t")
    List<String> findAllTags();
}
