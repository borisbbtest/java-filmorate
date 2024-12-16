# java-filmorate
Template repository for Filmorate project.



# Схема базы данных Filmorate

В данном проекте используется реляционная база данных для управления информацией о пользователях, фильмах, жанрах и их связях. Схема базы данных разработана с учетом нормализации для обеспечения целостности данных и удобства выполнения запросов.

## Схема базы данных

![Схема базы данных](diagramDB.png)

### Таблицы и их описание

#### `films`
Хранит информацию о фильмах:
- **id** (PK) — уникальный идентификатор фильма.
- **name** — название фильма.
- **description** — описание фильма.
- **release_date** — дата выхода фильма.
- **duration** — продолжительность фильма в минутах.
- **rating** — возрастной рейтинг (MPAA).

#### `genres`
Содержит список доступных жанров:
- **genre_id** (PK) — уникальный идентификатор жанра.
- **name** — название жанра.

#### `film_genres`
Таблица-связка для отношения "многие-ко-многим" между фильмами и жанрами:
- **film_id** (FK, PK) — идентификатор фильма.
- **genre_id** (FK, PK) — идентификатор жанра.

#### `users`
Хранит информацию о пользователях:
- **id** (PK) — уникальный идентификатор пользователя.
- **email** — адрес электронной почты.
- **login** — логин пользователя.
- **name** — имя пользователя.
- **birthday** — дата рождения.

#### `friendships`
Отражает связи дружбы между пользователями:
- **user_id** (FK, PK) — идентификатор пользователя.
- **friend_id** (FK, PK) — идентификатор друга.
- **status** — статус дружбы (подтвержденная/неподтвержденная).

## Примеры SQL-запросов

```
Table: films
-------------------------
| id (PK)               |
| name                  |
| description           |
| release_date          |
| duration              |
| rating (ENUM)         |

Table: genres
-------------------------
| genre_id (PK)         |
| name                  |

Table: film_genres
-------------------------
| film_id (FK, PK)      |
| genre_id (FK, PK)     |

Table: users
-------------------------
| id (PK)               |
| email                 |
| login                 |
| name                  |
| birthday              |

Table: friendships
-------------------------
| user_id (FK, PK)      |
| friend_id (FK, PK)    |
| status (ENUM)         |

```
## Cхемы базы данных в PostgreSQL:
```sql

CREATE TABLE films (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор фильма
    name VARCHAR(255) NOT NULL, -- Название фильма
    description TEXT, -- Описание фильма
    release_date DATE NOT NULL, -- Дата выхода
    duration INT NOT NULL, -- Продолжительность в минутах
    rating VARCHAR(10) -- Возрастной рейтинг (G, PG, PG-13, R, NC-17)
);

CREATE TABLE genres (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор жанра
    name VARCHAR(255) NOT NULL -- Название жанра
);

CREATE TABLE film_genres (
    film_id INT NOT NULL REFERENCES films(id) ON DELETE CASCADE, -- Ссылка на фильм
    genre_id INT NOT NULL REFERENCES genres(id) ON DELETE CASCADE, -- Ссылка на жанр
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY, -- Уникальный идентификатор пользователя
    email VARCHAR(255) NOT NULL UNIQUE, -- Адрес электронной почты
    login VARCHAR(50) NOT NULL UNIQUE, -- Логин
    name VARCHAR(255), -- Имя
    birthday DATE NOT NULL -- Дата рождения
);

CREATE TABLE friendships (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- Ссылка на пользователя
    friend_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- Ссылка на друга
    status VARCHAR(20) DEFAULT 'pending', -- Статус дружбы (pending, confirmed)
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE likes (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- Пользователь, поставивший лайк
    film_id INT NOT NULL REFERENCES films(id) ON DELETE CASCADE, -- Фильм, которому поставлен лайк
    PRIMARY KEY (user_id, film_id)
);

```