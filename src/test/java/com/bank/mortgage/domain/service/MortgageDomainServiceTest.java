package com.bank.mortgage.domain.service;

import com.bank.mortgage.domain.entity.MortgageApplicant;
import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class MortgageDomainServiceTest {

    private final MortgageDomainService mortgageDomainService = new MortgageDomainService();

    /**
     * This test checks that a mortgage is not feasible if the loan value is greater than four times the income
     */
    @Test
    public void testCheckMortgageFeasibility_Given_loan_greater_than_four_times_income_Then_mortgage_is_not_feasible() {
        // Given:
        MortgageRate mortgageRate = MortgageRate.builder()
                .maturityPeriod(10) // 10 years
                .interestRate(BigDecimal.valueOf(0.05)) // 5%
                .build();

        // loan value greater than four times the income
        MortgageApplicant mortgageApplicant = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(5000))
                .loanValue(BigDecimal.valueOf(90000))
                .homeValue(BigDecimal.valueOf(100000))
                .build();

        // When:
        MortgageFeasibilityResult result = mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicant);

        // Then:
        assertThat(result.isFeasible()).isFalse();
        assertThat(result.getMonthlyCost()).isEqualTo(BigDecimal.ZERO);
    }

    /**
     * This test checks that a mortgage is not feasible if the loan value is greater than the home value
     */
    @Test
    public void testCheckMortgageFeasibility_Given_loan_greater_than_home_value_Then_mortgage_is_not_feasible() {
        // Given:
        MortgageRate mortgageRate = MortgageRate.builder()
                .maturityPeriod(10) // 10 years
                .interestRate(BigDecimal.valueOf(0.05)) // 5%
                .build();

        // loan value greater than home value
        MortgageApplicant mortgageApplicant = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(100000))
                .loanValue(BigDecimal.valueOf(110000))
                .homeValue(BigDecimal.valueOf(100000))
                .build();

        // When:
        MortgageFeasibilityResult result = mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicant);

        // Then:
        assertThat(result.isFeasible()).isFalse();
        assertThat(result.getMonthlyCost()).isEqualTo(BigDecimal.ZERO);
    }

    /**
     * This test checks the calculated monthly cost of a feasible mortgage
     */
    @Test
    public void testCheckMortgageFeasibility_Given_feasible_mortgage_Then_calculates_correct_monthly_cost() {
        // Given
        MortgageRate mortgageRate = MortgageRate.builder()
                .maturityPeriod(10) // 10 years
                .interestRate(BigDecimal.valueOf(0.05)) // 5%
                .build();

        MortgageApplicant mortgageApplicant = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(5000))
                .loanValue(BigDecimal.valueOf(10000))
                .homeValue(BigDecimal.valueOf(100000))
                .build();
        BigDecimal expectedMonthlyCost = BigDecimal.valueOf(106.07); // fixed-rate mortgage payment formula

        // When:
        MortgageFeasibilityResult result = mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicant);

        // Then:
        assertThat(result.isFeasible()).isTrue();
        assertThat(result.getMonthlyCost()).isCloseTo(expectedMonthlyCost, Percentage.withPercentage(0.01));
    }

}