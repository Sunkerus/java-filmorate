package ru.yandex.practicum.filmorate.storage.film.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> Genre.builder().id(rs.getInt("ID")).name(rs.getString("NAME")).build()
        );
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        String sql = "SELECT * FROM GENRE WHERE id = ?";
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            (rs, rowNum) -> Genre.builder()
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

    public void add(String genre) {
        String sql = "INSERT INTO GENRE (NAME) VALUES (?)";
        jdbcTemplate.update(sql, genre);
    }
}