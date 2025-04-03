package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.Crypto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CryptoService {

    // API URL to fetch ticker data from Kraken for the top pairs
    private static final String API_URL = "https://api.kraken.com/0/public/Ticker?pair=XBTUSD,ETHUSD,LTCUSD,ADAUSD,BNBUSD,DOGEUSD,USDTUSD";

    private final RestTemplate restTemplate;

    public CryptoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Fetch top cryptocurrencies from Kraken API
    public List<Crypto> fetchTopCryptos() {
        // Send the GET request to Kraken API and parse the response
        ResponseEntity<Map> response = restTemplate.getForEntity(API_URL, Map.class);

        // Extract the "result" from the response
        Map<String, Object> body = response.getBody();
        Map<String, Map<String, Object>> result = (Map<String, Map<String, Object>>) body.get("result");

        List<Crypto> topCryptos = new ArrayList<>();

        // Loop through each entry in the result to extract necessary data
        for (String pair : result.keySet()) {
            Map<String, Object> cryptoData = result.get(pair);

            // Extract price and volume from the Kraken API response
            List<String> priceData = (List<String>) cryptoData.get("c"); // Current price (last trade price)
            List<String> volumeData = (List<String>) cryptoData.get("v"); // Volume

            // Create Crypto objects from the extracted data
            String name = pair.split("/")[0];  // Name like "BTC"
            String symbol = pair.split("/")[1];  // Symbol like "USD"

            // Map price and volume to doubles
            double price = Double.parseDouble(priceData.get(0));  // Last trade price
            double volume = Double.parseDouble(volumeData.get(1));  // Volume traded

            // Create a new Crypto object and add it to the list
            topCryptos.add(new Crypto(name, symbol, price, volume));
        }

        return topCryptos;
    }
}
