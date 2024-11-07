package com.bank.mortgage.infrastructure.adapter.out.database.h2.mapper;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.infrastructure.adapter.out.database.h2.entity.MortgageRateEntity;

public class MortgageRateEntityMapper {

    /**
     * Map Mortgage persistence object to domain model
     *
     * @param mortgageRateEntity Persistence layer mortgage rate object
     * @return Domain layer mortgage rate object
     */
    public static MortgageRate toDomain(MortgageRateEntity mortgageRateEntity) {
        if (mortgageRateEntity == null) {
            return null;
        }

        return MortgageRate.builder()
                .maturityPeriod(mortgageRateEntity.getMaturityPeriod())
                .interestRate(mortgageRateEntity.getInterestRate())
                .lastUpdate(mortgageRateEntity.getLastUpdate())
                .build();

    }


}
