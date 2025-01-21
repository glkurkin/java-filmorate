package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("Друг не найден."));

        user.getFriends().add((long) friendId);
        friend.getFriends().add((long) userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User friend = userStorage.findById(friendId).orElseThrow(() -> new NotFoundException("Друг не найден."));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        return user.getFriends().stream()
                .map(friendId -> userStorage.findById(Math.toIntExact(friendId)).orElseThrow(() -> new NotFoundException("Друг не найден.")))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        User other = userStorage.findById(otherId).orElseThrow(() -> new NotFoundException("Другой пользователь не найден."));

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(friendId -> userStorage.findById(Math.toIntExact(friendId)).orElseThrow(() -> new NotFoundException("Друг не найден.")))
                .collect(Collectors.toList());
    }
}
