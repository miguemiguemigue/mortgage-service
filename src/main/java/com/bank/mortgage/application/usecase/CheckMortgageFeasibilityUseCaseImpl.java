package com.bank.mortgage.application.usecase;

import com.bank.mortgage.domain.entity.MortgageApplicant;
import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.exception.MortgageNotFoundException;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import com.bank.mortgage.domain.port.in.CheckMortgageFeasibilityUseCase;
import com.bank.mortgage.domain.port.out.MortgageRateRepositoryPort;
import com.bank.mortgage.domain.service.MortgageDomainService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class CheckMortgageFeasibilityUseCaseImpl implements CheckMortgageFeasibilityUseCase {

    private final MortgageRateRepositoryPort mortgageRateRepositoryPort;
    private final MortgageDomainService mortgageDomainService;

    @Override
    public MortgageFeasibilityResult checkMortgageFeasibility(Integer maturityPeriod, BigDecimal income,
                                                              BigDecimal loanValue, BigDecimal homeValue) {

        log.info("Checking mortgage feasibility for maturity period: {} years, income: {}, loan value: {}, home value: {}",
                maturityPeriod, income, loanValue, homeValue);

        // Validate maturity period before querying. Income, loan and home value will be validated in domain layer
        if (maturityPeriod == null || maturityPeriod <= 0) {
            log.error("Invalid maturity period: {}. It must be greater than zero.", maturityPeriod);
            throw new IllegalArgumentException("Invalid maturity period: It must be greater than zero.");
        }

        // Find Mortgage rate by maturity period
        Optional<MortgageRate> mortgageRate = mortgageRateRepositoryPort.findByMaturityPeriod(maturityPeriod);

        if (mortgageRate.isEmpty()) {
            log.error("No mortgage rate was found related to maturity period of: {} years. Cannot check mortgage feasibility",
                    maturityPeriod);
            throw new MortgageNotFoundException(
                    String.format("Could not find mortgage rate for maturity period of %s years", maturityPeriod)
            );
        }

        MortgageApplicant mortgageApplicant = new MortgageApplicant(income, loanValue, homeValue);

        return mortgageDomainService.checkMortgageFeasibility(mortgageRate.get(), mortgageApplicant);
    }

}
