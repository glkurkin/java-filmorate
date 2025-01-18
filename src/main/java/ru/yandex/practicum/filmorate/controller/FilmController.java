package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final List<Film> films = new ArrayList<>();

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        try {
            log.info("Получен запрос на добавление фильма: {}", film);
            validateFilm(film);
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
            films.removeIf(existingFilm -> existingFilm.getId() == film.getId());
            films.add(film);
            log.info("Фильм успешно обновлён: {}", film);
            return film;
        } catch (ValidationException ex) {
            log.error("Ошибка при обновлении фильма: {}", ex.getMessage());
            throw ex;
        }
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
}
