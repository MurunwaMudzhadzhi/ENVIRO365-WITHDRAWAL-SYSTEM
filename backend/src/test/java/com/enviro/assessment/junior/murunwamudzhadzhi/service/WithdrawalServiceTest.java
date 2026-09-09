package com.enviro.assessment.junior.murunwamudzhadzhi.service;

import com.enviro.assessment.junior.murunwamudzhadzhi.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Investor;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Product;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.WithdrawalNotice;
import com.enviro.assessment.junior.murunwamudzhadzhi.enums.ProductType;
import com.enviro.assessment.junior.murunwamudzhadzhi.exception.BusinessRuleException;
import com.enviro.assessment.junior.murunwamudzhadzhi.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.murunwamudzhadzhi.mapper.EntityMapper;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.ProductRepository;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    @Mock
    private ProductRepository productRepository;

    private final EntityMapper mapper = new EntityMapper();

    @InjectMocks
    private WithdrawalService withdrawalService;

    private Investor over65;
    private Investor under65;

    @BeforeEach
    void setUp() {
        // manually inject the real mapper since @InjectMocks won't construct it for us
        withdrawalService = new WithdrawalService(withdrawalNoticeRepository, productRepository, mapper);

        over65 = Investor.builder()
                .id(1L)
                .firstName("Thandiwe")
                .lastName("Mokoena")
                .email("t@example.com")
                .dateOfBirth(LocalDate.now().minusYears(70))
                .build();

        under65 = Investor.builder()
                .id(2L)
                .firstName("Sipho")
                .lastName("Nkosi")
                .email("s@example.com")
                .dateOfBirth(LocalDate.now().minusYears(30))
                .build();
    }

    @Test
    void retirementWithdrawal_rejectedWhenInvestorNotOver65() {
        Product product = Product.builder()
                .id(10L).investor(under65).productName("RA")
                .productType(ProductType.RETIREMENT_ANNUITY)
                .balance(new BigDecimal("10000.00"))
                .build();
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO(10L, new BigDecimal("1000"), null);

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("65");
    }

    @Test
    void retirementWithdrawal_allowedWhenInvestorOver65() {
        Product product = Product.builder()
                .id(11L).investor(over65).productName("RA")
                .productType(ProductType.RETIREMENT_ANNUITY)
                .balance(new BigDecimal("10000.00"))
                .build();
        when(productRepository.findById(11L)).thenReturn(Optional.of(product));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(inv -> {
                    WithdrawalNotice n = inv.getArgument(0);
                    n.setId(100L);
                    return n;
                });

        WithdrawalRequestDTO request = new WithdrawalRequestDTO(11L, new BigDecimal("1000"), "planned withdrawal");

        WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getBalanceAfter()).isEqualByComparingTo("9000.00");
    }

    @Test
    void withdrawal_rejectedWhenAmountExceedsBalance() {
        Product product = Product.builder()
                .id(12L).investor(under65).productName("Unit Trust")
                .productType(ProductType.UNIT_TRUST)
                .balance(new BigDecimal("5000.00"))
                .build();
        when(productRepository.findById(12L)).thenReturn(Optional.of(product));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO(12L, new BigDecimal("6000"), null);

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("exceeds available balance");
    }

    @Test
    void withdrawal_rejectedWhenAmountExceeds90PercentOfBalance() {
        Product product = Product.builder()
                .id(13L).investor(under65).productName("Savings Plan")
                .productType(ProductType.SAVINGS_PLAN)
                .balance(new BigDecimal("1000.00"))
                .build();
        when(productRepository.findById(13L)).thenReturn(Optional.of(product));

        // 950 is within balance but exceeds the 90% cap (900)
        WithdrawalRequestDTO request = new WithdrawalRequestDTO(13L, new BigDecimal("950"), null);

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("90%");
    }

    @Test
    void withdrawal_allowedAtExactly90PercentOfBalance() {
        Product product = Product.builder()
                .id(14L).investor(under65).productName("Savings Plan")
                .productType(ProductType.SAVINGS_PLAN)
                .balance(new BigDecimal("1000.00"))
                .build();
        when(productRepository.findById(14L)).thenReturn(Optional.of(product));
        when(withdrawalNoticeRepository.save(any(WithdrawalNotice.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO(14L, new BigDecimal("900.00"), null);

        WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);

        assertThat(response.getBalanceAfter()).isEqualByComparingTo("100.00");
    }

    @Test
    void createWithdrawal_throwsWhenProductDoesNotExist() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        WithdrawalRequestDTO request = new WithdrawalRequestDTO(999L, new BigDecimal("100"), null);

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
