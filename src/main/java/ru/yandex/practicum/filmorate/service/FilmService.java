package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
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

    public void addLike(int userId, int filmId) {
        // Проверяем, существует ли фильм с указанным ID
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("Film with ID " + filmId + " does not exist");
        }

        // Проверяем, существует ли пользователь с указанным ID
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }

        // Добавляем лайк
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(int id, Film film) {
        // Получаем существующий фильм из хранилища
        Film existingFilm = filmStorage.getFilmById(id);

        // Проверка на существование фильма
        if (existingFilm == null) {
            throw new EntityNotFoundException("Film with ID " + id + " not found");
        }

        // Обновляем поля существующего фильма на основе данных из запроса
        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());

        // Обновляем фильм в хранилище
        filmStorage.updateFilm(existingFilm);

        // Возвращаем обновленный фильм
        return existingFilm;
    }

    public List<Film> getAllFilm() {
        return  filmStorage.getAllFilms();
    }

    public void removeLike(int userId, int filmId) {
        // Проверяем, существует ли фильм
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new EntityNotFoundException("Film with ID " + filmId + " does not exist");
        }

        // Проверяем, существует ли пользователь
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist");
        }

        // Проверяем, поставлен ли лайк, перед удалением
        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("User with ID " + userId + " has not liked the film with ID " + filmId);
        }

        // Удаляем лайк
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
