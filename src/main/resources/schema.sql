-- Создание таблицы для хранения информации о возрастных рейтингах, если она не существует
CREATE TABLE IF NOT EXISTS mpa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
);

-- Создание таблицы для хранения информации о фильмах, если она не существует
CREATE TABLE IF NOT EXISTS films (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    rating_id INT DEFAULT NULL,
    FOREIGN KEY (rating_id) REFERENCES mpa(id)
);
-- Создание таблицы для хранения информации о жанрах, если она не существует
CREATE TABLE IF NOT EXISTS genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Создание таблицы для связи фильмов с жанрами (многие ко многим), если она не существует
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- Создание таблицы для пользователей, если она не существует
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE NOT NULL
);

-- Создание таблицы для управления дружбой между пользователями, если она не существует
CREATE TABLE IF NOT EXISTS friendships (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Создание таблицы для лайков фильмов пользователями, если она не существует
CREATE TABLE IF NOT EXISTS likes (
    user_id INT NOT NULL,
    film_id INT NOT NULL,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE
);

-- Вставка начальных данных для жанров, если они не существуют
INSERT INTO genres (name)
SELECT 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Комедия');
INSERT INTO genres (name)
SELECT 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Драма');
INSERT INTO genres (name)
SELECT 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Мультфильм');
INSERT INTO genres (name)
SELECT 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Триллер');
INSERT INTO genres (name)
SELECT 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Документальный');
INSERT INTO genres (name)
SELECT 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name = 'Боевик');

-- Вставка начальных данных для возрастных рейтингов, если они не существуют
INSERT INTO mpa (name)
SELECT 'G' WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE name = 'G');
INSERT INTO mpa (name)
SELECT 'PG' WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE name = 'PG');
INSERT INTO mpa (name)
SELECT 'PG-13' WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE name = 'PG-13');
INSERT INTO mpa (name)
SELECT 'R' WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE name = 'R');
INSERT INTO mpa (name)
SELECT 'NC-17' WHERE NOT EXISTS (SELECT 1 FROM mpa WHERE name = 'NC-17');