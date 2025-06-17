package com.volbit.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PriceService {
    private final WebClient webClient;

    public PriceService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.coingecko.com/api/v3")
                .build();
    }

    /**
     * Fetch a single coin’s USD price by delegating to the batch method.
     */
    public Mono<Double> getUsdPrice(String coinId) {
        return getUsdPrices(List.of(coinId))
                .map(map -> map.getOrDefault(coinId, 0.0));
    }

    /**
     * Fetch USD prices for multiple coins in one request.
     * Returns a map coinId → price.
     */
    public Mono<Map<String, Double>> getUsdPrices(List<String> coinIds) {
        var idsParam = String.join(",", coinIds);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/simple/price")
                        .queryParam("ids", idsParam)
                        .queryParam("vs_currencies", "usd")
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Map<String, Double>>>() {})
                .map(raw -> raw.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().getOrDefault("usd", 0.0)
                        ))
                );
    }
}
