package com.bank.mortgage.domain.service;

import com.bank.mortgage.domain.entity.MortgageApplicant;
import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;

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

        log.info("Calculating mortgage feasibility for income: {}, home value: {}, maturity period: {} years, loan: {}",
                mortgageApplicant.getIncome(), mortgageApplicant.getHomeValue(), mortgageRate.getMaturityPeriod(),
                mortgageApplicant.getLoanValue());

        /*
        Check mortgage feasibility. A mortgage should not exceed:
            - 4 times the income
            - The home value
         */

        BigDecimal maxAllowedLoanByIncome = mortgageApplicant.getIncome().multiply(BigDecimal.valueOf(4));
        BigDecimal homeValue = mortgageApplicant.getHomeValue();

        if (mortgageApplicant.getLoanValue().compareTo(maxAllowedLoanByIncome) > 0 ||
                mortgageApplicant.getLoanValue().compareTo(homeValue) > 0) {

            log.info("Mortgage is not feasible");
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

        BigDecimal monthlyInterestRate = interestRate.divide(BigDecimal.valueOf(12), MathContext.DECIMAL128);
        int numPayments = maturityPeriod * 12;

        BigDecimal onePlusMonthlyInterestRatePowNumPayments =
                BigDecimal.ONE.add(monthlyInterestRate).pow(numPayments, MathContext.DECIMAL128);

        BigDecimal numerator = monthlyInterestRate.multiply(onePlusMonthlyInterestRatePowNumPayments);
        BigDecimal denominator = onePlusMonthlyInterestRatePowNumPayments.subtract(BigDecimal.ONE);

        return loanValue.multiply(numerator).divide(denominator, MathContext.DECIMAL128);
    }

}
