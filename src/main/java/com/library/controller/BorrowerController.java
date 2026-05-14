package com.library.controller;

import com.library.dto.BorrowerRequest;
import com.library.dto.BorrowerResponse;
import com.library.service.BorrowerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {

    private final BorrowerService borrowerService;

    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BorrowerResponse> createBorrower(@Valid @RequestBody BorrowerRequest request) {
        BorrowerResponse response = borrowerService.createBorrower(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BorrowerResponse>> getAllBorrowers() {
        List<BorrowerResponse> borrowers = borrowerService.getAllBorrowers();
        return ResponseEntity.ok(borrowers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowerResponse> getBorrowerById(@PathVariable UUID id) {
        BorrowerResponse borrower = borrowerService.getBorrowerById(id);
        return ResponseEntity.ok(borrower);
    }
}
