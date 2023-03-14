package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<User> getAll() {
        log.info("Поиск всех юзеров");
        String sql = "SELECT U.*, group_concat(S.SUBSCRIBER separator ',') AS SUBSCRIBERS\n" +
                "FROM USERS U\n" +
                "LEFT JOIN SUBSCRIBES S ON U.ID = S.AUTHOR\n" +
                "GROUP BY U.ID";
        return jdbcTemplate.query(sql, this::userBuilder);
    }

    @Override
    public User add(User user) {
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, gkh);
        Integer id = Objects.requireNonNull(gkh.getKey()).intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Начало обновления юзера в таблице");
        String sql = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE ID=?";
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        int rowNum = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("{} строк обновлено", rowNum);
        return user;
    }



    @Override
    public boolean containsUser(Integer id) {
        return get(id).isPresent();
    }

    @Override
    public Optional<User> get(Integer id) {
        log.info("Поиск юзера по id={}", id);
        String sql = "SELECT U.*, group_concat(S.SUBSCRIBER separator ',') AS SUBSCRIBERS\n" +
                "FROM USERS U\n" +
                "LEFT JOIN SUBSCRIBES S ON U.ID = S.AUTHOR WHERE U.ID = ?\n" +
                "GROUP BY U.ID";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::userBuilder, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }




    @Override
    public List<User> getSubscribers(Integer id) {
        log.info("Поиск всех подписчиков");
        String sql = "SELECT U.*\n" +
                "FROM SUBSCRIBES\n" +
                "JOIN USERS U on U.ID = SUBSCRIBES.SUBSCRIBER\n" +
                "WHERE AUTHOR=?";
        return jdbcTemplate.query(sql, this::userBuilder, id);
    }

    private User userBuilder(ResultSet rs, int rowNum) throws SQLException {
        String subscribers;
        try {
            subscribers = rs.getString("SUBSCRIBERS");
        } catch (SQLException e) {
            subscribers = null;
        }
        List<Integer> subscribersList = subscribers != null ? Arrays
                .stream(subscribers.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList()) : null;
        return User.builder()
                .id(rs.getInt("ID"))
                .email(rs.getString("EMAIL"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("NAME"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .subscribers(subscribersList)
                .build();
    }

    private Subscriber subscribeBuilder(ResultSet rs, int rowNum) throws SQLException {
        return Subscriber.builder()
                .authorId(rs.getInt("AUTHOR"))
                .subscriberId(rs.getInt("SUBSCRIBER"))
                .build();
    }

    @Override
    public void createSubscriber(Integer authorId, Integer subscriberId) {
        String sql = "INSERT INTO SUBSCRIBES (AUTHOR, SUBSCRIBER) VALUES (?, ?)";
        jdbcTemplate.update(sql, authorId, subscriberId);
        log.info("Подписка {} на {} создана", subscriberId, authorId);
    }

    @Override
    public boolean checkIsSubscriber(Integer authorId, Integer subscriberId) {
        String sql = "SELECT * FROM SUBSCRIBES WHERE AUTHOR=? AND SUBSCRIBER=?";
        return !jdbcTemplate.query(sql, this::subscribeBuilder, authorId, subscriberId).isEmpty();
    }

    @Override
    public void deleteSubscriber(Integer authorId, Integer subscriberId) {
        String sql = "DELETE FROM SUBSCRIBES WHERE AUTHOR=? AND SUBSCRIBER=?";
        jdbcTemplate.update(sql, authorId, subscriberId);
        log.info("Подписка {} на {} удалена", subscriberId, authorId);
    }

}