package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.validation.OnCreate;
import com.cashigo.expensio.common.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    @Null(groups = OnCreate.class, message = "Id not allowed while saving category")
    @NotNull(groups = OnUpdate.class, message = "Category Id cannot be null")
    @Schema(example = "3")
    private Long id;
    @NotBlank(message = "Category name cannot be empty", groups = {OnCreate.class, OnUpdate.class})
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean system;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, defaultValue = "[]")
    private List<SubCategoryDto> subCategories;
}
