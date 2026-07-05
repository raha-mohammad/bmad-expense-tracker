package com.bmad.expensetracker.dto;

// Serializes to the AD-8 error shape: { "error": { "code": "...", "message": "..." } }
public record ErrorResponse(ErrorBody error) {

    public record ErrorBody(String code, String message) {
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(new ErrorBody(code, message));
    }
}
