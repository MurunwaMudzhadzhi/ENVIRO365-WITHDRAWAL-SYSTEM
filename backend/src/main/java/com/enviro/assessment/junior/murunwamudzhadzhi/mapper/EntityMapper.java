package com.enviro.assessment.junior.murunwamudzhadzhi.mapper;

import com.enviro.assessment.junior.murunwamudzhadzhi.dto.PortfolioDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.dto.ProductDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Investor;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Product;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.WithdrawalNotice;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Keeps entity <-> DTO conversion out of the service layer so business
 * logic stays focused on the actual rules.
 */
@Component
public class EntityMapper {

    public ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productType(product.getProductType())
                .balance(product.getBalance())
                .build();
    }

    public PortfolioDTO toPortfolioDTO(Investor investor, List<Product> products) {
        return PortfolioDTO.builder()
                .investorId(investor.getId())
                .fullName(investor.getFirstName() + " " + investor.getLastName())
                .email(investor.getEmail())
                .dateOfBirth(investor.getDateOfBirth())
                .age(investor.getAge())
                .products(products.stream().map(this::toProductDTO).toList())
                .build();
    }

    public WithdrawalResponseDTO toWithdrawalResponseDTO(WithdrawalNotice notice) {
        return WithdrawalResponseDTO.builder()
                .id(notice.getId())
                .productId(notice.getProduct().getId())
                .productName(notice.getProduct().getProductName())
                .productType(notice.getProduct().getProductType())
                .amount(notice.getAmount())
                .balanceAfter(notice.getBalanceAfter())
                .requestDate(notice.getRequestDate())
                .status(notice.getStatus())
                .notes(notice.getNotes())
                .build();
    }
}
