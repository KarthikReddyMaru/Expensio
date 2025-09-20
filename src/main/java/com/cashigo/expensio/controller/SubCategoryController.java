package com.cashigo.expensio.controller;

import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.service.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subcategory")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<Response<List<SubCategoryDto>>> getSubCategories(@PathVariable Long categoryId) {
        Response<List<SubCategoryDto>> response = new Response<>();
        List<SubCategoryDto> subCategories = subCategoryService.getSubCategories(categoryId);
        response.setData(subCategories);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<SubCategoryDto>> saveSubCategory(@RequestBody SubCategoryDto subCategory) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto savedSubCategory = subCategoryService.saveSubCategory(subCategory);
        response.setData(savedSubCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response<SubCategoryDto>> updateSubCategory(@RequestBody SubCategoryDto subCategory) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto savedSubCategory = subCategoryService.saveSubCategory(subCategory);
        response.setData(savedSubCategory);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{subCategoryId}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long subCategoryId) {
        subCategoryService.deleteSubCategory(subCategoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
