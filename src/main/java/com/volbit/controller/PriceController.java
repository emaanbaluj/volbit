package com.volbit.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.volbit.service.PriceService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * GET /api/price/{coinId}
     * Returns: { "coin":"bitcoin", "price":12345.67 }
     */
    @GetMapping("/price/{coinId}")
    public Mono<Map<String, Object>> getPrice(@PathVariable String coinId) {
        return priceService.getUsdPrice(coinId)            // use getUsdPrice here
                .map(price -> Map.of(
                        "coin",  coinId,
                        "price", price
                ));
    }
}
