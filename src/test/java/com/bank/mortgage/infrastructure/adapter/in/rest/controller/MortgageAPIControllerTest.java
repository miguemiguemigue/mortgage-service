package com.bank.mortgage.infrastructure.adapter.in.rest.controller;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import com.bank.mortgage.domain.port.in.CheckMortgageFeasibilityUseCase;
import com.bank.mortgage.domain.port.in.GetAllMortgageRatesUseCase;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageCheckRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MortgageAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetAllMortgageRatesUseCase getAllMortgageRatesUseCase;

    @MockBean
    private CheckMortgageFeasibilityUseCase checkMortgageFeasibilityUseCase;

    @Test
    void getInterestRates_Given_valid_request_Then_return_rates() throws Exception {
        // Given:
        MortgageRate mr1 = MortgageRate.builder()
                .interestRate(BigDecimal.valueOf(0.03))
                .maturityPeriod(15)
                .lastUpdate(LocalDateTime.now())
                .build();
        MortgageRate mr2 = MortgageRate.builder()
                .interestRate(BigDecimal.valueOf(0.05))
                .maturityPeriod(30)
                .lastUpdate(LocalDateTime.now())
                .build();
        List<MortgageRate> mortgageRates = Arrays.asList(
                mr1,
                mr2
        );

        when(getAllMortgageRatesUseCase.getAllMortgageRates()).thenReturn(mortgageRates);

        // When:
        mockMvc.perform(get("/v1/api/interest-rates"))
                // Then:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maturityPeriod").value(mr1.getMaturityPeriod()))
                .andExpect(jsonPath("$[0].interestRate").value(mr1.getInterestRate()))
                .andExpect(jsonPath("$[0].lastUpdate").value(mr1.getLastUpdate().truncatedTo(ChronoUnit.MILLIS).toString()))
                .andExpect(jsonPath("$[1].maturityPeriod").value(mr2.getMaturityPeriod()))
                .andExpect(jsonPath("$[1].interestRate").value(mr2.getInterestRate()))
                .andExpect(jsonPath("$[1].lastUpdate").value(mr2.getLastUpdate().truncatedTo(ChronoUnit.MILLIS).toString()));
        verify(getAllMortgageRatesUseCase).getAllMortgageRates();
    }

    @Test
    void checkMortgageFeasibility_Given_valid_request_Then_return_mortgage_check_result() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(5000.0);
        request.setLoanValue(150000.0);
        request.setHomeValue(200000.0);
        request.setMaturityPeriod(20);

        BigDecimal expectedMonthlyCost = BigDecimal.valueOf(200.0);
        boolean expectedIsFeasible = true;
        MortgageFeasibilityResult result = MortgageFeasibilityResult.builder()
                .feasible(expectedIsFeasible)
                .monthlyCost(expectedMonthlyCost)
                .build();

        doReturn(result).when(checkMortgageFeasibilityUseCase).checkMortgageFeasibility(
                eq(20),
                eq(BigDecimal.valueOf(5000.0)),
                eq(BigDecimal.valueOf(150000.0)),
                eq(BigDecimal.valueOf(200000.0))
        );

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feasible").value(expectedIsFeasible))
                .andExpect(jsonPath("$.monthlyCost").value(expectedMonthlyCost));

        verify(checkMortgageFeasibilityUseCase).checkMortgageFeasibility(eq(20), eq(BigDecimal.valueOf(5000.0)), eq(BigDecimal.valueOf(150000.0)), eq(BigDecimal.valueOf(200000.0)));
    }

    @Test
    void checkMortgageFeasibility_Given_negative_maturity_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(5000.00);
        request.setLoanValue(150000.00);
        request.setHomeValue(200000.00);
        request.setMaturityPeriod(-20); // negative maturity period

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("maturityPeriod: must be greater than or equal to 1"));

        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }

    @Test
    void checkMortgageFeasibility_Given_null_maturity_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(5000.00);
        request.setLoanValue(150000.00);
        request.setHomeValue(200000.00);
        request.setMaturityPeriod(null); // Null value for maturity

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("maturityPeriod: must not be null"));


        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }


    @Test
    void checkMortgageFeasibility_Given_negative_income_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(-5000.00); // Invalid income value
        request.setLoanValue(150000.00);
        request.setHomeValue(200000.00);
        request.setMaturityPeriod(20);

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("income: must be greater than 0"));

        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }

    @Test
    void checkMortgageFeasibility_Given_null_income_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(null); // Null value for income
        request.setLoanValue(150000.00);
        request.setHomeValue(200000.00);
        request.setMaturityPeriod(20);

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("income: must not be null"));


        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }

    @Test
    void checkMortgageFeasibility_Given_negative_loan_value_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(5000.00);
        request.setLoanValue(-150000.00); // Invalid loan value
        request.setHomeValue(200000.00);
        request.setMaturityPeriod(20);

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("loanValue: must be greater than 0"));

        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }

    @Test
    void checkMortgageFeasibility_Given_null_loan_value_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(5000.00);
        request.setLoanValue(null); // null loan value
        request.setHomeValue(200000.00);
        request.setMaturityPeriod(20);

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("loanValue: must not be null"));

        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }

    @Test
    void checkMortgageFeasibility_Given_negative_home_value_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(5000.00);
        request.setLoanValue(150000.00);
        request.setHomeValue(-200000.00); // negative home value
        request.setMaturityPeriod(20);

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("homeValue: must be greater than 0"));

        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }

    @Test
    void checkMortgageFeasibility_Given_null_home_value_Then_return_bad_request() throws Exception {
        // Given:
        MortgageCheckRequest request = new MortgageCheckRequest();
        request.setIncome(5000.00);
        request.setLoanValue(150000.00);
        request.setHomeValue(null); // null home value
        request.setMaturityPeriod(20);

        // When:
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                // Then:
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("Bad Request"))
                .andExpect(jsonPath("$.errorMessage").value("homeValue: must not be null"));

        verify(checkMortgageFeasibilityUseCase, never()).checkMortgageFeasibility(any(), any(), any(), any());
    }

}