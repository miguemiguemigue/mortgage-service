package com.bank.mortgage.application.usecase;

import com.bank.mortgage.domain.entity.MortgageApplicant;
import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.exception.MortgageNotFoundException;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import com.bank.mortgage.domain.port.out.MortgageRateRepositoryPort;
import com.bank.mortgage.domain.service.MortgageDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckMortgageFeasibilityUseCaseImplTest {

    @Mock
    private MortgageRateRepositoryPort mortgageRateRepositoryPort;

    @Mock
    private MortgageDomainService mortgageDomainService;

    @InjectMocks
    private CheckMortgageFeasibilityUseCaseImpl checkMortgageFeasibilityUseCase;

    @Test
    public void checkMortgageFeasibility_Given_found_mortgage_rate_by_maturity_Then_return_feasibility_result() {
        // Given:
        Integer maturityPeriod = 10;
        BigDecimal income = BigDecimal.valueOf(10000);
        BigDecimal loanValue = BigDecimal.valueOf(7000);
        BigDecimal homeValue = BigDecimal.valueOf(60000);

        MortgageRate mortgageRate = MortgageRate.builder()
                .maturityPeriod(10)
                .interestRate(BigDecimal.valueOf(0.1))
                .lastUpdate(LocalDateTime.now())
                .build();

        MortgageApplicant mortgageApplicant = MortgageApplicant.builder()
                .income(income)
                .loanValue(loanValue)
                .homeValue(homeValue)
                .build();

        Optional<MortgageRate> mortgageRate1 = Optional.of(mortgageRate);
        when(mortgageRateRepositoryPort.findByMaturityPeriod(maturityPeriod))
                .thenReturn(mortgageRate1);

        when(mortgageDomainService.checkMortgageFeasibility(eq(mortgageRate), eq(mortgageApplicant)))
                .thenReturn(
                        MortgageFeasibilityResult.builder()
                                .feasible(true)
                                .monthlyCost(BigDecimal.valueOf(100))
                                .build()
                );

        // When
        MortgageFeasibilityResult result =
                checkMortgageFeasibilityUseCase.checkMortgageFeasibility(maturityPeriod, income, loanValue, homeValue);

        // Then
        assertThat(result).isEqualTo(result);
        verify(mortgageRateRepositoryPort).findByMaturityPeriod(eq(maturityPeriod));
        verify(mortgageDomainService).checkMortgageFeasibility(eq(mortgageRate), eq(mortgageApplicant));
    }

    @Test
    public void checkMortgageFeasibility_Given_not_found_mortgage_rate_by_maturity_Then_return_MortgageNotFoundException() {
        // Given:
        Integer maturityPeriod = 10;
        BigDecimal income = BigDecimal.valueOf(10000);
        BigDecimal loanValue = BigDecimal.valueOf(7000);
        BigDecimal homeValue = BigDecimal.valueOf(60000);

        when(mortgageRateRepositoryPort.findByMaturityPeriod(maturityPeriod))
                .thenReturn(Optional.empty());

        // When
        assertThatExceptionOfType(MortgageNotFoundException.class)
                .isThrownBy(() -> checkMortgageFeasibilityUseCase.checkMortgageFeasibility(maturityPeriod, income,
                        loanValue, homeValue))
                .withMessage("Could not find mortgage rate for maturity period of 10 years");

        // Then
        verify(mortgageRateRepositoryPort).findByMaturityPeriod(eq(maturityPeriod));
        verify(mortgageDomainService, never()).checkMortgageFeasibility(any(), any());
    }

    @Test
    public void checkMortgageFeasibility_Given_null_negative_or_zero_maturity_Then_return_MortgageDomainException() {
        // Given:
        Integer nullMaturityPeriod = null;
        Integer negativeMaturityPeriod = -10;
        Integer zeroMaturityPeriod = 0;
        BigDecimal income = BigDecimal.valueOf(10000);
        BigDecimal loanValue = BigDecimal.valueOf(7000);
        BigDecimal homeValue = BigDecimal.valueOf(60000);

        // When
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> checkMortgageFeasibilityUseCase.checkMortgageFeasibility(nullMaturityPeriod, income,
                        loanValue, homeValue))
                .withMessage("Invalid maturity period: It must be greater than zero.");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> checkMortgageFeasibilityUseCase.checkMortgageFeasibility(negativeMaturityPeriod, income,
                        loanValue, homeValue))
                .withMessage("Invalid maturity period: It must be greater than zero.");


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> checkMortgageFeasibilityUseCase.checkMortgageFeasibility(zeroMaturityPeriod, income,
                        loanValue, homeValue))
                .withMessage("Invalid maturity period: It must be greater than zero.");


        // Then
        verify(mortgageRateRepositoryPort, never()).findByMaturityPeriod(any());
        verify(mortgageDomainService, never()).checkMortgageFeasibility(any(), any());
    }


}