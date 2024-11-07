package com.bank.mortgage.domain.port.in;

import com.bank.mortgage.domain.model.MortgageFeasibilityResult;

import java.math.BigDecimal;

public interface CheckMortgageFeasibilityUseCase {

    /**
     * Check a mortgage viability and calculates the monthly cost, if applicable
     *
     * @param income,         applicant income
     * @param maturityPeriod, mortgage maturity period in years
     * @param loanValue,      loan value for applicant
     * @param homeValue,      home's applicant value
     * @return A MortgageFeasibilityResult, containing the viability of the mortgage and the monthly cost, if applicable
     */
    MortgageFeasibilityResult checkMortgageFeasibility(Integer maturityPeriod, BigDecimal income, BigDecimal loanValue,
                                                       BigDecimal homeValue);

}