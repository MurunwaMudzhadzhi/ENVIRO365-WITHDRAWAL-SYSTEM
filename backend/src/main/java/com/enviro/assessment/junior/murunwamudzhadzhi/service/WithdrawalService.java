package com.enviro.assessment.junior.murunwamudzhadzhi.service;

import com.enviro.assessment.junior.murunwamudzhadzhi.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Investor;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.Product;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.WithdrawalNotice;
import com.enviro.assessment.junior.murunwamudzhadzhi.enums.ProductType;
import com.enviro.assessment.junior.murunwamudzhadzhi.enums.WithdrawalStatus;
import com.enviro.assessment.junior.murunwamudzhadzhi.exception.BusinessRuleException;
import com.enviro.assessment.junior.murunwamudzhadzhi.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.murunwamudzhadzhi.mapper.EntityMapper;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.ProductRepository;
import com.enviro.assessment.junior.murunwamudzhadzhi.repository.WithdrawalNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    /** An investor may never withdraw more than this fraction of a product's balance in one notice. */
    static final BigDecimal MAX_WITHDRAWAL_FRACTION = new BigDecimal("0.90");

    /** Minimum age for a retirement annuity withdrawal to be allowed. */
    static final int MINIMUM_RETIREMENT_AGE = 65;

    private final WithdrawalNoticeRepository withdrawalNoticeRepository;
    private final ProductRepository productRepository;
    private final EntityMapper mapper;

    @Transactional
    public WithdrawalResponseDTO createWithdrawal(WithdrawalRequestDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        validateBusinessRules(product, request.getAmount());

        BigDecimal balanceAfter = product.getBalance().subtract(request.getAmount());
        product.setBalance(balanceAfter);
        productRepository.save(product);

        WithdrawalNotice notice = WithdrawalNotice.builder()
                .product(product)
                .amount(request.getAmount())
                .balanceAfter(balanceAfter)
                .requestDate(LocalDateTime.now())
                .status(WithdrawalStatus.PROCESSED)
                .notes(request.getNotes())
                .build();

        WithdrawalNotice saved = withdrawalNoticeRepository.save(notice);
        return mapper.toWithdrawalResponseDTO(saved);
    }

    /**
     * Applies, in order: retirement age rule, sufficient-balance rule,
     * 90%-of-balance cap. Throws BusinessRuleException with a clear,
     * user-facing message on the first rule that fails.
     */
    private void validateBusinessRules(Product product, BigDecimal amount) {
        Investor investor = product.getInvestor();

        if (product.getProductType() == ProductType.RETIREMENT_ANNUITY
                && investor.getAge() <= MINIMUM_RETIREMENT_AGE) {
            throw new BusinessRuleException(
                    "Retirement annuity withdrawals are only allowed for investors older than "
                            + MINIMUM_RETIREMENT_AGE + " (current age: " + investor.getAge() + ").");
        }

        if (amount.compareTo(product.getBalance()) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount (" + amount + ") exceeds available balance (" + product.getBalance() + ").");
        }

        BigDecimal maxAllowed = product.getBalance()
                .multiply(MAX_WITHDRAWAL_FRACTION)
                .setScale(2, RoundingMode.HALF_UP);

        if (amount.compareTo(maxAllowed) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount (" + amount + ") exceeds the maximum allowed withdrawal of 90% of balance ("
                            + maxAllowed + ").");
        }
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponseDTO> getHistory(Long investorId) {
        return withdrawalNoticeRepository.findByProduct_Investor_IdOrderByRequestDateDesc(investorId)
                .stream()
                .map(mapper::toWithdrawalResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WithdrawalNotice> getForExport(Long investorId, Long productId,
                                                LocalDateTime fromDate, LocalDateTime toDate) {
        return withdrawalNoticeRepository.findForExport(investorId, productId, fromDate, toDate);
    }
}
