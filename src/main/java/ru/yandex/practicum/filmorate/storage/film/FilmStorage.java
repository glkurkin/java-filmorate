package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Film getById(int id);

    List<Film> getAll();

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    /**
     * Получение списка популярных фильмов с ограничением по количеству.
     *
     * @param count количество фильмов
     * @return список популярных фильмов
     */
    List<Film> getPopularFilms(int count);
}