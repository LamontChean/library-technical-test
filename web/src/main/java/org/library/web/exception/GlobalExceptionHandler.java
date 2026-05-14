package org.library.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for standardized error responses.
 * All exceptions are caught here and converted to ApiResponse format.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle library service exceptions (400 or 404 based on error code)
     */
    @ExceptionHandler(LibraryServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleLibraryService(LibraryServiceException ex) {
        log.error("Library service error: {}", ex.getMessage(), ex);
        
        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        
        ApiResponse<Void> response = ApiResponse.error(
                ex.getErrorCode().getCode(),
                ex.getMessage()
        );
        
        return ResponseEntity.status(status).body(response);
    }
    
    /**
     * Determine HTTP status based on error code
     */
    private HttpStatus determineHttpStatus(ErrorCode errorCode) {
        String code = errorCode.getCode();
        if (code.endsWith("001")) {
            // NOT_FOUND errors
            return HttpStatus.NOT_FOUND;
        }
        // All other errors are BAD_REQUEST
        return HttpStatus.BAD_REQUEST;
    }
    
    /**
     * Handle validation errors (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        log.error("Validation failed: {}", fieldErrors);
        
        ApiResponse<Map<String, String>> response = ApiResponse.error(
                ErrorCode.VALIDATION_FAILED.getCode(),
                "Validation failed"
        );
        response.setData(fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle all other exceptions (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.UNKNOWN_ERROR.getCode(),
                "An unexpected error occurred. Please try again later."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
