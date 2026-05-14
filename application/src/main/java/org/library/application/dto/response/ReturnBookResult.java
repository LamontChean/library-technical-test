package org.library.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.library.domain.model.Book;
import org.library.domain.model.BorrowingRecord;

/**
 * Result DTO for return book operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBookResult {
    private BorrowingRecord borrowingRecord;
    private Book book;
    private Long borrowingDays;
}
