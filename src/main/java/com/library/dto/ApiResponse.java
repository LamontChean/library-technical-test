package com.library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standardized API response wrapper.
 * All API endpoints return this structure.
 *
 * @param <T> the type of response data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * Whether the request was successful
     */
    private boolean success;
    
    /**
     * Error code (only present when success = false)
     */
    private String errorCode;
    
    /**
     * Human-readable message
     */
    private String message;
    
    /**
     * Response data (only present when success = true)
     */
    private T data;
    
    /**
     * Request timestamp
     */
    private Long timestamp;
    
    public ApiResponse() {}
    
    public ApiResponse(boolean success, String errorCode, String message, T data, Long timestamp) {
        this.success = success;
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }
    
    /**
     * Create success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, "Operation successful", data, System.currentTimeMillis());
    }
    
    /**
     * Create success response with custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, null, message, data, System.currentTimeMillis());
    }
    
    /**
     * Create error response
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(false, errorCode, message, null, System.currentTimeMillis());
    }
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
