package org.library.facade;

import org.library.application.dto.request.BorrowerRequest;
import org.library.domain.model.Borrower;

/**
 * Facade interface for borrower-related operations.
 * Defines the contract for borrower workflow orchestration.
 */
public interface BorrowerFacade {
    
    /**
     * Register a new borrower with validation
     * 
     * @param request borrower registration request
     * @return registered borrower
     */
    Borrower registerBorrower(BorrowerRequest request);
}
