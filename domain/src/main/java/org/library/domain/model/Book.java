package org.library.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book represents a physical copy of a book in the library.
 * Multiple books can share the same catalog (ISBN, title, author).
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id", nullable = false)
    private BookCatalog catalog;

    @Column(nullable = false)
    private boolean available = true;

    // Convenience methods to access catalog data
    public String getIsbn() {
        return catalog != null ? catalog.getIsbn() : null;
    }

    public String getTitle() {
        return catalog != null ? catalog.getTitle() : null;
    }

    public String getAuthor() {
        return catalog != null ? catalog.getAuthor() : null;
    }

    public void setIsbn(String isbn) {
        if (catalog != null) {
            catalog.setIsbn(isbn);
        }
    }

    public void setTitle(String title) {
        if (catalog != null) {
            catalog.setTitle(title);
        }
    }

    public void setAuthor(String author) {
        if (catalog != null) {
            catalog.setAuthor(author);
        }
    }

    public Book(BookCatalog catalog) {
        this.catalog = catalog;
        this.available = true;
    }
}
