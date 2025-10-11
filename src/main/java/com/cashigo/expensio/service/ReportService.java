package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.CategoryReportDto;
import com.cashigo.expensio.dto.ReportDto;
import com.cashigo.expensio.dto.ReportProjection;
import com.cashigo.expensio.dto.mapper.ReportProjectionToCategoryMapper;
import com.cashigo.expensio.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserContext userContext;
    @Value("${zone.id}")
    private String zone;

    private final TransactionRepository transactionRepository;
    private final ReportProjectionToCategoryMapper mapper;

    public ReportDto getReportOf(int month, int year) {

        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Invalid Month");

        List<ReportProjection> transactionReport = getReportProjections(month, year);

        BigDecimal totalAmountSpent = transactionReport
                .stream()
                .map(ReportProjection::getAmountSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoryReportDto> categoryReports = getCategoryReports(transactionReport, totalAmountSpent);

        return ReportDto
                .builder()
                .title(String.format("%s %d REPORT", Month.of(month).name(), year))
                .startDate(YearMonth.of(year, month).atDay(1))
                .endDate(YearMonth.of(year, month).atEndOfMonth())
                .totalAmountSpent(totalAmountSpent)
                .categoryReports(categoryReports)
                .build();
    }

    private List<ReportProjection> getReportProjections(int month, int year) {

        String userId = userContext.getUserId();

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        ZoneId zoneId = ZoneId.of(zone);
        Instant startInstant = startDate.atStartOfDay(zoneId).toInstant();
        Instant endInstant = endDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant().truncatedTo(ChronoUnit.SECONDS);

        return transactionRepository
                .findTransactionReportByInstantRange(startInstant, endInstant, userId);
    }

    private List<CategoryReportDto> getCategoryReports(List<ReportProjection> transactionReport, BigDecimal totalAmountSpent) {

        List<CategoryReportDto> categoryReports = transactionReport
                .stream()
                .map(mapper::map)
                .toList();

        categoryReports
                .forEach(categoryReport -> {
                    BigDecimal amountSpent = categoryReport.getAmountSpent();
                    if (amountSpent.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal percentage = amountSpent
                                .multiply(BigDecimal.valueOf(100))
                                .divide(totalAmountSpent, 2, RoundingMode.HALF_DOWN);
                        categoryReport.setPercentage(percentage);
                    } else
                        categoryReport.setPercentage(BigDecimal.ZERO);
                });

        return categoryReports;
    }

}
