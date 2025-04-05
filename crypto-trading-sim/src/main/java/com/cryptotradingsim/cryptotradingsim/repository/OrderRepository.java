package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.OrderStatus;
import com.cryptotradingsim.cryptotradingsim.model.OrderType;
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

    private final RowMapper<Order> orderRowMapper = new RowMapper<>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Order(
                    rs.getInt("id"),
                    rs.getLong("account_id"),
                    rs.getString("symbol"),
                    rs.getBigDecimal("quantity"),
                    rs.getBigDecimal("price"),
                    OrderType.valueOf(rs.getString("type")),   // ✅ Enum conversion
                    OrderStatus.valueOf(rs.getString("status")), // ✅ Enum conversion
                    rs.getTimestamp("time_ordered") != null
                            ? rs.getTimestamp("time_ordered").toLocalDateTime() : null,
                    rs.getTimestamp("time_executed") != null
                            ? rs.getTimestamp("time_executed").toLocalDateTime() : null,
                    rs.getBigDecimal("profit_loss") // ✅ Supports profit/loss tracking
            );
        }
    };

    public void saveOrder(Order order) {
        String sql = """
            INSERT INTO orders (account_id, type, symbol, quantity, price, status, time_ordered, time_executed, profit_loss)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                order.accountId(),
                order.type().name(),
                order.symbol(),
                order.quantity(),
                order.price(),
                order.status().name(),
                order.timeOrdered(),
                order.timeExecuted(),
                order.profitLoss()
        );
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders ORDER BY time_ordered DESC";
        return jdbcTemplate.query(sql, orderRowMapper);
    }

    public List<Order> getOrdersByAccountId(long accountId) {
        String sql = "SELECT * FROM orders WHERE account_id = ? ORDER BY time_ordered DESC";
        return jdbcTemplate.query(sql, orderRowMapper, accountId);
    }

    public void markOrderExecuted(int orderId, LocalDateTime executedAt) {
        String sql = """
            UPDATE orders
            SET status = ?, time_executed = ?
            WHERE id = ?
        """;
        jdbcTemplate.update(sql, OrderStatus.EXECUTED.name(), executedAt, orderId);
    }

    public List<Order> getBuyOrdersBySymbol(String symbol) {
        String sql = """
            SELECT * FROM orders
            WHERE type = ? AND symbol = ?
        """;
        return jdbcTemplate.query(sql, orderRowMapper, OrderType.BUY.name(), symbol);
    }
}
