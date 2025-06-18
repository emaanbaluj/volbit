package com.volbit.controller;

import com.volbit.model.OptionRequest;
import com.volbit.model.PricingRequest;
import com.volbit.pricing.BlackScholesModel;
import com.volbit.service.LivePriceService;
import com.volbit.service.BinanceWebSocketService.PriceTick;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/options")
public class GreeksController {

    private final LivePriceService live;

    public GreeksController(LivePriceService live) {
        this.live = live;
    }

    private Mono<Double> fetchSpot(String symbol) {
        return Mono.justOrEmpty(live.get(symbol.toLowerCase()))
                .map(PriceTick::getPrice)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Unknown symbol " + symbol)));
    }

    /**
     * Calculate and return all five Black–Scholes Greeks for the given option.
     */
    @PostMapping("/greeks")
    public Mono<ResponseEntity<Map<String, Object>>> greeks(@RequestBody PricingRequest req) {
        BlackScholesModel bs = new BlackScholesModel(req.riskFreeRate(), req.volatility());

        return fetchSpot(req.symbol())
                .map(spot -> {
                    OptionRequest or = new OptionRequest(req.strike(), req.timeToExpiry(), req.call());

                    double delta = bs.delta(or, spot);
                    double gamma = bs.gamma(or, spot);
                    double vega  = bs.vega(or, spot);
                    double theta = bs.theta(or, spot);
                    double rho   = bs.rho(or, spot);

                    return ResponseEntity.ok(Map.<String, Object>of(
                            "model",  "BlackScholes",
                            "symbol", req.symbol(),
                            "spot",   spot,
                            "delta",  delta,
                            "gamma",  gamma,
                            "vega",   vega,
                            "theta",  theta,
                            "rho",    rho
                    ));
                })
                .onErrorResume(IllegalArgumentException.class, ex ->
                        Mono.just(ResponseEntity
                                .badRequest()
                                .body(Map.of("error", ex.getMessage())))
                );
    }
}
