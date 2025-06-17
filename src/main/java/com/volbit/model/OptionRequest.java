// src/main/java/com/volbit/model/OptionRequest.java
package com.volbit.model;

import java.time.Instant;

/**
 * Request parameters for pricing an option.
 */
public class OptionRequest {
    private String coinId;
    private double strike;
    private Instant expiry;
    private boolean isCall;


    public OptionRequest() {}

    public OptionRequest(String coinId, double strike, Instant expiry, boolean isCall) {
        this.coinId = coinId;
        this.strike = strike;
        this.expiry = expiry;
        this.isCall = isCall;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public double getStrike() {
        return strike;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }
}