package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final List<Film> films = new ArrayList<>();

    private int generateFilmId() {
        return films.size() + 1;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        try {
            log.info("Получен запрос на добавление фильма: {}", film);
            validateFilm(film);
            film.setId(generateFilmId());
            films.add(film);
            log.info("Фильм успешно добавлен: {}", film);
            return film;
        } catch (ValidationException ex) {
            log.error("Ошибка при добавлении фильма: {}", ex.getMessage());
            throw ex;
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        try {
            log.info("Получен запрос на обновление фильма: {}", film);
            validateFilm(film);
            Film existingFilm = findFilmById(film.getId());
            if (existingFilm == null) {
                log.error("Фильм с ID {} не найден", film.getId());
                throw new RuntimeException("Фильм не найден");
            }

            films.removeIf(existing -> existing.getId() == film.getId());
            films.add(film);

            log.info("Фильм успешно обновлён: {}", film);
            return film;
        } catch (Exception ex) {
            log.error("Ошибка при обновлении фильма: {}", ex.getMessage());
            throw ex;
        }
    }

    private Film findFilmById(long id) {
        return films.stream()
                .filter(film -> film.getId() == id)
                .findFirst()
                .orElse(null);
    }


    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return films;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.warn("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Описание фильма превышает 200 символов");
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(java.time.LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза фильма слишком ранняя");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        Film film = findFilmById(id);
        film.getLikes().add((long) userId);
        log.info("Лайк добавлен фильму ID {} пользователем ID {}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        Film film = findFilmById(id);
        if (!film.getLikes().remove((long) userId)) {
            throw new NotFoundException("Лайк от пользователя с ID " + userId + " не найден");
        }
        log.info("Лайк удалён у фильма ID {} пользователем ID {}", id, userId);
    }

    private Film findFilmById(int id) {
        return films.stream()
                .filter(film -> film.getId() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос на получение популярных фильмов, количество: {}", count);

        return films.stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }
}