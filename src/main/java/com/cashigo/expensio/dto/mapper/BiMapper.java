package com.cashigo.expensio.dto.mapper;

public interface BiMapper<T, V> {
    V mapToEntity(T dto);
    T mapToDto(V entity);
}
