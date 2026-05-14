package org.library.web.controller;

import jakarta.validation.Valid;
import org.library.application.dto.request.BorrowBookRequest;
import org.library.application.dto.request.ReturnBookRequest;
import org.library.application.service.BorrowingService;
import org.library.domain.model.BorrowingRecord;
import org.library.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<ApiResponse<BorrowingRecord>> borrowBook(@Valid @RequestBody BorrowBookRequest request) {
        BorrowingRecord record = borrowingService.borrowBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(record, "Book borrowed successfully"));
    }

    @PostMapping("/return")
    public ResponseEntity<ApiResponse<BorrowingRecord>> returnBook(@Valid @RequestBody ReturnBookRequest request) {
        BorrowingRecord record = borrowingService.returnBook(request);
        return ResponseEntity.ok(ApiResponse.success(record, "Book returned successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<BorrowingRecord>>> getAllBorrowingRecords() {
        return ResponseEntity.ok(ApiResponse.success(borrowingService.getAllBorrowingRecords()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BorrowingRecord>> getBorrowingRecord(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(borrowingService.getBorrowingRecordById(id)));
    }
}
