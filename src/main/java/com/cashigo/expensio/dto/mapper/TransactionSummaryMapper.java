package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.TransactionSummaryDto;
import com.cashigo.expensio.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class TransactionSummaryMapper implements Mapper<Transaction, TransactionSummaryDto> {

    @Value("${zone.id}")
    private String zoneId;

    @Override
    public TransactionSummaryDto map(Transaction entity) {

        TransactionSummaryDto summaryDto = new TransactionSummaryDto();
        summaryDto.setId(entity.getId());
        summaryDto.setAmount(entity.getAmount());
        if(entity.getSubCategory() != null) {
            summaryDto.setSubCategory(entity.getSubCategory().getName());
            if(entity.getSubCategory().getCategory() != null)
                summaryDto.setCategory(entity.getSubCategory().getCategory().getName());
        }
        summaryDto.setTransactionDateTime(entity.getTransactionDateTime().atZone(ZoneId.of(zoneId)));
        summaryDto.setNote(entity.getNote());
        return summaryDto;

    }
}
