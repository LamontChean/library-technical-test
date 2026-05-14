package com.library.service;

import com.library.domain.Book;
import com.library.domain.BookCatalog;
import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.exception.ErrorCode;
import com.library.exception.LibraryServiceException;
import com.library.repository.BookCatalogRepository;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookCatalogRepository catalogRepository;

    public BookService(BookRepository bookRepository, BookCatalogRepository catalogRepository) {
        this.bookRepository = bookRepository;
        this.catalogRepository = catalogRepository;
    }

    public BookResponse createBook(BookRequest request) {
        // Find or create catalog entry
        BookCatalog catalog = catalogRepository.findByIsbn(request.getIsbn())
                .orElseGet(() -> {
                    BookCatalog newCatalog = new BookCatalog();
                    newCatalog.setIsbn(request.getIsbn());
                    newCatalog.setTitle(request.getTitle());
                    newCatalog.setAuthor(request.getAuthor());
                    return catalogRepository.save(newCatalog);
                });

        // Validate that existing catalog has same title/author
        if (!catalog.getTitle().equals(request.getTitle()) || 
            !catalog.getAuthor().equals(request.getAuthor())) {
            throw new LibraryServiceException(ErrorCode.ISBN_CONFLICT, 
                    catalog.getIsbn(), catalog.getTitle(), catalog.getAuthor());
        }

        // Create a new physical copy
        Book book = new Book();
        book.setCatalog(catalog);
        book.setAvailable(true);
        
        Book savedBook = bookRepository.save(book);
        return toResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(UUID id) {
        return bookRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BOOK_NOT_FOUND, id));
    }

    @Transactional(readOnly = true)
    public BookResponse findAvailableBookByIsbn(String isbn) {
        BookCatalog catalog = catalogRepository.findByIsbn(isbn)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BOOK_NOT_FOUND, isbn));
        
        Book availableCopy = bookRepository.findFirstAvailableByCatalog(catalog)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BOOK_NOT_AVAILABLE));
        
        return toResponse(availableCopy);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable(),
                book.getCreatedAt()
        );
    }
}
