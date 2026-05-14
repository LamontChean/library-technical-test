package com.library.dto;

import jakarta.validation.constraints.NotBlank;

public class ReturnRequest {
    
    @NotBlank(message = "Borrowing ID is required")
    private String borrowingId;

    public ReturnRequest() {}

    public ReturnRequest(String borrowingId) {
        this.borrowingId = borrowingId;
    }

    public String getBorrowingId() {
        return borrowingId;
    }

    public void setBorrowingId(String borrowingId) {
        this.borrowingId = borrowingId;
    }
}
