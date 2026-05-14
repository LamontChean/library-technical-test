package com.library.service;

import com.library.domain.Book;
import com.library.domain.BookCatalog;
import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.exception.ErrorCode;
import com.library.exception.LibraryServiceException;
import com.library.repository.BookCatalogRepository;
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

    @Mock
    private BookCatalogRepository catalogRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;
    private BookCatalog sampleCatalog;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        sampleCatalog = new BookCatalog();
        sampleCatalog.setId(UUID.randomUUID());
        sampleCatalog.setIsbn("978-3-16-148410-0");
        sampleCatalog.setTitle("Sample Book");
        sampleCatalog.setAuthor("John Doe");
        sampleCatalog.setCreatedAt(LocalDateTime.now());

        sampleBook = new Book();
        sampleBook.setId(UUID.randomUUID());
        sampleBook.setCatalog(sampleCatalog);
        sampleBook.setAvailable(true);
        sampleBook.setCreatedAt(LocalDateTime.now());

        bookRequest = new BookRequest();
        bookRequest.setIsbn("978-3-16-148410-0");
        bookRequest.setTitle("Sample Book");
        bookRequest.setAuthor("John Doe");
    }

    @Test
    void createBook_WhenCatalogDoesNotExist_ShouldCreateCatalogAndBook() {
        when(catalogRepository.findByIsbn(bookRequest.getIsbn())).thenReturn(Optional.empty());
        when(catalogRepository.save(any(BookCatalog.class))).thenReturn(sampleCatalog);
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        BookResponse response = bookService.createBook(bookRequest);

        assertNotNull(response);
        assertEquals(sampleBook.getId(), response.getId());
        assertEquals(sampleCatalog.getIsbn(), response.getIsbn());
        assertEquals(sampleCatalog.getTitle(), response.getTitle());
        assertEquals(sampleCatalog.getAuthor(), response.getAuthor());
        assertTrue(response.isAvailable());

        verify(catalogRepository, times(1)).findByIsbn(bookRequest.getIsbn());
        verify(catalogRepository, times(1)).save(any(BookCatalog.class));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_WhenCatalogExists_ShouldReuseCatalog() {
        when(catalogRepository.findByIsbn(bookRequest.getIsbn())).thenReturn(Optional.of(sampleCatalog));
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

        BookResponse response = bookService.createBook(bookRequest);

        assertNotNull(response);
        assertEquals(sampleBook.getId(), response.getId());

        verify(catalogRepository, times(1)).findByIsbn(bookRequest.getIsbn());
        verify(catalogRepository, never()).save(any(BookCatalog.class));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void createBook_WhenCatalogHasDifferentTitleOrAuthor_ShouldThrowException() {
        BookRequest differentRequest = new BookRequest();
        differentRequest.setIsbn("978-3-16-148410-0");
        differentRequest.setTitle("Different Title");
        differentRequest.setAuthor("John Doe");

        when(catalogRepository.findByIsbn(differentRequest.getIsbn())).thenReturn(Optional.of(sampleCatalog));

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            bookService.createBook(differentRequest);
        });

        assertEquals(ErrorCode.ISBN_CONFLICT, exception.getErrorCode());
        verify(catalogRepository, times(1)).findByIsbn(differentRequest.getIsbn());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getAllBooks_ShouldReturnListOfBooks() {
        BookCatalog anotherCatalog = new BookCatalog();
        anotherCatalog.setId(UUID.randomUUID());
        anotherCatalog.setIsbn("978-3-16-148410-1");
        anotherCatalog.setTitle("Another Book");
        anotherCatalog.setAuthor("Jane Doe");
        anotherCatalog.setCreatedAt(LocalDateTime.now());

        Book anotherBook = new Book();
        anotherBook.setId(UUID.randomUUID());
        anotherBook.setCatalog(anotherCatalog);
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

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            bookService.getBookById(bookId);
        });

        assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
        verify(bookRepository, times(1)).findById(bookId);
    }
}
