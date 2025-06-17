package com.volbit.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceCacheService {
    private final ConcurrentHashMap<String, Double> latest = new ConcurrentHashMap<>();

    public void updatePrice(String coinId, Double price) {
        latest.put(coinId, price);
    }

    public Map<String, Double> getAllPrices() {
        return Collections.unmodifiableMap(latest);
    }

    /** New: return just one coin’s latest price (or null if not yet polled) */
    public Double getPrice(String coinId) {
        return latest.get(coinId);
    }
}
