package org.library.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.library.application.dto.request.BookRequest;
import org.library.application.service.BookService;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Book;
import org.library.domain.model.BookCatalog;
import org.library.infrastructure.repository.BookCatalogRepository;
import org.library.infrastructure.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for book operations.
 * Supports multiple physical copies of books with the same ISBN.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookCatalogRepository catalogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BOOK_NOT_FOUND, id));
    }

    @Override
    @Transactional
    public Book registerBook(BookRequest request) {
        validateIsbnFormat(request.getIsbn());
        
        // Find or create catalog entry
        BookCatalog catalog = catalogRepository.findByIsbn(request.getIsbn())
                .orElseGet(() -> {
                    BookCatalog newCatalog = new BookCatalog(
                            request.getIsbn(),
                            request.getTitle(),
                            request.getAuthor()
                    );
                    return catalogRepository.save(newCatalog);
                });

        // Validate that existing catalog has same title/author
        if (!catalog.getTitle().equals(request.getTitle()) || 
            !catalog.getAuthor().equals(request.getAuthor())) {
            throw new LibraryServiceException(
                    ErrorCode.ISBN_CONFLICT,
                    catalog.getIsbn(),
                    catalog.getTitle(),
                    catalog.getAuthor()
            );
        }

        // Create a new physical copy
        Book book = new Book(catalog);
        return bookRepository.save(book);
    }

    private void validateIsbnFormat(String isbn) {
        if (!isValidIsbn(isbn)) {
            throw new LibraryServiceException(ErrorCode.INVALID_ISBN);
        }
    }

    /**
     * Validate ISBN format (supports ISBN-10 and ISBN-13)
     */
    private boolean isValidIsbn(String isbn) {
        if (isbn == null) return false;
        String cleaned = isbn.replace("-", "").replace(" ", "");
        return cleaned.matches("^\\d{9}(\\d|X)$") || cleaned.matches("^\\d{12}(\\d|X)$");
    }
}
