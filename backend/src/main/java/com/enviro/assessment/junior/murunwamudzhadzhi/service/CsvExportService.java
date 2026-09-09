package com.enviro.assessment.junior.murunwamudzhadzhi.service;

import com.enviro.assessment.junior.murunwamudzhadzhi.entity.WithdrawalNotice;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CsvExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] HEADERS = {
            "Notice ID", "Product Name", "Product Type", "Amount",
            "Balance After", "Request Date", "Status", "Notes"
    };

    public String buildCsv(List<WithdrawalNotice> notices) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", HEADERS)).append("\n");

        for (WithdrawalNotice n : notices) {
            sb.append(csvEscape(String.valueOf(n.getId()))).append(",")
              .append(csvEscape(n.getProduct().getProductName())).append(",")
              .append(csvEscape(n.getProduct().getProductType().name())).append(",")
              .append(csvEscape(n.getAmount().toPlainString())).append(",")
              .append(csvEscape(n.getBalanceAfter().toPlainString())).append(",")
              .append(csvEscape(n.getRequestDate().format(DATE_FORMAT))).append(",")
              .append(csvEscape(n.getStatus().name())).append(",")
              .append(csvEscape(n.getNotes() == null ? "" : n.getNotes()))
              .append("\n");
        }
        return sb.toString();
    }

    /** Wraps a field in quotes and escapes embedded quotes if it contains a comma, quote or newline. */
    private String csvEscape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
