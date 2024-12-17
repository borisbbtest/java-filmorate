package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult result) {
        validateRequest(result);
        log.info("Adding user: {}", user);
        return userService.addUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @Valid @RequestBody User user, BindingResult result) {
        validateRequest(result);
        log.info("Updating user with ID {}: {}", id, user);
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        return updatedUser;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        log.info("Fetching user with ID: {}", id);
        User user = userService.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Adding friend with ID {} to user with ID {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Removing friend with ID {} from user with ID {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Fetching friends for user with ID: {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Fetching common friends for users with IDs {} and {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    /**
     * Проверяет наличие ошибок валидации.
     *
     * @param result BindingResult для проверки ошибок
     */
    private void validateRequest(BindingResult result) {
        if (result.hasErrors()) {
            // Собираем ошибки
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(error -> "Field: " + error.getField() +
                            ", Value: " + error.getRejectedValue() +
                            ", Message: " + error.getDefaultMessage())
                    .toList();

            log.error("Validation errors: {}", errorMessages);

            // Выбрасываем ValidationException с подробными ошибками
            throw new ValidationException("Validation failed", errorMessages);
        }
    }
}
