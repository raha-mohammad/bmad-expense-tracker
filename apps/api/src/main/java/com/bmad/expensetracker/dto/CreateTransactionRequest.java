package com.bmad.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

// transactionDate is intentionally nullable: omitting it means "today," defaulted server-side
// in Asia/Kolkata by TransactionService (AD-9) - the client never computes this itself.
// amount is deliberately still BigDecimal here (not annotated with a JSON string shape): Jackson
// already deserializes a quoted JSON string into BigDecimal by default, so no read-side change
// is needed for the code-review decision to send money as a string - only TransactionDto's
// response side needs to force that shape on write.
public record CreateTransactionRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotNull Long categoryId,
        @NotBlank @Size(max = 255) String description,
        LocalDate transactionDate) {
}
