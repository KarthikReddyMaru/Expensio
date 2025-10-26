package com.cashigo.expensio.service;

import com.cashigo.expensio.common.security.UserContext;
import com.cashigo.expensio.dto.TransactionExportProjection;
import com.cashigo.expensio.dto.mapper.TransactionExportProjectionMapper;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.cashigo.expensio.repository.TransactionRepository;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.comparators.FixedOrderComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionExportService {

    @Value("${zone.id}")
    private String zone;

    private final TransactionRepository transactionRepository;
    private final UserContext userContext;
    private final TransactionExportProjectionMapper mapper;

    public void exportTransactionByMonth(int month, int year, HttpServletResponse response)
            throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        YearMonth yearMonth = YearMonth.of(year, month);
        ZoneId zoneId = ZoneId.of(zone);
        String userId = userContext.getUserId();

        Instant startTime = yearMonth.atDay(1).atStartOfDay(zoneId).toInstant();
        Instant endTime = yearMonth.atEndOfMonth().atTime(LocalTime.MAX).truncatedTo(ChronoUnit.SECONDS).atZone(zoneId).toInstant();
        List<TransactionExportProjection> transactions = transactionRepository
                .findTransactionsByInstantRange(startTime, endTime, userId);
        List<TransactionSummaryDto> summary = transactions.stream().map(mapper::map).toList();

        new StatefulBeanToCsvBuilder<TransactionSummaryDto>(response.getWriter())
                .withApplyQuotesToAll(false)
                .withMappingStrategy(getStrategy())
                .build()
                .write(summary);
    }

    private HeaderColumnNameMappingStrategy<TransactionSummaryDto> getStrategy() {
        HeaderColumnNameMappingStrategy<TransactionSummaryDto> strategy =
                new HeaderColumnNameMappingStrategy<>();
        strategy.setType(TransactionSummaryDto.class);
        strategy.setColumnOrderOnWrite(new FixedOrderComparator<>(
                "DATE",
                "CATEGORY",
                "SUBCATEGORY",
                "AMOUNT",
                "NOTE"
        ));
        return strategy;
    }

}
