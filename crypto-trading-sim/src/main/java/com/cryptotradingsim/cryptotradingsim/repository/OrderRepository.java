package com.cryptotradingsim.cryptotradingsim.repository;

import com.cryptotradingsim.cryptotradingsim.model.OrderRequest;
import com.cryptotradingsim.cryptotradingsim.model.OrderStatus;
import com.cryptotradingsim.cryptotradingsim.model.OrderType;
import com.cryptotradingsim.cryptotradingsim.model.Order;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

    public long saveOrder(long accountId, OrderRequest order) {
        String sql = """
            INSERT INTO orders (account_id, type, symbol, quantity, price, status, time_ordered)
            VALUES (:accountId, :type, :symbol, :quantity, :price, :status, :timeOrdered)
        """;

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("accountId", accountId);
        params.addValue("type", order.type().name());
        params.addValue("symbol", order.symbol());
        params.addValue("quantity", order.quantity());
        params.addValue("price", order.price());
        params.addValue("status", OrderStatus.ORDERED.name());
        params.addValue("timeOrdered", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Order> getOrdersByAccountId(long accountId) {
        String sql = "SELECT * FROM orders WHERE account_id = :account_id ORDER BY time_ordered DESC";
        Map<String, Object> params = Map.of("account_id", accountId);
        return namedParameterJdbcTemplate.query(sql, params, orderRowMapper);
    }

    public List<Order> getExecutedOrdersByAccountIdAndSymbol(long accountId, String symbol) {
        String sql = """
            SELECT * FROM orders 
                     WHERE account_id = :accountId
                       AND symbol = :symbol
                       AND status = 'EXECUTED' ORDER BY time_executed DESC
        """;
        Map<String, Object> params =
                Map.of("accountId", accountId, "symbol", symbol);

        return namedParameterJdbcTemplate.query(sql, params, orderRowMapper);
    }

    public Order getOrderById(long id){
        String sql = "SELECT * FROM orders WHERE id = :id";

        Map<String, Object> params = Map.of("id", id);

        return namedParameterJdbcTemplate.queryForObject(sql, params, orderRowMapper);
    }



    public void markOrderExecuted(long orderId, LocalDateTime executedAt, BigDecimal profitLoss) {
        String sql = """
            UPDATE orders
            SET status = :status, time_executed = :executed_at, profit_loss = :profit_loss
            WHERE id = :order_id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("order_id", orderId);
        params.addValue("status", OrderStatus.EXECUTED.name());
        params.addValue("executed_at", executedAt);
        params.addValue("profit_loss", profitLoss);

        namedParameterJdbcTemplate.update(sql, params);
    }

    private static final RowMapper<Order> orderRowMapper = new RowMapper<>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Order(
                    rs.getInt("id"),
                    rs.getLong("account_id"),
                    rs.getString("symbol"),
                    rs.getBigDecimal("quantity"),
                    rs.getBigDecimal("price"),
                    OrderType.valueOf(rs.getString("type")),
                    OrderStatus.valueOf(rs.getString("status")),
                    rs.getTimestamp("time_ordered") != null
                            ? rs.getTimestamp("time_ordered").toLocalDateTime() : null,
                    rs.getTimestamp("time_executed") != null
                            ? rs.getTimestamp("time_executed").toLocalDateTime() : null,
                    rs.getBigDecimal("profit_loss")
            );
        }

    };

    public void deleteOrdersByAccountId(long accountId) {
        String sql = "DELETE FROM orders WHERE account_id = :accountId";
        Map<String, Object> params = Map.of("accountId", accountId);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
