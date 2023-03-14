package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> get(Integer id) {
        log.info("Поиск фильма по id={}", id);
        String sql = "SELECT F.*,\n" +
                "       M.NAME AS MPA_NAME,\n" +
                "       group_concat(concat(G.ID, ':', G.NAME) separator ',') AS GENRE\n" +
                "FROM FILMS F\n" +
                "LEFT JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
                "LEFT JOIN MPA M ON F.MPA = M.ID\n" +
                "LEFT JOIN GENRE G ON FG.GENRE_ID = G.ID\n" +
                "WHERE F.ID = ?\n" +
                "GROUP BY F.ID";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::filmBuilder, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    @Override
    public List<Film> getAll() {
        String sql = "SELECT F.*,\n" +
                "       M.NAME AS MPA_NAME,\n" +
                "       group_concat(concat(G.ID, ':', G.NAME) separator ',') AS GENRE\n" +
                "FROM FILMS F\n" +
                "LEFT JOIN FILM_GENRE FG ON F.ID = FG.FILM_ID\n" +
                "LEFT JOIN MPA M ON F.MPA = M.ID\n" +
                "LEFT JOIN GENRE G ON FG.GENRE_ID = G.ID\n" +
                "GROUP BY F.ID";
        log.info("Получили все фильмы из базы данных");
        return jdbcTemplate.query(sql, this::filmBuilder);
    }



    @Override
    public void decreaseRating(Integer filmId) {
        String sql = "UPDATE FILMS SET RATE=RATE-5 WHERE id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void increaseRating(Integer filmId) {
        String sql = "UPDATE FILMS SET RATE=RATE+5 WHERE id = ?";
        jdbcTemplate.update(sql, filmId);
    }


    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO FILMS (NAME, MPA, RATE, DESCRIPTION, RELEASE_DATE, DURATION) VALUES (?, ?, ?, ?, ?, ?)";
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setInt(2, film.getMpa().getId());
            ps.setInt(3, film.getRate());
            ps.setString(4, film.getDescription());
            ps.setDate(5, Date.valueOf(film.getReleaseDate()));
            ps.setInt(6, film.getDuration());
            return ps;
        }, gkh);
        int filmId = Objects.requireNonNull(gkh.getKey()).intValue();
        if (film.getGenres() != null) {
            String sqlGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlGenre, filmId, genre.getId()));
        }
        film.setId(filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(
                "UPDATE FILMS SET NAME=?, MPA=?, RATE=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=? WHERE ID=?",
                film.getName(),
                film.getMpa().getId(),
                film.getRate(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId()

        );
        log.info("База была обновлена, добавлен 1 фильм");
        String sqlDelGenre = "DELETE FROM FILM_GENRE WHERE FILM_ID=?";
        jdbcTemplate.update(sqlDelGenre, film.getId());
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            String sqlInsGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            film.getGenres().forEach(
                    genre -> {
                        try {
                            jdbcTemplate.update(sqlInsGenre, film.getId(), genre.getId());
                        } catch (DuplicateKeyException e) {
                            log.info(e.getMessage());
                        }
                    }
            );
        }
        return this.get(film.getId()).orElseThrow();
    }


    @Override
    public boolean containsFilm(Integer id) {
        String sql = "SELECT F.*, M.NAME AS MPA_NAME FROM FILMS F LEFT JOIN MPA M ON F.MPA = M.ID WHERE F.id = ?";
        return !jdbcTemplate.query(sql, this::filmBuilder, id).isEmpty();
    }


    private Film filmBuilder(ResultSet rs, int row) throws SQLException {
        List<Genre> genreList;
        try {
            String genres = rs.getString("GENRE");
            genreList = Arrays.stream(genres.split(","))
                    .map(genreStr -> {
                        String[] genreArr = genreStr.split(":");
                        return Genre.builder()
                                .id(Integer.valueOf(genreArr[0]))
                                .name(genreArr[1])
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            genreList = Collections.emptyList();
        }
        Mpa mpa = Mpa.builder()
                .id(rs.getInt("MPA"))
                .name(rs.getString("MPA_NAME"))
                .build();
        return Film.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .genres(genreList)
                .mpa(mpa)
                .rate(rs.getInt("RATE"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .build();
    }
}