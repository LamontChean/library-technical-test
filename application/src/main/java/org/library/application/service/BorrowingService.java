package org.library.application.service;

import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.domain.model.BorrowingRecord;

import java.util.List;

/**
 * Service interface for borrowing operations.
 * Handles both queries and commands.
 */
public interface BorrowingService {
    
    /**
     * Get all borrowing records
     */
    List<BorrowingRecord> getAllBorrowingRecords();
    
    /**
     * Get borrowing record by ID
     */
    BorrowingRecord getBorrowingRecordById(String id);
    
    /**
     * Borrow a book with business validation and process orchestration
     */
    BorrowingRecord borrowBook(BorrowBookRequest request);
    
    /**
     * Return a book with business validation and process orchestration
     */
    BorrowingRecord returnBook(ReturnBookRequest request);
}
