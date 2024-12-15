package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;


import java.time.LocalDate;

@Data
public class User {
    private int id;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Login cannot be empty or contain spaces")
    private String login;

    private String name;

    @Past(message = "Birthday cannot be in the future")
    private LocalDate birthday;
}
