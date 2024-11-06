package com.bank.mortgage.domain.service;

import com.bank.mortgage.domain.entity.MortgageApplicant;
import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.exception.MortgageDomainException;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;


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
        assertThat(result.getMonthlyCost()).isEqualTo(expectedMonthlyCost);
    }

    /**
     * This test checks that a MortgageDomainException is thrown when income is negative or null
     */
    @Test
    public void testCheckMortgageFeasibility_Given_null_or_negative_income_Then_throws_MortgageDomainException() {
        // Given:
        MortgageRate mortgageRate = MortgageRate.builder()
                .maturityPeriod(10)
                .interestRate(BigDecimal.valueOf(0.05))
                .build();

        MortgageApplicant mortgageApplicantWithNullIncome = MortgageApplicant.builder()
                .income(null) // null income
                .loanValue(BigDecimal.valueOf(50000))
                .homeValue(BigDecimal.valueOf(100000))
                .build();

        MortgageApplicant mortgageApplicantWithNegativeIncome = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(-1000)) // negative income
                .loanValue(BigDecimal.valueOf(50000))
                .homeValue(BigDecimal.valueOf(100000))
                .build();

        // Then:
        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicantWithNullIncome))
                .withMessage("Invalid income: It must be greater than zero.");

        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicantWithNegativeIncome))
                .withMessage("Invalid income: It must be greater than zero.");
    }

    /**
     * This test checks that a MortgageDomainException is thrown when loan is negative or null
     */
    @Test
    public void testCheckMortgageFeasibility_Given_null_or_negative_loan_value_Then_throws_MortgageDomainException() {
        // Given:
        MortgageRate mortgageRate = MortgageRate.builder()
                .maturityPeriod(10)
                .interestRate(BigDecimal.valueOf(0.05))
                .build();

        MortgageApplicant mortgageApplicantWithNullLoanValue = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(5000))
                .loanValue(null) // null home value
                .homeValue(BigDecimal.valueOf(100000))
                .build();

        MortgageApplicant mortgageApplicantWithNegativeLoanValue = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(5000))
                .loanValue(BigDecimal.valueOf(-10000)) // negative loan value
                .homeValue(BigDecimal.valueOf(100000))
                .build();

        // Then:
        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicantWithNullLoanValue))
                .withMessage("Invalid loan value: It must be greater than zero.");

        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicantWithNegativeLoanValue))
                .withMessage("Invalid loan value: It must be greater than zero.");
    }

    /**
     * This test checks that a MortgageDomainException is thrown when home value is negative or null
     */
    @Test
    public void testCheckMortgageFeasibility_Given_null_or_negative_home_value_Then_throws_MortgageDomainException() {
        // Given:
        MortgageRate mortgageRate = MortgageRate.builder()
                .maturityPeriod(10)
                .interestRate(BigDecimal.valueOf(0.05))
                .build();

        MortgageApplicant mortgageApplicantWithNullHomeValue = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(5000))
                .loanValue(BigDecimal.valueOf(50000))
                .homeValue(null) // null home value
                .build();

        MortgageApplicant mortgageApplicantWithNegativeHomeValue = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(5000))
                .loanValue(BigDecimal.valueOf(50000))
                .homeValue(BigDecimal.valueOf(-100000)) // negative home value
                .build();

        // Then
        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicantWithNullHomeValue))
                .withMessage("Invalid home value: It must be greater than zero.");

        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRate, mortgageApplicantWithNegativeHomeValue))
                .withMessage("Invalid home value: It must be greater than zero.");
    }

    /**
     * This test checks that a MortgageDomainException is thrown when maturity period is negative or null
     */
    @Test
    public void testCheckMortgageFeasibility_Given_null_or_negative_maturity_period_Then_throws_MortgageDomainException() {
        // Given:
        MortgageRate mortgageRateWithNullMaturity = MortgageRate.builder()
                .maturityPeriod(null)  // null maturity
                .interestRate(BigDecimal.valueOf(0.05))
                .build();

        MortgageRate mortgageRateWithNegativeMaturity = MortgageRate.builder()
                .maturityPeriod(-3)  // not positive maturity period
                .interestRate(BigDecimal.valueOf(0.05))
                .build();

        MortgageApplicant mortgageApplicant = MortgageApplicant.builder()
                .income(BigDecimal.valueOf(5000))
                .loanValue(BigDecimal.valueOf(30000))
                .homeValue(BigDecimal.valueOf(100000))
                .build();

        // Then:
        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRateWithNullMaturity, mortgageApplicant))
                .withMessage("Invalid maturity period: It must be greater than zero.");

        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRateWithNegativeMaturity, mortgageApplicant))
                .withMessage("Invalid maturity period: It must be greater than zero.");
    }

}