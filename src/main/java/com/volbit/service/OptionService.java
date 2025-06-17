//// src/main/java/com/volbit/service/OptionService.java
//package com.volbit.service;
//
//import com.volbit.model.Option;
//import com.volbit.model.OptionRequest;
//import com.volbit.model.Price;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OptionService {
//
//    private final PriceCacheService cacheService;
//
//    public OptionService(PriceCacheService cacheService) {
//        this.cacheService = cacheService;
//    }
//
//    /**
//     * Fetches the current spot price from the cache and returns
//     * a partial Option object (optionPrice = 0 for now).
//     */
//    public OptionRequest fetchCurrentOptionRequest(OptionRequest req) {
//        double spotPrice = cacheService.getAllPrices().(req.getCoinId(), 0.0);
//
//        return new OptionRequest(
//                req.getCoinId(),
//                req.getStrike(),
//                req.getExpiry(),
//                req.isCall(),
//                spot.getPrice(),     // underlying spot
//                0.0,                 // option price placeholder
//                spot.getTimestamp()  // original timestamp
//        );
//    }
//}
