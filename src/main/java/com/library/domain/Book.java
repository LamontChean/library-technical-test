package com.library.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a physical copy of a book.
 * Multiple copies can reference the same BookCatalog entry.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalog_id", nullable = false)
    private BookCatalog catalog;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public BookCatalog getCatalog() { return catalog; }
    public void setCatalog(BookCatalog catalog) { this.catalog = catalog; }
    
    public String getIsbn() { return catalog != null ? catalog.getIsbn() : null; }
    public String getTitle() { return catalog != null ? catalog.getTitle() : null; }
    public String getAuthor() { return catalog != null ? catalog.getAuthor() : null; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
