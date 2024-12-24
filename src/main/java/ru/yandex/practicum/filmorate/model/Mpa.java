package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "mpa")
public class Mpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, length = 10)
    private String name;
}
