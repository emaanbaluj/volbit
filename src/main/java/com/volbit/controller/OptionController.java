//package com.volbit.controller;
//
//import com.volbit.model.OptionRequest;
//import com.volbit.service.PriceCacheService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/options")
//public class OptionController {
//
//    private final PriceCacheService cacheService;
//    private final OptionPricingService pricingService;
//
//    public OptionController(PriceCacheService cacheService,
//                            OptionPricingService pricingService) {
//        this.cacheService = cacheService;
//        this.pricingService = pricingService;
//    }
//
//    @PostMapping("/price")
//    public Mono<OptionPrice> priceOption(@RequestBody OptionRequest req) {
//        // 1) Fetch the current spot from the cache
//        double spot = cacheService.getPrice(req.getCoinId());
//
//        // 2) Compute the option price however you’re doing it
//        double optPrice = pricingService.calculate(req, spot);
//
//        // 3) Build the response DTO
//        return Mono.just(new OptionPrice(
//                req.getCoinId(),
//                req.getStrike(),
//                req.getExpiry(),
//                req.isCall(),
//                spot,
//                optPrice,
//                Instant.now()
//        ));
//    }
//}
