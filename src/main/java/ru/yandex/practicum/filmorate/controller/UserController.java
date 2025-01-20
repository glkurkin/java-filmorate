package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

    private User findUserById(int id) {
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
}
