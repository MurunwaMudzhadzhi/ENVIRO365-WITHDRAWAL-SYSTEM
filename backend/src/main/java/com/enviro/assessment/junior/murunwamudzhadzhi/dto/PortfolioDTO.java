package com.enviro.assessment.junior.murunwamudzhadzhi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * "Retrieve investor portfolio (details + products)" - investor
 * personal/account details plus the list of products they hold.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDTO {
    private Long investorId;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private int age;
    private List<ProductDTO> products;
}
