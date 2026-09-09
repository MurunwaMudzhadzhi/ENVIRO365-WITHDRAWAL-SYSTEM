package com.enviro.assessment.junior.murunwamudzhadzhi.dto;

import com.enviro.assessment.junior.murunwamudzhadzhi.enums.ProductType;
import com.enviro.assessment.junior.murunwamudzhadzhi.enums.WithdrawalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private ProductType productType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime requestDate;
    private WithdrawalStatus status;
    private String notes;
}
