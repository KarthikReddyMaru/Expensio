package com.cashigo.expensio.controller;

import com.cashigo.expensio.config.SecurityConfig;
import com.cashigo.expensio.dto.SubCategoryDto;
import com.cashigo.expensio.dto.exception.NoSubCategoryFoundException;
import com.cashigo.expensio.dto.exception.SystemPropertiesCannotBeModifiedException;
import com.cashigo.expensio.service.SubCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubCategoryController.class)
@Import(SecurityConfig.class)
public class SubCategoryControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubCategoryService subCategoryService;

    @Test
    @WithMockUser
    void whenFetchingSubCategoryById_thenDtoIsReturned() throws Exception {
        Long subCategoryId = 10L;
        String subCategoryName = "SubCategory10";
        SubCategoryDto subCategoryDto = createSubCategory(subCategoryId, subCategoryName);

        when(subCategoryService.getSubCategoryById(subCategoryId)).thenReturn(subCategoryDto);

        mockMvc.perform(get("/subcategory/{id}", subCategoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(subCategoryId))
                .andExpect(jsonPath("$.data.name").value(subCategoryName));
        verify(subCategoryService).getSubCategoryById(subCategoryId);
    }

    @Test
    @WithMockUser
    void whenSavingSubCategory_thenSubCategoryIsCreated() throws Exception {
        String subCategoryName = "New SubCategory";
        SubCategoryDto unsavedSubCategory = createSubCategory(null, subCategoryName);
        Long savedSubCategoryId = 50L;
        SubCategoryDto savedSubCategory = createSubCategory(savedSubCategoryId, subCategoryName);

        when(subCategoryService.saveSubCategory(unsavedSubCategory)).thenReturn(savedSubCategory);

        mockMvc.perform(post("/subcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unsavedSubCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(savedSubCategoryId))
                .andExpect(jsonPath("$.data.name").value(subCategoryName))
                .andExpect(jsonPath("$.data.system").value(false));
        verify(subCategoryService).saveSubCategory(unsavedSubCategory);
    }

    @Test
    @WithMockUser
    void whenUpdatingSubCategory_thenSubCategoryIsUpdated() throws Exception {
        Long subCategoryId = 40L;
        String updatedSubCategoryName = "Updated SubCategory";
        SubCategoryDto subCategoryDto = createSubCategory(subCategoryId, updatedSubCategoryName);
        SubCategoryDto updatedSubCategoryDto = createSubCategory(subCategoryId, updatedSubCategoryName);

        when(subCategoryService.updateSubCategory(subCategoryDto)).thenReturn(updatedSubCategoryDto);

        mockMvc.perform(put("/subcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(subCategoryId))
                .andExpect(jsonPath("$.data.name").value(updatedSubCategoryName));
        verify(subCategoryService).updateSubCategory(subCategoryDto);
    }

    @Test
    @WithMockUser
    void whenDeletingSubCategory_thenSubCategoryIsRemovedFromDB() throws Exception {
        Long subCategoryId = 50L;
        mockMvc.perform(delete("/subcategory/{id}", subCategoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void whenFetchingOtherUserSubCategoryById_thenReturnNotFound() throws Exception {
        Long subCategoryId = 10L;

        when(subCategoryService.getSubCategoryById(subCategoryId))
                .thenThrow(NoSubCategoryFoundException.class);

        mockMvc.perform(get("/subcategory/{id}", subCategoryId))
                .andExpect(status().isNotFound());
        verify(subCategoryService).getSubCategoryById(subCategoryId);
    }

    @Test
    @WithMockUser
    void whenUpdatingOtherUserSubCategory_thenReturnNotFound() throws Exception {
        Long subCategoryId = 40L;
        String updatedSubCategoryName = "Updated SubCategory";
        SubCategoryDto subCategoryDto = createSubCategory(subCategoryId, updatedSubCategoryName);

        when(subCategoryService.updateSubCategory(subCategoryDto))
                .thenThrow(NoSubCategoryFoundException.class);

        mockMvc.perform(put("/subcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subCategoryDto)))
                .andExpect(status().isNotFound());
        verify(subCategoryService).updateSubCategory(subCategoryDto);
    }

    @Test
    @WithAnonymousUser
    void whenFetchingWithoutAuthentication_thenReturnUnAuthenticated() throws Exception {
        Long subCategoryId = 21L;
        mockMvc.perform(get("/subcategory/{id}", subCategoryId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void whenSavingWithoutAuthentication_thenReturnUnAuthenticated() throws Exception {
        SubCategoryDto subCategoryDto = createSubCategory(null, "New SubCategory");
        mockMvc.perform(post("/subcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subCategoryDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void whenUpdatingWithoutAuthentication_thenReturnUnAuthenticated() throws Exception {
        Long subCategoryId = 40L;
        SubCategoryDto subCategoryDto = createSubCategory(subCategoryId, "New SubCategory");
        mockMvc.perform(put("/subcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subCategoryDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void whenDeletingWithoutAuthentication_thenReturnUnAuthenticated() throws Exception {
        Long subCategoryId = 41L;
        mockMvc.perform(delete("/subcategory/{id}", subCategoryId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void whenUpdatingSystemSubCategory_thenReturnNotAcceptable() throws Exception {
        Long systemSubCategoryId = 5L;
        String updatedSubCategoryName = "Updated System SubCategory Name";
        SubCategoryDto systemSubCategory = createSubCategory(systemSubCategoryId, updatedSubCategoryName);

        when(subCategoryService.updateSubCategory(systemSubCategory))
                .thenThrow(SystemPropertiesCannotBeModifiedException.class);

        mockMvc.perform(put("/subcategory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(systemSubCategory)))
                .andExpect(status().isNotAcceptable());
        verify(subCategoryService).updateSubCategory(systemSubCategory);
    }

    private SubCategoryDto createSubCategory(Long id, String name) {
        SubCategoryDto subCategoryDto = new SubCategoryDto();
        subCategoryDto.setId(id);
        subCategoryDto.setName(name);
        subCategoryDto.setCategoryId(3L);
        return subCategoryDto;
    }
}
