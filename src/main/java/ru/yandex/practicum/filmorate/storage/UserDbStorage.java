package ru.yandex.practicum.filmorate.storage;

import com.sun.jdi.connect.VMStartException;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Profile("db")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());

        String idSql = "SELECT id FROM users WHERE email = ?";
        int id = jdbcTemplate.queryForObject(idSql, Integer.class, user.getEmail());
        user.setId(id);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int updatedRows = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        if (updatedRows == 0) {
            throw new EntityNotFoundException("User with ID " + user.getId() + " not found.");
        }

        return user;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sql, id);

        if (deletedRows == 0) {
            throw new EntityNotFoundException("User with ID " + id + " not found.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new IllegalArgumentException("User cannot add themselves as a friend.");
        }

        // Проверка существования обоих пользователей
        getUserById(userId);
        getUserById(friendId);

        // Проверка на существующую дружбу
        String checkFriendshipSql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkFriendshipSql, Integer.class, userId, friendId);

        if (count != null && count > 0) {
            throw new IllegalArgumentException("Friendship already exists between User ID " + userId + " and User ID " + friendId);
        }

        // Добавление записи о дружбе
        String addFriendshipSql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, 'confirmed')";
        jdbcTemplate.update(addFriendshipSql, userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        checkUserExistence(userId);
        checkUserExistence(friendId);
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);

    }

    // Вспомогательный метод для проверки существования пользователя
    private void checkUserExistence(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);

        if (count == null || count == 0) {
            throw new EntityNotFoundException("User with ID " + userId + " does not exist.");
        }
    }

    public List<User> getFriends(int userId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "JOIN friendships f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "JOIN friendships f1 ON u.id = f1.friend_id " +
                "JOIN friendships f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";

        return jdbcTemplate.query(sql, new UserRowMapper(), userId, otherId);
    }
}
