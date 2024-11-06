package com.bank.mortgage.domain.model;

import java.math.BigDecimal;

/**
 * Represents the result of a mortgage feasibility check.
 */
public class MortgageFeasibilityResult {

    /**
     * Indicates whether the mortgage is feasible or not.
     */
    private final boolean feasible;

    /**
     * The monthly cost of the mortgage if it is feasible.
     */
    private final BigDecimal monthlyCost;

    public static Builder builder() {
        return new Builder();
    }

    private MortgageFeasibilityResult(Builder builder) {
        this.feasible = builder.feasible;
        this.monthlyCost = builder.monthlyCost;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public BigDecimal getMonthlyCost() {
        return monthlyCost;
    }

    /**
     * Builder class for constructing MortgageFeasibilityResult instances.
     */
    public static final class Builder {
        private boolean feasible;
        private BigDecimal monthlyCost;

        private Builder() {
        }

        public Builder feasible(boolean feasible) {
            this.feasible = feasible;
            return this;
        }

        public Builder monthlyCost(BigDecimal monthlyCost) {
            this.monthlyCost = monthlyCost;
            return this;
        }

        public MortgageFeasibilityResult build() {
            return new MortgageFeasibilityResult(this);
        }
    }
}
