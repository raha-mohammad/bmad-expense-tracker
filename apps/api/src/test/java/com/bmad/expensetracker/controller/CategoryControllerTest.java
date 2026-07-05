package com.bmad.expensetracker.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmad.expensetracker.dto.CategoryDto;
import com.bmad.expensetracker.service.CategoryService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    // AC3 (via AD-3): the endpoint returns DTOs, never the @Entity, with the seeded shape.
    @Test
    void getCategoriesReturnsDtoPayload() throws Exception {
        given(categoryService.getAllCategories())
                .willReturn(List.of(
                        new CategoryDto(1L, "Food", "food", "DEFAULT"),
                        new CategoryDto(6L, "Uncategorized", "help-circle", "SYSTEM")));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo("Food")))
                .andExpect(jsonPath("$[1].kind", equalTo("SYSTEM")));
    }

    // AC2: a CORS preflight from the configured dev origin succeeds without a CORS error.
    @Test
    void corsPreflightFromConfiguredOriginSucceeds() throws Exception {
        mockMvc.perform(options("/api/categories")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    // AC4: this story has no other real controller to trigger @ControllerAdvice through, so the
    // one real endpoint is used, with the service mocked to throw.
    @Test
    void unhandledExceptionReturnsControllerAdviceErrorShape() throws Exception {
        given(categoryService.getAllCategories()).willThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.code", equalTo("INTERNAL_ERROR")))
                .andExpect(jsonPath("$.error.message").exists());
    }
}
