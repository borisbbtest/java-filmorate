package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private List<Film> films = new ArrayList<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, BindingResult result) {
        if (result.hasErrors()) {
            log.error("Invalid film data: {}", film);
            throw new ValidationException("Invalid film data");
        }
        films.add(film);
        log.info("Film added: {}", film);
        return film;
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable int id, @Valid @RequestBody Film film, BindingResult result) {
        if (result.hasErrors()) {
            log.error("Invalid film data: {}", film);
            throw new ValidationException("Invalid film data");
        }
        Film existingFilm = films.stream().filter(f -> f.getId() == id).findFirst()
                .orElseThrow(() -> {
                    log.error("Film not found with ID: {}", id);
                    return new RuntimeException("Film not found");
                });
        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        log.info("Film updated: {}", existingFilm);
        return existingFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Fetching all films");
        return films;
    }
}

