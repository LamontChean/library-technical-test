package org.library.application.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.dto.response.BorrowBookResult;
import org.library.application.process.common.AbstractProcessTemplate;
import org.library.application.process.context.BorrowBookContext;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Book;
import org.library.domain.model.Borrower;
import org.library.domain.model.BorrowingRecord;
import org.library.infrastructure.repository.BookRepository;
import org.library.infrastructure.repository.BorrowerRepository;
import org.library.infrastructure.repository.BorrowingRecordRepository;

/**
 * Process for borrowing a book.
 * Uses AbstractProcessTemplate for standardized workflow execution.
 */
@Slf4j
@RequiredArgsConstructor
public class BorrowBookProcess extends AbstractProcessTemplate<BorrowBookRequest, BorrowBookResult, BorrowBookContext> {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    @Override
    protected void preCheck(BorrowBookContext context) {
        BorrowBookRequest request = context.getRequestData();
        
        // Validate book exists and is available
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new LibraryServiceException(
                        ErrorCode.BOOK_NOT_FOUND, request.getBookId()));
        
        if (!book.isAvailable()) {
            throw new LibraryServiceException(ErrorCode.BOOK_NOT_AVAILABLE);
        }
        
        // Validate no active borrowing record exists
        borrowingRecordRepository.findActiveBorrowingByBookId(request.getBookId())
                .ifPresent(record -> {
                    throw new LibraryServiceException(ErrorCode.BOOK_ALREADY_BORROWED);
                });
        
        // Validate borrower exists
        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new LibraryServiceException(
                        ErrorCode.BORROWER_NOT_FOUND, request.getBorrowerId()));
        
        // Store in typed context fields
        context.setBook(book);
        context.setBorrower(borrower);
    }

    @Override
    protected BorrowBookResult doExecute(BorrowBookContext context) {
        Book book = context.getBook();
        Borrower borrower = context.getBorrower();
        
        log.info("Processing book borrow - BookId: {}, BorrowerId: {}", 
                book.getId(), borrower.getId());
        
        // Mark book as unavailable
        book.setAvailable(false);
        bookRepository.save(book);
        
        // Create borrowing record
        BorrowingRecord borrowingRecord = new BorrowingRecord(book, borrower);
        borrowingRecord = borrowingRecordRepository.save(borrowingRecord);
        
        log.info("Book borrowed successfully - BorrowingRecordId: {}", borrowingRecord.getId());
        
        return BorrowBookResult.builder()
                .borrowingRecord(borrowingRecord)
                .book(book)
                .borrower(borrower)
                .build();
    }

    @Override
    protected void postProcess(BorrowBookContext context, 
                               BorrowBookResult result) {
        log.info("Post-processing for book borrow - Adding audit metadata");
        
        // Set audit fields using typed context
        context.setAuditBookId(result.getBook().getId());
        context.setAuditBorrowerId(result.getBorrower().getId());
        context.setAuditBorrowingRecordId(result.getBorrowingRecord().getId());
        
        // In real application, you might:
        // - Send notification email to borrower
        // - Send push notification
        // - Update borrower statistics
        // - Log to audit system
    }

    @Override
    protected void handleError(BorrowBookContext context, 
                               Exception exception) {
        log.warn("Error during book borrow process, performing cleanup");
        
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
        return "BorrowBookProcess";
    }
}
