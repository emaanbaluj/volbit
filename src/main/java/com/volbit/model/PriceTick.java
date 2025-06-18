package com.volbit.model;

import lombok.Getter;

import java.time.Instant;

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