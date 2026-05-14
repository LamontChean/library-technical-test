package com.library.service;

import com.library.domain.Book;
import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        sampleBook = new Book();
        sampleBook.setId(UUID.randomUUID());
        sampleBook.setIsbn("978-3-16-148410-0");
        sampleBook.setTitle("Sample Book");
        sampleBook.setAuthor("John Doe");
        sampleBook.setAvailable(true);
        sampleBook.setCreatedAt(LocalDateTime.now());

        bookRequest = new BookRequest();
        bookRequest.setIsbn("978-3-16-148410-0");
        bookRequest.setTitle("Sample Book");
        bookRequest.setAuthor("John Doe");
    }

    @Test
    void createBook_ShouldReturnBookResponse() {
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        BookResponse response = bookService.createBook(bookRequest);

        assertNotNull(response);
        assertEquals(sampleBook.getId(), response.getId());
        assertEquals(sampleBook.getIsbn(), response.getIsbn());
        assertEquals(sampleBook.getTitle(), response.getTitle());
        assertEquals(sampleBook.getAuthor(), response.getAuthor());
        assertTrue(response.isAvailable());

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        Book anotherBook = new Book();
        anotherBook.setId(UUID.randomUUID());
        anotherBook.setIsbn("978-3-16-148410-1");
        anotherBook.setTitle("Another Book");
        anotherBook.setAuthor("Jane Doe");
        anotherBook.setAvailable(true);
        anotherBook.setCreatedAt(LocalDateTime.now());

        when(bookRepository.findAll()).thenReturn(Arrays.asList(sampleBook, anotherBook));

        List<BookResponse> responses = bookService.getAllBooks();

        assertEquals(2, responses.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBookResponse() {
        UUID bookId = sampleBook.getId();
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(sampleBook));

        BookResponse response = bookService.getBookById(bookId);

        assertNotNull(response);
        assertEquals(bookId, response.getId());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookById_WhenBookNotFound_ShouldThrowException() {
        UUID bookId = UUID.randomUUID();
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.getBookById(bookId);
        });

        assertEquals("Book not found with id: " + bookId, exception.getMessage());
        verify(bookRepository, times(1)).findById(bookId);
    }
}
