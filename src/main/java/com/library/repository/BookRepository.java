package com.library.repository;

import com.library.domain.Book;
import com.library.domain.BookCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    
    @Query("SELECT b FROM Book b WHERE b.catalog = :catalog AND b.available = true ORDER BY b.createdAt ASC LIMIT 1")
    Optional<Book> findFirstAvailableByCatalog(@Param("catalog") BookCatalog catalog);
    
    long countByCatalogAndAvailableTrue(BookCatalog catalog);
}
