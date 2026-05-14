package org.library.application.process.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.process.common.ProcessContext;
import org.library.domain.model.Book;
import org.library.domain.model.Borrower;

/**
 * Strongly-typed context for BorrowBookProcess.
 * Eliminates magic strings and provides compile-time safety.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookContext extends ProcessContext<BorrowBookRequest> {
    
    /**
     * Validated book entity (set during preCheck)
     */
    private Book book;
    
    /**
     * Validated borrower entity (set during preCheck)
     */
    private Borrower borrower;
    
    /**
     * Book ID for audit logging (set during postProcess)
     */
    private String auditBookId;
    
    /**
     * Borrower ID for audit logging (set during postProcess)
     */
    private String auditBorrowerId;
    
    /**
     * Borrowing record ID for audit logging (set during postProcess)
     */
    private String auditBorrowingRecordId;
}
