package com.volbit.controller;

import com.volbit.service.LivePriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LivePriceController {
    private final LivePriceService live;

    public LivePriceController(LivePriceService live) {
        this.live = live;
    }

    @GetMapping("/price/{symbol}")
    public Mono<ResponseEntity<Map<String, Serializable>>> price(@PathVariable String symbol) {
        return Mono.justOrEmpty(live.get(symbol.toLowerCase()))
                .map(t -> ResponseEntity.ok(Map.<String, Serializable>of(
                        "symbol",    t.getSymbol(),
                        "price",     t.getPrice(),
                        "timestamp", t.getTimestamp()
                )))
                .defaultIfEmpty(ResponseEntity
                        .status(404)
                        .body(Map.<String, Serializable>of("error", "no tick yet"))
                );
    }

}
