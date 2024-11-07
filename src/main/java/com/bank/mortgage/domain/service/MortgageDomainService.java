package com.bank.mortgage.domain.service;

import com.bank.mortgage.domain.entity.MortgageApplicant;
import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.exception.MortgageDomainException;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
public class MortgageDomainService {

    /**
     * Check if a Mortgage is feasible.
     * If true, calculates monthly cost.
     * If false, monthly cost will be zero.
     *
     * @param mortgageRate,      Mortgage information
     * @param mortgageApplicant, Mortgage applicant information
     * @return Mortgage feasibility and monthly cost
     */
    public MortgageFeasibilityResult checkMortgageFeasibility(MortgageRate mortgageRate,
                                                              MortgageApplicant mortgageApplicant) {

        // Validate input data
        validateMortgageData(mortgageRate, mortgageApplicant);

        log.info("Calculating mortgage feasibility for income: {}, home value: {}, maturity period: {} years, loan: {}",
                mortgageApplicant.getIncome(), mortgageApplicant.getHomeValue(), mortgageRate.getMaturityPeriod(),
                mortgageApplicant.getLoanValue());

        /*
        Check mortgage feasibility. A mortgage should not exceed:
            - 4 times the income
            - The home value
         */

        BigDecimal maxAllowedLoanByIncome = mortgageApplicant.getIncome().multiply(BigDecimal.valueOf(4));
        boolean loanExceedsFourTimesIncome = mortgageApplicant.getLoanValue().compareTo(maxAllowedLoanByIncome) > 0;
        boolean loanExceedsHomeValue = mortgageApplicant.getLoanValue().compareTo(mortgageApplicant.getHomeValue()) > 0;

        if (loanExceedsFourTimesIncome) {
            log.info("Loan exceeds 4 times the income. Mortgage is not feasible.");
        }

        if (loanExceedsHomeValue) {
            log.info("Loan exceeds the home value. Mortgage is not feasible.");
        }

        if (loanExceedsFourTimesIncome || loanExceedsHomeValue) {
            // If not feasible, return with 0 monthly cost
            return MortgageFeasibilityResult.builder()
                    .feasible(false)
                    .monthlyCost(BigDecimal.ZERO)
                    .build();
        }
        // If feasible, calculates monthly cost
        BigDecimal monthlyCost = calculateMonthlyCostFixedRateMortgage(mortgageRate.getMaturityPeriod(),
                mortgageRate.getInterestRate(), mortgageApplicant.getLoanValue());


        log.info("Mortgage is feasible. Calculated monthly cost: {}", monthlyCost);

        return MortgageFeasibilityResult.builder()
                .feasible(true)
                .monthlyCost(monthlyCost)
                .build();

    }

    /**
     * This method calculates the monthly cost of a mortgage with fixed interest rate.
     * Implements the fixed-rate mortgage payment formula.
     * C = P x (i ((1+i)^n)) / (((1+i)^n) - 1)
     * Where:
     * C = Monthly cost
     * P = Loan value
     * i = Monthly interest rate (anual interest rate divided by 12)
     * n = Number of months to paid (years * 12)
     *
     * @return Monthly cost of the mortgage
     */
    private BigDecimal calculateMonthlyCostFixedRateMortgage(Integer maturityPeriod, BigDecimal interestRate,
                                                             BigDecimal loanValue) {

        // i
        BigDecimal monthlyInterestRate = interestRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        // n
        int numPayments = maturityPeriod * 12;

        // (1+i)^n
        BigDecimal onePlusMonthlyInterestRatePowNumPayments =
                BigDecimal.ONE.add(monthlyInterestRate).pow(numPayments, MathContext.DECIMAL128);

        BigDecimal numerator = monthlyInterestRate.multiply(onePlusMonthlyInterestRatePowNumPayments);
        BigDecimal denominator = onePlusMonthlyInterestRatePowNumPayments.subtract(BigDecimal.ONE);

        return loanValue.multiply(numerator)
                .divide(denominator, MathContext.DECIMAL128)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Validates the mortgage applicant and mortgage rate data.
     * Throws MortgageDomainException if any of the data is invalid.
     */
    private void validateMortgageData(MortgageRate mortgageRate, MortgageApplicant mortgageApplicant) {
        // Validaciones de entrada
        if (mortgageApplicant.getIncome() == null || mortgageApplicant.getIncome().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid income: {}. It must be greater than zero.", mortgageApplicant.getIncome());
            throw new MortgageDomainException("Invalid income: It must be greater than zero.");
        }

        if (mortgageApplicant.getLoanValue() == null || mortgageApplicant.getLoanValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid loan value: {}. It must be greater than zero.", mortgageApplicant.getLoanValue());
            throw new MortgageDomainException("Invalid loan value: It must be greater than zero.");
        }

        if (mortgageApplicant.getHomeValue() == null || mortgageApplicant.getHomeValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid home value: {}. It must be greater than zero.", mortgageApplicant.getHomeValue());
            throw new MortgageDomainException("Invalid home value: It must be greater than zero.");
        }

        if (mortgageRate.getMaturityPeriod() == null || mortgageRate.getMaturityPeriod() <= 0) {
            log.error("Invalid maturity period: {}. It must be greater than zero.", mortgageRate.getMaturityPeriod());
            throw new MortgageDomainException("Invalid maturity period: It must be greater than zero.");
        }
    }

}
