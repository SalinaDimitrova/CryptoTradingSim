package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.Portfolio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PortfolioRepository {

    private final JdbcTemplate jdbcTemplate;

    public PortfolioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Portfolio> getAllHoldings() {
        String sql = "SELECT * FROM portfolio";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Portfolio(
                rs.getInt("id"),
                rs.getString("symbol"),
                rs.getBigDecimal("quantity")
        ));
    }

    public Portfolio findBySymbol(String symbol) {
        String sql = "SELECT * FROM portfolio WHERE symbol = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Portfolio(
                rs.getInt("id"),
                rs.getString("symbol"),
                rs.getBigDecimal("quantity")
        ), symbol);
    }

    public void updateHolding(String symbol, double quantity) {
        String sql = "UPDATE portfolio SET quantity = ? WHERE symbol = ?";
        jdbcTemplate.update(sql, quantity, symbol);
    }

    public void insertHolding(String symbol, double quantity) {
        String sql = "INSERT INTO portfolio (symbol, quantity) VALUES (?, ?)";
        jdbcTemplate.update(sql, symbol, quantity);
    }

    public void deleteHolding(String symbol) {
        String sql = "DELETE FROM portfolio WHERE symbol = ?";
        jdbcTemplate.update(sql, symbol);
    }
}