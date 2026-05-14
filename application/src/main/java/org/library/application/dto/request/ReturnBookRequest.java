package org.library.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for return book operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBookRequest {
    
    @NotBlank(message = "Book ID is required")
    private String bookId;
}
