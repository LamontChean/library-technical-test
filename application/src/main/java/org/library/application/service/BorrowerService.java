package org.library.application.service;

import org.library.application.dto.request.BorrowerRequest;
import org.library.domain.model.Borrower;

/**
 * Service interface for borrower operations.
 * Handles both queries and commands.
 */
public interface BorrowerService {
    
    /**
     * Get all borrowers
     */
    java.util.List<Borrower> getAllBorrowers();
    
    /**
     * Get borrower by ID
     */
    Borrower getBorrowerById(String id);
    
    /**
     * Register a new borrower with business validation
     */
    Borrower registerBorrower(BorrowerRequest request);
}
