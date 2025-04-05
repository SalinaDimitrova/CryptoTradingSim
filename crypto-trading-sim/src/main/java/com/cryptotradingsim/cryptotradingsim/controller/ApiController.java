package com.cryptotradingsim.cryptotradingsim.controller;

import com.cryptotradingsim.cryptotradingsim.websocket.KrakenMarketDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cryptocurrencies")
public class ApiController {

    private final KrakenMarketDataService krakenMarketDataService;

    // Injecting the service
    public ApiController(KrakenMarketDataService krakenMarketDataService) {
        this.krakenMarketDataService = krakenMarketDataService;
    }

    // Endpoint to fetch top 20 cryptos
    @GetMapping("/top-20")
    public List<String> getTopCryptos() {
        return krakenMarketDataService.getTopTradingPairs(20);  // Fetch the top 20 trading pairs
    }
}
