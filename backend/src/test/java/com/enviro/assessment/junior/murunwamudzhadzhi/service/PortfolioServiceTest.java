package com.enviro.assessment.junior.murunwamudzhadzhi.service;

import com.enviro.assessment.junior.murunwamudzhadzhi.dto.PortfolioDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Investor;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Product;
import com.enviro.assessment.junior.murunwamudzhadzhi.enums.ProductType;
import com.enviro.assessment.junior.murunwamudzhadzhi.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.murunwamudzhadzhi.mapper.EntityMapper;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.InvestorRepository;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private ProductRepository productRepository;

    private final EntityMapper mapper = new EntityMapper();

    @Test
    void getPortfolio_returnsInvestorDetailsAndProducts() {
        PortfolioService portfolioService = new PortfolioService(investorRepository, productRepository, mapper);

        Investor investor = Investor.builder()
                .id(1L).firstName("Thandiwe").lastName("Mokoena")
                .email("t@example.com")
                .dateOfBirth(LocalDate.now().minusYears(70))
                .build();

        Product product = Product.builder()
                .id(10L).investor(investor).productName("RA")
                .productType(ProductType.RETIREMENT_ANNUITY)
                .balance(new BigDecimal("50000.00"))
                .build();

        when(investorRepository.findById(1L)).thenReturn(Optional.of(investor));
        when(productRepository.findByInvestorId(1L)).thenReturn(List.of(product));

        PortfolioDTO result = portfolioService.getPortfolio(1L);

        assertThat(result.getFullName()).isEqualTo("Thandiwe Mokoena");
        assertThat(result.getAge()).isEqualTo(70);
        assertThat(result.getProducts()).hasSize(1);
        assertThat(result.getProducts().get(0).getBalance()).isEqualByComparingTo("50000.00");
    }

    @Test
    void getPortfolio_throwsWhenInvestorNotFound() {
        PortfolioService portfolioService = new PortfolioService(investorRepository, productRepository, mapper);
        when(investorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> portfolioService.getPortfolio(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
