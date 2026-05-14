package org.library.facade.impl;

import lombok.RequiredArgsConstructor;
import org.library.application.dto.request.BookRequest;
import org.library.application.service.BookService;
import org.library.domain.model.Book;
import org.library.facade.BookFacade;
import org.springframework.stereotype.Component;

/**
 * Facade implementation for book operations.
 * Delegates to business service for actual processing.
 */
@Component
@RequiredArgsConstructor
public class BookFacadeImpl implements BookFacade {

    private final BookService bookService;

    @Override
    public Book registerBook(BookRequest request) {
        return bookService.registerBook(request);
    }
}
