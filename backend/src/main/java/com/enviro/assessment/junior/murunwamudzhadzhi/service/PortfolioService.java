package com.enviro.assessment.junior.murunwamudzhadzhi.service;

import com.enviro.assessment.junior.murunwamudzhadzhi.dto.PortfolioDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Investor;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Product;
import com.enviro.assessment.junior.murunwamudzhadzhi.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.murunwamudzhadzhi.mapper.EntityMapper;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.InvestorRepository;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public PortfolioDTO getPortfolio(Long investorId) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + investorId));

        List<Product> products = productRepository.findByInvestorId(investorId);
        return mapper.toPortfolioDTO(investor, products);
    }

    @Transactional(readOnly = true)
    public List<Investor> getAllInvestors() {
        return investorRepository.findAll();
    }
}
