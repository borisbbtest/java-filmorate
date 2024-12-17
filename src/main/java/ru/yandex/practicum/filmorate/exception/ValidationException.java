package ru.yandex.practicum.filmorate.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> errorDetails;

    // Конструктор, принимающий сообщение и карту с деталями ошибок
    public ValidationException(String message, Map<String, String> errorDetails) {
        super(message); // Вызов конструктора родительского класса (RuntimeException)
        this.errorDetails = errorDetails;
    }

    public ValidationException(String message) {
        super(message); // Вызов конструктора родительского класса (RuntimeException)
        this.errorDetails = null;
    }

    // Получение карты ошибок
    public Map<String, String> getErrorDetails() {
        return errorDetails;
    }
}
