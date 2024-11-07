package com.bank.mortgage.infrastructure.adapter.in.rest.controller;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.model.MortgageFeasibilityResult;
import com.bank.mortgage.domain.port.in.CheckMortgageFeasibilityUseCase;
import com.bank.mortgage.domain.port.in.GetAllMortgageRatesUseCase;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageCheckRequest;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageCheckResponse;
import com.bank.mortgage.infrastructure.adapter.in.rest.dto.MortgageRateResponse;
import com.bank.mortgage.infrastructure.adapter.in.rest.mapper.MortgageCheckResponseMapper;
import com.bank.mortgage.infrastructure.adapter.in.rest.mapper.MortgageRateResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/")
public class MortgageAPIController implements MortgageApi {

    private final GetAllMortgageRatesUseCase getAllMortgageRatesUseCase;
    private final CheckMortgageFeasibilityUseCase checkMortgageFeasibilityUseCase;

    @Override
    public ResponseEntity<List<MortgageRateResponse>> getInterestRates() {
        log.info("Getting all mortgage rates");

        List<MortgageRate> allMortgageRates = getAllMortgageRatesUseCase.getAllMortgageRates();

        List<MortgageRateResponse> mortgageRateResponses = allMortgageRates.stream()
                .map(MortgageRateResponseMapper::fromDomain)
                .collect(Collectors.toList());

        return ResponseEntity.ok(mortgageRateResponses);
    }

    @Override
    public ResponseEntity<MortgageCheckResponse> checkMortgageFeasibility(MortgageCheckRequest mortgageCheckRequest) {
        log.info("Checking mortgage feasibility");

        // Parse double to BigDecimal
        BigDecimal income = BigDecimal.valueOf(mortgageCheckRequest.getIncome());
        BigDecimal loanValue = BigDecimal.valueOf(mortgageCheckRequest.getLoanValue());
        BigDecimal homeValue = BigDecimal.valueOf(mortgageCheckRequest.getHomeValue());

        MortgageFeasibilityResult mortgageFeasibilityResult =
                checkMortgageFeasibilityUseCase.checkMortgageFeasibility(mortgageCheckRequest.getMaturityPeriod(),
                        income, loanValue, homeValue);

        MortgageCheckResponse mortgageCheckResponse = MortgageCheckResponseMapper.fromDomain(mortgageFeasibilityResult);

        return ResponseEntity.ok(mortgageCheckResponse);
    }
}
