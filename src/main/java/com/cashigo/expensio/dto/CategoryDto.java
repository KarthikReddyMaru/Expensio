package com.cashigo.expensio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @Positive(message = "Invalid ID")
    private Long id;
    @NotBlank(message = "Category name cannot be empty")
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean system;
    @Valid
    private List<SubCategoryDto> subCategories;
}
