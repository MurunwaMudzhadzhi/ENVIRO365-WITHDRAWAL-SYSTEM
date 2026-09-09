package com.enviro.assessment.junior.murunwamudzhadzhi.controller;

import com.enviro.assessment.junior.murunwamudzhadzhi.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.murunwamudzhadzhi.entity.WithdrawalNotice;
import com.enviro.assessment.junior.murunwamudzhadzhi.service.CsvExportService;
import com.enviro.assessment.junior.murunwamudzhadzhi.service.WithdrawalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000", "https://enviro365-withdrawal-system-1.onrender.com"})
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final CsvExportService csvExportService;

    @PostMapping
    public ResponseEntity<WithdrawalResponseDTO> createWithdrawal(@Valid @RequestBody WithdrawalRequestDTO request) {
        WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<WithdrawalResponseDTO> getHistory(@RequestParam Long investorId) {
        return withdrawalService.getHistory(investorId);
    }

    /**
     * CSV export with optional filtering by product and date range.
     * Example: GET /api/withdrawals/export?investorId=1&productId=2&from=2026-01-01T00:00:00
     */
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam Long investorId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<WithdrawalNotice> notices = withdrawalService.getForExport(investorId, productId, from, to);
        String csv = csvExportService.buildCsv(notices);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "withdrawal-statement-" + investorId + ".csv");

        return ResponseEntity.ok().headers(headers).body(csv.getBytes());
    }
}
