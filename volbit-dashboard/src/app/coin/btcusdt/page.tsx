"use client"

import { useEffect, useState, FormEvent } from "react";
import { useRouter } from "next/navigation";

interface Tick {
    symbol: string;
    price: number;
    timestamp: string;
}

interface PricingResponse {
    model: string;
    symbol: string;
    price: number;
    steps?: number;
    simulations?: number;
}

interface GreeksResponse {
    delta: number;
    gamma: number;
    vega: number;
    theta: number;
    rho: number;
}

type Model = "bs" | "binomial" | "mc";

export default function CoinPage() {
    const router = useRouter();
    const symbolParam = typeof window !== "undefined"
        ? window.location.pathname.split("/").pop()
        : null;
    const symbol = symbolParam || "";

    const [spot, setSpot] = useState<number | null>(null);
    const [loadingSpot, setLoadingSpot] = useState(true);
    const [error, setError] = useState<string>("");

    // form state
    const [model, setModel] = useState<Model>("bs");
    const [strike, setStrike] = useState(0);
    const [timeToExpiry, setTimeToExpiry] = useState(0.25);
    const [isCall, setIsCall] = useState(true);
    const [volatility, setVolatility] = useState(0.6);
    const [riskFreeRate, setRiskFreeRate] = useState(0.01);
    const [steps, setSteps] = useState(100);
    const [simulations, setSimulations] = useState(10000);

    // results
    const [priceRes, setPriceRes] = useState<PricingResponse | null>(null);
    const [greeksRes, setGreeksRes] = useState<GreeksResponse | null>(null);

    useEffect(() => {
        setLoadingSpot(true);
        fetch(`/api/price/${symbol}`)
            .then(r => {
                if (!r.ok) throw new Error("Failed to fetch spot");
                return r.json();
            })
            .then((t: Tick) => setSpot(t.price))
            .catch(err => setError(err.message))
            .finally(() => setLoadingSpot(false));
    }, [symbol]);

    async function onSubmit(e: FormEvent) {
        e.preventDefault();
        setError("");
        setPriceRes(null);
        setGreeksRes(null);
        if (!spot) {
            setError("No spot price available");
            return;
        }

        const payload = {
            symbol,
            strike,
            timeToExpiry,
            call: isCall,
            volatility,
            riskFreeRate,
            steps: model === "binomial" ? steps : null,
            simulations: model === "mc" ? simulations : null
        };

        const endpoint =
            model === "bs"
                ? "/api/options/bs"
                : model === "binomial"
                    ? "/api/options/binomial"
                    : "/api/options/mc";

        try {
            const res = await fetch(endpoint, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
            const data = await res.json();
            if (!res.ok || data.error) throw new Error(data.error || "Pricing failed");
            setPriceRes(data as PricingResponse);

            // fetch greeks only for Black–Scholes
            if (model === "bs") {
                const gres = await fetch("/api/options/greeks", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                }).then(r => r.json());
                if (gres.error) throw new Error(gres.error);
                setGreeksRes(gres as GreeksResponse);
            }
        } catch (err: any) {
            setError(err.message);
        }
    }

    return (
        <div style={{ padding: "2rem", maxWidth: "1000px", margin: "auto" }}>
            <button
                onClick={() => router.back()}
                style={{
                    marginBottom: "20px",
                    backgroundColor: "#0070f3",
                    color: "white",
                    padding: "8px 16px",
                    borderRadius: "4px",
                    cursor: "pointer",
                    border: "none",
                }}
            >
                ← Back
            </button>

            <h1 style={{ fontSize: "32px", color: "#333", marginBottom: "1rem" }}>
                {symbol.toUpperCase()}
            </h1>

            {loadingSpot ? (
                <p>Loading spot price…</p>
            ) : spot !== null ? (
                <p>
                    Spot: <strong>${spot.toLocaleString()}</strong>
                </p>
            ) : (
                <p style={{ color: "red" }}>{error || "No spot available"}</p>
            )}

            <form
                onSubmit={onSubmit}
                style={{
                    marginTop: "2rem",
                    display: "grid",
                    gap: "1rem",
                    gridTemplateColumns: "1fr 1fr",
                    alignItems: "center",
                }}
            >
                <div>
                    <label>Model:</label>
                    <select
                        value={model}
                        onChange={(e) => setModel(e.target.value as Model)}
                        style={{
                            width: "100%",
                            padding: "8px",
                            borderRadius: "4px",
                            border: "1px solid #ccc",
                        }}
                    >
                        <option value="bs">Black–Scholes</option>
                        <option value="binomial">Binomial</option>
                        <option value="mc">Monte Carlo</option>
                    </select>
                </div>

                <div>
                    <label>Strike:</label>
                    <input
                        type="number"
                        value={strike}
                        onChange={(e) => setStrike(parseFloat(e.target.value))}
                        style={{
                            width: "100%",
                            padding: "8px",
                            borderRadius: "4px",
                            border: "1px solid #ccc",
                        }}
                    />
                </div>

                <div>
                    <label>Time to expiry (yrs):</label>
                    <input
                        type="number"
                        step="0.01"
                        value={timeToExpiry}
                        onChange={(e) => setTimeToExpiry(parseFloat(e.target.value))}
                        style={{
                            width: "100%",
                            padding: "8px",
                            borderRadius: "4px",
                            border: "1px solid #ccc",
                        }}
                    />
                </div>

                <div>
                    <label>
                        <input
                            type="radio"
                            checked={isCall}
                            onChange={() => setIsCall(true)}
                        />
                        Call
                    </label>
                    <label>
                        <input
                            type="radio"
                            checked={!isCall}
                            onChange={() => setIsCall(false)}
                        />
                        Put
                    </label>
                </div>

                <div>
                    <label>Volatility:</label>
                    <input
                        type="number"
                        step="0.01"
                        value={volatility}
                        onChange={(e) => setVolatility(parseFloat(e.target.value))}
                        style={{
                            width: "100%",
                            padding: "8px",
                            borderRadius: "4px",
                            border: "1px solid #ccc",
                        }}
                    />
                </div>

                <div>
                    <label>Risk-free rate:</label>
                    <input
                        type="number"
                        step="0.001"
                        value={riskFreeRate}
                        onChange={(e) => setRiskFreeRate(parseFloat(e.target.value))}
                        style={{
                            width: "100%",
                            padding: "8px",
                            borderRadius: "4px",
                            border: "1px solid #ccc",
                        }}
                    />
                </div>

                {model === "binomial" && (
                    <div>
                        <label>Binomial steps:</label>
                        <input
                            type="number"
                            value={steps}
                            onChange={(e) => setSteps(parseInt(e.target.value, 10))}
                            style={{
                                width: "100%",
                                padding: "8px",
                                borderRadius: "4px",
                                border: "1px solid #ccc",
                            }}
                        />
                    </div>
                )}

                {model === "mc" && (
                    <div>
                        <label>Simulations:</label>
                        <input
                            type="number"
                            value={simulations}
                            onChange={(e) => setSimulations(parseInt(e.target.value, 10))}
                            style={{
                                width: "100%",
                                padding: "8px",
                                borderRadius: "4px",
                                border: "1px solid #ccc",
                            }}
                        />
                    </div>
                )}

                <button
                    type="submit"
                    style={{
                        padding: "10px 20px",
                        backgroundColor: "#0070f3",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                        fontSize: "16px",
                    }}
                >
                    Price Option
                </button>
            </form>

            {error && <p style={{ color: "red", marginTop: "12px" }}>{error}</p>}

            {priceRes && (
                <div
                    style={{
                        marginTop: "24px",
                        padding: "16px",
                        border: "1px solid #eee",
                        borderRadius: "4px",
                    }}
                >
                    <h2>Result</h2>
                    <p>
                        <strong>Model:</strong> {priceRes.model}
                    </p>
                    <p>
                        <strong>Price:</strong> ${priceRes.price.toLocaleString()}
                    </p>
                    {priceRes.steps != null && (
                        <p>
                            <strong>Steps:</strong> {priceRes.steps}
                        </p>
                    )}
                    {priceRes.simulations != null && (
                        <p>
                            <strong>Simulations:</strong> {priceRes.simulations}
                        </p>
                    )}
                </div>
            )}

            {greeksRes && (
                <div
                    style={{
                        marginTop: "16px",
                        padding: "16px",
                        border: "1px solid #eee",
                        borderRadius: "4px",
                    }}
                >
                    <h3>Greeks (BS only)</h3>
                    <p>
                        <strong>Δ (delta):</strong> {greeksRes.delta.toFixed(6)}
                    </p>
                    <p>
                        <strong>Γ (gamma):</strong> {greeksRes.gamma.toExponential(3)}
                    </p>
                    <p>
                        <strong>ν (vega):</strong> {greeksRes.vega.toFixed(4)}
                    </p>
                    <p>
                        <strong>θ (theta):</strong> {greeksRes.theta.toFixed(4)}
                    </p>
                    <p>
                        <strong>ρ (rho):</strong> {greeksRes.rho.toFixed(4)}
                    </p>
                </div>
            )}
        </div>
    );
}
