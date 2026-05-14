package org.library.infrastructure.repository;

import org.library.domain.model.BookCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCatalogRepository extends JpaRepository<BookCatalog, String> {
    
    Optional<BookCatalog> findByIsbn(String isbn);
}
