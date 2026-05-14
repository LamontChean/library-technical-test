package org.library.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standardized error codes for the library system.
 * Format: LIB-{MODULE}-{SEQUENCE}
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // ==================== Common Errors (LIB-COMMON-XXX) ====================
    UNKNOWN_ERROR("LIB-COMMON-001", "An unexpected error occurred"),
    INVALID_REQUEST("LIB-COMMON-002", "Invalid request parameters"),
    VALIDATION_FAILED("LIB-COMMON-003", "Validation failed"),
    BUSINESS_RULE_VIOLATION("LIB-COMMON-004", "Business rule violation: %s"),
    
    // ==================== Book Errors (LIB-BOOK-XXX) ====================
    BOOK_NOT_FOUND("LIB-BOOK-001", "Book not found"),
    BOOK_ALREADY_EXISTS("LIB-BOOK-002", "Book already exists"),
    INVALID_ISBN("LIB-BOOK-003", "Invalid ISBN format"),
    BOOK_NOT_AVAILABLE("LIB-BOOK-004", "Book is not available for borrowing"),
    
    // ==================== Borrower Errors (LIB-BORROWER-XXX) ====================
    BORROWER_NOT_FOUND("LIB-BORROWER-001", "Borrower not found"),
    BORROWER_ALREADY_EXISTS("LIB-BORROWER-002", "Borrower already exists"),
    INVALID_EMAIL("LIB-BORROWER-003", "Invalid email format"),
    EMAIL_ALREADY_REGISTERED("LIB-BORROWER-004", "Email is already registered"),
    
    // ==================== Borrowing Errors (LIB-BORROWING-XXX) ====================
    BORROWING_RECORD_NOT_FOUND("LIB-BORROWING-001", "Borrowing record not found"),
    BOOK_ALREADY_BORROWED("LIB-BORROWING-002", "Book is already borrowed by another borrower"),
    NO_ACTIVE_BORROWING("LIB-BORROWING-003", "No active borrowing record found for this book"),
    BOOK_ALREADY_RETURNED("LIB-BORROWING-004", "Book has already been returned"),
    BORROWING_LIMIT_EXCEEDED("LIB-BORROWING-005", "Borrower has reached maximum borrowing limit"),
    OVERDUE_BOOK("LIB-BORROWING-006", "Book is overdue"),
    
    // ==================== System Errors (LIB-SYS-XXX) ====================
    DATABASE_ERROR("LIB-SYS-001", "Database error occurred"),
    EXTERNAL_SERVICE_ERROR("LIB-SYS-002", "External service error");
    
    /**
     * Error code (e.g., "LIB-BOOK-001")
     */
    private final String code;
    
    /**
     * Default error message
     */
    private final String defaultMessage;
    
    /**
     * Get error message with dynamic parameters
     */
    public String formatMessage(Object... args) {
        return String.format(defaultMessage, args);
    }
}
