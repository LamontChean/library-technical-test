package org.library.application.process.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.application.process.common.ProcessContext;
import org.library.domain.model.Book;
import org.library.domain.model.BorrowingRecord;

/**
 * Strongly-typed context for ReturnBookProcess.
 * Eliminates magic strings and provides compile-time safety.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBookContext extends ProcessContext<ReturnBookRequest> {
    
    /**
     * Validated book entity (set during preCheck)
     */
    private Book book;
    
    /**
     * Active borrowing record (set during preCheck)
     */
    private BorrowingRecord activeRecord;
    
    /**
     * Book ID for audit logging (set during postProcess)
     */
    private String auditBookId;
    
    /**
     * Borrowing record ID for audit logging (set during postProcess)
     */
    private String auditBorrowingRecordId;
    
    /**
     * Calculated borrowing duration in days (set during doExecute)
     */
    private Long borrowingDays;
}
