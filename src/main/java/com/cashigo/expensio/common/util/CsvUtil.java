package com.cashigo.expensio.common.util;

import com.cashigo.expensio.dto.ImportErrorDto;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.comparators.FixedOrderComparator;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CsvUtil {

    private static final ThreadLocal<String[]> headerCache = new ThreadLocal<>();

    public static HeaderColumnNameMappingStrategy<TransactionSummaryDto> getStrategy() {
        HeaderColumnNameMappingStrategy<TransactionSummaryDto> strategy =
                new HeaderColumnNameMappingStrategy<>();
        strategy.setType(TransactionSummaryDto.class);
        strategy.setColumnOrderOnWrite(new FixedOrderComparator<>(
                "DATE",
                "TIME",
                "CATEGORY",
                "SUBCATEGORY",
                "AMOUNT",
                "NOTE"
        ));
        return strategy;
    }

    public static HeaderColumnNameMappingStrategy<ImportErrorDto> getErrorStrategy() {
        HeaderColumnNameMappingStrategy<ImportErrorDto> strategy =
                new HeaderColumnNameMappingStrategy<>();
        strategy.setType(ImportErrorDto.class);
        strategy.setColumnOrderOnWrite(new FixedOrderComparator<>(
                "REASON",
                "DATE",
                "TIME",
                "CATEGORY",
                "SUBCATEGORY",
                "AMOUNT",
                "NOTE"
        ));
        return strategy;
    }

    public static List<TransactionSummaryDto> parseCSV(MultipartFile multipartFile, List<ImportErrorDto> exceptions) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {

            cacheHeaders(reader);

            CsvToBean<TransactionSummaryDto> csvToBean = new CsvToBeanBuilder<TransactionSummaryDto>(reader)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(CsvUtil.getStrategy())
                    .withErrorLocale(Locale.ENGLISH)
                    .withThrowExceptions(false)
                    .build();

            List<TransactionSummaryDto> transactions = csvToBean.parse();

            Map<Long, List<CsvException>> csvExceptions = csvToBean.getCapturedExceptions()
                    .stream()
                    .collect(Collectors.groupingBy(CsvException::getLineNumber));

            for (Map.Entry<Long, List<CsvException>> exception : csvExceptions.entrySet()) {
                ImportErrorDto error = parseError(exception.getValue().getFirst().getLine(), exception.getValue());
                exceptions.add(error);
            }
            return transactions;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            headerCache.remove();
        }
    }

    private static void cacheHeaders(BufferedReader reader) throws IOException {

        reader.mark(10000);
        String[] headers = reader.readLine().split(",", -1);
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
            headers[i] = headers[i].toUpperCase();
        }
        headerCache.set(headers);
        reader.reset();

    }

    private static ImportErrorDto parseError(String[] record, List<CsvException> exceptions) {

        String reason = exceptions.stream()
                .map(e -> e.getCause() != null ? e.getCause().getMessage() : e.getMessage())
                .collect(Collectors.joining(" | "));

        String[] headers = headerCache.get();

        ImportErrorDto.ImportErrorDtoBuilder builder = ImportErrorDto.builder();

        for (int i = 0; i < Math.min(headers.length, record.length); i++) {
            switch (headers[i]) {
                case "DATE" -> builder.transactionDate(record[i]);
                case "TIME" -> builder.transactionTime(record[i]);
                case "CATEGORY" -> builder.category(record[i]);
                case "SUBCATEGORY" -> builder.subCategory(record[i]);
                case "AMOUNT" -> builder.amount(record[i]);
                case "NOTE" -> builder.note(record[i]);
            }
        }

        return builder.reason(reason).build();
    }

}
