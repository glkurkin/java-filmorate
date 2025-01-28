package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        List<Mpa> result = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getInt("id"), rs.getString("name")), id);
        return result.isEmpty() ? null : result.get(0);
    }
}
