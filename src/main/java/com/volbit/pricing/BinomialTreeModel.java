package com.volbit.pricing;

import com.volbit.model.OptionRequest;

public class BinomialTreeModel {
    private final double riskFreeRate;
    private final double volatility;
    private final int steps;

    /**
     * @param riskFreeRate annual risk-free rate, e.g. 0.01
     * @param volatility annual volatility, e.g. 0.6
     * @param steps number of binomial steps
     */
    public BinomialTreeModel(double riskFreeRate, double volatility, int steps) {
        this.riskFreeRate = riskFreeRate;
        this.volatility  = volatility;
        this.steps       = steps;
    }

    /**
     * Price a European call or put via CRR tree.
     * @param req strike, timeToExpiry (yrs), call/put flag
     * @param spot current spot price
     */
    public double price(OptionRequest req, double spot) {
        double K = req.getStrike();
        double T = req.getTimeToExpiry();
        boolean isCall = req.isCall();

        double dt = T / steps;
        double u  = Math.exp(volatility * Math.sqrt(dt));
        double d  = 1.0 / u;
        double disc = Math.exp(-riskFreeRate * dt);
        double p = (Math.exp(riskFreeRate * dt) - d) / (u - d);

        // initialize asset prices at maturity
        double[] values = new double[steps + 1];
        for (int i = 0; i <= steps; i++) {
            double ST = spot * Math.pow(u, steps - i) * Math.pow(d, i);
            values[i] = isCall ? Math.max(0, ST - K)
                    : Math.max(0, K - ST);
        }

        // back–propagate option values
        for (int step = steps - 1; step >= 0; step--) {
            for (int i = 0; i <= step; i++) {
                values[i] = disc * (p * values[i] + (1 - p) * values[i + 1]);
            }
        }

        return values[0];
    }

}
