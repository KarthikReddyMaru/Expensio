package com.cashigo.expensio.controller;

import com.cashigo.expensio.config.SecurityConfig;
import com.cashigo.expensio.dto.CategoryDto;
import com.cashigo.expensio.dto.exception.NoCategoryFoundException;
import com.cashigo.expensio.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @Test @WithMockUser
    void whenFetchingAllCategories_thenAllSystemAndCustomCategoriesReturned() throws Exception {
        List<CategoryDto> categories = List.of(
                createCategory("Apparel", true),
                createCategory("Education", false)
        );

        when(categoryService.getAllCategoriesByUserId()).thenReturn(categories);

        mockMvc
                .perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Apparel"))
                .andExpect(jsonPath("$.data[0].system").value(true))
                .andExpect(jsonPath("$.data[1].name").value("Education"))
                .andExpect(jsonPath("$.data[1].system").value(false))
                .andExpect(jsonPath("$.data.size()").value(categories.size()));
        verify(categoryService).getAllCategoriesByUserId();
    }

    @Test @WithMockUser
    void whenFetchingCategoryById_thenCategoryDtoIsReturned() throws Exception {
        Long categoryId = 3L;
        CategoryDto dto = new CategoryDto();
        dto.setId(categoryId);
        dto.setName("3rd Category");

        when(categoryService.getCategoryById(categoryId)).thenReturn(dto);

        mockMvc.perform(get("/category/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value("3rd Category"))
                .andExpect(jsonPath("$.data.id").value(categoryId));
        verify(categoryService).getCategoryById(categoryId);
    }

    @Test @WithMockUser
    void whenSavingCategory_thenCategoryIsCreated() throws Exception {
        String categoryName = "New Category";
        CategoryDto unsavedCategory = createCategory(categoryName, false);
        Long categoryId = 10L;
        CategoryDto savedCategory = createCategory(categoryName, false);
        savedCategory.setId(categoryId);

        when(categoryService.saveCategory(unsavedCategory)).thenReturn(savedCategory);

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unsavedCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value(categoryName))
                .andExpect(jsonPath("$.data.id").value(categoryId));
        verify(categoryService).saveCategory(unsavedCategory);
    }

    @Test @WithMockUser
    void whenUpdatingCategory_thenCategoryIsUpdated() throws Exception {
        String categoryName = "Updated Category";
        Long categoryId = 10L;
        CategoryDto unsavedCategory = createCategory(categoryName, false);
        unsavedCategory.setId(categoryId);
        CategoryDto savedCategory = createCategory(categoryName, false);
        savedCategory.setId(categoryId);

        when(categoryService.updateCategory(unsavedCategory)).thenReturn(savedCategory);

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unsavedCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value(categoryName))
                .andExpect(jsonPath("$.data.id").value(categoryId));
        verify(categoryService).updateCategory(unsavedCategory);
    }

    @Test @WithMockUser
    void whenDeletingCategoryById_thenCategoryIsDeleted() throws Exception {
        Long categoryId = 10L;

        mockMvc.perform(delete("/category/{id}", categoryId))
                .andExpect(status().isNoContent());
        verify(categoryService).deleteCategoryById(categoryId);
    }

    @Test @WithMockUser
    void whenFetchingOtherUserCategoryById_thenReturnNotFound() throws Exception {
        Long categoryId = 30L;

        when(categoryService.getCategoryById(categoryId)).thenThrow(NoCategoryFoundException.class);

        mockMvc.perform(get("/category/{id}", categoryId))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void whenUpdatingOtherUserCategory_thenReturnNotFound() throws Exception {
        Long categoryId = 30L;
        CategoryDto categoryDto = createCategory("Category", false);
        categoryDto.setId(categoryId);

        when(categoryService.updateCategory(categoryDto)).thenThrow(NoCategoryFoundException.class);

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    void whenSavingWithoutCategoryName_thenReturnBadRequest() throws Exception {
        CategoryDto categoryDto = createCategory(null, false);

        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isBadRequest());
    }

    CategoryDto createCategory(String name, boolean isSystem) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(name);
        categoryDto.setSystem(isSystem);
        return categoryDto;
    }

}
