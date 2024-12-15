package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.time.LocalDate;

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
}
