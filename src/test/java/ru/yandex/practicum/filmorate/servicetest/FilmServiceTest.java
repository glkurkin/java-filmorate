package ru.yandex.practicum.filmorate.servicetest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FilmorateApplication.class)
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        String longDescription = "a".repeat(201);
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription(longDescription);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class,
                () -> filmService.createFilm(film));
    }
}
