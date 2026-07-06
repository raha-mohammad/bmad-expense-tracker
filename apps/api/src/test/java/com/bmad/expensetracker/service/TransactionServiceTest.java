package com.bmad.expensetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bmad.expensetracker.dto.CreateTransactionRequest;
import com.bmad.expensetracker.dto.FrequentExpenseDto;
import com.bmad.expensetracker.dto.TransactionDto;
import com.bmad.expensetracker.entity.Category;
import com.bmad.expensetracker.repository.CategoryRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
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

    private Category shoppingCategory() {
        return categoryRepository.findByNameIgnoreCase("Shopping").orElseThrow();
    }

    private Category billsCategory() {
        return categoryRepository.findByNameIgnoreCase("Bills").orElseThrow();
    }

    private Category entertainmentCategory() {
        return categoryRepository.findByNameIgnoreCase("Entertainment").orElseThrow();
    }

    private void logTransaction(Category category, String amount, String description) {
        transactionService.createTransaction(
                new CreateTransactionRequest(new BigDecimal(amount), category.getId(), description, null));
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

    // FR-1/AC6: an empty transactions table means no combination has ever repeated - returns an
    // empty list, not null and not an exception, so Quick Add can hide the shelf entirely.
    @Test
    void getFrequentExpensesReturnsEmptyListWhenNoTransactionsExist() {
        assertThat(transactionService.getFrequentExpenses()).isEmpty();
    }

    // FR-1/AC6: "habitual" is defined as having repeated at least once (this story's own ranking
    // decision) - a combo logged twice qualifies, a one-off combo does not.
    @Test
    void getFrequentExpensesOnlyIncludesCombinationsThatHaveRepeated() {
        logTransaction(foodCategory(), "150.00", "Coffee");
        logTransaction(foodCategory(), "150.00", "Coffee");
        logTransaction(transportCategory(), "40.00", "Bus");

        List<FrequentExpenseDto> result = transactionService.getFrequentExpenses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).categoryId()).isEqualTo(foodCategory().getId());
        assertThat(result.get(0).amount()).isEqualByComparingTo(new BigDecimal("150.00"));
        assertThat(result.get(0).description()).isEqualTo("Coffee");
    }

    // FR-1/AC1: capped at the top 5 by frequency when more than 5 distinct combinations qualify,
    // ordered by descending count - the lowest-count 6th combination is excluded.
    @Test
    void getFrequentExpensesCapsAtFiveOrderedByDescendingFrequency() {
        seedCombination(foodCategory(), "10.00", "Combo1", 7);
        seedCombination(transportCategory(), "20.00", "Combo2", 6);
        seedCombination(shoppingCategory(), "30.00", "Combo3", 5);
        seedCombination(billsCategory(), "40.00", "Combo4", 4);
        seedCombination(entertainmentCategory(), "50.00", "Combo5", 3);
        seedCombination(foodCategory(), "60.00", "Combo6", 2);

        List<FrequentExpenseDto> result = transactionService.getFrequentExpenses();

        assertThat(result).hasSize(5);
        assertThat(result.stream().map(FrequentExpenseDto::description).toList())
                .containsExactly("Combo1", "Combo2", "Combo3", "Combo4", "Combo5");
    }

    private void seedCombination(Category category, String amount, String description, int occurrences) {
        for (int i = 0; i < occurrences; i++) {
            logTransaction(category, amount, description);
        }
    }

    // Code review decision: description matching is normalized (case-folded, trimmed) so
    // "Coffee" and " coffee " count as the same repeat purchase, but the displayed description
    // is the exact literal string of the most-recently-logged transaction, not a normalized form.
    @Test
    void getFrequentExpensesNormalizesCaseAndWhitespaceButDisplaysMostRecentCasing() {
        logTransaction(foodCategory(), "150.00", "Coffee");
        logTransaction(foodCategory(), "150.00", " coffee ");

        List<FrequentExpenseDto> result = transactionService.getFrequentExpenses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo(" coffee ");
    }

    // Code review finding: two combinations tied on both COUNT and MAX(transactionDate) must
    // still sort deterministically - the final MAX(t.id) DESC tiebreaker resolves this.
    @Test
    void getFrequentExpensesBreaksTiesByMostRecentTransactionId() {
        seedCombination(foodCategory(), "10.00", "ComboA", 3);
        seedCombination(transportCategory(), "20.00", "ComboB", 3);

        List<FrequentExpenseDto> result = transactionService.getFrequentExpenses();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).description()).isEqualTo("ComboB");
        assertThat(result.get(1).description()).isEqualTo("ComboA");
    }
}
