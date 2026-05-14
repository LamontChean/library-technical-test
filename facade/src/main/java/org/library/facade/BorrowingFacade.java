package org.library.facade;

import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.domain.model.BorrowingRecord;

/**
 * Facade interface for borrowing-related operations.
 * Defines the contract for book borrowing workflow orchestration.
 */
public interface BorrowingFacade {
    
    /**
     * Borrow a book with validation and process orchestration
     * 
     * @param request borrow book request
     * @return borrowing record
     */
    BorrowingRecord borrowBook(BorrowBookRequest request);
    
    /**
     * Return a book with validation and process orchestration
     * 
     * @param request return book request
     * @return borrowing record
     */
    BorrowingRecord returnBook(ReturnBookRequest request);
}
