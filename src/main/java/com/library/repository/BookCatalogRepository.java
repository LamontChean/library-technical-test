package com.library.repository;

import com.library.domain.BookCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookCatalogRepository extends JpaRepository<BookCatalog, UUID> {
    
    Optional<BookCatalog> findByIsbn(String isbn);
    
    boolean existsByIsbn(String isbn);
}
