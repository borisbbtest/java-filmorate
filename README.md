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
### Примеры SQL-запросов для работы с описанной схемой базы данных

### **Фильмы**
1. **Добавить фильм:**
   ```sql
   INSERT INTO films (name, description, release_date, duration, rating)
   VALUES ('Интерстеллар', 'Космическая одиссея', '2014-11-07', 169, 'PG-13');
   ```

2. **Получить все фильмы:**
   ```sql
   SELECT * FROM films;
   ```

3. **Получить фильмы по жанру:**
   ```sql
   SELECT f.id, f.name, g.name AS genre
   FROM films f
   JOIN film_genres fg ON f.id = fg.film_id
   JOIN genres g ON fg.genre_id = g.id
   WHERE g.name = 'Драма';
   ```

4. **Обновить описание фильма:**
   ```sql
   UPDATE films
   SET description = 'Эпическое приключение в космосе'
   WHERE id = 1;
   ```

5. **Удалить фильм:**
   ```sql
   DELETE FROM films WHERE id = 1;
   ```

---

### **Жанры**
1. **Добавить жанр:**
   ```sql
   INSERT INTO genres (name) VALUES ('Драма');
   ```

2. **Связать фильм с жанром:**
   ```sql
   INSERT INTO film_genres (film_id, genre_id)
   VALUES (1, 2); -- где 1 - ID фильма, 2 - ID жанра
   ```

3. **Получить все жанры фильма:**
   ```sql
   SELECT g.name
   FROM genres g
   JOIN film_genres fg ON g.id = fg.genre_id
   WHERE fg.film_id = 1;
   ```

---

### **Пользователи**
1. **Добавить пользователя:**
   ```sql
   INSERT INTO users (email, login, name, birthday)
   VALUES ('example@mail.com', 'example_user', 'Иван Иванов', '1990-01-01');
   ```

2. **Получить всех пользователей:**
   ```sql
   SELECT * FROM users;
   ```

3. **Найти пользователя по email:**
   ```sql
   SELECT * FROM users WHERE email = 'example@mail.com';
   ```

---

### **Дружба**
1. **Отправить запрос на дружбу:**
   ```sql
   INSERT INTO friendships (user_id, friend_id, status)
   VALUES (1, 2, 'pending');
   ```

2. **Подтвердить дружбу:**
   ```sql
   UPDATE friendships
   SET status = 'confirmed'
   WHERE user_id = 1 AND friend_id = 2;
   ```

3. **Получить друзей пользователя:**
   ```sql
   SELECT u.id, u.name
   FROM users u
   JOIN friendships f ON u.id = f.friend_id
   WHERE f.user_id = 1 AND f.status = 'confirmed';
   ```

---

### **Лайки**
1. **Добавить лайк фильму:**
   ```sql
   INSERT INTO likes (user_id, film_id) VALUES (1, 2);
   ```

2. **Получить количество лайков у фильма:**
   ```sql
   SELECT COUNT(*) AS like_count
   FROM likes
   WHERE film_id = 2;
   ```

3. **Топ-3 популярных фильмов по лайкам:**
   ```sql
   SELECT f.id, f.name, COUNT(l.user_id) AS like_count
   FROM films f
   LEFT JOIN likes l ON f.id = l.film_id
   GROUP BY f.id, f.name
   ORDER BY like_count DESC
   LIMIT 3;
   ```