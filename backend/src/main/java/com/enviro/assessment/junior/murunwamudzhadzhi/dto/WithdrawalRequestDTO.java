package com.enviro.assessment.junior.murunwamudzhadzhi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Incoming payload for POST /api/withdrawals.
 * Field-level validation is enforced by @Valid in the controller;
 * the business rules (balance, 90% cap, retirement age) are enforced
 * afterwards in WithdrawalService, since they need data from the DB.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestDTO {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private BigDecimal amount;

    private String notes;
}
