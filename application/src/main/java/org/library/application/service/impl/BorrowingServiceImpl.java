package org.library.application.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.application.dto.response.BorrowBookResult;
import org.library.application.process.BorrowBookProcess;
import org.library.application.process.ReturnBookProcess;
import org.library.application.process.common.ProcessResult;
import org.library.application.process.context.BorrowBookContext;
import org.library.application.process.context.ReturnBookContext;
import org.library.application.service.BorrowingService;
import org.library.domain.exception.ErrorCode;
import org.library.domain.exception.LibraryServiceException;
import org.library.domain.model.BorrowingRecord;
import org.library.infrastructure.repository.BookRepository;
import org.library.infrastructure.repository.BorrowerRepository;
import org.library.infrastructure.repository.BorrowingRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for borrowing operations.
 * Uses process template for complex workflows.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowBookProcess borrowBookProcess;
    private final ReturnBookProcess returnBookProcess;
    private final BorrowingRecordRepository borrowingRecordRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BorrowingRecord> getAllBorrowingRecords() {
        return borrowingRecordRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowingRecord getBorrowingRecordById(String id) {
        return borrowingRecordRepository.findById(id)
                .orElseThrow(() -> new LibraryServiceException(ErrorCode.BORROWING_RECORD_NOT_FOUND, id));
    }

    @Override
    @Transactional
    public BorrowingRecord borrowBook(BorrowBookRequest request) {
        // Create typed process context
        BorrowBookContext context = new BorrowBookContext();
        context.setRequestData(request);

        // Execute process
        ProcessResult<BorrowBookResult> result =
                borrowBookProcess.execute(context);

        if (!result.isSuccess()) {
            throw new LibraryServiceException(ErrorCode.BUSINESS_RULE_VIOLATION, result.getErrorMessage());
        }

        return result.getData().getBorrowingRecord();
    }

    @Override
    @Transactional
    public BorrowingRecord returnBook(ReturnBookRequest request) {
        // Create typed process context
        ReturnBookContext context = new ReturnBookContext();
        context.setRequestData(request);

        // Execute process
        ProcessResult<org.library.application.dto.response.ReturnBookResult> result = 
                returnBookProcess.execute(context);

        if (!result.isSuccess()) {
            throw new LibraryServiceException(ErrorCode.BUSINESS_RULE_VIOLATION, result.getErrorMessage());
        }

        return result.getData().getBorrowingRecord();
    }
}
