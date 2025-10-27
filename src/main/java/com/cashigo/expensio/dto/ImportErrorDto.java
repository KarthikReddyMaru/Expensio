package com.cashigo.expensio.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ImportErrorDto {

    private int successCount;
    private int errorCount;
    private List<ErrorMessage> errors;

    @Builder
    @Data
    public static class ErrorMessage {
        private String data;
        private String message;
    }

}

