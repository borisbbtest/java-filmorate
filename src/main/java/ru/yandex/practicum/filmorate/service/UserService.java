package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        userStorage.addUser(user);
        return user;
    }


    public User updateUser(int id, User user) {
        User existingUser = userStorage.getUserById(id);
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());
        userStorage.updateUser(existingUser);
        return existingUser;
    }

    public User getUser(int id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public List<User> getFriends(int userId) {
        return userStorage.getUserById(userId).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        User user1 = userStorage.getUserById(userId1);
        User user2 = userStorage.getUserById(userId2);

        user1.getFriends().retainAll(user2.getFriends());
        return user1.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
