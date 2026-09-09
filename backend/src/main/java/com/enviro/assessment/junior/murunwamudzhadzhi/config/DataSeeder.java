package com.enviro.assessment.junior.murunwamudzhadzhi.config;

import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Investor;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Product;
import com.enviro.assessment.junior.murunwamudzhadzhi.enums.ProductType;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.InvestorRepository;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds two demo investors so the retirement-age rule (age > 65) can be
 * exercised immediately: Thandiwe is over 65, Sipho is not.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        Investor thandiwe = investorRepository.save(Investor.builder()
                .firstName("Thandiwe")
                .lastName("Mokoena")
                .email("thandiwe.mokoena@example.com")
                .dateOfBirth(LocalDate.of(1955, 3, 12)) // > 65
                .build());

        Investor sipho = investorRepository.save(Investor.builder()
                .firstName("Sipho")
                .lastName("Nkosi")
                .email("sipho.nkosi@example.com")
                .dateOfBirth(LocalDate.of(1990, 7, 20)) // < 65
                .build());

        productRepository.save(Product.builder()
                .investor(thandiwe)
                .productName("Enviro365 Retirement Annuity")
                .productType(ProductType.RETIREMENT_ANNUITY)
                .balance(new BigDecimal("500000.00"))
                .build());

        productRepository.save(Product.builder()
                .investor(thandiwe)
                .productName("Enviro365 Unit Trust")
                .productType(ProductType.UNIT_TRUST)
                .balance(new BigDecimal("120000.00"))
                .build());

        productRepository.save(Product.builder()
                .investor(sipho)
                .productName("Enviro365 Retirement Annuity")
                .productType(ProductType.RETIREMENT_ANNUITY)
                .balance(new BigDecimal("80000.00"))
                .build());

        productRepository.save(Product.builder()
                .investor(sipho)
                .productName("Enviro365 Savings Plan")
                .productType(ProductType.SAVINGS_PLAN)
                .balance(new BigDecimal("35000.00"))
                .build());
    }
}
