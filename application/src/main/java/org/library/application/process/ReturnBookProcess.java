package org.library.application.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.application.dto.response.ReturnBookResult;
import org.library.application.process.common.AbstractProcessTemplate;
import org.library.application.process.context.ReturnBookContext;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Book;
import org.library.domain.model.BorrowingRecord;
import org.library.infrastructure.repository.BookRepository;
import org.library.infrastructure.repository.BorrowingRecordRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Process for returning a book.
 * Uses AbstractProcessTemplate for standardized workflow execution.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReturnBookProcess extends AbstractProcessTemplate<ReturnBookRequest, ReturnBookResult, ReturnBookContext> {

    private final BookRepository bookRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    @Override
    protected void preCheck(ReturnBookContext context) {
        ReturnBookRequest request = context.getRequestData();
        
        // Validate book exists
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new LibraryServiceException(
                        ErrorCode.BOOK_NOT_FOUND, request.getBookId()));
        
        // Validate book is currently borrowed
        if (book.isAvailable()) {
            throw new LibraryServiceException(ErrorCode.NO_ACTIVE_BORROWING);
        }
        
        // Find active borrowing record
        BorrowingRecord activeRecord = borrowingRecordRepository.findActiveBorrowingByBookId(request.getBookId())
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.NO_ACTIVE_BORROWING));
        
        // Store in typed context fields
        context.setBook(book);
        context.setActiveRecord(activeRecord);
    }

    @Override
    protected ReturnBookResult doExecute(ReturnBookContext context) {
        Book book = context.getBook();
        BorrowingRecord activeRecord = context.getActiveRecord();
        
        log.info("Processing book return - BookId: {}, BorrowingRecordId: {}", 
                book.getId(), activeRecord.getId());
        
        // Calculate borrowing duration
        long borrowingDays = java.time.Duration.between(
                activeRecord.getBorrowDate(), 
                java.time.LocalDateTime.now()
        ).toDays();
        
        // Mark as returned
        activeRecord.setReturned(true);
        activeRecord.setReturnDate(java.time.LocalDateTime.now());
        borrowingRecordRepository.save(activeRecord);
        
        // Mark book as available
        book.setAvailable(true);
        bookRepository.save(book);
        
        log.info("Book returned successfully - BookId: {}, Duration: {} days", 
                book.getId(), borrowingDays);
        
        // Store in typed context
        context.setBorrowingDays(borrowingDays);
        
        return ReturnBookResult.builder()
                .borrowingRecord(activeRecord)
                .book(book)
                .borrowingDays(borrowingDays)
                .build();
    }

    @Override
    protected void postProcess(ReturnBookContext context, 
                               ReturnBookResult result) {
        log.info("Post-processing for book return - Adding audit metadata");
        
        // Set audit fields using typed context
        context.setAuditBookId(result.getBook().getId());
        context.setAuditBorrowingRecordId(result.getBorrowingRecord().getId());
        context.setBorrowingDays(result.getBorrowingDays());
        
        // In real application, you might:
        // - Send return confirmation email
        // - Calculate and charge late fees
        // - Update borrower statistics
        // - Send book availability notification to waitlist
        // - Log to audit system
    }

    @Override
    protected void handleError(ReturnBookContext context, 
                               Exception exception) {
        log.warn("Error during book return process, performing cleanup");
        
        // In real application, you might:
        // - Rollback book availability if partially updated
        // - Send error notification
        // - Update error metrics
    }

    @Override
    protected String getErrorCode(Exception exception) {
        if (exception instanceof LibraryServiceException ex) {
            return ex.getErrorCode().getCode();
        }
        return ErrorCode.UNKNOWN_ERROR.getCode();
    }

    @Override
    protected String getProcessName() {
        return "ReturnBookProcess";
    }
}
