package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Account getAccount() {
        String sql = "SELECT * FROM account WHERE id = 1";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Account(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getBigDecimal("balance"),
                rs.getString("currency")
        ));
    }

    public void updateBalance(long id, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, newBalance, id);
    }

    public void resetAccount(BigDecimal balance, String currency) {
        String sql = "UPDATE account SET balance = ?, currency = ? WHERE id = 1";
        jdbcTemplate.update(sql, balance, currency);
    }
}