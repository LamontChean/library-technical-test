package org.library.application.process.common;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Enterprise-style process template using the Template Method pattern.
 * Suitable for fintech/backend workflow systems.
 *
 * <p>Lifecycle:
 * <ol>
 *   <li>preCheck() - Validation and prerequisites (optional override)</li>
 *   <li>doExecute() - Core business logic (must implement)</li>
 *   <li>postProcess() - Cleanup and notifications (optional override)</li>
 * </ol>
 *
 * <p>Key Features:
 * <ul>
 *   <li>Final execute() method prevents template override</li>
 *   <li>Strongly-typed context object for compile-time safety</li>
 *   <li>Built-in logging and metrics</li>
 *   <li>Standardized error handling</li>
 *   <li>Execution time tracking</li>
 * </ul>
 *
 * @param <T> the type of request data in the process context
 * @param <R> the type of result data returned by the process
 * @param <C> the type of process context (must extend ProcessContext<T>)
 */
@Slf4j
public abstract class AbstractProcessTemplate<T, R, C extends ProcessContext<T>> {

    /**
     * Main entry point - FINAL to prevent override.
     * Executes the complete process lifecycle.
     *
     * @param context the process context containing request data
     * @return standardized process result
     */
    public final ProcessResult<R> execute(C context) {
        long startTime = System.currentTimeMillis();
        
        // Initialize context
        initializeContext(context);
        
        log.info("Starting process: {} | ProcessId: {}", 
                getProcessName(), context.getProcessId());

        try {
            // Step 1: Pre-check (validation, authorization, prerequisites)
            preCheck(context);

            // Step 2: Execute core business logic
            R result = doExecute(context);

            // Step 3: Post-process (cleanup, notifications, audit)
            postProcess(context, result);

            // Mark as completed
            context.complete(result);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Process completed successfully: {} | ProcessId: {} | ExecutionTime: {}ms",
                    getProcessName(), context.getProcessId(), executionTime);

            return ProcessResult.success(result, executionTime);

        } catch (Exception e) {
            // Handle any exceptions
            context.fail(e.getMessage());
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.error("Process failed: {} | ProcessId: {} | Error: {} | ExecutionTime: {}ms",
                    getProcessName(), context.getProcessId(), e.getMessage(), executionTime, e);

            handleError(context, e);
            
            return ProcessResult.failure(getErrorCode(e), e.getMessage(), executionTime);
        }
    }

    /**
     * Initialize the process context with default values.
     * Can be overridden if custom initialization is needed.
     *
     * @param context the process context to initialize
     */
    protected void initializeContext(C context) {
        if (context.getProcessId() == null) {
            context.setProcessId(UUID.randomUUID().toString());
        }
        if (context.getStatus() == null) {
            context.setStatus(ProcessContext.ProcessStatus.PENDING);
        }
        context.start();
    }

    /**
     * Pre-check phase: validation, authorization, prerequisites.
     * Default implementation is empty - override if needed.
     *
     * <p>Common use cases:
     * <ul>
     *   <li>Validate request data</li>
     *   <li>Check user permissions</li>
     *   <li>Verify preconditions</li>
     *   <li>Check for duplicate requests</li>
     * </ul>
     *
     * @param context the process context
     * @throws RuntimeException if pre-check fails
     */
    protected void preCheck(C context) {
        // Default: no-op
        // Override in subclass to add validation logic
    }

    /**
     * Execute phase: core business logic.
     * MUST be implemented by subclasses.
     *
     * <p>This is where the main business operation happens.
     *
     * @param context the process context with validated request data
     * @return the result of the business operation
     * @throws Exception if business logic fails
     */
    protected abstract R doExecute(C context) throws Exception;

    /**
     * Post-process phase: cleanup, notifications, audit logging.
     * Default implementation is empty - override if needed.
     *
     * <p>Common use cases:
     * <ul>
     *   <li>Send notifications (email, SMS, webhook)</li>
     *   <li>Update audit logs</li>
     *   <li>Trigger downstream processes</li>
     *   <li>Clear caches</li>
     *   <li>Update metrics</li>
     * </ul>
     *
     * @param context the process context
     * @param result  the result from doExecute
     */
    protected void postProcess(C context, R result) {
        // Default: no-op
        // Override in subclass to add post-processing logic
    }

    /**
     * Error handling: custom error processing.
     * Default implementation is empty - override if needed.
     *
     * <p>Common use cases:
     * <ul>
     *   <li>Compensating transactions (rollback)</li>
     *   <li>Send error notifications</li>
     *   <li>Update error metrics</li>
     *   <li>Cleanup partial state</li>
     * </ul>
     *
     * @param context   the process context
     * @param exception the exception that occurred
     */
    protected void handleError(C context, Exception exception) {
        // Default: no-op
        // Override in subclass to add custom error handling
    }

    /**
     * Extract error code from exception.
     * Default implementation returns generic error code.
     *
     * @param exception the exception
     * @return error code string
     */
    protected String getErrorCode(Exception exception) {
        return "PROCESS_ERROR";
    }

    /**
     * Get the process name for logging and tracking.
     * MUST be implemented by subclasses.
     *
     * @return the process name
     */
    protected abstract String getProcessName();
}
