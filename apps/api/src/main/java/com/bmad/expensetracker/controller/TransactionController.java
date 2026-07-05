package com.bmad.expensetracker.controller;

import com.bmad.expensetracker.dto.CreateTransactionRequest;
import com.bmad.expensetracker.dto.LastUsedCategoryDto;
import com.bmad.expensetracker.dto.TransactionDto;
import com.bmad.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Only POST (create) and reads exist here - no update/delete endpoint for transactions in MVP
// scope (AD-10). GET /api/transactions (filter/list) is FR-10/Epic 3's job, not built here.
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionDto created = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Server-derived last-used-category default for Quick Add's category pre-selection - a
    // deliberate scope addition beyond epics.md, per ARCHITECTURE-SPINE.md's Capability Map.
    @GetMapping("/last-category")
    public LastUsedCategoryDto getLastUsedCategory() {
        return new LastUsedCategoryDto(transactionService.getLastUsedCategoryId());
    }
}
