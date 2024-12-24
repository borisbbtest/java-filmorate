package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    User getUserById(int id);

    void deleteUser(int id);

    List<User> getAllUsers();

    void addFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    void removeFriend(int userId, int friendId);

    List<User> getCommonFriends(int userId, int otherId);

}
