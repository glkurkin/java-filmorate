package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT id, name FROM MPA_RATINGS";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("id"), rs.getString("name"))
        );
    }

    @Override
    public Mpa getById(int id) {
        String sql = "SELECT id, name FROM MPA_RATINGS WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new Mpa(rs.getInt("id"), rs.getString("name")), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
