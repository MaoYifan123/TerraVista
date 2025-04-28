package com.example.terravista.repository;

import com.example.terravista.entity.Poi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoiRepository extends JpaRepository<Poi, Long>, JpaSpecificationExecutor<Poi> {
    // 可以添加自定义查询方法
    List<Poi> findByProvince(String province);
    List<Poi> findByCategory(String category);

}