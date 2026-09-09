package com.enviro.assessment.junior.murunwamudzhadzhi.repository;

import com.enviro.assessment.junior.murunwamudzhadzhi.entity.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {

    List<WithdrawalNotice> findByProduct_Investor_IdOrderByRequestDateDesc(Long investorId);

    @Query("""
           select w from WithdrawalNotice w
           where w.product.investor.id = :investorId
             and (:productId is null or w.product.id = :productId)
             and (:fromDate is null or w.requestDate >= :fromDate)
             and (:toDate is null or w.requestDate <= :toDate)
           order by w.requestDate desc
           """)
    List<WithdrawalNotice> findForExport(@Param("investorId") Long investorId,
                                          @Param("productId") Long productId,
                                          @Param("fromDate") LocalDateTime fromDate,
                                          @Param("toDate") LocalDateTime toDate);
}
