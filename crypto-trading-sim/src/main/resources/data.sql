INSERT INTO account (id, first_name, last_name, email, balance, currency)
VALUES (1, 'Test', 'User',
        'test.user@example.com',
        10000.00, 'USD');

INSERT INTO portfolio (symbol, quantity)
VALUES('BTC', 0.05),
      ('ETH', 1.2),
      ('DOGE', 3000.0);