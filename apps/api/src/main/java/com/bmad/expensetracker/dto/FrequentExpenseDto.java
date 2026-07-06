package com.bmad.expensetracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

// A ranked (category, amount, description) combination Sam has logged repeatedly - not a
// persisted Transaction, so there's no id. Tapping the resulting chip creates a brand new
// Transaction via the existing POST /api/transactions with these exact preset values.
public record FrequentExpenseDto(
        Long categoryId,
        // Serialized as a JSON string (e.g. "150.00"), matching TransactionDto.amount's
        // established string-typed-money convention (code review decision, Story 1.2).
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount,
        String description) {
}
