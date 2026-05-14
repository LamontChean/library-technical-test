package com.library.service;

import com.library.domain.Borrower;
import com.library.dto.BorrowerRequest;
import com.library.dto.BorrowerResponse;
import com.library.exception.ErrorCode;
import com.library.exception.LibraryServiceException;
import com.library.repository.BorrowerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowerResponse createBorrower(BorrowerRequest request) {
        if (borrowerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new LibraryServiceException(ErrorCode.EMAIL_ALREADY_REGISTERED, request.getEmail());
        }

        Borrower borrower = new Borrower();
        borrower.setName(request.getName());
        borrower.setEmail(request.getEmail());
        
        Borrower savedBorrower = borrowerRepository.save(borrower);
        return toResponse(savedBorrower);
    }

    @Transactional(readOnly = true)
    public List<BorrowerResponse> getAllBorrowers() {
        return borrowerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BorrowerResponse getBorrowerById(UUID id) {
        return borrowerRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BORROWER_NOT_FOUND, id));
    }

    private BorrowerResponse toResponse(Borrower borrower) {
        return new BorrowerResponse(
                borrower.getId(),
                borrower.getName(),
                borrower.getEmail(),
                borrower.getCreatedAt()
        );
    }
}
