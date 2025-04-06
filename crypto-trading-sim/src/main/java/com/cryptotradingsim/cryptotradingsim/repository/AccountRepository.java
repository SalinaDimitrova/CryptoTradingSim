package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.Account;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public class AccountRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AccountRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Account getAccount(long accountId) {
        String sql = "SELECT * FROM account WHERE id = :accountId";

        return namedParameterJdbcTemplate.queryForObject(sql,
                Map.of("accountId", accountId),
                (rs, rowNum) -> new Account(
                rs.getLong("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getBigDecimal("balance"),
                rs.getString("currency")
        ));
    }

    public void updateBalance(long id, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = :balance WHERE id = :accountId";
        namedParameterJdbcTemplate.update(sql, Map.of("balance", newBalance, "accountId", id));
    }

    public void resetAccount(long accountId, BigDecimal balance, String currency) {
        String sql = "UPDATE account SET balance = :balance, currency = :currency WHERE id = :accountId";
        namedParameterJdbcTemplate.update(sql,
                Map.of("balance", balance,
                "currency", currency,
                "accountId", accountId));
    }
}