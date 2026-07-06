package com.bmad.expensetracker.repository;

import java.math.BigDecimal;

// Internal aggregation-only projection, never exposed via the API (see FrequentExpenseDto for
// that). Groups habitual purchases by a normalized (case/whitespace-insensitive) description key
// so "Coffee" and "coffee " count as the same repeat purchase - TransactionService resolves the
// actual most-recently-used literal description for display separately, via
// findMostRecentByNormalizedDescription().
public record FrequentExpenseGroup(Long categoryId, BigDecimal amount, String normalizedDescription) {
}
