package com.cashigo.expensio.dto.mapper;

public interface Mapper<T, V> {
    V map(T entity);
}
