package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(int id, User user) {
        User existingUser = userStorage.getUserById(id);
        if (existingUser == null) {
            throw new EntityNotFoundException("User with ID " + id + " not found.");
        }
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());
        return userStorage.updateUser(existingUser);
    }

    public User getUser(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + id + " not found.");
        }
        return user;
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId,friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with ID " + userId + " not found.");
        }
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int id, int userId) {
       return userStorage.getCommonFriends(id, userId );
    }
}
