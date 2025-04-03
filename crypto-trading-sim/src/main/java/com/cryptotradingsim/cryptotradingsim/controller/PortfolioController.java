package com.cryptotradingsim.cryptotradingsim.controller;

import com.cryptotradingsim.cryptotradingsim.model.Portfolio;
import com.cryptotradingsim.cryptotradingsim.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<List<Portfolio>> getPortfolio() {
        return ResponseEntity.ok(portfolioService.getAllHoldings());
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Portfolio> getBySymbol(@PathVariable String symbol) {
        return ResponseEntity.ok(portfolioService.getBySymbol(symbol));
    }
}
