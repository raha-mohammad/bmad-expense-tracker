package com.bmad.expensetracker.repository;

import com.bmad.expensetracker.entity.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findTopByOrderByTransactionDateDescIdDesc();

    // FR-1: ranks (category, amount, normalized description) combinations Sam has logged at
    // least twice - "habitual" is defined here as having repeated at least once, per this
    // story's own decision (PRD/architecture deliberately left the ranking algorithm open).
    // Description is normalized (case-folded, trimmed) so "Coffee" and "coffee " count as the
    // same repeat purchase (code review decision) - the actual literal description to display
    // is resolved separately via findMostRecentByNormalizedDescription(). Ties broken by
    // most-recent use, then by MAX(t.id) as a final deterministic tiebreaker (unique per group,
    // since a transaction belongs to exactly one group) - without it, two groups tied on both
    // count and date would have no guaranteed stable order, letting the shelf flicker between
    // calls with no underlying data change (code review finding). Computed live on every call
    // (AD-1) - no schema change, no stored frequency column.
    @Query("SELECT new com.bmad.expensetracker.repository.FrequentExpenseGroup(t.category.id, t.amount, LOWER(TRIM(t.description))) "
            + "FROM Transaction t "
            + "GROUP BY t.category.id, t.amount, LOWER(TRIM(t.description)) "
            + "HAVING COUNT(t) >= 2 "
            + "ORDER BY COUNT(t) DESC, MAX(t.transactionDate) DESC, MAX(t.id) DESC")
    List<FrequentExpenseGroup> findFrequentExpenseGroups(Pageable pageable);

    // Resolves a normalized group key back to its single most-recent transaction, whose exact
    // literal description (original casing/whitespace) is what the chip displays and replays -
    // consistent with findTopByOrderByTransactionDateDescIdDesc()'s "most recent wins"
    // convention. Called with a Pageable capped to 1 row; a match is always structurally
    // guaranteed since the caller only ever passes a group key derived from existing rows.
    @Query("SELECT t FROM Transaction t "
            + "WHERE t.category.id = :categoryId AND t.amount = :amount AND LOWER(TRIM(t.description)) = :normalizedDescription "
            + "ORDER BY t.transactionDate DESC, t.id DESC")
    List<Transaction> findMostRecentByNormalizedDescription(
            @Param("categoryId") Long categoryId,
            @Param("amount") BigDecimal amount,
            @Param("normalizedDescription") String normalizedDescription,
            Pageable pageable);
}
