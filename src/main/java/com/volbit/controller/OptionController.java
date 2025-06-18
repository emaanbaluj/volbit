package com.volbit.controller;

import com.volbit.model.OptionRequest;
import com.volbit.model.PricingRequest;
import com.volbit.pricing.BinomialTreeModel;
import com.volbit.pricing.BlackScholesModel;
import com.volbit.pricing.MonteCarloModel;
import com.volbit.service.LivePriceService;
import com.volbit.service.BinanceWebSocketService.PriceTick;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/options")
public class OptionController {

    private final LivePriceService live;

    public OptionController(LivePriceService live) {
        this.live = live;
    }

    private Mono<Double> fetchSpot(String symbol) {
        return Mono.justOrEmpty(live.get(symbol.toLowerCase()))
                .map(PriceTick::getPrice)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Unknown symbol " + symbol)));
    }

    @PostMapping("/bs")
    public Mono<ResponseEntity<?>> blackScholes(@RequestBody PricingRequest req) {
        BlackScholesModel bs = new BlackScholesModel(req.riskFreeRate(), req.volatility());
        return fetchSpot(req.symbol())
                .map(spot -> {
                    var or = new OptionRequest(req.strike(), req.timeToExpiry(), req.call());
                    double price = bs.price(or, spot);
                    return Map.<String, Object>of(
                            "model",  "BlackScholes",
                            "symbol", req.symbol(),
                            "price",  price
                    );
                })
                .map(ResponseEntity::ok);
    }

    @PostMapping("/binomial")
    public Mono<ResponseEntity<?>> binomial(@RequestBody PricingRequest req) {
        if (req.steps() == null) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "`steps` parameter required for binomial")));
        }
        BinomialTreeModel bin = new BinomialTreeModel(
                req.riskFreeRate(),
                req.volatility(),
                req.steps()
        );
        return fetchSpot(req.symbol())
                .map(spot -> {
                    var or = new OptionRequest(req.strike(), req.timeToExpiry(), req.call());
                    double price = bin.price(or, spot);
                    return Map.<String, Object>of(
                            "model",  "Binomial",
                            "symbol", req.symbol(),
                            "steps",  req.steps(),
                            "price",  price
                    );
                })
                .map(ResponseEntity::ok);
    }

    @PostMapping("/mc")
    public Mono<ResponseEntity<?>> monteCarlo(@RequestBody PricingRequest req) {
        if (req.simulations() == null) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "`simulations` parameter required for Monte Carlo")));
        }
        MonteCarloModel mc = new MonteCarloModel(
                req.riskFreeRate(),
                req.volatility(),
                req.simulations()
        );
        return fetchSpot(req.symbol())
                .map(spot -> {
                    var or = new OptionRequest(req.strike(), req.timeToExpiry(), req.call());
                    double price = mc.price(or, spot);
                    return Map.<String, Object>of(
                            "model",       "MonteCarlo",
                            "symbol",      req.symbol(),
                            "simulations", req.simulations(),
                            "price",       price
                    );
                })
                .map(ResponseEntity::ok);
    }
}
