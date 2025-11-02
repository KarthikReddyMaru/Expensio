package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.model.SubCategory;
import org.springframework.stereotype.Component;

@Component
public class SubCategoryMapper implements BiMapper<SubCategoryDto, SubCategory> {

    @Override
    public SubCategory mapToEntity(SubCategoryDto dto) {
        if (dto == null) return null;

        SubCategory sub = new SubCategory();
        sub.setId(dto.getId());
        sub.setName(dto.getName());
        sub.setSystem(dto.isSystem());
        return sub;
    }

    @Override
    public SubCategoryDto mapToDto(SubCategory entity) {
        if (entity == null) return null;

        SubCategoryDto dto = new SubCategoryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSystem(entity.isSystem());

        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
        }

        return dto;
    }
}
