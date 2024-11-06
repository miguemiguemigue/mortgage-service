package com.bank.mortgage.domain.port.in;

import com.bank.mortgage.domain.entity.MortgageRate;

import java.util.List;

public interface GetAllMortgageRatesUseCase {

    /**
     * Get all mortgage rates present in the system
     *
     * @return List of mortgage rates
     */
    List<MortgageRate> getAllMortgageRates();
}
