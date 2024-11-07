package com.bank.mortgage.infrastructure.adapter.in.rest.mapper;

import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageCheckResponse;

public class MortgageCheckResponseMapper {

    /**
     * Map from domain object to infrastructure dto
     *
     * @param mortgageFeasibilityResult, Mortgage feasibility check result, including monthly cost if feasible
     * @return MortgageCheckResponse
     */
    public static MortgageCheckResponse fromDomain(MortgageFeasibilityResult mortgageFeasibilityResult) {
        if (mortgageFeasibilityResult == null) {
            return null;
        }

        // OpenAPI generator doesn't provide builder function
        MortgageCheckResponse mortgageRateResponse = new MortgageCheckResponse();
        mortgageRateResponse.setFeasible(mortgageFeasibilityResult.isFeasible());
        mortgageRateResponse.setMonthlyCost(mortgageFeasibilityResult.getMonthlyCost().doubleValue());

        return mortgageRateResponse;

    }

}
