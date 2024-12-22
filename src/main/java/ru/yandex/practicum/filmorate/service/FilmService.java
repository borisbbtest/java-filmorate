package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(int id, Film film) {
        Film existingFilm = filmStorage.getFilmById(id);
        if (existingFilm == null) {
            throw new EntityNotFoundException("Film with ID " + id + " not found");
        }

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        existingFilm.setGenres(film.getGenres());
        existingFilm.setRating(film.getRating());

        filmStorage.updateFilm(existingFilm);

        return existingFilm;
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmByIdWithGenres(id);
       // enrichFilmWithGenres(film);
        return film;
    }

    private Film enrichFilmWithGenres(Film film) {
        List<Genre> genres = filmStorage.getGenresByFilmId(film.getId());
        film.setGenres(genres.stream().collect(Collectors.toSet()));
        Mpa mpa = filmStorage.getMpaByFilmId(film.getId());
        film.setRating(mpa);
        return film;
    }

    public List<Film> getAllFilm() {
        return filmStorage.getAllFilms();
    }


    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public int getLikesCount(int filmId) {
        return filmStorage.getLikesCount(filmId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }
}
