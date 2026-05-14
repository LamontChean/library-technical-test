package org.library.infrastructure.repository;

import org.library.domain.model.Book;
import org.library.domain.model.BookCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    
    @Query("SELECT b FROM Book b WHERE b.catalog = :catalog AND b.available = true")
    List<Book> findAvailableBooksByCatalog(@Param("catalog") BookCatalog catalog);
    
    @Query("SELECT b FROM Book b JOIN b.catalog c WHERE c.isbn = :isbn AND b.available = true")
    Optional<Book> findFirstAvailableBookByIsbn(@Param("isbn") String isbn);
    
    List<Book> findByCatalog(BookCatalog catalog);
}
