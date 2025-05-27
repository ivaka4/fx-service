CREATE TABLE conversion (
  id INT AUTO_INCREMENT PRIMARY KEY,
  transaction_id VARCHAR(36)      NOT NULL,
  from_currency VARCHAR(3)     NOT NULL,
  to_currency   VARCHAR(3)     NOT NULL,
  rate_precision DECIMAL(19,6) NOT NULL,
  amount        DECIMAL(19,6)  NOT NULL,
  converted_amount DECIMAL(19,6) NOT NULL,
  converted_at   DATETIME(6)    NOT NULL,
  INDEX idx_txn (transaction_id),
  INDEX idx_date (converted_at)
);