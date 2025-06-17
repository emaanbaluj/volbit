// src/main/java/com/volbit/controller/PriceCacheController.java
package com.volbit.controller;

import com.volbit.service.PriceCacheService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PriceCacheController {
    private final PriceCacheService cacheService;

    public PriceCacheController(PriceCacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * GET /api/prices
     */
    @GetMapping("/prices")
    public Map<String, Double> getAllPrices() {
        return cacheService.getAllPrices();
    }

    /** GET /api/prices/{coinId} */
    @GetMapping("/prices/{coinId}")
    public Double getPrice(@PathVariable String coinId) {
        return cacheService.getPrice(coinId);
    }
}


