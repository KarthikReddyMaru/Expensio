package com.cashigo.expensio.controller;

import com.cashigo.expensio.common.documentation.StandardErrorResponses;
import com.cashigo.expensio.common.validation.OnCreate;
import com.cashigo.expensio.common.validation.OnUpdate;
import com.cashigo.expensio.dto.Response;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.model.SubCategory;
import com.cashigo.expensio.service.SubCategoryService;
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

@RestController
@RequestMapping("/subcategory")
@RequiredArgsConstructor
@StandardErrorResponses
@Tag(name = "SubCategory")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping(path = "/{subCategoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Sub category by Sub category ID") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<SubCategoryDto>> getSubCategoryById(@PathVariable Long subCategoryId) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto subCategoryDto = subCategoryService.getSubCategoryById(subCategoryId);
        response.setData(subCategoryDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Save Sub Category") @ApiResponse(responseCode = "201")
    public ResponseEntity<Response<SubCategoryDto>> saveSubCategory(@Validated(OnCreate.class)
                                                                        @RequestBody SubCategoryDto subCategory) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto savedSubCategory = subCategoryService.saveSubCategory(subCategory);
        response.setData(savedSubCategory);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update Sub Category") @ApiResponse(responseCode = "200")
    public ResponseEntity<Response<SubCategoryDto>> updateSubCategory(@Validated(OnUpdate.class)
                                                                          @RequestBody SubCategoryDto subCategory) {
        Response<SubCategoryDto> response = new Response<>();
        SubCategoryDto savedSubCategory = subCategoryService.updateSubCategory(subCategory);
        response.setData(savedSubCategory);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{subCategoryId}")
    @Operation(summary = "Delete Sub Category By Sub category Id") @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long subCategoryId) {
        subCategoryService.deleteSubCategory(subCategoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
