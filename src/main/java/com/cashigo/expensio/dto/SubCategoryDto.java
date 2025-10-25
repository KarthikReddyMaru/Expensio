package com.cashigo.expensio.dto;

import com.cashigo.expensio.common.validation.OnCreate;
import com.cashigo.expensio.common.validation.OnUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryDto {

    @Null(groups = OnCreate.class, message = "Id not allowed while saving sub category")
    @NotNull(groups = OnUpdate.class, message = "Sub category Id cannot be null")
    @Schema(example = "3")
    private Long id;

    @NotBlank(message = "Sub Category name cannot be empty", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean system;

    @Positive
    @NotNull(message = "Invalid category Id", groups = {OnUpdate.class, OnCreate.class})
    @Schema(name = "categoryId", example = "3")
    private Long categoryId;
}
