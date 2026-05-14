package com.library.service;

import com.library.domain.Book;
import com.library.domain.BookCatalog;
import com.library.domain.Borrower;
import com.library.domain.Borrowing;
import com.library.dto.BorrowRequest;
import com.library.dto.BorrowingResponse;
import com.library.exception.ErrorCode;
import com.library.exception.LibraryServiceException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import com.library.repository.BorrowingRepository;
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
class BorrowingServiceTest {

    @Mock
    private BorrowingRepository borrowingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowingService borrowingService;

    private Book sampleBook;
    private Borrower sampleBorrower;
    private Borrowing sampleBorrowing;
    private BorrowRequest borrowRequest;

    @BeforeEach
    void setUp() {
        BookCatalog catalog = new BookCatalog();
        catalog.setId(UUID.randomUUID());
        catalog.setIsbn("978-3-16-148410-0");
        catalog.setTitle("Sample Book");
        catalog.setAuthor("John Doe");
        catalog.setCreatedAt(LocalDateTime.now());

        sampleBook = new Book();
        sampleBook.setId(UUID.randomUUID());
        sampleBook.setCatalog(catalog);
        sampleBook.setAvailable(true);
        sampleBook.setCreatedAt(LocalDateTime.now());

        sampleBorrower = new Borrower();
        sampleBorrower.setId(UUID.randomUUID());
        sampleBorrower.setName("John Doe");
        sampleBorrower.setEmail("john.doe@example.com");
        sampleBorrower.setCreatedAt(LocalDateTime.now());

        sampleBorrowing = new Borrowing();
        sampleBorrowing.setId(UUID.randomUUID());
        sampleBorrowing.setBook(sampleBook);
        sampleBorrowing.setBorrower(sampleBorrower);
        sampleBorrowing.setBorrowedAt(LocalDateTime.now());
        sampleBorrowing.setReturnedAt(null);

        borrowRequest = new BorrowRequest();
        borrowRequest.setBookId(sampleBook.getId());
        borrowRequest.setBorrowerId(sampleBorrower.getId());
    }

    @Test
    void borrowBook_ShouldReturnBorrowingResponse() {
        when(bookRepository.findById(borrowRequest.getBookId())).thenReturn(Optional.of(sampleBook));
        when(borrowerRepository.findById(borrowRequest.getBorrowerId())).thenReturn(Optional.of(sampleBorrower));
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(sampleBorrowing);

        BorrowingResponse response = borrowingService.borrowBook(borrowRequest);

        assertNotNull(response);
        assertEquals(sampleBook.getId(), response.getBookId());
        assertEquals(sampleBorrower.getId(), response.getBorrowerId());
        assertFalse(sampleBook.isAvailable());

        verify(bookRepository, times(1)).findById(borrowRequest.getBookId());
        verify(borrowerRepository, times(1)).findById(borrowRequest.getBorrowerId());
        verify(bookRepository, times(1)).save(sampleBook);
        verify(borrowingRepository, times(1)).save(any(Borrowing.class));
    }

