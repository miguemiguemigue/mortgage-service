package com.bank.mortgage.infrastructure.adapter.out.database.h2.repository;

import com.bank.mortgage.infrastructure.adapter.out.database.h2.entity.MortgageRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MortgageRateRepository extends JpaRepository<MortgageRateEntity, Long> {

    /**
     * Find Mortgage rate by maturity period
     *
     * @param maturityPeriod, maturity period in years
     * @return MortgageRateEntity related to a given maturity period
     */
    Optional<MortgageRateEntity> findByMaturityPeriod(int maturityPeriod);

}
