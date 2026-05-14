package com.library.controller;

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
    public ResponseEntity<BorrowingResponse> borrowBook(@Valid @RequestBody BorrowRequest request) {
        BorrowingResponse response = borrowingService.borrowBook(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/return/{borrowingId}")
    public ResponseEntity<BorrowingResponse> returnBook(@PathVariable UUID borrowingId) {
        BorrowingResponse response = borrowingService.returnBook(borrowingId);
        return ResponseEntity.ok(response);
    }
}
