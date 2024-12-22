package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int id);

    void deleteFilm(int id);

    List<Film> getAllFilms();

    Film getFilmByIdWithGenres(int id);

    List<Genre> getGenresByFilmId(int id);

    Mpa getMpaByFilmId(int id);

    List<Film> getTopFilms(int count);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    int getLikesCount(int filmId);
}
