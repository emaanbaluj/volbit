//package com.volbit.service;
//
//import org.springframework.stereotype.Service;
//
//import com.volbit.model.OptionRequest;
//
//@Service
//public class OptionService {
//    private final PriceCacheService cacheService;
//
//    public OptionService(PriceCacheService cacheService) {
//        this.cacheService = cacheService;
//    }
//
//    /**
//     * For now, we just return the current spot price from the cache.
//     */
//    public Double fetchCurrentSpot(OptionRequest req) {
//        return cacheService.getPrice(req.getCoinId());
//    }
//}
