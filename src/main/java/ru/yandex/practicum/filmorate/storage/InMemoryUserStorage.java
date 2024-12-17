package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Component
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
                .orElseThrow(() -> new ValidationException("User with ID " + id + " not found"));
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
}
