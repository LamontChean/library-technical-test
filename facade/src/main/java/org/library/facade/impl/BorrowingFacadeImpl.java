package org.library.facade.impl;

import lombok.RequiredArgsConstructor;
import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.application.service.BorrowingService;
import org.library.domain.model.BorrowingRecord;
import org.library.facade.BorrowingFacade;
import org.springframework.stereotype.Component;

/**
 * Facade implementation for borrowing operations.
 * Delegates to business service for actual processing.
 */
@Component
@RequiredArgsConstructor
public class BorrowingFacadeImpl implements BorrowingFacade {

    private final BorrowingService borrowingService;

    @Override
    public BorrowingRecord borrowBook(BorrowBookRequest request) {
        return borrowingService.borrowBook(request);
    }

    @Override
    public BorrowingRecord returnBook(ReturnBookRequest request) {
        return borrowingService.returnBook(request);
    }
}
