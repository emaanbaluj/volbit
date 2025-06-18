package com.volbit.model;

/**
 * Request payload for all three models.
 */
public record PricingRequest(
        String symbol,            // e.g. "btcusdt"
        double strike,            // strike price
        double timeToExpiry,      // in years (e.g. 0.5)
        boolean call,             // true = call, false = put
        double volatility,        // annual vol (e.g. 0.6)
        double riskFreeRate,      // annual rate (e.g. 0.01)
        Integer steps,            // binomial steps (only for binomial)
        Integer simulations       // Monte/​Carlo sample count (only for MC)
) { }  // <-- added braces
