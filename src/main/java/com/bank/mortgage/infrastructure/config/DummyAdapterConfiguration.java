package com.bank.mortgage.infrastructure.config;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.port.out.MortgageRateRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class DummyAdapterConfiguration {

    /**
     * Dummy impl until adapters are implemented
     * @return Dummy bean
     */
    @Bean
    public MortgageRateRepositoryPort mortgageRateRepositoryPort() {
        return new MortgageRateRepositoryPort() {
            @Override
            public List<MortgageRate> findAllMortgageRates() {
                return new ArrayList<>();
            }

            @Override
            public Optional<MortgageRate> findByMaturityPeriod(Integer maturityPeriod) {
                return Optional.empty();
            }
        };
    }
}
