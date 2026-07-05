package com.bmad.expensetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bmad.expensetracker.dto.CreateTransactionRequest;
import com.bmad.expensetracker.dto.TransactionDto;
import com.bmad.expensetracker.entity.Category;
import com.bmad.expensetracker.repository.CategoryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

// @Transactional rolls back every test's writes at the end, so this suite never leaves rows
// behind in the shared local dev database (unlike CategoryBootstrapRunnerTest's fixed seed data,
// Transaction rows are created fresh by these tests and must not accumulate across runs).
@SpringBootTest
@Transactional
class TransactionServiceTest {

    private static final ZoneId KOLKATA = ZoneId.of("Asia/Kolkata");

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category foodCategory() {
        return categoryRepository.findByNameIgnoreCase("Food").orElseThrow();
    }

    private Category transportCategory() {
        return categoryRepository.findByNameIgnoreCase("Transport").orElseThrow();
    }

    // AD-9: omitting transactionDate defaults to "today" in Asia/Kolkata, computed server-side.
    @Test
    void omittedTransactionDateDefaultsToTodayInKolkata() {
        var request = new CreateTransactionRequest(new BigDecimal("150.00"), foodCategory().getId(), "Coffee", null);

        TransactionDto dto = transactionService.createTransaction(request);

        assertThat(dto.transactionDate()).isEqualTo(LocalDate.now(KOLKATA));
    }

    // FR-3/AD-9: a supplied (backdated) date is persisted exactly as given, no future-date
    // restriction is invented.
    @Test
    void suppliedTransactionDateIsPreservedExactlyAsGiven() {
        LocalDate backdate = LocalDate.now(KOLKATA).minusDays(3);
        var request = new CreateTransactionRequest(new BigDecimal("40.00"), transportCategory().getId(), "Bus", backdate);

        TransactionDto dto = transactionService.createTransaction(request);

        assertThat(dto.transactionDate()).isEqualTo(backdate);
    }

    // AD-7: amount round-trips as BigDecimal, never float/double; compareTo (not equals) avoids
    // false failures from scale differences (e.g. "40" vs "40.00").
    @Test
    void amountRoundTripsAsBigDecimal() {
        var request =
                new CreateTransactionRequest(new BigDecimal("1234.56"), foodCategory().getId(), "Groceries", null);

        TransactionDto dto = transactionService.createTransaction(request);

        assertThat(dto.amount()).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    // Edge case not covered by epics.md: a categoryId that doesn't reference a real category
    // must not 500 or silently corrupt data.
    @Test
    void unknownCategoryIdThrowsCategoryNotFoundException() {
        var request = new CreateTransactionRequest(new BigDecimal("10.00"), 999_999L, "Mystery", null);

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    // First-ever-use edge case: no transaction has ever been logged yet.
    @Test
    void getLastUsedCategoryIdReturnsNullWhenNoTransactionsExist() {
        assertThat(transactionService.getLastUsedCategoryId()).isNull();
    }

    // AC5 + Capability Map: last-used category is derived from the most recently dated
    // transaction, server-side.
    @Test
    void getLastUsedCategoryIdReturnsMostRecentTransactionsCategory() {
        LocalDate today = LocalDate.now(KOLKATA);
        transactionService.createTransaction(
                new CreateTransactionRequest(new BigDecimal("100.00"), foodCategory().getId(), "Lunch", today.minusDays(2)));
        transactionService.createTransaction(new CreateTransactionRequest(
                new BigDecimal("40.00"), transportCategory().getId(), "Bus", today));

        assertThat(transactionService.getLastUsedCategoryId()).isEqualTo(transportCategory().getId());
    }

    // Tie-break: when two transactions share the same transactionDate, the most recently
    // inserted (highest id) wins, per findTopByOrderByTransactionDateDescIdDesc().
    @Test
    void getLastUsedCategoryIdBreaksSameDateTiesByHighestId() {
        LocalDate today = LocalDate.now(KOLKATA);
        transactionService.createTransaction(
                new CreateTransactionRequest(new BigDecimal("100.00"), foodCategory().getId(), "Lunch", today));
        transactionService.createTransaction(
                new CreateTransactionRequest(new BigDecimal("40.00"), transportCategory().getId(), "Bus", today));

        assertThat(transactionService.getLastUsedCategoryId()).isEqualTo(transportCategory().getId());
    }
}
