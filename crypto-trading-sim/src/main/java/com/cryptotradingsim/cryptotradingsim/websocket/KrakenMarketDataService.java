package com.cryptotradingsim.cryptotradingsim.websocket;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KrakenMarketDataService {

    // Top 20 crypto by market cap, as per Kraken's pair naming
    public List<String> getTopTradingPairs(int count) {
        return List.of(
                "XBT/USD", "ETH/USD", "BNB/USD", "SOL/USD", "XRP/USD",
                "DOGE/USD", "ADA/USD", "AVAX/USD", "DOT/USD", "TRX/USD",
                "LINK/USD", "MATIC/USD", "TON/USD", "UNI/USD", "LTC/USD",
                "SHIB/USD", "BCH/USD", "XLM/USD", "NEAR/USD", "APT/USD"
        ).subList(0, count);
    }
}
