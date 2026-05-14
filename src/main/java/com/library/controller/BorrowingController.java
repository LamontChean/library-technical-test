package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.BorrowRequest;
import com.library.dto.BorrowingResponse;
import com.library.service.BorrowingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<BorrowingResponse>> borrowBook(@Valid @RequestBody BorrowRequest request) {
        BorrowingResponse response = borrowingService.borrowBook(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Book borrowed successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/return/{borrowingId}")
    public ResponseEntity<ApiResponse<BorrowingResponse>> returnBook(@PathVariable UUID borrowingId) {
        BorrowingResponse response = borrowingService.returnBook(borrowingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Book returned successfully"));
    }
}
