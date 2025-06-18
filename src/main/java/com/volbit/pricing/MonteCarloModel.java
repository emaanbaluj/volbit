package com.volbit.pricing;

import com.volbit.model.OptionRequest;

import java.util.Random;
import java.util.stream.DoubleStream;

public class MonteCarloModel {
    private final double riskFreeRate;
    private final double volatility;
    private final int paths;
    private final Random rng = new Random();

    /**
     * @param riskFreeRate annual risk-free rate
     * @param volatility annual volatility
     * @param paths number of Monte Carlo paths
     */
    public MonteCarloModel(double riskFreeRate, double volatility, int paths) {
        this.riskFreeRate = riskFreeRate;
        this.volatility   = volatility;
        this.paths        = paths;
    }

    /**
     * Price a European call or put via Monte Carlo.
     * @param req strike, timeToExpiry (yrs), call/put flag
     * @param spot current spot price
     */
    public double price(OptionRequest req, double spot) {
        double K = req.getStrike();
        double T = req.getTimeToExpiry();
        boolean isCall = req.isCall();

        // generate terminal payoffs
        double sumPayoff = DoubleStream
                .generate(() -> {
                    double z = rng.nextGaussian();
                    double ST = spot * Math.exp((riskFreeRate - 0.5 * volatility * volatility) * T
                            + volatility * Math.sqrt(T) * z);
                    return isCall
                            ? Math.max(0, ST - K)
                            : Math.max(0, K - ST);
                })
                .limit(paths)
                .sum();

        double meanPayoff = sumPayoff / paths;
        // discount back to today
        return Math.exp(-riskFreeRate * T) * meanPayoff;
    }
}
