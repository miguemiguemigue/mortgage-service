package com.bank.mortgage.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a mortgage rate.
 *
 * @param maturityPeriod The maturity period of the mortgage in years.
 * @param interestRate The interest rate applied to the mortgage.
 * @param lastUpdate The timestamp of the last update of the mortgage.
 */
public record MortgageRate(
        Integer maturityPeriod,
        BigDecimal interestRate,
        LocalDateTime lastUpdate) {
}
