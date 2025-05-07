package com.example.terravista.controller;

import com.example.terravista.entity.Poi;
import com.example.terravista.service.PoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/poi")
public class PoiController {

    private final PoiService poiService;

    @Autowired
    public PoiController(PoiService poiService) {
        this.poiService = poiService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Poi>> getAllPois() {
        return ResponseEntity.ok(poiService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poi> getPoiById(@PathVariable Long id) {
        Poi poi = poiService.findById(id);
        return poi != null ? ResponseEntity.ok(poi) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Poi>> searchPois(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) Double minLon,
            @RequestParam(required = false) Double maxLon,
            @RequestParam(required = false) Double minLat,
            @RequestParam(required = false) Double maxLat,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(poiService.search(name, province, minLon, maxLon, minLat, maxLat, pageable));
    }
}