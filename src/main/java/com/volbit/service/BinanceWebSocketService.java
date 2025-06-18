package com.volbit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.volbit.model.PriceTick;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.WebsocketClientSpec;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class BinanceWebSocketService {

    private final Sinks.Many<PriceTick> sink = Sinks.many().multicast().onBackpressureBuffer();
    private final Flux<PriceTick> priceFlux = sink.asFlux();

    @Value("${crypto.ws.streams}")
    private String streams;  // e.g. "btcusdt@trade,ethusdt@trade"

    @PostConstruct
    public void start() {
        String url = "wss://stream.binance.com:9443/stream?streams=" + streams;
        log.info("→ opening WS: wss://stream.binance.com:9443/stream?streams={}", streams);

        HttpClient.create()
                .websocket(WebsocketClientSpec.builder().handlePing(true).build())
                .uri(url)
                .handle(this::handleSession)
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(5)))
                .doOnError(err -> log.error("WebSocket error", err))
                .subscribe();
    }

    private Flux<Void> handleSession(WebsocketInbound inbound, WebsocketOutbound outbound) {
        return inbound.receive()
                .asString()
                .map(this::toTick)
                .doOnNext(tick -> {
                    if (tick != null) sink.tryEmitNext(tick);
                })
                .thenMany(Flux.never());
    }

    private PriceTick toTick(String json) {
        try {
            JsonNode root = JsonMapper.builder().build().readTree(json);
            JsonNode data = root.path("data");
            String stream = root.path("stream").asText();
            String symbol = stream.split("@")[0];
            double price     = data.path("p").asDouble();          // last trade price
            Instant ts       = Instant.ofEpochMilli(data.path("T").asLong());
            return new PriceTick(symbol, price, ts);
        } catch (Exception e) {
            log.warn("Malformed WS message: {}", json);
            return null;
        }
    }



    /**
     * Expose a hot flux of live ticks
     */
    public Flux<PriceTick> priceStream() {
        return priceFlux.filter(pt -> pt != null);
    }

    /** DTO for each trade tick */
    @Getter
    public class PriceTick {
        private final String symbol;
        private final double price;
        private final Instant timestamp;

        public PriceTick(String symbol, double price, Instant timestamp) {
            this.symbol = symbol;
            this.price = price;
            this.timestamp = timestamp;
        }

    }
}
