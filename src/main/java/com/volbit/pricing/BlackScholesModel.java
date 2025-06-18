package com.volbit.pricing;

import com.volbit.model.OptionRequest;

public class BlackScholesModel {
    private double riskFreeRate;  // annual r
    private double volatility;    // annual vol

    public BlackScholesModel(double riskFreeRate, double volatility) {
        this.riskFreeRate = riskFreeRate;
        this.volatility   = volatility;
    }

    public double price(OptionRequest req, double spot) {
        double K = req.getStrike();
        double T = req.getTimeToExpiry();
        boolean isCall = req.isCall();

        double sqrtT = Math.sqrt(T);
        double d1 = (Math.log(spot / K) + (riskFreeRate + 0.5 * volatility * volatility) * T)
                / (volatility * sqrtT);
        double d2 = d1 - volatility * sqrtT;

        double disc = Math.exp(-riskFreeRate * T);
        if (isCall) {
            return spot * normCdf(d1) - K * disc * normCdf(d2);
        } else {
            return K * disc * normCdf(-d2) - spot * normCdf(-d1);
        }
    }

    /** Delta: sensitivity to spot price */
    public double delta(OptionRequest req, double spot) {
        double T = req.getTimeToExpiry();
        double sqrtT = Math.sqrt(T);
        double d1 = (Math.log(spot / req.getStrike())
                + (riskFreeRate + 0.5 * volatility * volatility) * T)
                / (volatility * sqrtT);
        return req.isCall()
                ? normCdf(d1)
                : normCdf(d1) - 1.0;
    }

    /** Gamma: second derivative wrt spot */
    public double gamma(OptionRequest req, double spot) {
        double T = req.getTimeToExpiry();
        double sqrtT = Math.sqrt(T);
        double d1 = (Math.log(spot / req.getStrike())
                + (riskFreeRate + 0.5 * volatility * volatility) * T)
                / (volatility * sqrtT);
        return normPdf(d1) / (spot * volatility * sqrtT);
    }

    /** Vega: sensitivity to volatility (per 1.0 = 100% vol) */
    public double vega(OptionRequest req, double spot) {
        double T = req.getTimeToExpiry();
        double sqrtT = Math.sqrt(T);
        double d1 = (Math.log(spot / req.getStrike())
                + (riskFreeRate + 0.5 * volatility * volatility) * T)
                / (volatility * sqrtT);
        return spot * normPdf(d1) * sqrtT;
    }

    /** Theta: sensitivity to time decay (per year) */
    public double theta(OptionRequest req, double spot) {
        double K = req.getStrike();
        double T = req.getTimeToExpiry();
        double sqrtT = Math.sqrt(T);
        double d1 = (Math.log(spot / K)
                + (riskFreeRate + 0.5 * volatility * volatility) * T)
                / (volatility * sqrtT);
        double d2 = d1 - volatility * sqrtT;

        double pdf1 = normPdf(d1);
        double disc = Math.exp(-riskFreeRate * T);

        double term1 = - (spot * pdf1 * volatility) / (2 * sqrtT);
        double term2 = req.isCall()
                ? riskFreeRate * K * disc * normCdf(d2)
                : -riskFreeRate * K * disc * normCdf(-d2);
        return term1 + term2;
    }

    /** Rho: sensitivity to risk-free rate (per 1.0 = 100% rate) */
    public double rho(OptionRequest req, double spot) {
        double K = req.getStrike();
        double T = req.getTimeToExpiry();
        double d1 = (Math.log(spot / K)
                + (riskFreeRate + 0.5 * volatility * volatility) * T)
                / (volatility * Math.sqrt(T));
        double d2 = d1 - volatility * Math.sqrt(T);
        double disc = Math.exp(-riskFreeRate * T);

        if (req.isCall()) {
            return  K * T * disc * normCdf(d2);
        } else {
            return -K * T * disc * normCdf(-d2);
        }
    }

    // Standard normal probability density
    private static double normPdf(double x) {
        return Math.exp(-0.5 * x * x) / Math.sqrt(2 * Math.PI);
    }

    // Cumulative normal distribution (Abramowitz & Stegun approximation)
    private static double normCdf(double x) {
        double sign = x < 0 ? -1 : 1;
        x = Math.abs(x) / Math.sqrt(2);
        // constants
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;
        // Abramowitz & Stegun 7.1.26
        double t = 1.0 / (1.0 + p * x);
        double erf = 1 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1)
                * t * Math.exp(-x * x);
        return 0.5 * (1.0 + sign * erf);
    }
}
