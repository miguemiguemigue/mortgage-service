package com.bank.mortgage.infrastructure.adapter.out.database.h2.adapter;

import com.bank.mortgage.domain.entity.MortgageRate;
import com.bank.mortgage.infrastructure.adapter.out.database.h2.entity.MortgageRateEntity;
import com.bank.mortgage.infrastructure.adapter.out.database.h2.repository.MortgageRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ComponentScan(basePackages = "com.bank.mortgage")
public class MortgageRateRepositoryAdapterIntegrationTest {

    @Autowired
    private MortgageRateRepositoryAdapter mortgageRateRepositoryAdapter;

    @Autowired
    private MortgageRateRepository mortgageRateRepository;

    @BeforeEach
    void setUp() {
        mortgageRateRepository.deleteAll();  // Clean database
    }

    /**
     * Checks findAllMortgageRates find all existing mortgage rates
     */
    @Test
    void findAllMortgageRates_Given_existing_mortgage_rates_Then_find_all() {
        // Given:
        int mortgageRate1MaturityPeriod = 4;
        BigDecimal mortgageRate1InterestRate = new BigDecimal("0.035");
        LocalDateTime mortgageRate1LastUpdate = LocalDateTime.now();
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .id(1L)
                .maturityPeriod(mortgageRate1MaturityPeriod)
                .interestRate(mortgageRate1InterestRate)
                .lastUpdate(mortgageRate1LastUpdate)
                .build();

        int mortgageRate2MaturityPeriod = 8;
        BigDecimal mortgageRate2InterestRate = new BigDecimal("0.04");
        LocalDateTime mortgageRate2LastUpdate = LocalDateTime.now();
        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .id(2L)
                .maturityPeriod(mortgageRate2MaturityPeriod)
                .interestRate(mortgageRate2InterestRate)
                .lastUpdate(mortgageRate2LastUpdate)
                .build();

        mortgageRateRepository.saveAll(List.of(mr1, mr2));

        // When:
        List<MortgageRate> mortgageRates = mortgageRateRepositoryAdapter.findAllMortgageRates();

        // Then:
        assertThat(mortgageRates).hasSize(2);
        assertThat(mortgageRates.get(0).getMaturityPeriod()).isEqualTo(mortgageRate1MaturityPeriod);
        assertThat(mortgageRates.get(0).getInterestRate()).isEqualTo(mortgageRate1InterestRate);
        assertThat(mortgageRates.get(1).getMaturityPeriod()).isEqualTo(mortgageRate2MaturityPeriod);
        assertThat(mortgageRates.get(1).getInterestRate()).isEqualTo(mortgageRate2InterestRate);
    }

    /**
     * Checks findAllMortgageRates return empty mortgage rates if there's no data
     */
    @Test
    void findAllMortgageRates_Given_empty_mortgage_rates_Then_return_empty_list() {
        // When:
        List<MortgageRate> mortgageRates = mortgageRateRepositoryAdapter.findAllMortgageRates();

        // Then:
        assertThat(mortgageRates).hasSize(0);
    }

    /**
     * Checks findByMaturityPeriod find related mortgage rate by maturity
     */
    @Test
    void findByMaturityPeriod_Given_existing_mortgage_rate_Then_find_by_maturity() {
        // Given:
        int mortgageRate1MaturityPeriod = 4;
        BigDecimal mortgageRate1InterestRate = new BigDecimal("0.035");
        LocalDateTime mortgageRate1LastUpdate = LocalDateTime.now();
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .id(1L)
                .maturityPeriod(mortgageRate1MaturityPeriod)
                .interestRate(mortgageRate1InterestRate)
                .lastUpdate(mortgageRate1LastUpdate)
                .build();

        int mortgageRate2MaturityPeriod = 8;
        BigDecimal mortgageRate2InterestRate = new BigDecimal("0.04");
        LocalDateTime mortgageRate2LastUpdate = LocalDateTime.now();
        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .id(2L)
                .maturityPeriod(mortgageRate2MaturityPeriod)
                .interestRate(mortgageRate2InterestRate)
                .lastUpdate(mortgageRate2LastUpdate)
                .build();

        mortgageRateRepository.saveAll(List.of(mr1, mr2));

        // When:
        MortgageRate mortgageRate =
                mortgageRateRepositoryAdapter.findByMaturityPeriod(mortgageRate2MaturityPeriod).orElse(null);

        // Then:
        assertThat(mortgageRate).isNotNull();
        assertThat(mortgageRate.getMaturityPeriod()).isEqualTo(mortgageRate2MaturityPeriod);
        assertThat(mortgageRate.getInterestRate()).isEqualTo(mortgageRate2InterestRate);
    }

    /**
     * Checks findByMaturityPeriod return empty optional if no related mortgage is found for a given maturity
     */
    @Test
    void findByMaturityPeriod_Given_invalid_maturity_Then_return_empty_optional() {
        // Given:
        int mortgageRate1MaturityPeriod = 4;
        BigDecimal mortgageRate1InterestRate = new BigDecimal("0.035");
        LocalDateTime mortgageRate1LastUpdate = LocalDateTime.now();
        MortgageRateEntity mr1 = MortgageRateEntity.builder()
                .id(1L)
                .maturityPeriod(mortgageRate1MaturityPeriod)
                .interestRate(mortgageRate1InterestRate)
                .lastUpdate(mortgageRate1LastUpdate)
                .build();

        int mortgageRate2MaturityPeriod = 8;
        BigDecimal mortgageRate2InterestRate = new BigDecimal("0.04");
        LocalDateTime mortgageRate2LastUpdate = LocalDateTime.now();
        MortgageRateEntity mr2 = MortgageRateEntity.builder()
                .id(2L)
                .maturityPeriod(mortgageRate2MaturityPeriod)
                .interestRate(mortgageRate2InterestRate)
                .lastUpdate(mortgageRate2LastUpdate)
                .build();

        mortgageRateRepository.saveAll(List.of(mr1, mr2));

        // When:
        Optional<MortgageRate> mortgageRate = mortgageRateRepositoryAdapter.findByMaturityPeriod(15);

        // Then:
        assertThat(mortgageRate).isEmpty();
    }

}