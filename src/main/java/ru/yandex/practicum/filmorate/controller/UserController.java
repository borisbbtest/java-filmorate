package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final List<User> users = new ArrayList<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            log.error("Invalid user data: {}", user);
            throw new ValidationException("Invalid user data");
        }
        users.add(user);
        log.info("User added: {}", user);
        return user;
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            log.error("Invalid user data: {}", user);
            throw new ValidationException("Invalid user data");
        }
        User existingUser = users.stream().filter(u -> u.getId() == id).findFirst()
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());
        log.info("User updated: {}", existingUser);
        return existingUser;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return users;
    }
}
