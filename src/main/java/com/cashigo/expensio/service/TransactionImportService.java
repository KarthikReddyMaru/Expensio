package com.cashigo.expensio.service;

import com.cashigo.expensio.common.util.CsvUtil;
import com.cashigo.expensio.dto.ImportErrorDto.ErrorMessage;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionImportService {

    private final Validator validator;

    public void createTransactions(MultipartFile multipartFile) {

        if (multipartFile.isEmpty())
            return;

        List<ErrorMessage> errorMessages = new ArrayList<>();
        List<TransactionSummaryDto> transactions = CsvUtil.parseCSV(multipartFile, errorMessages);

        transactions = transactions.stream().filter(transaction -> {
            Errors errors = validator.validateObject(transaction);
            if (!errors.getFieldErrors().isEmpty()) {
                String message = errors.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining("; "));
                ErrorMessage errorMessage = ErrorMessage.builder().message(message).data(transaction.toCsv()).build();
                errorMessages.add(errorMessage);
                return false;
            }
            return true;
        }).toList();

        transactions.forEach(transaction -> log.info("{}", transaction));

        errorMessages.forEach(error -> {
            log.info("{}", error);
        });
    }
}
