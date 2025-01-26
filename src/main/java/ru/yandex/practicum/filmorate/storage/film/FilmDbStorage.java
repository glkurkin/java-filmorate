package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);

        saveGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILMS SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";

        int rowsUpdated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        if (rowsUpdated == 0) {
            return null;
        }

        deleteFilmGenres(film.getId());
        saveFilmGenres(film);

        return getById(film.getId());
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name " +
                "FROM FILMS f " +
                "LEFT JOIN MPA_RATINGS m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), id);
        if (films.isEmpty()) {
            return null;
        }
        return films.get(0);
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name " +
                "FROM FILMS f " +
                "LEFT JOIN MPA_RATINGS m ON f.mpa_id = m.id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");
        if (!rs.wasNull()) {
            Mpa mpa = new Mpa(mpaId, rs.getString("mpa_name"));
            film.setMpa(mpa);
        }

        film.setLikes(getLikesForFilm(film.getId()));
        film.setGenres(getGenresForFilm(film.getId()));

        return film;
    }

    private Set<Integer> getLikesForFilm(int filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> userIds = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        return new HashSet<>(userIds);
    }

    private Set<Genre> getGenresForFilm(int filmId) {
        String sql = "SELECT g.id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?" +
                "ORDER BY g.id";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getInt("id"), rs.getString("name")), filmId);
        return new LinkedHashSet<>(genres);
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        Set<Integer> uniqueGenreIds = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        for (Integer genreId : uniqueGenreIds) {
            jdbcTemplate.update(sql, film.getId(), genreId);
        }
    }

    private void deleteFilmGenres(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sql, film.getId(), genre.getId());
            }
        }
    }
}