package com.bank.mortgage.domain.service;

import com.bank.mortgage.domain.entity.MortgageApplicant;
import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.exception.MortgageDomainException;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        MortgageRate mortgageRate = new MortgageRate(
                10, // 10 years
                BigDecimal.valueOf(0.05), // 5%,
                LocalDateTime.now()
        );

        // loan value greater than four times the income
        MortgageApplicant mortgageApplicant = new MortgageApplicant(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(90000),
                BigDecimal.valueOf(100000)
        );

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
        MortgageRate mortgageRate = new MortgageRate(
                10, // 10 years
                BigDecimal.valueOf(0.05), // 5%
                LocalDateTime.now() // Current timestamp for last update
        );

        // loan value greater than home value
        MortgageApplicant mortgageApplicant = new MortgageApplicant(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(110000),
                BigDecimal.valueOf(100000)
        );

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
        MortgageRate mortgageRate = new MortgageRate(
                10, // 10 years
                BigDecimal.valueOf(0.05), // 5%
                LocalDateTime.now()
        );

        MortgageApplicant mortgageApplicant = new MortgageApplicant(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(100000)
        );

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
        MortgageRate mortgageRate = new MortgageRate(
                10, // 10 years
                BigDecimal.valueOf(0.05), // 5%
                LocalDateTime.now() // Current timestamp for last update
        );

        MortgageApplicant mortgageApplicantWithNullIncome = new MortgageApplicant(
                null, // null income
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(100000)
        );


        MortgageApplicant mortgageApplicantWithNegativeIncome = new MortgageApplicant(
                BigDecimal.valueOf(-1000), // negative income
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(100000)
        );

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
        MortgageRate mortgageRate = new MortgageRate(
                10, // 10 years
                BigDecimal.valueOf(0.05), // 5%
                LocalDateTime.now() // Current timestamp for last update
        );

        MortgageApplicant mortgageApplicantWithNullLoanValue = new MortgageApplicant(
                BigDecimal.valueOf(5000),
                null, // null home value
                BigDecimal.valueOf(100000)
        );

        MortgageApplicant mortgageApplicantWithNegativeLoanValue = new MortgageApplicant(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(-10000), // negative loan value
                BigDecimal.valueOf(100000)
        );

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
        MortgageRate mortgageRate = new MortgageRate(
                10, // 10 years
                BigDecimal.valueOf(0.05), // 5%
                LocalDateTime.now() // Current timestamp for last update
        );

        MortgageApplicant mortgageApplicantWithNullHomeValue = new MortgageApplicant(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(50000),
                null // null home value
        );

        MortgageApplicant mortgageApplicantWithNegativeHomeValue = new MortgageApplicant(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(-100000) // negative home value
        );

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
        MortgageRate mortgageRateWithNullMaturity = new MortgageRate(
                null, // null maturity
                BigDecimal.valueOf(0.05), // Interest rate of 5%
                LocalDateTime.now() // Current timestamp for last update
        );

        MortgageRate mortgageRateWithNegativeMaturity = new MortgageRate(
                -3, // Not a positive maturity period
                BigDecimal.valueOf(0.05), // Interest rate of 5%
                LocalDateTime.now() // Current timestamp for last update
        );

        MortgageApplicant mortgageApplicant = new MortgageApplicant(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(100000)
        );

        // Then:
        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRateWithNullMaturity, mortgageApplicant))
                .withMessage("Invalid maturity period: It must be greater than zero.");

        assertThatExceptionOfType(MortgageDomainException.class)
                .isThrownBy(() -> mortgageDomainService.checkMortgageFeasibility(mortgageRateWithNegativeMaturity, mortgageApplicant))
                .withMessage("Invalid maturity period: It must be greater than zero.");
    }

}