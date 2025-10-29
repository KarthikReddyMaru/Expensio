package com.cashigo.expensio.common.util;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;

import java.util.stream.Collectors;

public class ErrorUtil {

    public static String parseErrorMessages(Errors errors) {
        return errors
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
    }

}
