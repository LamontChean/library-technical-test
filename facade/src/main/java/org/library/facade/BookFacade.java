package org.library.facade;

import org.library.application.dto.request.BookRequest;
import org.library.domain.model.Book;

/**
 * Facade interface for book-related operations.
 * Defines the contract for book workflow orchestration.
 */
public interface BookFacade {
    
    /**
     * Register a new book with validation
     * 
     * @param request book registration request
     * @return registered book
     */
    Book registerBook(BookRequest request);
}
