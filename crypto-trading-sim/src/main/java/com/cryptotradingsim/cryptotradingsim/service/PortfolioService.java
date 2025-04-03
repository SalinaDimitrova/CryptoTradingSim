package com.cryptotradingsim.cryptotradingsim.service;

import com.cryptotradingsim.cryptotradingsim.model.Portfolio;
import com.cryptotradingsim.cryptotradingsim.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public List<Portfolio> getAllHoldings() {
        return portfolioRepository.getAllHoldings();
    }

    public Portfolio getBySymbol(String symbol) {
        return portfolioRepository.findBySymbol(symbol);
    }

    public void updateHolding(String symbol, double quantity) {
        portfolioRepository.updateHolding(symbol, quantity);
    }

    public void insertHolding(String symbol, double quantity) {
        portfolioRepository.insertHolding(symbol, quantity);
    }

    public void deleteHolding(String symbol) {
        portfolioRepository.deleteHolding(symbol);
    }
}