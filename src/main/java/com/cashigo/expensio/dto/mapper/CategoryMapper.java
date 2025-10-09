package com.cashigo.expensio.dto.mapper;

import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryMapper implements BiMapper<CategoryDto, Category> {

    private final SubCategoryMapper subCategoryMapper;

    @Override
    public Category mapToEntity(CategoryDto dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setSystem(dto.isSystem());

        return category;
    }

    @Override
    public CategoryDto mapToDto(Category entity) {
        if (entity == null) return null;

        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSystem(entity.isSystem());

        if (entity.getSubCategories() != null) {
            dto.setSubCategories(
                    entity.getSubCategories()
                            .stream()
                            .map(subCategoryMapper::mapToDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
