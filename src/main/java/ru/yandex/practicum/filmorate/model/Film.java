package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    private String description;

    private LocalDate releaseDate;

    @Min(value = 1, message = "Duration must be positive")
    private int duration;

    private Set<Integer> likes = new HashSet<>();
}
