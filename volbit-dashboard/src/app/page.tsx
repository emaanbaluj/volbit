"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import styles from "./page.module.css";

interface Tick {
    symbol: string;
    price: number;
    timestamp: string;
}

// little “icons” for each coin
const ICONS: Record<string, string> = {
    btcusdt: "₿",
    ethusdt: "Ξ",
    xrpusdt: "✕",
    ltcusdt: "Ł",
    dogeusdt: "🐕",
    adausdt: "A",
    solusdt: "◎",
};

const COINS = Object.keys(ICONS);

export default function Home() {
    const [ticks, setTicks] = useState<Record<string, Tick | null>>({});

    useEffect(() => {
        Promise.all(
            COINS.map((sym) =>
                fetch(`/api/price/${sym}`)
                    .then((r) => (r.ok ? r.json() : Promise.reject()))
                    .then((t: Tick) => ({ [sym]: t }))
                    .catch(() => ({ [sym]: null }))
            )
        ).then((arr) => setTicks(Object.assign({}, ...arr)));
    }, []);

    return (
        <>
            <header className={styles.header}>
                <h1>
                    Volbit <span className={styles.accent}>⚡</span>
                </h1>
            </header>

            <main className={styles.grid}>
                {COINS.map((sym) => {
                    const tick = ticks[sym];
                    const icon = ICONS[sym];
                    return (
                        <Link
                            key={sym}
                            href={`/coin/${sym}`}
                            className={`${styles.card} ${styles[`accent__${sym}`]}`}
                        >
                            <div className={styles.cardInner}>
                                <h2 className={styles.symbol}>
                                    <span className={styles.icon}>{icon}</span>{" "}
                                    {sym.toUpperCase()}
                                </h2>
                                {tick ? (
                                    <>
                                        <p className={styles.price}>
                                            ${tick.price.toLocaleString()}
                                        </p>
                                        <p className={styles.time}>
                                            {new Date(tick.timestamp).toLocaleTimeString()}
                                        </p>
                                    </>
                                ) : (
                                    <p className={styles.loading}>Loading…</p>
                                )}
                            </div>
                        </Link>
                    );
                })}
            </main>
        </>
    );
}
