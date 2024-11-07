CREATE TABLE IF NOT EXISTS mortgage_rate (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             maturity_period INT NOT NULL,
                                             interest_rate DECIMAL(5, 4) NOT NULL,
    last_update TIMESTAMP NOT NULL
    );

-- Add unique constraint to maturity period
CREATE UNIQUE INDEX idx_maturity_period ON mortgage_rate(maturity_period);
