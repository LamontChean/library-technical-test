package com.library.controller;

import com.library.dto.ApiResponse;
import com.library.dto.BorrowerRequest;
import com.library.dto.BorrowerResponse;
import com.library.service.BorrowerService;
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
@RequestMapping("/api/borrowers")
@Tag(name = "Borrower Management", description = "APIs for managing library borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new borrower", description = "Create a new borrower account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Borrower registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or email already registered")
    })
    public ResponseEntity<ApiResponse<BorrowerResponse>> createBorrower(@Valid @RequestBody BorrowerRequest request) {
        BorrowerResponse response = borrowerService.createBorrower(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Borrower registered successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all borrowers", description = "Retrieve a list of all borrowers")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved borrower list")
    public ResponseEntity<ApiResponse<List<BorrowerResponse>>> getAllBorrowers() {
        List<BorrowerResponse> borrowers = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(ApiResponse.success(borrowers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get borrower by ID", description = "Retrieve a specific borrower by their ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Borrower found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Borrower not found")
    })
    public ResponseEntity<ApiResponse<BorrowerResponse>> getBorrowerById(@PathVariable UUID id) {
        BorrowerResponse borrower = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(ApiResponse.success(borrower));
    }
}
