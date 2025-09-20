package com.cashigo.expensio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryDto implements Dto {
    private Long id;
    private String name;
    private boolean isSystem;
    private Long categoryId;
}
