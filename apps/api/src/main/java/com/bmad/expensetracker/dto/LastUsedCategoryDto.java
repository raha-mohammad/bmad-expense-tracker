package com.bmad.expensetracker.dto;

// categoryId is null when no transaction has ever been logged yet (first-ever use of the app).
public record LastUsedCategoryDto(Long categoryId) {
}
