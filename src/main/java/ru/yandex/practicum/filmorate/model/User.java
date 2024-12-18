package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    private int id;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @NotBlank(message = "Login cannot be empty")
    @Pattern(regexp = "^[^\\s]+$", message = "Login cannot contain spaces")
    private String login;

    private String name;

    @NotNull(message = "Birthday cannot be null")
    @Past(message = "Birthday cannot be in the future")
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();
}
