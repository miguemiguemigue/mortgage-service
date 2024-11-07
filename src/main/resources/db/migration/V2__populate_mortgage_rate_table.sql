INSERT INTO mortgage_rate (maturity_period, interest_rate, last_update)
VALUES
    (5, 0.03, CURRENT_TIMESTAMP),  -- 3% of interest rate for 5 years
    (10, 0.035, CURRENT_TIMESTAMP), -- 3.5% of interest rate for 10 years
    (15, 0.04, CURRENT_TIMESTAMP),  -- 4% of interest rate for 15 years
    (20, 0.045, CURRENT_TIMESTAMP), -- 4.5% of interest rate for 20 years
    (25, 0.05, CURRENT_TIMESTAMP),  -- 5% of interest rate for 25 years
    (30, 0.055, CURRENT_TIMESTAMP); -- 5.5% of interest rate for 30 years