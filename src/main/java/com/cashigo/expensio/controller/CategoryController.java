package com.cashigo.expensio.controller;

import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Response<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categoryDtoList = categoryService.getAllCategoriesByUserId();
        Response<List<CategoryDto>> response = new Response<>();
        response.setData(categoryDtoList);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CategoryDto>> getCategoryById(@PathVariable Long id) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        Response<CategoryDto> response = new Response<>();
        response.setData(categoryDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<CategoryDto>> saveCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Response<CategoryDto> response = new Response<>();
        CategoryDto savedCategory = categoryService.saveAndUpdateCategory(categoryDto);
        response.setData(savedCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response<CategoryDto>> updateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        Response<CategoryDto> response = new Response<>();
        CategoryDto savedCategory = categoryService.saveAndUpdateCategory(categoryDto);
        response.setData(savedCategory);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
