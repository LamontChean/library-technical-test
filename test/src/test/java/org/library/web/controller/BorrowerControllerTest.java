package org.library.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.library.application.dto.request.BorrowerRequest;
import org.library.application.service.BorrowerService;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Borrower;
import org.library.test.config.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowerController.class)
@ContextConfiguration(classes = TestApplication.class)
class BorrowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowerService borrowerService;

    @Test
    void testRegisterBorrower_Success() throws Exception {
        BorrowerRequest request = new BorrowerRequest("John Doe", "john.doe@example.com");
        Borrower borrower = new Borrower("John Doe", "john.doe@example.com");
        borrower.setId("borrower-123");

        when(borrowerService.registerBorrower(any(BorrowerRequest.class))).thenReturn(borrower);

        mockMvc.perform(post("/api/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("borrower-123"))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.message").value("Borrower registered successfully"));

        verify(borrowerService, times(1)).registerBorrower(any(BorrowerRequest.class));
    }

    @Test
    void testRegisterBorrower_InvalidRequest() throws Exception {
        BorrowerRequest invalidRequest = new BorrowerRequest("", "invalid-email");

        mockMvc.perform(post("/api/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(borrowerService, never()).registerBorrower(any(BorrowerRequest.class));
    }

    @Test
    void testGetBorrower_Success() throws Exception {
        Borrower borrower = new Borrower("Jane Smith", "jane.smith@example.com");
        borrower.setId("borrower-456");

        when(borrowerService.getBorrowerById("borrower-456")).thenReturn(borrower);

        mockMvc.perform(get("/api/borrowers/{id}", "borrower-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("borrower-456"))
                .andExpect(jsonPath("$.data.name").value("Jane Smith"))
                .andExpect(jsonPath("$.data.email").value("jane.smith@example.com"));

        verify(borrowerService, times(1)).getBorrowerById("borrower-456");
    }

    @Test
    void testGetBorrower_NotFound() throws Exception {
        when(borrowerService.getBorrowerById("non-existent"))
                .thenThrow(new LibraryServiceException(ErrorCode.BORROWER_NOT_FOUND, "Borrower not found"));

        mockMvc.perform(get("/api/borrowers/{id}", "non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BORROWER_NOT_FOUND.getCode()));

        verify(borrowerService, times(1)).getBorrowerById("non-existent");
    }

    @Test
    void testGetAllBorrowers() throws Exception {
        Borrower borrower1 = new Borrower("John Doe", "john@example.com");
        borrower1.setId("borrower-1");
        Borrower borrower2 = new Borrower("Jane Smith", "jane@example.com");
        borrower2.setId("borrower-2");
        java.util.List<Borrower> borrowers = java.util.Arrays.asList(borrower1, borrower2);

        when(borrowerService.getAllBorrowers()).thenReturn(borrowers);

        mockMvc.perform(get("/api/borrowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("borrower-1"))
                .andExpect(jsonPath("$.data[1].id").value("borrower-2"));

        verify(borrowerService, times(1)).getAllBorrowers();
    }
}
