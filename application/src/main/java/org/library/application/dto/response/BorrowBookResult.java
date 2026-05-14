package org.library.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.library.domain.model.Book;
import org.library.domain.model.Borrower;
import org.library.domain.model.BorrowingRecord;

/**
 * Result DTO for borrow book operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookResult {
    private BorrowingRecord borrowingRecord;
    private Book book;
    private Borrower borrower;
}
