package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class FilmService {

    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            UserService userService,
            MpaService mpaService,
            GenreService genreService
    ) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            if (mpaService.getMpaById(mpaId) == null) {
                throw new NoSuchElementException("MPA с id=" + mpaId + " не найден");
            }
        }

        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                if (genreService.getGenreById(g.getId()) == null) {
                    throw new NoSuchElementException("Жанр с id=" + g.getId() + " не найден");
                }
            }
        }

        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            if (mpaService.getMpaById(mpaId) == null) {
                throw new NoSuchElementException("MPA с id=" + mpaId + " не найден");
            }
        }

        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                if (genreService.getGenreById(g.getId()) == null) {
                    throw new NoSuchElementException("Жанр с id=" + g.getId() + " не найден");
                }
            }
        }

        Film updatedFilm = filmStorage.update(film);
        if (updatedFilm == null) {
            throw new NoSuchElementException("Фильм не найден");
        }
        return updatedFilm;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new NoSuchElementException("Фильм не найден");
        }
        return film;
    }

    public void addLike(int filmId, int userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        getFilmById(filmId);
        userService.getUserById(userId);
        filmStorage.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null
                && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException(
                    "Описание фильма не может быть длиннее " + MAX_DESCRIPTION_LENGTH + " символов"
            );
        }
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            throw new ValidationException(
                    "Дата релиза фильма не может быть раньше " + EARLIEST_RELEASE_DATE
            );
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
    }
}