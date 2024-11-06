package com.bank.mortgage.domain.port.out;

import com.bank.mortgage.domain.entity.MortgageRate;

import java.util.List;
import java.util.Optional;

public interface MortgageRateRepositoryPort {

    /**
     * Find all mortgage rates
     *
     * @return A list of mortgage rates in the system
     */
    List<MortgageRate> findAllMortgageRates();

    /**
     * Find the mortgage rate related to a given maturityPeriod
     *
     * @param maturityPeriod, maturity period of the mortgage rate in years
     * @return An Optional containing the MortgageRate, if found.
     */
    Optional<MortgageRate> findByMaturityPeriod(Integer maturityPeriod);

}
