package com.bmad.expensetracker.service;

import com.bmad.expensetracker.dto.CreateTransactionRequest;
import com.bmad.expensetracker.dto.FrequentExpenseDto;
import com.bmad.expensetracker.dto.TransactionDto;
import com.bmad.expensetracker.entity.Category;
import com.bmad.expensetracker.entity.Transaction;
import com.bmad.expensetracker.repository.CategoryRepository;
import com.bmad.expensetracker.repository.FrequentExpenseGroup;
import com.bmad.expensetracker.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final ZoneId CLOCK_ZONE = ZoneId.of("Asia/Kolkata");
    private static final int FREQUENT_EXPENSE_LIMIT = 5;

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    // AD-9: when the request omits transactionDate, "today" is stamped here, server-side, in
    // Asia/Kolkata - the client never computes this itself. A supplied (backdated) date is used
    // exactly as given, with no future-date restriction (none is specified anywhere in the spec).
    public TransactionDto createTransaction(CreateTransactionRequest request) {
        Category category = categoryRepository
                .findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        LocalDate transactionDate =
                request.transactionDate() != null ? request.transactionDate() : LocalDate.now(CLOCK_ZONE);

        Transaction transaction =
                new Transaction(category, request.amount(), transactionDate, request.description());
        Transaction saved = transactionRepository.save(transaction);
        return toDto(saved);
    }

    // Server-derived (never client-cached) per ARCHITECTURE-SPINE.md's Capability Map. Returns
    // null when no transaction has ever been logged yet - a real first-use state, not an error.
    public Long getLastUsedCategoryId() {
        Optional<Transaction> lastTransaction = transactionRepository.findTopByOrderByTransactionDateDescIdDesc();
        return lastTransaction.map(t -> t.getCategory().getId()).orElse(null);
    }

    // FR-1: server-derived (never client-cached), never a stored/cached result (AD-1) - computed
    // live on every call. Returns an empty list, not null or an error, when no combination has
    // repeated yet (first-ever use, or every purchase so far is a one-off) - this empty list is
    // what lets Quick Add hide the Frequent-Expenses Shelf entirely rather than show a broken or
    // placeholder state.
    public List<FrequentExpenseDto> getFrequentExpenses() {
        return transactionRepository.findFrequentExpenseGroups(PageRequest.of(0, FREQUENT_EXPENSE_LIMIT)).stream()
                .map(this::toFrequentExpenseDto)
                .toList();
    }

    // Resolves a normalized group's displayed description to the exact literal string of its
    // single most-recently-dated transaction (code review decision) - so a chip replays the
    // casing/whitespace Sam actually typed most recently, not an arbitrary or normalized form.
    private FrequentExpenseDto toFrequentExpenseDto(FrequentExpenseGroup group) {
        List<Transaction> mostRecent = transactionRepository.findMostRecentByNormalizedDescription(
                group.categoryId(), group.amount(), group.normalizedDescription(), PageRequest.of(0, 1));
        return new FrequentExpenseDto(group.categoryId(), group.amount(), mostRecent.get(0).getDescription());
    }

    private TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getCategory().getId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription());
    }
}
