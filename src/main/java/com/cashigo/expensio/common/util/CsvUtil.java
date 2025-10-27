package com.cashigo.expensio.common.util;

import com.cashigo.expensio.dto.ImportErrorDto;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.apache.commons.collections4.comparators.FixedOrderComparator;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CsvUtil {

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

    public static List<TransactionSummaryDto> parseCSV(MultipartFile multipartFile, List<ImportErrorDto.ErrorMessage> exceptions) {
        try (Reader reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()))) {
            return new CsvToBeanBuilder<TransactionSummaryDto>(reader)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withMappingStrategy(CsvUtil.getStrategy())
                    .withErrorLocale(Locale.ENGLISH)
                    .withExceptionHandler(e -> {
                        ImportErrorDto.ErrorMessage message = ImportErrorDto.ErrorMessage.builder()
                                .data(String.join(", ", e.getLine()))
                                .message(e.getCause() != null ? e.getCause().getMessage() : e.getMessage())
                                .build();
                        exceptions.add(message);
                        return null;
                    })
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
