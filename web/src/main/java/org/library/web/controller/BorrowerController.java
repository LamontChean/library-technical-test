package org.library.web.controller;

import jakarta.validation.Valid;
import org.library.application.dto.request.BorrowerRequest;
import org.library.application.service.BorrowerService;
import org.library.domain.model.Borrower;
import org.library.web.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Borrower>> registerBorrower(@Valid @RequestBody BorrowerRequest request) {
        Borrower borrower = borrowerService.registerBorrower(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(borrower, "Borrower registered successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Borrower>> getBorrower(@PathVariable String id) {
        Borrower borrower = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(ApiResponse.success(borrower));
    }
}
