package com.bank.mortgage.infrastructure.config;

import com.bank.mortgage.domain.service.MortgageDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MortgageServiceConfig {

    @Bean
    public MortgageDomainService mortgageDomainService() {
        return new MortgageDomainService();
    }

}
