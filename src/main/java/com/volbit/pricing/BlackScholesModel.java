package com.volbit.pricing;// src/main/java/com/volbit/model/pricing/BlackScholesModel.java


import com.volbit.model.OptionRequest;

public class BlackScholesModel {
    private double riskFreeRate;
    private double volatility;

    /**
     * @param riskFreeRate annual risk-free interest rate (e.g. 0.01 for 1%)
     * @param volatility annualized volatility (e.g. 0.6 for 60%)
     */
    public BlackScholesModel(double riskFreeRate, double volatility) {
        this.riskFreeRate = riskFreeRate;
        this.volatility = volatility;
    }

    public double getRiskFreeRate() {
        return riskFreeRate;
    }

    public void setRiskFreeRate(double riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    /**
     * Compute option price using Black–Scholes
     * @param req contains strike, expiry, call/put flag
     * @param spot current spot price
     * @return theoretical option price
     */
    public double price(OptionRequest req, double spot) {
        // TODO: implement BS formula
        throw new UnsupportedOperationException("Black–Scholes pricing not yet implemented");
    }
}
