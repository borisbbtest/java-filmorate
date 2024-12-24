package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Adding film: {}", film);
        return filmService.addFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Fetching all films");
        return filmService.getAllFilm();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info("Fetching film with ID: {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable int id, @Valid @RequestBody Film film) {
        log.info("Updating film with ID {}: {}", id, film);

        try {
            // Обновляем фильм через сервис
            Film updatedFilm = filmService.updateFilm(id, film);
            return updatedFilm;
        } catch (IllegalArgumentException e) {
            // Если фильм не найден, выбрасываем исключение с ошибкой
            throw new NotFoundException("Film not found with ID: " + id);
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        int id = film.getId();
        log.info("Updating film with ID {}: {}", id, film);

        try {
            // Обновляем фильм через сервис
            Film updatedFilm = filmService.updateFilm(id, film);
            return updatedFilm;
        } catch (IllegalArgumentException e) {
            // Если фильм не найден, выбрасываем исключение с ошибкой
            throw new EntityNotFoundException("Film not found with ID: " + id);
        }
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable int filmId, @PathVariable int userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/{filmId}/likes/count")
    public int getLikesCount(@PathVariable int filmId) {
        return filmService.getLikesCount(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Fetching top {} popular films", count);
        List<Film> films = filmService.getTopFilms(count);
        if (films.isEmpty()) {
            log.warn("No popular films found");
            throw new NotFoundException("No popular films found");
        }
        return films;
    }
}