    @Test
    void borrowBook_WhenBookNotFound_ShouldThrowException() {
        when(bookRepository.findById(borrowRequest.getBookId())).thenReturn(Optional.empty());

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            borrowingService.borrowBook(borrowRequest);
        });

        assertEquals(ErrorCode.BOOK_NOT_FOUND, exception.getErrorCode());
        verify(bookRepository, times(1)).findById(borrowRequest.getBookId());
        verify(borrowerRepository, never()).findById(any());
    }

    @Test
    void borrowBook_WhenBorrowerNotFound_ShouldThrowException() {
        when(bookRepository.findById(borrowRequest.getBookId())).thenReturn(Optional.of(sampleBook));
        when(borrowerRepository.findById(borrowRequest.getBorrowerId())).thenReturn(Optional.empty());

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            borrowingService.borrowBook(borrowRequest);
        });

        assertEquals(ErrorCode.BORROWER_NOT_FOUND, exception.getErrorCode());
        verify(bookRepository, times(1)).findById(borrowRequest.getBookId());
        verify(borrowerRepository, times(1)).findById(borrowRequest.getBorrowerId());
    }

    @Test
    void borrowBook_WhenBookNotAvailable_ShouldThrowException() {
        sampleBook.setAvailable(false);
        when(bookRepository.findById(borrowRequest.getBookId())).thenReturn(Optional.of(sampleBook));
        when(borrowerRepository.findById(borrowRequest.getBorrowerId())).thenReturn(Optional.of(sampleBorrower));

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            borrowingService.borrowBook(borrowRequest);
        });

        assertEquals(ErrorCode.BOOK_NOT_AVAILABLE, exception.getErrorCode());
        verify(bookRepository, times(1)).findById(borrowRequest.getBookId());
        verify(borrowerRepository, times(1)).findById(borrowRequest.getBorrowerId());
        verify(borrowingRepository, never()).save(any());
    }

    @Test
    void returnBook_ShouldReturnBorrowingResponse() {
        UUID borrowingId = sampleBorrowing.getId();
        when(borrowingRepository.findById(borrowingId)).thenReturn(Optional.of(sampleBorrowing));
        when(borrowingRepository.save(any(Borrowing.class))).thenReturn(sampleBorrowing);

        BorrowingResponse response = borrowingService.returnBook(borrowingId);

        assertNotNull(response);
        assertNotNull(response.getReturnedAt());
        assertTrue(sampleBook.isAvailable());

        verify(borrowingRepository, times(1)).findById(borrowingId);
        verify(borrowingRepository, times(1)).save(sampleBorrowing);
    }

    @Test
    void returnBook_WhenBorrowingNotFound_ShouldThrowException() {
        UUID borrowingId = UUID.randomUUID();
        when(borrowingRepository.findById(borrowingId)).thenReturn(Optional.empty());

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            borrowingService.returnBook(borrowingId);
        });

        assertEquals(ErrorCode.BORROWING_RECORD_NOT_FOUND, exception.getErrorCode());
        verify(borrowingRepository, times(1)).findById(borrowingId);
    }

    @Test
    void returnBook_WhenAlreadyReturned_ShouldThrowException() {
        sampleBorrowing.setReturnedAt(LocalDateTime.now());
        UUID borrowingId = sampleBorrowing.getId();
        when(borrowingRepository.findById(borrowingId)).thenReturn(Optional.of(sampleBorrowing));

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            borrowingService.returnBook(borrowingId);
        });

        assertEquals(ErrorCode.BOOK_ALREADY_RETURNED, exception.getErrorCode());
        verify(borrowingRepository, times(1)).findById(borrowingId);
        verify(borrowingRepository, never()).save(any());
    }

    @Test
    void getAllBorrowings_ShouldReturnListOfBorrowings() {
        Borrowing anotherBorrowing = new Borrowing();
        anotherBorrowing.setId(UUID.randomUUID());
        anotherBorrowing.setBook(sampleBook);
        anotherBorrowing.setBorrower(sampleBorrower);
        anotherBorrowing.setBorrowedAt(LocalDateTime.now());
        anotherBorrowing.setReturnedAt(null);

        when(borrowingRepository.findAll()).thenReturn(Arrays.asList(sampleBorrowing, anotherBorrowing));

        List<BorrowingResponse> responses = borrowingService.getAllBorrowings();

        assertEquals(2, responses.size());
        verify(borrowingRepository, times(1)).findAll();
    }

    @Test
    void getBorrowingById_WhenBorrowingExists_ShouldReturnBorrowingResponse() {
        UUID borrowingId = sampleBorrowing.getId();
        when(borrowingRepository.findById(borrowingId)).thenReturn(Optional.of(sampleBorrowing));

        BorrowingResponse response = borrowingService.getBorrowingById(borrowingId);

        assertNotNull(response);
        assertEquals(borrowingId, response.getId());
        assertEquals(sampleBook.getId(), response.getBookId());
        assertEquals(sampleBorrower.getId(), response.getBorrowerId());
        verify(borrowingRepository, times(1)).findById(borrowingId);
    }

    @Test
    void getBorrowingById_WhenBorrowingNotFound_ShouldThrowException() {
        UUID borrowingId = UUID.randomUUID();
        when(borrowingRepository.findById(borrowingId)).thenReturn(Optional.empty());

        LibraryServiceException exception = assertThrows(LibraryServiceException.class, () -> {
            borrowingService.getBorrowingById(borrowingId);
        });

        assertEquals(ErrorCode.BORROWING_RECORD_NOT_FOUND, exception.getErrorCode());
        verify(borrowingRepository, times(1)).findById(borrowingId);
    }
}
