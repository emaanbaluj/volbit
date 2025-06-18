import Link from 'next/link';
import styles from './CoinCard.module.css';

interface Props {
    symbol: string;
    price: number;
}

export default function CoinCard({ symbol, price }: Props) {
    return (
        <Link href={`/coin/${symbol}`}>
            <a className={styles.card}>
                <h2 className={styles.cardTitle}>
                    {symbol.toUpperCase()}
                </h2>
                <p className={styles.cardPrice}>
                    ${price.toLocaleString()}
                </p>
            </a>
        </Link>
    );
}
