package com.bank.mortgage.infrastructure.adapter.in.rest.mapper;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageRateResponse;

public class MortgageRateResponseMapper {

    /**
     * Map from domain object to infrastructure dto
     *
     * @param mortgageRate, Mortgage rate information
     * @return MortgageRateResponse
     */
    public static MortgageRateResponse fromDomain(MortgageRate mortgageRate) {
        if (mortgageRate == null) {
            return null;
        }

        // OpenAPI generator doesn't provide builder function
        MortgageRateResponse mortgageRateResponse = new MortgageRateResponse();
        mortgageRateResponse.setMaturityPeriod(mortgageRate.getMaturityPeriod());
        mortgageRateResponse.setInterestRate(mortgageRate.getInterestRate().doubleValue());
        mortgageRateResponse.setLastUpdate(mortgageRate.getLastUpdate());

        return mortgageRateResponse;

    }

}
