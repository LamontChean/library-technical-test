package org.library.facade.impl;

import lombok.RequiredArgsConstructor;
import org.library.application.dto.request.BorrowerRequest;
import org.library.application.service.BorrowerService;
import org.library.domain.model.Borrower;
import org.library.facade.BorrowerFacade;
import org.springframework.stereotype.Component;

/**
 * Facade implementation for borrower operations.
 * Delegates to business service for actual processing.
 */
@Component
@RequiredArgsConstructor
public class BorrowerFacadeImpl implements BorrowerFacade {

    private final BorrowerService borrowerService;

    @Override
    public Borrower registerBorrower(BorrowerRequest request) {
        return borrowerService.registerBorrower(request);
    }
}
