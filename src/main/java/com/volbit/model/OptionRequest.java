package com.volbit.model;

public class OptionRequest {
    /**
     * Strike price of the option.
     */
    private double strike;

    /**
     * Time to expiry in years (e.g. 0.5 for six months).
     */
    private double timeToExpiry;

    /**
     * True = call option, false = put option.
     */
    private boolean call;

    public OptionRequest() { }

    public OptionRequest(double strike, double timeToExpiry, boolean call) {
        this.strike = strike;
        this.timeToExpiry = timeToExpiry;
        this.call = call;
    }

    public double getStrike() {
        return strike;
    }

    public void setStrike(double strike) {
        this.strike = strike;
    }

    public double getTimeToExpiry() {
        return timeToExpiry;
    }

    public void setTimeToExpiry(double timeToExpiry) {
        this.timeToExpiry = timeToExpiry;
    }

    public boolean isCall() {
        return call;
    }

    public void setCall(boolean call) {
        this.call = call;
    }
}
