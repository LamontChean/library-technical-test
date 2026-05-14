package com.library.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class BorrowingResponse {
    private UUID id;
    private UUID bookId;
    private String bookTitle;
    private UUID borrowerId;
    private String borrowerName;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    public BorrowingResponse() {}

    public BorrowingResponse(UUID id, UUID bookId, String bookTitle, UUID borrowerId, String borrowerName, LocalDateTime borrowedAt, LocalDateTime returnedAt) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowerId = borrowerId;
        this.borrowerName = borrowerName;
        this.borrowedAt = borrowedAt;
        this.returnedAt = returnedAt;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public UUID getBorrowerId() { return borrowerId; }
    public void setBorrowerId(UUID borrowerId) { this.borrowerId = borrowerId; }
    
    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }
    
    public LocalDateTime getBorrowedAt() { return borrowedAt; }
    public void setBorrowedAt(LocalDateTime borrowedAt) { this.borrowedAt = borrowedAt; }
    
    public LocalDateTime getReturnedAt() { return returnedAt; }
    public void setReturnedAt(LocalDateTime returnedAt) { this.returnedAt = returnedAt; }
}
