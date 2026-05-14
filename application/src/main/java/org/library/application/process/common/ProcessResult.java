package org.library.application.process.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized process result wrapper.
 * Provides consistent response structure for all processes.
 *
 * @param <T> the type of result data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult<T> {

    /**
     * Whether the process was successful
     */
    private boolean success;

    /**
     * The result data if successful
     */
    private T data;

    /**
     * Error code if failed
     */
    private String errorCode;

    /**
     * Error message if failed
     */
    private String errorMessage;

    /**
     * Process execution time in milliseconds
     */
    private Long executionTimeMs;

    /**
     * Timestamp when result was created
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Create a successful result
     */
    public static <T> ProcessResult<T> success(T data) {
        return ProcessResult.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Create a successful result with execution time
     */
    public static <T> ProcessResult<T> success(T data, Long executionTimeMs) {
        return ProcessResult.<T>builder()
                .success(true)
                .data(data)
                .executionTimeMs(executionTimeMs)
                .build();
    }

    /**
     * Create a failure result
     */
    public static <T> ProcessResult<T> failure(String errorCode, String errorMessage) {
        return ProcessResult.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * Create a failure result with execution time
     */
    public static <T> ProcessResult<T> failure(String errorCode, String errorMessage, Long executionTimeMs) {
        return ProcessResult.<T>builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .executionTimeMs(executionTimeMs)
                .build();
    }
}
