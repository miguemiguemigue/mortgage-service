package com.bank.mortgage.infrastructure.adapter.out.database.h2.adapter;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.domain.port.out.MortgageRateRepositoryPort;
import com.bank.mortgage.infrastructure.adapter.out.database.h2.mapper.MortgageRateEntityMapper;
import com.bank.mortgage.infrastructure.adapter.out.database.h2.repository.MortgageRateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MortgageRateRepositoryAdapter implements MortgageRateRepositoryPort {

    private final MortgageRateRepository mortgageRateRepository;

    @Override
    public List<MortgageRate> findAllMortgageRates() {
        return mortgageRateRepository.findAll().stream()
                .map(MortgageRateEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MortgageRate> findByMaturityPeriod(Integer maturityPeriod) {
        return mortgageRateRepository.findByMaturityPeriod(maturityPeriod)
                .map(MortgageRateEntityMapper::toDomain);
    }
}
