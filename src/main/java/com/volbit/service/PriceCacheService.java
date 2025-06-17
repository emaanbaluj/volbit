package com.volbit.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores the latest USD price for each coin in memory.
 */
@Service
public class PriceCacheService {

    private final ConcurrentHashMap<String, Double> latestPrices = new ConcurrentHashMap<>();

    /**
     * Called by PricePoller to update the in-memory cache.
     */
    public void updatePrice(String coinId, Double price) {
        latestPrices.put(coinId, price);
    }

    /**
     * Return an unmodifiable snapshot of all latest prices.
     */
    public Map<String, Double> getAllPrices() {
        return Collections.unmodifiableMap(latestPrices);
    }
}
