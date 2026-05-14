package org.library.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.library.application.dto.request.BookRequest;
import org.library.application.service.BookService;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Book;
import org.library.domain.model.BookCatalog;
import org.library.test.config.TestApplication;
import org.library.web.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@ContextConfiguration(classes = TestApplication.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void testRegisterBook_Success() throws Exception {
        BookRequest request = new BookRequest("9783161484100", "Clean Code", "Robert C. Martin");
        BookCatalog catalog = new BookCatalog("9783161484100", "Clean Code", "Robert C. Martin");
        Book book = new Book(catalog);
        book.setId("book-123");

        when(bookService.registerBook(any(BookRequest.class))).thenReturn(book);

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("book-123"))
                .andExpect(jsonPath("$.data.isbn").value("9783161484100"))
                .andExpect(jsonPath("$.data.title").value("Clean Code"));

        verify(bookService, times(1)).registerBook(any(BookRequest.class));
    }

    @Test
    void testRegisterBook_InvalidRequest() throws Exception {
        BookRequest invalidRequest = new BookRequest("", "", "");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).registerBook(any(BookRequest.class));
    }

    @Test
    void testGetAllBooks() throws Exception {
        BookCatalog catalog1 = new BookCatalog("978-3-16-148410-0", "Book 1", "Author 1");
        Book book1 = new Book(catalog1);
        book1.setId("book-1");
        BookCatalog catalog2 = new BookCatalog("978-3-16-148410-1", "Book 2", "Author 2");
        Book book2 = new Book(catalog2);
        book2.setId("book-2");
        List<Book> books = Arrays.asList(book1, book2);

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("book-1"))
                .andExpect(jsonPath("$.data[1].id").value("book-2"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById_Success() throws Exception {
        BookCatalog catalog = new BookCatalog("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        Book book = new Book(catalog);
        book.setId("book-123");

        when(bookService.getBookById("book-123")).thenReturn(book);

        mockMvc.perform(get("/api/books/{id}", "book-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("book-123"))
                .andExpect(jsonPath("$.data.title").value("Clean Code"));

        verify(bookService, times(1)).getBookById("book-123");
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        when(bookService.getBookById("non-existent"))
                .thenThrow(new LibraryServiceException(ErrorCode.BOOK_NOT_FOUND, "Book not found with id: non-existent"));

        mockMvc.perform(get("/api/books/{id}", "non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.BOOK_NOT_FOUND.getCode()));

        verify(bookService, times(1)).getBookById("non-existent");
    }
}
