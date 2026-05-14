package com.library.service;

import com.library.domain.Book;
import com.library.domain.Borrower;
import com.library.domain.Borrowing;
import com.library.dto.BorrowRequest;
import com.library.dto.BorrowingResponse;
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
        sampleBook = new Book();
        sampleBook.setId(UUID.randomUUID());
        sampleBook.setIsbn("978-3-16-148410-0");
        sampleBook.setTitle("Sample Book");
        sampleBook.setAuthor("John Doe");
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowingService.borrowBook(borrowRequest);
        });

        assertEquals("Book not found with id: " + borrowRequest.getBookId(), exception.getMessage());
        verify(bookRepository, times(1)).findById(borrowRequest.getBookId());
        verify(borrowerRepository, never()).findById(any());
    }

    @Test
    void borrowBook_WhenBorrowerNotFound_ShouldThrowException() {
        when(bookRepository.findById(borrowRequest.getBookId())).thenReturn(Optional.of(sampleBook));
        when(borrowerRepository.findById(borrowRequest.getBorrowerId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowingService.borrowBook(borrowRequest);
        });

        assertEquals("Borrower not found with id: " + borrowRequest.getBorrowerId(), exception.getMessage());
        verify(bookRepository, times(1)).findById(borrowRequest.getBookId());
        verify(borrowerRepository, times(1)).findById(borrowRequest.getBorrowerId());
    }

    @Test
    void borrowBook_WhenBookNotAvailable_ShouldThrowException() {
        sampleBook.setAvailable(false);
        when(bookRepository.findById(borrowRequest.getBookId())).thenReturn(Optional.of(sampleBook));
        when(borrowerRepository.findById(borrowRequest.getBorrowerId())).thenReturn(Optional.of(sampleBorrower));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowingService.borrowBook(borrowRequest);
        });

        assertEquals("Book is not available for borrowing", exception.getMessage());
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

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowingService.returnBook(borrowingId);
        });

        assertEquals("Borrowing record not found with id: " + borrowingId, exception.getMessage());
        verify(borrowingRepository, times(1)).findById(borrowingId);
    }

    @Test
    void returnBook_WhenAlreadyReturned_ShouldThrowException() {
        sampleBorrowing.setReturnedAt(LocalDateTime.now());
        UUID borrowingId = sampleBorrowing.getId();
        when(borrowingRepository.findById(borrowingId)).thenReturn(Optional.of(sampleBorrowing));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            borrowingService.returnBook(borrowingId);
        });

        assertEquals("Book has already been returned", exception.getMessage());
        verify(borrowingRepository, times(1)).findById(borrowingId);
        verify(borrowingRepository, never()).save(any());
    }
}
