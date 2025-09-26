package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TransactionMapper implements BiMapper<TransactionDto, Transaction> {

    @Value("${zone.id}")
    private String zone;

    @Override
    public Transaction mapToEntity(TransactionDto dto) {
        if (dto == null) return null;

        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setAmount(dto.getAmount());
        transaction.setNote(dto.getNote());

        Instant transactionDateTimeInstant = mapToInstant(dto.getTransactionDateTime());
        transaction.setTransactionDateTime(transactionDateTimeInstant);

        if (dto.getSubCategoryId() != null) {
            SubCategory subCategory = new SubCategory();
            subCategory.setId(dto.getSubCategoryId());
            transaction.setSubCategory(subCategory);
        }

        return transaction;
    }

    @Override
    public TransactionDto mapToDto(Transaction entity) {
        if (entity == null) return null;

        TransactionDto dto = new TransactionDto();
        dto.setId(entity.getId());
        dto.setAmount(entity.getAmount());
        dto.setNote(entity.getNote());

        LocalDateTime transactionDateTime = mapToLocalDateTime(entity.getTransactionDateTime());
        dto.setTransactionDateTime(transactionDateTime);

        if (entity.getSubCategory() != null) {
            dto.setSubCategoryId(entity.getSubCategory().getId());
        }

        return dto;
    }

    public Instant mapToInstant(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant();
    }

    public LocalDateTime mapToLocalDateTime(Instant instant) {
        ZoneId zoneId = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return zonedDateTime.toLocalDateTime();
    }
}
