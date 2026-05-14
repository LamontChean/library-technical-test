package com.library.service;

import com.library.domain.Book;
import com.library.domain.Borrower;
import com.library.domain.Borrowing;
import com.library.dto.BorrowRequest;
import com.library.dto.BorrowingResponse;
import com.library.exception.ErrorCode;
import com.library.exception.LibraryServiceException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowerRepository;
import com.library.repository.BorrowingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookService bookService;
    private final BorrowerService borrowerService;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    public BorrowingService(BorrowingRepository borrowingRepository, 
                           BookService bookService,
                           BorrowerService borrowerService,
                           BookRepository bookRepository,
                           BorrowerRepository borrowerRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookService = bookService;
        this.borrowerService = borrowerService;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowingResponse borrowBook(BorrowRequest request) {
        // Find any available copy of the book by ISBN
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BOOK_NOT_FOUND, request.getBookId()));
        
        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BORROWER_NOT_FOUND, request.getBorrowerId()));

        if (!book.isAvailable()) {
            throw new LibraryServiceException(ErrorCode.BOOK_NOT_AVAILABLE);
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setBorrower(borrower);
        
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        return toResponse(savedBorrowing);
    }

    public BorrowingResponse returnBook(UUID borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BORROWING_RECORD_NOT_FOUND, borrowingId));

        if (!borrowing.isActive()) {
            throw new LibraryServiceException(ErrorCode.BOOK_ALREADY_RETURNED);
        }

        borrowing.returnBook();
        Borrowing savedBorrowing = borrowingRepository.save(borrowing);
        return toResponse(savedBorrowing);
    }

    private BorrowingResponse toResponse(Borrowing borrowing) {
        return new BorrowingResponse(
                borrowing.getId(),
                borrowing.getBook().getId(),
                borrowing.getBook().getTitle(),
                borrowing.getBorrower().getId(),
                borrowing.getBorrower().getName(),
                borrowing.getBorrowedAt(),
                borrowing.getReturnedAt()
        );
    }
}
