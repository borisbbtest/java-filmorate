package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Adding film: {}", film);
        return filmService.addFilm(film);
    }

    @GetMapping
    public List<Film> getAllUsers() {
        log.info("Fetching all users");
        return filmService.getAllFilm();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        try {
            log.info("User with ID {} is liking film with ID {}", userId, id);
            filmService.addLike(userId, id);
        } catch (Exception e) {
            throw new ValidationException("Failed to add like to film");
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        try {
            log.info("User with ID {} is removing like from film with ID {}", userId, id);
            filmService.removeLike(userId, id);
        } catch (Exception e) {
            throw new ValidationException("Failed to remove like from film");
        }
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Fetching top {} popular films", count);
        List<Film> films = filmService.getTopFilms(count);
        if (films.isEmpty()) {
            throw new NotFoundException("No popular films found");
        }
        return films;
    }
}

