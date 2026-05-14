package org.library.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.library.application.dto.request.BorrowerRequest;
import org.library.application.service.BorrowerService;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.Borrower;
import org.library.infrastructure.repository.BorrowerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for borrower operations.
 */
@Service
@RequiredArgsConstructor
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository;

    @Override
    @Transactional(readOnly = true)
    public Borrower getBorrowerById(String id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BORROWER_NOT_FOUND, id));
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    @Override
    @Transactional
    public Borrower registerBorrower(BorrowerRequest request) {
        validateEmailUnique(request.getEmail());
        return createAndSaveBorrower(request);
    }

    private void validateEmailUnique(String email) {
        if (borrowerRepository.existsByEmail(email)) {
            throw new LibraryServiceException(ErrorCode.EMAIL_ALREADY_REGISTERED, email);
        }
    }

    private Borrower createAndSaveBorrower(BorrowerRequest request) {
        Borrower borrower = new Borrower(request.getName(), request.getEmail());
        return borrowerRepository.save(borrower);
    }
}
