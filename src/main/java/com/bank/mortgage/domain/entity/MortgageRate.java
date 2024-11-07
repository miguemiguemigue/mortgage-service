package com.bank.mortgage.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a mortgage rate.
 */
public class MortgageRate {

    /**
     * The maturity period of the mortgage in years.
     * This indicates the number of years over which the mortgage will be paid.
     */
    private final Integer maturityPeriod;

    /**
     * The interest rate applied to the mortgage.
     * The value is in decimal format (e.g., 0.05 for a 5% interest rate).
     * This rate is used to calculate the monthly payments of the mortgage.
     */
    private final BigDecimal interestRate;

    /**
     * The timestamp of the last update of the mortgage.
     * This is used to track when the mortgage information was last updated,
     * such as changes to the interest rate or maturity period.
     */
    private final LocalDateTime lastUpdate;

    private MortgageRate(Builder builder) {
        this.maturityPeriod = builder.maturityPeriod;
        this.interestRate = builder.interestRate;
        this.lastUpdate = builder.lastUpdate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getMaturityPeriod() {
        return maturityPeriod;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public static final class Builder {
        private Integer maturityPeriod;
        private BigDecimal interestRate;
        private LocalDateTime lastUpdate;

        private Builder() {
        }

        public Builder maturityPeriod(Integer maturityPeriod) {
            this.maturityPeriod = maturityPeriod;
            return this;
        }

        public Builder interestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public Builder lastUpdate(LocalDateTime lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public MortgageRate build() {
            return new MortgageRate(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MortgageRate that = (MortgageRate) o;
        return maturityPeriod.equals(that.maturityPeriod) && interestRate.equals(that.interestRate) && lastUpdate.equals(that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maturityPeriod, interestRate, lastUpdate);
    }
}
