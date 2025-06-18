package com.volbit.service;

import com.volbit.service.BinanceWebSocketService.PriceTick;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LivePriceService {
    private final Map<String, PriceTick> latest = new ConcurrentHashMap<>();
    private final Disposable subscription;

    public LivePriceService(BinanceWebSocketService ws) {
        Flux<PriceTick> stream = ws.priceStream();
        // Subscribe once at startup, stash every tick
        this.subscription = stream.subscribe(tick ->
                latest.put(tick.getSymbol().toLowerCase(), tick)
        );
    }

    public Optional<PriceTick> get(String symbol) {
        return Optional.ofNullable(latest.get(symbol));
    }

    // If you ever want to clean up:
    public void shutdown() {
        subscription.dispose();
    }
}
