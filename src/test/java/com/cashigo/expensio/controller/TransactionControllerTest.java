package com.cashigo.expensio.controller;

import com.cashigo.expensio.config.SecurityConfig;
import com.cashigo.expensio.dto.TransactionDto;
import com.cashigo.expensio.dto.exception.NoTransactionFoundException;
import com.cashigo.expensio.service.transaction.TransactionService;
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

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    @WithMockUser
    void whenFetchingTransactionById_thenDtoIsReturned() throws Exception {
        UUID transactionId = UUID.randomUUID();
        Long subCategoryId = 10L;
        TransactionDto transactionDto = createTransaction(transactionId, subCategoryId);

        when(transactionService.getTransactionById(transactionId)).thenReturn(transactionDto);

        mockMvc.perform(get("/transaction/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.data.subCategoryId").value(subCategoryId));
    }

    @Test
    @WithMockUser
    void whenSavingTransaction_thenItIsSavedInDB() throws Exception {
        Long subCategoryId = 40L;
        TransactionDto unsavedTransaction = createTransaction(null, subCategoryId);
        UUID savedTransactionId = UUID.randomUUID();
        TransactionDto savedTransaction = createTransaction(savedTransactionId, subCategoryId);

        when(transactionService.saveTransaction(unsavedTransaction)).thenReturn(savedTransaction);

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unsavedTransaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(savedTransactionId.toString()));
        verify(transactionService).saveTransaction(unsavedTransaction);
    }

    @Test
    @WithMockUser
    void whenUpdatingTransaction_thenItIsUpdated() throws Exception {
        UUID transactionId = UUID.randomUUID();
        Long subCategoryId = 30L;
        TransactionDto transactionDto = createTransaction(transactionId, subCategoryId);
        TransactionDto updatedTransaction = createTransaction(transactionId, subCategoryId);

        when(transactionService.updateTransaction(transactionDto)).thenReturn(updatedTransaction);

        mockMvc.perform(put("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk());
        verify(transactionService).updateTransaction(transactionDto);
    }

    @Test
    @WithMockUser
    void whenDeletingTransactionById_thenItIsRemovedFromDB() throws Exception {
        UUID transactionId = UUID.randomUUID();

        mockMvc.perform(delete("/transaction/{id}", transactionId))
                .andExpect(status().isNoContent());
        verify(transactionService).deleteTransaction(transactionId);
    }

    @Test
    @WithAnonymousUser
    void whenFetchingTransactionWithoutAuthentication_thenReturnUnAuthorized() throws Exception {
        UUID transactionId = UUID.randomUUID();
        mockMvc.perform(get("/transaction/{id}", transactionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void whenSavingTransactionWithoutAuthentication_thenReturnUnAuthorized() throws Exception {
        Long subCategoryId = 30L;
        TransactionDto transaction = createTransaction(null, subCategoryId);
        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void whenUpdatingTransactionWithoutAuthentication_thenReturnUnAuthorized() throws Exception {
        Long subCategoryId = 30L;
        UUID transactionId = UUID.randomUUID();
        TransactionDto transaction = createTransaction(transactionId, subCategoryId);
        mockMvc.perform(put("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void whenDeletingTransactionWithoutAuthentication_thenReturnUnAuthorized() throws Exception {
        UUID transactionId = UUID.randomUUID();
        mockMvc.perform(delete("/transaction/{id}", transactionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void whenFetchingOtherUserTransactionById_thenReturnNotFound() throws Exception {
        UUID transactionId = UUID.randomUUID();

        when(transactionService.getTransactionById(transactionId))
                .thenThrow(NoTransactionFoundException.class);

        mockMvc.perform(get("/transaction/{id}", transactionId))
                .andExpect(status().isNotFound());
        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    @WithMockUser
    void whenUpdatingOtherUserTransaction_thenReturnNotFound() throws Exception {
        UUID transactionId = UUID.randomUUID();
        Long subCategoryId = 30L;
        TransactionDto transactionDto = createTransaction(transactionId, subCategoryId);

        when(transactionService.updateTransaction(transactionDto))
                .thenThrow(NoTransactionFoundException.class);

        mockMvc.perform(put("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isNotFound());
        verify(transactionService).updateTransaction(transactionDto);
    }


    private TransactionDto createTransaction(UUID transactionId, Long subCategoryId) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transactionId);
        transactionDto.setSubCategoryId(subCategoryId);
        return transactionDto;
    }
}
