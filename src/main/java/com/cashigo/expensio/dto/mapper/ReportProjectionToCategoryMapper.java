package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.CategoryReportDto;
import com.cashigo.expensio.dto.ReportProjection;
import org.springframework.stereotype.Component;

@Component
public class ReportProjectionToCategoryMapper implements Mapper<ReportProjection, CategoryReportDto> {

    @Override
    public CategoryReportDto map(ReportProjection entity) {
        return CategoryReportDto
                .builder()
                .category(entity.getCategory())
                .amountSpent(entity.getAmountSpent())
                .build();
    }
}
