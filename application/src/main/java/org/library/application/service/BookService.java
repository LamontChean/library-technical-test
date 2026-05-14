package org.library.application.service;

import org.library.application.dto.request.BookRequest;
import org.library.domain.model.Book;

import java.util.List;

/**
 * Service interface for book operations.
 * Handles both queries and commands.
 */
public interface BookService {
    
    /**
     * Get all books
     */
    List<Book> getAllBooks();
    
    /**
     * Get book by ID
     */
    Book getBookById(String id);
    
    /**
     * Register a new book with business validation
     */
    Book registerBook(BookRequest request);
}
