package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.BorrowRequest;
import com.library.dto.BorrowingResponse;
import com.library.service.BorrowingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowings")
@Tag(name = "Borrowing Management", description = "APIs for borrowing and returning books")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Borrow a book", description = "Create a new borrowing record for a book")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Book borrowed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Book not available or invalid request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Book or borrower not found")
    })
    public ResponseEntity<ApiResponse<BorrowingResponse>> borrowBook(@Valid @RequestBody BorrowRequest request) {
        BorrowingResponse response = borrowingService.borrowBook(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Book borrowed successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/return/{borrowingId}")
    @Operation(summary = "Return a book", description = "Mark a borrowing record as returned")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Book already returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Borrowing record not found")
    })
    public ResponseEntity<ApiResponse<BorrowingResponse>> returnBook(@PathVariable UUID borrowingId) {
        BorrowingResponse response = borrowingService.returnBook(borrowingId);
        return ResponseEntity.ok(ApiResponse.success(response, "Book returned successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all borrowings", description = "Retrieve a list of all borrowing records")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved borrowing list")
    public ResponseEntity<ApiResponse<List<BorrowingResponse>>> getAllBorrowings() {
        List<BorrowingResponse> borrowings = borrowingService.getAllBorrowings();
        return ResponseEntity.ok(ApiResponse.success(borrowings));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get borrowing by ID", description = "Retrieve a specific borrowing record by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Borrowing record found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Borrowing record not found")
    })
    public ResponseEntity<ApiResponse<BorrowingResponse>> getBorrowingById(@PathVariable UUID id) {
        BorrowingResponse borrowing = borrowingService.getBorrowingById(id);
        return ResponseEntity.ok(ApiResponse.success(borrowing));
    }
}
