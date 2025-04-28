package com.example.terravista.controller;

import com.example.terravista.entity.Poi;
import com.example.terravista.service.PoiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/admin")
@io.github.resilience4j.ratelimiter.annotation.RateLimiter(name = "apiRateLimiter")
public class AdminController {
    private final PoiService poiService;

    @Autowired
    public AdminController(PoiService poiService) {
        this.poiService = poiService;
    }

    @PostMapping("/poi")
    public ResponseEntity<?> addPoi(@Valid @RequestBody Poi poi) {
        try {
            return ResponseEntity.ok(poiService.savePoi(poi));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", e.getMessage())
            );
        }
    }

    @DeleteMapping("/poi/{id}")
    public ResponseEntity<?> deletePoi(@PathVariable Long id) {
        try {
            poiService.deletePoi(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Collections.singletonMap("error", e.getMessage())
            );
        }
    }
}