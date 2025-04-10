package com.cryptotradingsim.cryptotradingsim.websocket;

import java.util.List;
import java.util.Map;

public record SubscribeMessage(String method,
                               Map<String, Object> params)
{

    public static final SubscribeMessage SUBSCRIBE_MESSAGE =
            new SubscribeMessage("subscribe",
                                         Map.of("channel", "ticker",
                                                 "symbol", List.of(
                                                         "BTC/USD", "ETH/USD", "USDT/USD", "XRP/USD", "ADA/USD",
                                                                    "DOGE/USD", "SOL/USD", "DOT/USD", "MATIC/USD", "LTC/USD",
                                                                    "SHIB/USD", "AVAX/USD", "UNI/USD", "XLM/USD", "BCH/USD",
                                                                    "ALGO/USD", "TRX/USD", "ICP/USD", "MANA/USD", "AXS/USD"
                        )
                ));
}