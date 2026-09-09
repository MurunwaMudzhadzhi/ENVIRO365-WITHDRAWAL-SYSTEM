package com.enviro.assessment.junior.murunwamudzhadzhi.controller;

import com.enviro.assessment.junior.murunwamudzhadzhi.dto.PortfolioDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Investor;
import com.enviro.assessment.junior.murunwamudzhadzhi.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investors")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PortfolioController {

    private final PortfolioService portfolioService;

    /** Used by the frontend to populate an investor selector. */
    @GetMapping
    public List<Map<String, Object>> getAllInvestors() {
        return portfolioService.getAllInvestors().stream()
                .map(i -> Map.<String, Object>of(
                        "id", i.getId(),
                        "fullName", i.getFirstName() + " " + i.getLastName()))
                .toList();
    }

    @GetMapping("/{investorId}/portfolio")
    public PortfolioDTO getPortfolio(@PathVariable Long investorId) {
        return portfolioService.getPortfolio(investorId);
    }
}
