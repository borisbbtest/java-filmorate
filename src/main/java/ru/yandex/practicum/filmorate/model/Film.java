package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28); // Константа для минимальной даты релиза

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    private String description;

    @ValidReleaseDate
    private LocalDate releaseDate;

    @Min(value = 1, message = "Duration must be positive")
    private int duration;

    private Set<Integer> likes = new HashSet<>();
}
