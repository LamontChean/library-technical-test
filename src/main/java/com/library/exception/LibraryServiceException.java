package com.library.exception;

/**
 * Unified exception for all library service errors.
 * Contains error code and message for standardized error handling.
 */
public class LibraryServiceException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public LibraryServiceException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }
    
    public LibraryServiceException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
    
    public LibraryServiceException(ErrorCode errorCode, Object... args) {
        super(errorCode.formatMessage(args));
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
