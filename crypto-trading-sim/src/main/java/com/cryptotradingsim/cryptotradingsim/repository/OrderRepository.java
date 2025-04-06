package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.OrderRequest;
import com.cryptotradingsim.cryptotradingsim.model.OrderStatus;
import com.cryptotradingsim.cryptotradingsim.model.OrderType;
import com.cryptotradingsim.cryptotradingsim.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OrderRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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

    public void saveOrder(long accountId, OrderRequest order, BigDecimal currentPrice) {
        String sql = """
            INSERT INTO orders (account_id, type, symbol, quantity, price, status, time_ordered)
            VALUES (:accountId, :type, :symbol, :quantity, :price, :status, :timeOrdered)
        """;


        namedParameterJdbcTemplate.update(sql, Map.of("accountId", accountId,
                                        "type", order.type().name(), "symbol", order.symbol(),
                                        "quantity", order.quantity(), "price", currentPrice,
                                        "status", OrderStatus.ORDERED.name(), "timeOrdered", LocalDateTime.now()));
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders ORDER BY time_ordered DESC";
        return namedParameterJdbcTemplate.query(sql, orderRowMapper);
    }

    public List<Order> getOrdersByAccountId(long accountId) {
        String sql = "SELECT * FROM orders WHERE account_id = :account_id ORDER BY time_ordered DESC";
        Map<String, Object> params = Map.of("account_id", accountId);
        return namedParameterJdbcTemplate.query(sql, params, orderRowMapper);
    }

    public void markOrderExecuted(int orderId, LocalDateTime executedAt) {
        String sql = """
            UPDATE orders
            SET status = :status, time_executed = :executed_at
            WHERE id = :order_id
        """;

        Map<String, Object> params = Map.of(
                "status", OrderStatus.EXECUTED.name(),
                "executed_at", executedAt,
                "order_id", orderId
        );

        namedParameterJdbcTemplate.update(sql, params);
    }

    public List<Order> getBuyOrdersBySymbol(String symbol) {
        String sql = """
            SELECT * FROM orders
            WHERE type = :type AND symbol = :symbol
        """;

        Map<String, Object> params = Map.of(
                "type", OrderType.BUY.name(),
                "symbol", symbol
        );

        return namedParameterJdbcTemplate.query(sql, params, orderRowMapper);
    }
}
