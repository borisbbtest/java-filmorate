package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final List<Film> films = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @Override
    public Film addFilm(Film film) {
        film.setId(idGenerator.incrementAndGet());
        films.add(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film existingFilm = getFilmById(film.getId());
        films.remove(existingFilm);
        films.add(film);
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        return films.stream()
                .filter(film -> film.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Film with ID " + id + " not found"));
    }

    @Override
    public void deleteFilm(int id) {
        Film film = getFilmById(id);
        films.remove(film);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films);
    }
}
