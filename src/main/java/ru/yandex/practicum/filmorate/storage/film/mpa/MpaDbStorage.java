package ru.yandex.practicum.filmorate.storage.film.mpa;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> Mpa.builder().id(rs.getInt("ID")).name(rs.getString("NAME")).build()
        );
    }

    @Override
    public Optional<Mpa> getMpa(long id) {
        String sql = "select * from mpa where id = ?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            (rs, rowNum) -> Mpa.builder()
                                    .id(rs.getInt("ID"))
                                    .name(rs.getString("NAME"))
                                    .build(),
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void add(String mpa) {
        String sql = "insert into mpa (name) values (?)";
        jdbcTemplate.update(sql, mpa);
    }
}
