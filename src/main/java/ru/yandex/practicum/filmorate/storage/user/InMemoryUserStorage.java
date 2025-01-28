package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int currentId = 0;

    private int generateId() {
        return ++currentId;
    }

    @Override
    public User create(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        int id = user.getId();
        if (!users.containsKey(id)) {
            log.error("Попытка обновить несуществующего пользователя с ID {}", id);
            return null;
        }
        users.put(id, user);
        return user;
    }

    @Override
    public User getById(int id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().add(friendId);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = users.get(userId);
        if (user != null) {
            user.getFriends().remove(friendId);
        }
    }

    @Override
    public List<User> getUsersByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}