package com.cashigo.expensio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryDto {
    private Long id;
    @NotBlank(message = "Sub Category name cannot be empty")
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isSystem;
    @Positive
    @NotNull(message = "Invalid category Id")
    private Long categoryId;
}
