package com.cashigo.expensio.controller;

import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.service.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subcategory")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/{subCategoryId}")
    public ResponseEntity<Response<SubCategoryDto>> getSubCategoryById(@PathVariable Long subCategoryId) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto subCategoryDto = subCategoryService.getSubCategoryById(subCategoryId);
        response.setData(subCategoryDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Response<SubCategoryDto>> saveSubCategory(@Valid @RequestBody SubCategoryDto subCategory) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto savedSubCategory = subCategoryService.saveSubCategory(subCategory);
        response.setData(savedSubCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response<SubCategoryDto>> updateSubCategory(@Valid @RequestBody SubCategoryDto subCategory) {
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
