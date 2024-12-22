package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("inMemory")
public class InMemoryUserStorage implements UserStorage {

    private final List<User> users = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @Override
    public User addUser(User user) {
        user.setId(idGenerator.incrementAndGet());
        users.add(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        users.remove(existingUser);
        users.add(user);
        return user;
    }

    @Override
    public User getUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
    }

    @Override
    public void deleteUser(int id) {
        User user = getUserById(id);
        users.remove(user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public void addFriend(int userId, int friendId) {

    }

    @Override
    public List<User> getFriends(int userId) {
        return List.of();
    }

    @Override
    public void removeFriend(int userId, int friendId) {

    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        return List.of();
    }
}
