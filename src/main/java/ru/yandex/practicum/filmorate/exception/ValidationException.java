package ru.yandex.practicum.filmorate.exception;

import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> errorDetails;

    public ValidationException(String message, List<String> errorDetails) {
        super(message);
        this.errorDetails = errorDetails;
    }

    public ValidationException(String message) {
        super(message);
        this.errorDetails = List.of();
    }

    public List<String> getErrorDetails() {
        return errorDetails;
    }
}
