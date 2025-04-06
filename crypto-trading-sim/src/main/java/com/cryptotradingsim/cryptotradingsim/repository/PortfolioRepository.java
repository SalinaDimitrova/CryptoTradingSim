package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.Portfolio;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public Optional<Portfolio> findBySymbol(String symbol) {
        String sql = "SELECT * FROM portfolio WHERE symbol = ?";
        try {
            Portfolio portfolio = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Portfolio(
                    rs.getInt("id"),
                    rs.getString("symbol"),
                    rs.getBigDecimal("quantity")
            ), symbol);

            return Optional.ofNullable(portfolio);
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    public void updateHolding(String symbol, double quantity) {
        String sql = "UPDATE portfolio SET quantity = ? WHERE symbol = ?";
        jdbcTemplate.update(sql, quantity, symbol);
    }

    public void insertHolding(long accountId, String symbol, double quantity) {
        String sql = "INSERT INTO portfolio (account_id, symbol, quantity) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, accountId, symbol, quantity);
    }

    public void deleteHolding(String symbol) {
        String sql = "DELETE FROM portfolio WHERE symbol = ?";
        jdbcTemplate.update(sql, symbol);
    }

    public void deleteAllHoldingsByAccountId(long accountId) {
        String sql = "DELETE FROM portfolio WHERE account_id = ?";
        jdbcTemplate.update(sql, accountId);
    }
}