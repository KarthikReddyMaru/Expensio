package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper implements BiMapper<TransactionDto, Transaction> {

    @Override
    public Transaction mapToEntity(TransactionDto dto) {
        if (dto == null) return null;

        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setUserId(dto.getUserId());
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDateTime(dto.getTransactionDateTime());
        transaction.setNote(dto.getNote());

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
        dto.setUserId(entity.getUserId());
        dto.setAmount(entity.getAmount());
        dto.setTransactionDateTime(entity.getTransactionDateTime());
        dto.setNote(entity.getNote());

        if (entity.getSubCategory() != null) {
            dto.setSubCategoryId(entity.getSubCategory().getId());
        }

        return dto;
    }
}
