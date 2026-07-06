package com.bmad.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmad.expensetracker.dto.FrequentExpenseDto;
import com.bmad.expensetracker.dto.TransactionDto;
import com.bmad.expensetracker.service.CategoryNotFoundException;
import com.bmad.expensetracker.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    // AC1/AC2: a valid request creates a Transaction and returns 201 + a DTO (AD-3). Both the
    // request and response carry amount as a JSON string ("150.00"), not a bare number - code
    // review decision so money never round-trips through a JS float on the frontend.
    @Test
    void createTransactionReturnsCreatedDto() throws Exception {
        var dto = new TransactionDto(1L, 2L, new BigDecimal("150.00"), LocalDate.of(2026, 7, 5), "Coffee");
        given(transactionService.createTransaction(any())).willReturn(dto);

        String body =
                """
                {"amount": "150.00", "categoryId": 2, "description": "Coffee", "transactionDate": "2026-07-05"}
                """;

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryId").value(2))
                .andExpect(jsonPath("$.amount").value("150.00"))
                .andExpect(jsonPath("$.description").value("Coffee"));
    }

    // AC4: a blank description fails @NotBlank validation, and (Task 7) now returns a proper 400
    // in the AD-8 error shape instead of falling through to the catch-all 500.
    @Test
    void createTransactionWithBlankDescriptionReturnsValidationError() throws Exception {
        String body = """
                {"amount": "150.00", "categoryId": 2, "description": ""}
                """;

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").exists());
    }

    // AC1/AC4: amount must be > 0.
    @Test
    void createTransactionWithZeroAmountReturnsValidationError() throws Exception {
        String body = """
                {"amount": "0", "categoryId": 2, "description": "Coffee"}
                """;

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    // Code review finding: amount beyond NUMERIC(12,2)'s precision must be rejected as a client
    // error (400), not allowed through to blow up as a DB overflow (500) at save time.
    @Test
    void createTransactionWithTooManyDecimalPlacesReturnsValidationError() throws Exception {
        String body = """
                {"amount": "150.123", "categoryId": 2, "description": "Coffee"}
                """;

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    // Code review finding: description beyond the entity's 255-char column must be rejected as
    // a client error (400), not allowed through to blow up as a DB overflow (500) at save time.
    @Test
    void createTransactionWithTooLongDescriptionReturnsValidationError() throws Exception {
        String tooLong = "x".repeat(256);
        String body = """
                {"amount": "150.00", "categoryId": 2, "description": "%s"}
                """
                .formatted(tooLong);

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    // Code review finding: an unparsable transactionDate used to fail Jackson deserialization
    // before @Valid ever ran, falling through to the catch-all 500 instead of a 400.
    @Test
    void createTransactionWithMalformedDateReturnsValidationError() throws Exception {
        String body = """
                {"amount": "150.00", "categoryId": 2, "description": "Coffee", "transactionDate": "not-a-date"}
                """;

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    // Edge case not in epics.md ACs: an unknown categoryId must not 500 or silently corrupt data.
    @Test
    void createTransactionWithUnknownCategoryReturnsCategoryNotFoundError() throws Exception {
        given(transactionService.createTransaction(any())).willThrow(new CategoryNotFoundException(999L));

        String body = """
                {"amount": "150.00", "categoryId": 999, "description": "Coffee"}
                """;

        mockMvc.perform(post("/api/transactions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("CATEGORY_NOT_FOUND"));
    }

    // AC5: the last-used category is exposed for Quick Add's pre-selection.
    @Test
    void getLastUsedCategoryReturnsCategoryId() throws Exception {
        given(transactionService.getLastUsedCategoryId()).willReturn(2L);

        mockMvc.perform(get("/api/transactions/last-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(2));
    }

    // First-ever-use edge case: no transaction has ever been logged yet.
    @Test
    void getLastUsedCategoryReturnsNullWhenNoTransactionsExist() throws Exception {
        given(transactionService.getLastUsedCategoryId()).willReturn(null);

        mockMvc.perform(get("/api/transactions/last-category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(org.hamcrest.Matchers.nullValue()));
    }

    // AC1: the shelf is populated from this dedicated endpoint's ranked list.
    @Test
    void getFrequentExpensesReturnsRankedList() throws Exception {
        given(transactionService.getFrequentExpenses())
                .willReturn(List.of(new FrequentExpenseDto(2L, new BigDecimal("150.00"), "Coffee")));

        mockMvc.perform(get("/api/transactions/frequent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].categoryId").value(2))
                .andExpect(jsonPath("$[0].amount").value("150.00"))
                .andExpect(jsonPath("$[0].description").value("Coffee"));
    }

    // AC6: no habitual purchase yet is a valid empty 200, not an error - lets the frontend hide
    // the shelf entirely rather than show a broken or placeholder state.
    @Test
    void getFrequentExpensesReturnsEmptyListWhenNoneHabitual() throws Exception {
        given(transactionService.getFrequentExpenses()).willReturn(List.of());

        mockMvc.perform(get("/api/transactions/frequent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }
}
