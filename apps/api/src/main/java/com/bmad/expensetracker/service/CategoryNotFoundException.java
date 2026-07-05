package com.bmad.expensetracker.service;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super("No category exists with id " + categoryId);
    }
}
