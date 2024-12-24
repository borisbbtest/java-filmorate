package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.time.LocalDate;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    private Film film;

    @BeforeEach
    void setUp() {
        film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1);  // Убедитесь, что такой MPA есть в вашей базе
        film.setRating(mpa);

        Genre genre = new Genre();
        genre.setId(1);  // Убедитесь, что такой жанр есть в базе
        film.setGenres(Set.of(genre));
    }

    @Test
    void testAddFilm() {
        Film savedFilm = filmDbStorage.addFilm(film);

        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo(film.getName());
        assertThat(savedFilm.getRating().getId()).isEqualTo(film.getRating().getId());
        assertThat(savedFilm.getGenres()).hasSize(1);
    }

    @Test
    void testUpdateFilm() {
        Film savedFilm = filmDbStorage.addFilm(film);

        savedFilm.setName("Updated Film");
        savedFilm.setDescription("Updated Description");
        savedFilm.setDuration(150);

        filmDbStorage.updateFilm(savedFilm);

        Film updatedFilm = filmDbStorage.getFilmByIdWithGenres(savedFilm.getId());

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedFilm.getDuration()).isEqualTo(150);
    }

    @Test
    void testGetFilmById() {
        Film savedFilm = filmDbStorage.addFilm(film);
        Film foundFilm = filmDbStorage.getFilmByIdWithGenres(savedFilm.getId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getId()).isEqualTo(savedFilm.getId());
        assertThat(foundFilm.getName()).isEqualTo(savedFilm.getName());
    }

}
