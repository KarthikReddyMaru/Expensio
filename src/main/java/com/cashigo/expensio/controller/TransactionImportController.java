package com.cashigo.expensio.controller;

import com.cashigo.expensio.service.TransactionImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionImportController {

    private final TransactionImportService importService;

    @PostMapping("/import")
    public void importTransactions(MultipartFile file) {
        importService.createTransactions(file);
    }

}
