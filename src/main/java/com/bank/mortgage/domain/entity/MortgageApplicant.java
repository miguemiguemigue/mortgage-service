package com.bank.mortgage.domain.entity;

import java.math.BigDecimal;

/**
 * Represents a mortgage applicant in the system.
 */
public class MortgageApplicant {

    /**
     * The income of the mortgage applicant.
     * This represents the monthly or annual income of the individual applying for the mortgage.
     */
    private final BigDecimal income;

    /**
     * The value of the mortgage loan requested by the applicant.
     * This is the total amount of money the applicant is seeking to borrow.
     */
    private final BigDecimal loanValue;

    /**
     * The value of the home the applicant is purchasing.
     * This represents the total value of the property the applicant is planning to buy.
     */
    private final BigDecimal homeValue;

    private MortgageApplicant(Builder builder) {
        this.income = builder.income;
        this.loanValue = builder.loanValue;
        this.homeValue = builder.homeValue;
    }

    public static Builder builder() {
        return new Builder();
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getLoanValue() {
        return loanValue;
    }

    public BigDecimal getHomeValue() {
        return homeValue;
    }

    /**
     * Builder class for constructing MortgageApplicant instances.
     */
    public static final class Builder {
        private BigDecimal income;
        private BigDecimal loanValue;
        private BigDecimal homeValue;

        private Builder() {
        }

        public Builder income(BigDecimal income) {
            this.income = income;
            return this;
        }

        public Builder loanValue(BigDecimal loanValue) {
            this.loanValue = loanValue;
            return this;
        }

        public Builder homeValue(BigDecimal homeValue) {
            this.homeValue = homeValue;
            return this;
        }

        public MortgageApplicant build() {
            return new MortgageApplicant(this);
        }
    }
}