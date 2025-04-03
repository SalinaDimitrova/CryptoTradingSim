package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✅ Updated RowMapper to include profit_loss
    private final RowMapper<Order> orderRowMapper = new RowMapper<>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Order(
                    rs.getInt("id"),
                    rs.getLong("account_id"),
                    rs.getString("symbol"),
                    rs.getBigDecimal("quantity"),
                    rs.getBigDecimal("price"),
                    rs.getString("type"),
                    rs.getString("status"),
                    rs.getTimestamp("time_ordered") != null
                            ? rs.getTimestamp("time_ordered").toLocalDateTime() : null,
                    rs.getTimestamp("time_executed") != null
                            ? rs.getTimestamp("time_executed").toLocalDateTime() : null,
                    rs.getBigDecimal("profit_loss") // ✅ New field
            );
        }
    };

    // ✅ Updated to include profit_loss field in INSERT
    public void saveOrder(Order order) {
        String sql = """
            INSERT INTO orders (account_id, type, symbol, quantity, price, status, time_ordered, time_executed, profit_loss)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                order.getAccountId(),
                order.getType(),
                order.getSymbol(),
                order.getQuantity(),
                order.getPrice(),
                order.getStatus(),
                order.getTimeOrdered(),
                order.getTimeExecuted(),
                order.getProfitLoss() // ✅ New param
        );
    }

    public List<Order> getAllOrders() {
        return jdbcTemplate.query("SELECT * FROM orders ORDER BY time_ordered DESC", orderRowMapper);
    }

    public List<Order> getOrdersByAccountId(long accountId) {
        String sql = "SELECT * FROM orders WHERE account_id = ? ORDER BY time_ordered DESC";
        return jdbcTemplate.query(sql, orderRowMapper, accountId);
    }

    public void markOrderExecuted(int orderId, LocalDateTime executedAt) {
        String sql = """
            UPDATE orders
            SET status = 'EXECUTED',
                time_executed = ?
            WHERE id = ?
        """;
        jdbcTemplate.update(sql, executedAt, orderId);
    }

    // ✅ New method used for profit/loss calculation
    public List<Order> getBuyOrdersBySymbol(String symbol) {
        String sql = """
            SELECT * FROM orders
            WHERE type = 'BUY' AND symbol = ?
        """;
        return jdbcTemplate.query(sql, orderRowMapper, symbol);
    }
}


