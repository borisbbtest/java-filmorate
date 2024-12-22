package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Profile("db")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initializeMpaData();
    }

    private void initializeMpaData() {
        String checkSql = "SELECT COUNT(*) FROM mpa";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);
        if (count == null || count == 0) {
            String insertSql = "INSERT INTO mpa (id, name) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17')";
            jdbcTemplate.update(insertSql);
        }
    }

    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new MpaRowMapper(), id);
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa ORDER BY id";
        return jdbcTemplate.query(sql, new MpaRowMapper());
    }

    private static class MpaRowMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }
    }

    @Override
    public Film addFilm(Film film) {
        Integer ratingId = (film.getRating() != null) ? film.getRating().getId() : null;

        // Проверка существования MPA рейтинга
        if (ratingId != null) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mpa WHERE id = ?", Integer.class, ratingId);
            if (count == null || count == 0) {
                throw new ValidationException("MPA with ID " + ratingId + " does not exist.");
            }
        }

        // Добавление фильма
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)"; // нет ретутна плохо
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (ratingId != null) {
                ps.setInt(5, ratingId);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            film.setId(key.intValue());
        }

        if (ratingId != null) {
            Mpa mpa = new Mpa();
            mpa.setId(ratingId);
            mpa.setName(jdbcTemplate.queryForObject("SELECT name FROM mpa WHERE id = ?", String.class, ratingId));
            film.setRating(mpa);
        }

        // Добавление жанров с проверкой на существование
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String checkGenreSql = "SELECT COUNT(*) FROM genres WHERE id = ?";
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            String checkFilmAdnGenreSql = "SELECT COUNT(*) FROM film_genres WHERE film_id = ? AND genre_id = ?";

            for (Genre genre : film.getGenres()) {
                int genreCount = jdbcTemplate.queryForObject(checkGenreSql, Integer.class, genre.getId());
                int genreFilmCount = jdbcTemplate.queryForObject(checkFilmAdnGenreSql, Integer.class, film.getId() , genre.getId());
                if (genreCount == 0) {
                    throw new ValidationException("Genre with ID " + genre.getId() + " does not exist.");
                }
                if (genreFilmCount == 0) {
                    jdbcTemplate.update(genreSql, film.getId(), genre.getId());
                }

            }
        }
        // Сортировка жанров по ID
        List<Genre> sortedGenres = getGenresByFilmId(film.getId());
        sortedGenres.sort(Comparator.comparing(Genre::getId));
        film.setGenres(new LinkedHashSet<>(sortedGenres));
        return film;
    }

    public void addLike(int filmId, int userId) {
        // Проверяем существование фильма и пользователя
        String checkFilmSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer filmCount = jdbcTemplate.queryForObject(checkFilmSql, Integer.class, filmId);

        String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(checkUserSql, Integer.class, userId);

        if (filmCount == 0) {
            throw new EntityNotFoundException("Film with ID " + filmId + " not found.");
        }

        if (userCount == 0) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }

        // Проверяем существование лайка перед вставкой
        String checkLikeSql = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        Integer likeCount = jdbcTemplate.queryForObject(checkLikeSql, Integer.class, filmId, userId);

        if (likeCount == 0) {
            // Вставляем лайк, только если его нет
            String likeSql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(likeSql, filmId, userId);
        }
    }

    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        int removed = jdbcTemplate.update(sql, filmId, userId);

        if (removed == 0) {
            throw new EntityNotFoundException("Like from User ID " + userId + " to Film ID " + filmId + " not found.");
        }
    }

    public int getLikesCount(int filmId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        Integer ratingId = (film.getRating() != null) ? film.getRating().getId() : null;

        // Проверка существования MPA рейтинга при обновлении
        if (ratingId != null) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mpa WHERE id = ?", Integer.class, ratingId);
            if (count == null || count == 0) {
                throw new EntityNotFoundException("MPA with ID " + ratingId + " does not exist.");
            }
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), ratingId, film.getId());

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Film with ID " + film.getId() + " not found.");
        }

        String deleteGenresSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String checkGenreSql = "SELECT COUNT(*) FROM genres WHERE id = ?";
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                int genreCount = jdbcTemplate.queryForObject(checkGenreSql, Integer.class, genre.getId());
                if (genreCount == 0) {
                    throw new EntityNotFoundException("Genre with ID " + genre.getId() + " does not exist.");
                }
                jdbcTemplate.update(genreSql, film.getId(), genre.getId());
            }
        }

        return film;
    }

    public List<Film> getTopFilms(int count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, m.id as mpa_id, m.name as mpa_name, " +
                "COUNT(l.user_id) as likes " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.id " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id, m.id, m.name " +
                "ORDER BY likes DESC, f.id ASC " +  // Добавляем сортировку по ID для стабильности
                "LIMIT ?";

        return jdbcTemplate.query(sql, new FilmRowMapper(), count);
    }

    public Film getFilmByIdWithGenres(int id) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id as mpa_id, m.name as mpa_name, g.id as genre_id, g.name as genre_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.id " +
                "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.id " +
                "WHERE f.id = ?";

        List<Film> films = jdbcTemplate.query(sql, (rs) -> {
            Map<Integer, Film> filmMap = new HashMap<>();

            while (rs.next()) {
                int filmId = rs.getInt("id");
                Film film = filmMap.computeIfAbsent(filmId, k -> {
                    Film f = new Film();
                    try {
                        f.setId(filmId);
                        f.setName(rs.getString("name"));
                        f.setDescription(rs.getString("description"));
                        f.setReleaseDate(rs.getDate("release_date").toLocalDate());
                        f.setDuration(rs.getInt("duration"));

                        Integer mpaId = rs.getObject("mpa_id", Integer.class);
                        if (mpaId != null) {
                            Mpa mpa = new Mpa();
                            mpa.setId(mpaId);
                            mpa.setName(rs.getString("mpa_name"));
                            f.setRating(mpa);
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    // Используем TreeSet для сортировки жанров
                    f.setGenres(new TreeSet<>(Comparator.comparingInt(Genre::getId)));
                    return f;
                });

                Integer genreId = rs.getObject("genre_id", Integer.class);
                if (genreId != null) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    film.getGenres().add(genre);
                }
            }
            return new ArrayList<>(filmMap.values());
        }, id);

        if (films.isEmpty()) {
            throw new EntityNotFoundException("Film with ID " + id + " not found.");
        }

        return films.get(0);
    }


    public List<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
    }

    public Mpa getMpaByFilmId(int filmId) {
        String sql = "SELECT m.id, m.name FROM mpa m " +
                "JOIN films f ON m.id = f.rating_id WHERE f.id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }, filmId);
    }

    @Override
    public Film getFilmById(int id) {
        String sql = """
        SELECT f.id, f.name, f.description, f.release_date, f.duration,
               m.id as mpa_id, m.name as mpa_name
        FROM films f
        LEFT JOIN mpa m ON f.rating_id = m.id
        WHERE f.id = ?
    """;
        return jdbcTemplate.queryForObject(sql, new FilmRowMapper(), id);
    }

    @Override
    public void deleteFilm(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);

        if (deletedRows == 0) {
            throw new EntityNotFoundException("Film with ID " + id + " not found.");
        }
    }

    public List<Film> getAllFilms() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, " +
                "m.id AS mpa_id, m.name AS mpa_name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.rating_id = m.id";
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            Integer mpaId = rs.getObject("mpa_id", Integer.class);
            if (mpaId != null) {
                Mpa mpa = new Mpa();
                mpa.setId(mpaId);
                mpa.setName(rs.getString("name"));
                film.setRating(mpa);
            } else {
                film.setRating(null);  // Устанавливаем null, если rating_id отсутствует
            }

            return film;
        }
    }

}
