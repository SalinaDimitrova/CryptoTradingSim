package com.cryptotradingsim.cryptotradingsim.websocket;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Component
public class KrakenWebSocketClient {

    private final KrakenMarketDataService krakenMarketDataService;

    @Autowired
    public KrakenWebSocketClient(KrakenMarketDataService krakenMarketDataService) {
        this.krakenMarketDataService = krakenMarketDataService;
    }

    // Connect to Kraken and subscribe to the top trading pairs
    public void connectToKraken() {
        // Get the top trading pairs from KrakenMarketDataService
        List<String> topPairs = krakenMarketDataService.getTopTradingPairs(20);

        // Pass the topPairs list to KrakenWebSocketClient
        // Assuming you have WebSocket client setup code here to connect
        System.out.println("Subscribing to Kraken WebSocket with pairs: " + topPairs);
        // Now pass the topPairs to the WebSocket client
        // Replace this with actual WebSocket connection code
        startWebSocketConnection(topPairs);
    }

    private void startWebSocketConnection(List<String> topPairs) {
        // Your WebSocket client logic to handle connection and subscription
        // Use the topPairs list to subscribe to the WebSocket channels
    }
}
