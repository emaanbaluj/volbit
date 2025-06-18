# import argparse
# import requests
# import numpy as np
# import pandas as pd
# import matplotlib.pyplot as plt
#
# def fetch_price(endpoint, symbol, strike, time_to_expiry, call, volatility, risk_free_rate):
#     payload = {
#         "symbol": symbol,
#         "strike": strike,
#         "timeToExpiry": time_to_expiry,
#         "call": call,
#         "volatility": volatility,
#         "riskFreeRate": risk_free_rate,
#         "steps": None,
#         "simulations": None
#     }
#     resp = requests.post(endpoint, json=payload)
#     resp.raise_for_status()
#     return resp.json()["price"]
#
# def main():
#     parser = argparse.ArgumentParser(description="Generate option price heatmap via Black‑Scholes API")
#     parser.add_argument("--endpoint", type=str, required=True,
#                         help="Full URL of your Black‑Scholes pricing endpoint")
#     parser.add_argument("--symbol", type=str, default="btcusdt",
#                         help="Underlying symbol (default: btcusdt)")
#     parser.add_argument("--volatility", type=float, default=0.6,
#                         help="Annualized volatility (default: 0.6)")
#     parser.add_argument("--risk_free_rate", type=float, default=0.01,
#                         help="Annual risk‑free rate (default: 0.01)")
#     parser.add_argument("--strikes", type=float, nargs="+", default=list(np.linspace(20000, 80000, 13)),
#                         help="List of strike prices (default: 20000 to 80000)")
#     parser.add_argument("--expiries", type=float, nargs="+", default=list(np.linspace(0.01, 1.0, 10)),
#                         help="List of times to expiry in years (default: 0.01 to 1)")
#     parser.add_argument("--output", type=str, default="heatmap.png",
#                         help="Output filename for the heatmap image")
#     args = parser.parse_args()
#
#     # build grid of prices
#     data = []
#     for T in args.expiries:
#         for K in args.strikes:
#             price = fetch_price(args.endpoint, args.symbol, K, T, True, args.volatility, args.risk_free_rate)
#             data.append((K, T, price))
#
#     # DataFrame and pivot for heatmap
#     df = pd.DataFrame(data, columns=["strike", "expiry", "price"])
#     pivot = df.pivot(index="expiry", columns="strike", values="price")
#
#     # Plot heatmap
#     plt.figure(figsize=(10, 6))
#     plt.title(f"{args.symbol.upper()} Call Pricing Heatmap")
#     plt.xlabel("Strike")
#     plt.ylabel("Time to Expiry (years)")
#     plt.imshow(pivot, aspect='auto', origin='lower',
#                extent=[min(args.strikes), max(args.strikes), min(args.expiries), max(args.expiries)])
#     plt.colorbar(label="Option Price")
#     plt.tight_layout()
#     plt.savefig(args.output)
#     print(f"Heatmap saved to {args.output}")
#
# if __name__ == "__main__":
#     main()
