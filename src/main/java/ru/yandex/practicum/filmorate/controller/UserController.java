package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final List<User> users = new ArrayList<>();

    private int generateUserId() {
        return users.size() + 1;
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        try {
            log.info("Получен запрос на добавление пользователя: {}", user);
            validateUser(user);
            user.setId(generateUserId());
            users.add(user);
            log.info("Пользователь успешно добавлен: {}", user);
            return user;
        } catch (ValidationException ex) {
            log.error("Ошибка при добавлении пользователя: {}", ex.getMessage());
            throw ex;
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        try {
            log.info("Получен запрос на обновление пользователя: {}", user);
            validateUser(user);
            User existingUser = findUserById(user.getId());
            if (existingUser == null) {
                log.error("Пользователь с ID {} не найден", user.getId());
                throw new RuntimeException("Пользователь не найден");
            }

            users.removeIf(existing -> existing.getId() == user.getId());
            users.add(user);

            log.info("Пользователь успешно обновлён: {}", user);
            return user;
        } catch (Exception ex) {
            log.error("Ошибка при обновлении пользователя: {}", ex.getMessage());
            throw ex;
        }
    }

    private User findUserById(long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }


    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return users;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Электронная почта не может быть пустой и должна содержать символ '@'");
            throw new ValidationException("Некорректный формат электронной почты");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.warn("Логин не может быть пустым или содержать пробелы");
            throw new ValidationException("Некорректный логин");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(java.time.LocalDate.now())) {
            log.warn("Дата рождения пользователя не может быть в будущем");
            throw new ValidationException("Некорректная дата рождения");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя пользователя пустое, будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);
        user.getFriends().add((long) friendId);
        friend.getFriends().add((long) id);
        log.info("Дружба установлена между ID {} и ID {}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);

        if (!user.getFriends().remove((long) friendId) || !friend.getFriends().remove((long) id)) {
            throw new NotFoundException("Дружба между ID " + id + " и ID " + friendId + " не найдена");
        }
        log.info("Дружба разорвана между ID {} и ID {}", id, friendId);
    }


    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Получен запрос на получение списка друзей пользователя ID: {}", id);
        User user = findUserById(id);

        if (user == null) {
            log.error("Пользователь с ID {} не найден", id);
            throw new RuntimeException("Пользователь не найден");
        }

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(findUserById(friendId));
        }

        log.info("Список друзей успешно получен для пользователя ID: {}", id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен запрос на общих друзей. Пользователь ID: {}, другой пользователь ID: {}", id, otherId);
        User user = findUserById(id);
        User otherUser = findUserById(otherId);

        if (user == null || otherUser == null) {
            log.error("Один из пользователей не найден: пользователь ID {}, другой пользователь ID {}", id, otherId);
            throw new RuntimeException("Один из пользователей не найден");
        }

        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            if (otherUser.getFriends().contains(friendId)) {
                commonFriends.add(findUserById(friendId));
            }
        }

        log.info("Список общих друзей успешно получен. Пользователь ID: {}, другой пользователь ID: {}", id, otherId);
        return commonFriends;
    }

    private User findUserById(int id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
