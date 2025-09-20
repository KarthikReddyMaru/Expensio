package com.cashigo.expensio.dto;

import lombok.Data;

@Data
public class Response<T> {
    T data;
}
