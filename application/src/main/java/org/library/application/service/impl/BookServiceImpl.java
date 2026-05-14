package org.library.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.library.application.dto.request.BookRequest;
import org.library.application.service.BookService;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Book;
import org.library.infrastructure.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for book operations.
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

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
        return createAndSaveBook(request);
    }

    private void validateIsbnFormat(String isbn) {
        if (!isValidIsbn(isbn)) {
            throw new LibraryServiceException(ErrorCode.INVALID_ISBN);
        }
    }

    private Book createAndSaveBook(BookRequest request) {
        Book book = new Book(request.getIsbn(), request.getTitle(), request.getAuthor());
        return bookRepository.save(book);
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
