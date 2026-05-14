package org.library.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for borrow book operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookRequest {
    
    @NotBlank(message = "Book ID is required")
    private String bookId;
    
    @NotBlank(message = "Borrower ID is required")
    private String borrowerId;
}
