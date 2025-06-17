package com.volbit.controller;

import com.volbit.service.PriceCacheService;
import org.springframework.web.bind.annotation.GetMapping;
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
     * Returns a JSON like:
     * { "bitcoin": 104000.5, "ethereum": 3100.2, ... }
     */
    @GetMapping("/prices")
    public Map<String, Double> getAllPrices() {
        return cacheService.getAllPrices();
    }
}
