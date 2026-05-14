package com.library.service;

import com.library.domain.Book;
import com.library.domain.Borrower;
import com.library.domain.Borrowing;
import com.library.dto.BorrowRequest;
import com.library.dto.BorrowingResponse;
import com.library.exception.BusinessRuleException;
import com.library.exception.ResourceNotFoundException;
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
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    public BorrowingService(BorrowingRepository borrowingRepository, 
                           BookRepository bookRepository,
                           BorrowerRepository borrowerRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowingResponse borrowBook(BorrowRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", request.getBookId()));
        
        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower", "id", request.getBorrowerId()));

        if (!book.isAvailable()) {
            throw new BusinessRuleException(
                    "Book is not available for borrowing",
                    "BOOK_NOT_AVAILABLE"
            );
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
                .orElseThrow(() -> new ResourceNotFoundException("Borrowing", "id", borrowingId));

        if (!borrowing.isActive()) {
            throw new BusinessRuleException(
                    "Book has already been returned",
                    "BOOK_ALREADY_RETURNED"
            );
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
