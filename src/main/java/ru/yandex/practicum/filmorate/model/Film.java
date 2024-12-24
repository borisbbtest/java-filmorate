package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "films")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28); // Константа для минимальной даты релиза

    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    @Column(name = "description", length = 200)
    private String description;

    @ValidReleaseDate
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Min(value = 1, message = "Duration must be positive")
    @Column(name = "duration", nullable = false)
    private int duration;

    // Связь многие ко многим с жанрами
    @ManyToMany
    @JoinTable(
            name = "film_genres",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    // Связь многие к одному с рейтингом (Mpa)
    @ManyToOne(optional = true)
    @JoinColumn(name = "rating_id", referencedColumnName = "id", nullable = true)
    private Mpa rating;

    public void setMpa(Mpa mpa) {
        this.rating = mpa;
    }

    public Mpa getMpa() {
        return rating;
    }

    // Связь многие ко многим с пользователями, которые поставили лайк
    @ManyToMany(mappedBy = "likedFilms")
    private Set<User> usersWhoLiked = new HashSet<>();
}
