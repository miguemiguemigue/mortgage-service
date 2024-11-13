package com.bank.mortgage.application.usecase;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.port.out.MortgageRateRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllMortgageRatesUseCaseImplTest {

    @Mock
    private MortgageRateRepositoryPort mortgageRateRepositoryPort;

    @InjectMocks
    private GetAllMortgageRatesUseCaseImpl getAllMortgageRatesUseCase;

    @Test
    public void getAllMortgageRates_Given_repository_return_multiple_mortgage_rates_Then_return_all_mortgage_rates() {
        // Given:
        LocalDateTime date = LocalDateTime.now();

        MortgageRate mr1 = new MortgageRate(5, BigDecimal.valueOf(0.05), date);
        MortgageRate mr2 = new MortgageRate(10, BigDecimal.valueOf(0.05), date);

        when(mortgageRateRepositoryPort.findAllMortgageRates())
                .thenReturn(List.of(mr1, mr2));

        // When
        List<MortgageRate> result = getAllMortgageRatesUseCase.getAllMortgageRates();

        // Then
        assertThat(result).containsExactly(mr1, mr2);
        verify(mortgageRateRepositoryPort).findAllMortgageRates();
    }

    @Test
    public void getAllMortgageRates_Given_repository_return_empty_list_Then_return_empty_list() {
        // Given:
        when(mortgageRateRepositoryPort.findAllMortgageRates())
                .thenReturn(new ArrayList<>());

        // When
        List<MortgageRate> result = getAllMortgageRatesUseCase.getAllMortgageRates();

        // Then
        assertThat(result).isEmpty();
        verify(mortgageRateRepositoryPort).findAllMortgageRates();
    }

}