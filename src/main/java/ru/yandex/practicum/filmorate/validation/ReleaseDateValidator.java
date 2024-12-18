package ru.yandex.practicum.filmorate.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        // Если дата релиза пуста, считаем её валидной
        if (releaseDate == null) {
            return true;
        }

        // Проверяем, что дата релиза не раньше минимальной даты
        return !releaseDate.isBefore(Film.MIN_RELEASE_DATE); // Используем константу MIN_RELEASE_DATE
    }
}