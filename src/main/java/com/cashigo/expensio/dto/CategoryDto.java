package com.cashigo.expensio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto implements Dto {
    private Long id;
    private String name;
    private boolean isSystem;
    private List<SubCategoryDto> subCategories;
}
