package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = ReleaseDateValidator.class) // Указываем валидатор
@Target({ElementType.FIELD, ElementType.PARAMETER}) // Применимо к полям и параметрам
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidReleaseDate {
    String message() default "Release date must not be earlier than the specified minimum date"; // Сообщение по умолчанию
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
