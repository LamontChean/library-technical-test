package org.library.application.process.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic process context object that carries data through the process lifecycle.
 * Suitable for fintech/backend workflow systems.
 *
 * @param <T> the type of request data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessContext<T> {

    /**
     * Unique process identifier for tracking and logging
     */
    private String processId;

    /**
     * The request/input data for this process
     */
    private T requestData;

    /**
     * The result/output data after process execution
     */
    private Object resultData;

    /**
     * Process status
     */
    private ProcessStatus status;

    /**
     * Error message if process failed
     */
    private String errorMessage;

    /**
     * Additional metadata for extensibility
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * Timestamp when process started
     */
    private LocalDateTime startedAt;

    /**
     * Timestamp when process completed
     */
    private LocalDateTime completedAt;

    /**
     * Helper method to add metadata
     */
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }

    /**
     * Helper method to get metadata
     */
    @SuppressWarnings("unchecked")
    public <V> V getMetadata(String key) {
        return this.metadata != null ? (V) this.metadata.get(key) : null;
    }

    /**
     * Mark process as started
     */
    public void start() {
        this.status = ProcessStatus.STARTED;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * Mark process as completed successfully
     */
    public void complete(Object resultData) {
        this.status = ProcessStatus.COMPLETED;
        this.resultData = resultData;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Mark process as failed
     */
    public void fail(String errorMessage) {
        this.status = ProcessStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Process status enum
     */
    public enum ProcessStatus {
        PENDING,
        STARTED,
        COMPLETED,
        FAILED,
        ROLLED_BACK
    }
}
