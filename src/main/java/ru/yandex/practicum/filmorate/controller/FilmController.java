package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult result) {
        validateRequest(result);
        log.info("Adding film: {}", film);
        return filmService.addFilm(film);
    }

    @GetMapping
    public List<Film> getAllUsers() {
        log.info("Fetching all films");
        return filmService.getAllFilm();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        try {
            log.info("User with ID {} is liking film with ID {}", userId, id);
            filmService.addLike(userId, id);
        } catch (Exception e) {
            log.error("Error while adding like: {}", e.getMessage());
            throw new ValidationException("Failed to add like to film");
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        try {
            log.info("User with ID {} is removing like from film with ID {}", userId, id);
            filmService.removeLike(userId, id);
        } catch (Exception e) {
            log.error("Error while removing like: {}", e.getMessage());
            throw new ValidationException("Failed to remove like from film");
        }
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Fetching top {} popular films", count);
        List<Film> films = filmService.getTopFilms(count);
        if (films.isEmpty()) {
            log.warn("No popular films found");
            throw new NotFoundException("No popular films found");
        }
        return films;
    }

    /**
     * Проверяет наличие ошибок валидации.
     *
     * @param result BindingResult для проверки ошибок
     */
    private void validateRequest(BindingResult result) {
        if (result.hasErrors()) {
            // Собираем ошибки
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> "Field: '" + error.getField() +
                            "', Rejected value: '" + error.getRejectedValue() +
                            "', Error: " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            log.error("Validation errors: {}", errors);

            // Выбрасываем ValidationException с подробными ошибками
            throw new ValidationException("Validation failed for film", errors);
        }
    }
}
