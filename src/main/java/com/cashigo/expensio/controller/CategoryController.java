package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.common.validation.OnCreate;
import com.cashigo.expensio.common.validation.OnUpdate;
import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "Category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get categories belongs to you") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categoryDtoList = categoryService.getAllCategoriesByUserId();
        Response<List<CategoryDto>> response = new Response<>();
        response.setData(categoryDtoList);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get category by category ID") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<CategoryDto>> getCategoryById(@PathVariable Long id) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        Response<CategoryDto> response = new Response<>();
        response.setData(categoryDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Save Category") @ApiResponse(responseCode = "201")
    public ResponseEntity<Response<CategoryDto>> saveCategory(@Validated(OnCreate.class)
                                                                  @RequestBody CategoryDto categoryDto) {
        Response<CategoryDto> response = new Response<>();
        CategoryDto savedCategory = categoryService.saveCategory(categoryDto);
        response.setData(savedCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update Category") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<CategoryDto>> updateCategory(@Validated(OnUpdate.class)
                                                                    @RequestBody CategoryDto categoryDto) {
        Response<CategoryDto> response = new Response<>();
        CategoryDto savedCategory = categoryService.updateCategory(categoryDto);
        response.setData(savedCategory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Category By category ID") @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
