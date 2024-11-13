package com.bank.mortgage.domain.entity;

import java.math.BigDecimal;

/**
 * Represents a mortgage applicant in the system.
 *
 * @param income The income of the mortgage applicant.
 *               This represents the annual income of the individual applying for the mortgage.
 * @param loanValue The value of the mortgage loan requested by the applicant.
 *                  This is the total amount of money the applicant is seeking to borrow.
 * @param homeValue The value of the home the applicant is purchasing.
 *                  This represents the total value of the property the applicant is planning to buy.
 */
public record MortgageApplicant(
        BigDecimal income,
        BigDecimal loanValue,
        BigDecimal homeValue) {
}
