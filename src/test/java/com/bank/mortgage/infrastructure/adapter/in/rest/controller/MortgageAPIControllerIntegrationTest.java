package com.bank.mortgage.infrastructure.adapter.in.rest.controller;

import com.bank.mortgage.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageCheckRequest;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageCheckResponse;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageRateResponse;
import com.bank.mortgage.infrastructure.adapter.out.database.h2.entity.MortgageRateEntity;
import com.bank.mortgage.infrastructure.adapter.out.database.h2.repository.MortgageRateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MortgageAPIControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MortgageRateRepository mortgageRateRepository;

    @BeforeEach
    void setUp() {
        // Clean the database between tests
        mortgageRateRepository.deleteAll();
    }

    /**
     * Check get interest rates return empty list if there's no mortgage rates in the system
     */
    @Test
    void getInterestRates_Given_empty_database_Then_return_empty_list() {
        // When:
        ResponseEntity<List<MortgageRateResponse>> response = restTemplate.exchange(
                "/v1/api/interest-rates", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        // Then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<MortgageRateResponse> rates = response.getBody();
        assertThat(rates).hasSize(0);
    }

    /**
     * Check get interest rates return all mortgage if there are existing mortgage rates in the system
     */
    @Test
    void getInterestRates_Given_existing_mortgage_rates_Then_return_all() {
        // Given:
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.03))
                .maturityPeriod(15)
                .lastUpdate(LocalDateTime.now())
                .build();

        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.05))
                .maturityPeriod(30)
                .lastUpdate(LocalDateTime.now())
                .build();

        mortgageRateRepository.saveAll(Arrays.asList(mr1, mr2));

        // When:
        ResponseEntity<List<MortgageRateResponse>> response = restTemplate.exchange(
                "/v1/api/interest-rates", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });

        // Then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<MortgageRateResponse> rates = response.getBody();

        assertThat(rates).hasSize(2);
        assertThat(rates.get(0).getMaturityPeriod()).isEqualTo(mr1.getMaturityPeriod());
        assertThat(rates.get(0).getInterestRate()).isEqualTo(mr1.getInterestRate().doubleValue());
        assertThat(rates.get(1).getMaturityPeriod()).isEqualTo(mr2.getMaturityPeriod());
        assertThat(rates.get(1).getInterestRate()).isEqualTo(mr2.getInterestRate().doubleValue());
    }

    /**
     * Check a mortgage is feasible if meets required conditions and correctly calculate monthly cost.
     * Used fixed-rate mortgage payment formula to calculate monthly cost
     */
    @Test
    void checkMortgageFeasibility_Given_related_mortgage_and_feasible_mortgage_Then_return_true_and_monthly_cost() {
        // Given:

        // Persist mortgage rates
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.03))
                .maturityPeriod(15)
                .lastUpdate(LocalDateTime.now())
                .build();

        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.05))
                .maturityPeriod(10)
                .lastUpdate(LocalDateTime.now())
                .build();

        mortgageRateRepository.saveAll(Arrays.asList(mr1, mr2));

        MortgageCheckRequest mortgageCheckRequest = new MortgageCheckRequest()
                .maturityPeriod(10)
                .income(5000d)
                .loanValue(10000d)
                .homeValue(100000d);

        // Expected monthly cost for a mortgage of 10 years and a loan of 10000
        // Used fixed-rate mortgage payment formula to calculate it
        double expectedMonthlyCost = 106.07;
        // When:
        ResponseEntity<MortgageCheckResponse> response = restTemplate.exchange(
                "/v1/api/mortgage-check", HttpMethod.POST,
                new HttpEntity<>(mortgageCheckRequest), MortgageCheckResponse.class);

        // Then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MortgageCheckResponse mortgageCheckResponse = response.getBody();
        assertThat(mortgageCheckResponse).isNotNull();

        assertThat(mortgageCheckResponse.getFeasible()).isEqualTo(true);
        assertThat(mortgageCheckResponse.getMonthlyCost()).isNotNull();
        assertThat(mortgageCheckResponse.getMonthlyCost()).isEqualTo(expectedMonthlyCost);
    }

    /**
     * Check a mortgage is not feasible if loan is greater than four times the income. Monthly cost should be zero
     */
    @Test
    void checkMortgageFeasibility_Given_not_feasible_mortgage_because_of_loan_greater_than_four_times_income_Then_return_false_and_monthly_cost_0() {
        // Given:

        // Persist mortgage rates
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.03))
                .maturityPeriod(15)
                .lastUpdate(LocalDateTime.now())
                .build();

        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.05))
                .maturityPeriod(10)
                .lastUpdate(LocalDateTime.now())
                .build();

        mortgageRateRepository.saveAll(Arrays.asList(mr1, mr2));

        MortgageCheckRequest mortgageCheckRequest = new MortgageCheckRequest()
                .maturityPeriod(10)
                .income(5000d)
                .loanValue(21000d) // loan value more than 4 times the income, so it's not feasible
                .homeValue(100000d);

        // As it's not feasible, monthly cost should be 0
        double expectedMonthlyCost = 0;
        // When:
        ResponseEntity<MortgageCheckResponse> response = restTemplate.exchange(
                "/v1/api/mortgage-check", HttpMethod.POST,
                new HttpEntity<>(mortgageCheckRequest), MortgageCheckResponse.class);

        // Then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MortgageCheckResponse mortgageCheckResponse = response.getBody();
        assertThat(mortgageCheckResponse).isNotNull();

        assertThat(mortgageCheckResponse.getFeasible()).isEqualTo(false);
        assertThat(mortgageCheckResponse.getMonthlyCost()).isNotNull();
        assertThat(mortgageCheckResponse.getMonthlyCost()).isEqualTo(expectedMonthlyCost);
    }

    /**
     * Check a mortgage is not feasible if loan is greater than home value. Monthly cost should be zero
     */
    @Test
    void checkMortgageFeasibility_Given_not_feasible_mortgage_because_of_loan_greater_than_home_value_Then_return_false_and_monthly_cost_0() {
        // Given:

        // Persist mortgage rates
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.03))
                .maturityPeriod(15)
                .lastUpdate(LocalDateTime.now())
                .build();

        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.05))
                .maturityPeriod(10)
                .lastUpdate(LocalDateTime.now())
                .build();

        mortgageRateRepository.saveAll(Arrays.asList(mr1, mr2));

        MortgageCheckRequest mortgageCheckRequest = new MortgageCheckRequest()
                .maturityPeriod(10)
                .income(30000d)
                .loanValue(105000d) // loan value greater than home value, so it's not feasible
                .homeValue(100000d);

        // As it's not feasible, monthly cost should be 0
        double expectedMonthlyCost = 0;
        // When:
        ResponseEntity<MortgageCheckResponse> response = restTemplate.exchange(
                "/v1/api/mortgage-check", HttpMethod.POST,
                new HttpEntity<>(mortgageCheckRequest), MortgageCheckResponse.class);

        // Then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MortgageCheckResponse mortgageCheckResponse = response.getBody();
        assertThat(mortgageCheckResponse).isNotNull();

        assertThat(mortgageCheckResponse.getFeasible()).isEqualTo(false);
        assertThat(mortgageCheckResponse.getMonthlyCost()).isNotNull();
        assertThat(mortgageCheckResponse.getMonthlyCost()).isEqualTo(expectedMonthlyCost);
    }

    /**
     * Check a validation error is shown if there's no related mortgage rate in the system for the given maturity period
     */
    @Test
    void checkMortgageFeasibility_Given_no_related_mortgage_Then_return_MortgageNotFoundException() {
        // Given:

        // Persist mortgage rates
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.03))
                .maturityPeriod(15)
                .lastUpdate(LocalDateTime.now())
                .build();

        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .interestRate(BigDecimal.valueOf(0.05))
                .maturityPeriod(10)
                .lastUpdate(LocalDateTime.now())
                .build();

        mortgageRateRepository.saveAll(Arrays.asList(mr1, mr2));

        MortgageCheckRequest mortgageCheckRequest = new MortgageCheckRequest()
                .maturityPeriod(8) // using a maturity period that does not have a related mortgage in the system
                .income(5000d)
                .loanValue(10000d)
                .homeValue(100000d);

        // When:
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/v1/api/mortgage-check", HttpMethod.POST,
                new HttpEntity<>(mortgageCheckRequest), ErrorResponse.class);

        // Then:
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getErrorCode()).isEqualTo("Not Found");
        assertThat(errorResponse.getErrorMessage()).isEqualTo("Could not find mortgage rate for maturity period of 8 years");
    }

}