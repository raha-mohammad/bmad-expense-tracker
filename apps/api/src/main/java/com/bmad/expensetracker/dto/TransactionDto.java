package com.bmad.expensetracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDto(
        Long id,
        Long categoryId,
        // Serialized as a JSON string (e.g. "150.00"), not a bare number - established project
        // convention for money on the wire (code review decision, Story 1.2) so a JS `number`'s
        // float representation never has a chance to introduce precision drift end to end.
        @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount,
        LocalDate transactionDate,
        String description) {
}
