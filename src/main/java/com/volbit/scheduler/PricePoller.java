package com.volbit.scheduler;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.volbit.service.PriceCacheService;
import com.volbit.service.PriceService;

import reactor.core.scheduler.Schedulers;

@Component
public class PricePoller {

    private final PriceService priceService;
    private final PriceCacheService cacheService;
    private final SimpMessagingTemplate template;
    private final List<String> coins;

    public PricePoller(PriceService priceService,
                       PriceCacheService cacheService,
                       SimpMessagingTemplate template,
                       @Value("${crypto.coins}") String coinList) {
        this.priceService = priceService;
        this.cacheService = cacheService;
        this.template     = template;
        this.coins        = List.of(coinList.split(","));
    }

    /**
     * Every crypto.api.poll-interval-ms, fetch all prices in one batch,
     * update cache, and broadcast each coin’s new price.
     */
    @Scheduled(fixedDelayString = "${crypto.api.poll-interval-ms}")
    public void pollAndBroadcast() {
        priceService.getUsdPrices(coins)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(priceMap -> {
                    Instant now = Instant.now();
                    priceMap.forEach((coinId, price) -> {
                        // 1) update our in-memory cache
                        cacheService.updatePrice(coinId, price);

                        // 2) broadcast over WebSocket
                        var payload = Map.of(
                                "coin",  coinId,
                                "price", price,
                                "time",  now.toString()
                        );
                        template.convertAndSend("/topic/prices/" + coinId, payload);
                    });
                }, err -> {
                    // Log errors but don’t kill the scheduler
                    System.err.println("[Poll] failed: " + err.getMessage());
                });
    }
}
