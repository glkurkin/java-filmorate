package ru.yandex.practicum.filmorate.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
class GenreDbStorageTest {

    @Autowired
    private GenreStorage genreStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM GENRES");

        jdbcTemplate.update("INSERT INTO GENRES (id, name) VALUES (?, ?)", 1, "Комедия");
        jdbcTemplate.update("INSERT INTO GENRES (id, name) VALUES (?, ?)", 2, "Драма");
        jdbcTemplate.update("INSERT INTO GENRES (id, name) VALUES (?, ?)", 3, "Мультфильм");
        jdbcTemplate.update("INSERT INTO GENRES (id, name) VALUES (?, ?)", 4, "Триллер");
        jdbcTemplate.update("INSERT INTO GENRES (id, name) VALUES (?, ?)", 5, "Документальный");
        jdbcTemplate.update("INSERT INTO GENRES (id, name) VALUES (?, ?)", 6, "Боевик");
    }

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres).hasSize(6);

        assertThat(genres).containsExactlyInAnyOrder(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")
        );
    }

    @Test
    void testGetGenreByIdExists() {
        Genre genre = genreStorage.getById(1);
        assertThat(genre).isNotNull();
        assertThat(genre).isEqualTo(new Genre(1, "Комедия"));
    }

    @Test
    void testGetGenreByIdNotExists() {
        Genre genre = genreStorage.getById(999);
        assertThat(genre).isNull();
    }

}