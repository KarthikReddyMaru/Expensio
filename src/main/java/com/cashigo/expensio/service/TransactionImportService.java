package com.cashigo.expensio.service;

import com.cashigo.expensio.common.util.CsvUtil;
import com.cashigo.expensio.dto.ImportErrorDto.ErrorMessage;
import com.cashigo.expensio.dto.summary.TransactionSummaryDto;
import com.opencsv.bean.CsvToBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionImportService {

    public void createTransactions(MultipartFile multipartFile) {
        if (multipartFile.isEmpty())
            return;
        List<ErrorMessage> errorMessages = new ArrayList<>();
        List<TransactionSummaryDto> transactions = CsvUtil.parseCSV(multipartFile, errorMessages);
        transactions.forEach(transaction -> {
            log.info("{}", transaction);
        });
        errorMessages.forEach(error -> {
            log.info("{}, {}", error.getRowNumber(), error.getMessage());
        });
    }


}
