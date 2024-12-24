package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.util.List;

@Service
public class MpaService {

    private final FilmDbStorage filmDbStorage;

    public MpaService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    public List<Mpa> getAllMpa() {
        return filmDbStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        var res = filmDbStorage.getMpaById(id);
        if (res == null) {
            throw new NotFoundException("Mpa with ID " + id + " does not exist");
        }
        return res;
    }
}
