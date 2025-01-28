package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        User updatedUser = userStorage.update(user);
        if (updatedUser == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        return updatedUser;
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User getUserById(int id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NoSuchElementException("Пользователь не найден");
        }
        return user;
    }

    @Transactional
    public void addFriend(int userId, int friendId) {
        List<User> users = userStorage.getUsersByIds(List.of(userId, friendId));
        if (users.size() < 2) {
            throw new NoSuchElementException("Один из пользователей не найден.");
        }

        userStorage.addFriend(userId, friendId);

        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    @Transactional
    public void removeFriend(int userId, int friendId) {
        List<User> users = userStorage.getUsersByIds(List.of(userId, friendId));
        if (users.size() < 2) {
            throw new NoSuchElementException("Один из пользователей не найден.");
        }

        userStorage.removeFriend(userId, friendId);

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = getUserById(userId);
        Set<Integer> friendIds = user.getFriends();
        if (friendIds.isEmpty()) {
            return List.of();
        }
        return userStorage.getUsersByIds(new ArrayList<>(friendIds));
    }


    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);

        Set<Integer> common = new HashSet<>(user.getFriends());
        common.retainAll(otherUser.getFriends());

        if (common.isEmpty()) {
            return List.of();
        }

        return userStorage.getUsersByIds(new ArrayList<>(common));
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный формат электронной почты");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}