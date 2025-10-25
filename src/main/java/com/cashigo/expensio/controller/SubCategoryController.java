package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.service.SubCategoryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subcategory")
@RequiredArgsConstructor
@StandardErrorResponses
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping(path = "/{subCategoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<SubCategoryDto>> getSubCategoryById(@PathVariable Long subCategoryId) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto subCategoryDto = subCategoryService.getSubCategoryById(subCategoryId);
        response.setData(subCategoryDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Response<SubCategoryDto>> saveSubCategory(@Valid @RequestBody SubCategoryDto subCategory) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto savedSubCategory = subCategoryService.saveSubCategory(subCategory);
        response.setData(savedSubCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<SubCategoryDto>> updateSubCategory(@Valid @RequestBody SubCategoryDto subCategory) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto savedSubCategory = subCategoryService.saveSubCategory(subCategory);
        response.setData(savedSubCategory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{subCategoryId}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long subCategoryId) {
        subCategoryService.deleteSubCategory(subCategoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
