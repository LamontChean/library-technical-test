package org.library.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.application.service.BorrowingService;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Book;
import org.library.domain.model.Borrower;
import org.library.domain.model.BorrowingRecord;
import org.library.test.config.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowingController.class)
@ContextConfiguration(classes = TestApplication.class)
class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowingService borrowingService;

    private Book book;
    private Borrower borrower;
    private BorrowingRecord borrowingRecord;

    @BeforeEach
    void setUp() {
        book = new Book("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        book.setId("book-123");
        book.setAvailable(true);

        borrower = new Borrower("John Doe", "john.doe@example.com");
        borrower.setId("borrower-123");

        borrowingRecord = new BorrowingRecord(book, borrower);
        borrowingRecord.setId("record-123");
    }

    @Test
    void testBorrowBook_Success() throws Exception {
        BorrowBookRequest request = new BorrowBookRequest("book-123", "borrower-123");

        when(borrowingService.borrowBook(any(BorrowBookRequest.class))).thenReturn(borrowingRecord);

        mockMvc.perform(post("/api/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("record-123"))
                .andExpect(jsonPath("$.data.returned").value(false))
                .andExpect(jsonPath("$.message").value("Book borrowed successfully"));

        verify(borrowingService, times(1)).borrowBook(any(BorrowBookRequest.class));
    }

    @Test
    void testBorrowBook_InvalidRequest() throws Exception {
        BorrowBookRequest invalidRequest = new BorrowBookRequest("", "");

        mockMvc.perform(post("/api/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(borrowingService, never()).borrowBook(any(BorrowBookRequest.class));
    }

    @Test
    void testBorrowBook_BookNotAvailable() throws Exception {
        BorrowBookRequest request = new BorrowBookRequest("book-123", "borrower-123");

        when(borrowingService.borrowBook(any(BorrowBookRequest.class)))
                .thenThrow(new LibraryServiceException(ErrorCode.BOOK_NOT_AVAILABLE, "Book is not available"));

        mockMvc.perform(post("/api/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BOOK_NOT_AVAILABLE.getCode()));

        verify(borrowingService, times(1)).borrowBook(any(BorrowBookRequest.class));
    }

    @Test
    void testReturnBook_Success() throws Exception {
        ReturnBookRequest request = new ReturnBookRequest("book-123");
        borrowingRecord.setReturned(true);
        borrowingRecord.setReturnDate(LocalDateTime.now());

        when(borrowingService.returnBook(any(ReturnBookRequest.class))).thenReturn(borrowingRecord);

        mockMvc.perform(post("/api/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("record-123"))
                .andExpect(jsonPath("$.data.returned").value(true))
                .andExpect(jsonPath("$.message").value("Book returned successfully"));

        verify(borrowingService, times(1)).returnBook(any(ReturnBookRequest.class));
    }

    @Test
    void testReturnBook_NoActiveBorrowing() throws Exception {
        ReturnBookRequest request = new ReturnBookRequest("book-123");

        when(borrowingService.returnBook(any(ReturnBookRequest.class)))
                .thenThrow(new LibraryServiceException(ErrorCode.NO_ACTIVE_BORROWING, "No active borrowing"));

        mockMvc.perform(post("/api/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.NO_ACTIVE_BORROWING.getCode()));

        verify(borrowingService, times(1)).returnBook(any(ReturnBookRequest.class));
    }

    @Test
    void testGetAllBorrowingRecords() throws Exception {
        BorrowingRecord record1 = new BorrowingRecord(book, borrower);
        record1.setId("record-1");
        BorrowingRecord record2 = new BorrowingRecord(book, borrower);
        record2.setId("record-2");
        record2.setReturned(true);
        record2.setReturnDate(LocalDateTime.now());
        List<BorrowingRecord> records = Arrays.asList(record1, record2);

        when(borrowingService.getAllBorrowingRecords()).thenReturn(records);

        mockMvc.perform(get("/api/borrowings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("record-1"))
                .andExpect(jsonPath("$.data[1].id").value("record-2"));

        verify(borrowingService, times(1)).getAllBorrowingRecords();
    }

    @Test
    void testGetBorrowingRecord_Success() throws Exception {
        when(borrowingService.getBorrowingRecordById("record-123")).thenReturn(borrowingRecord);

        mockMvc.perform(get("/api/borrowings/{id}", "record-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("record-123"));

        verify(borrowingService, times(1)).getBorrowingRecordById("record-123");
    }

    @Test
    void testGetBorrowingRecord_NotFound() throws Exception {
        when(borrowingService.getBorrowingRecordById("non-existent"))
                .thenThrow(new LibraryServiceException(ErrorCode.BORROWING_RECORD_NOT_FOUND, "Record not found"));

        mockMvc.perform(get("/api/borrowings/{id}", "non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BORROWING_RECORD_NOT_FOUND.getCode()));

        verify(borrowingService, times(1)).getBorrowingRecordById("non-existent");
    }
}
