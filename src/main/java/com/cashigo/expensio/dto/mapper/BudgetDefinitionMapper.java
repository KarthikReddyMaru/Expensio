package com.cashigo.expensio.dto.mapper;


import com.cashigo.expensio.dto.BudgetDefinitionDto;
import com.cashigo.expensio.model.BudgetDefinition;
import com.cashigo.expensio.model.Category;
import org.springframework.stereotype.Component;

@Component
public class BudgetDefinitionMapper implements BiMapper<BudgetDefinitionDto, BudgetDefinition> {

    @Override
    public BudgetDefinition mapToEntity(BudgetDefinitionDto dto) {
        if (dto == null) return null;
        BudgetDefinition entity = new BudgetDefinition();
        entity.setId(dto.getId());
        if (dto.getCategoryId() != null) {
            Category cat = new Category();
            cat.setId(dto.getCategoryId());
            entity.setCategory(cat);
        }
        entity.setBudgetAmount(dto.getBudgetAmount());
        entity.setBudgetRecurrenceType(dto.getBudgetRecurrenceType());
        return entity;
    }

    @Override
    public BudgetDefinitionDto mapToDto(BudgetDefinition entity) {
        if (entity == null) return null;

        BudgetDefinitionDto dto = new BudgetDefinitionDto();
        dto.setId(entity.getId());
        dto.setCategoryId(entity.getCategory() != null ? entity.getCategory().getId() : null);
        dto.setBudgetAmount(entity.getBudgetAmount());
        dto.setBudgetRecurrenceType(entity.getBudgetRecurrenceType());

        return dto;
    }


}
