package com.bank.mortgage.application.usecase;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.port.in.GetAllMortgageRatesUseCase;
import com.bank.mortgage.domain.port.out.MortgageRateRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class GetAllMortgageRatesUseCaseImpl implements GetAllMortgageRatesUseCase {

    private final MortgageRateRepositoryPort mortgageRateRepositoryPort;

    @Override
    public List<MortgageRate> getAllMortgageRates() {
        log.info("Finding all mortgage rates in the system");
        List<MortgageRate> allMortgageRates = mortgageRateRepositoryPort.findAllMortgageRates();
        log.info("Found {} mortgage rates", allMortgageRates.size());
        return allMortgageRates;
    }
}
