package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.instances.InternalServerException;
import ru.yandex.practicum.filmorate.exception.instances.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exception.instances.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validate.FilmValidator.validate;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film addFilm(Film film) throws ValidationException {
        validate(film);
        Film newFilm = filmStorage.add(film);
        log.debug("Был добавлен новый фильм: {}, {}", newFilm.getId(), newFilm.getName());
        return newFilm;
    }

    public Collection<Film> getAll() {
        Collection<Film> users = filmStorage.getAll();
        log.debug("Были получены все фильмы");
        return users;
    }

    public Film getFilmById(Integer id)  {
        return filmStorage.get(id).orElseThrow(() -> new  NotFoundObjectException("Фильм с" + id + "не может быть найден"));
    }

    public List<Film> getPopularFilms(Integer count) {
        log.debug("Получен список популярных фильмов");

        return filmStorage.getAll()
                .stream()
                .sorted((o1, o2) -> Integer.compare(o2.getRate(), o1.getRate()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film updateFilm(Film film)  {
        validate(film);
        if (!filmStorage.containsFilm(film.getId())) {
            throw new NotFoundObjectException("Фильм с" + film.getId() + "не может быть найден");
        }
        return filmStorage.update(film);
    }

    public void updateUserLikesFilms(Integer id, Integer userId) throws NotFoundObjectException, InternalServerException{
        if (!filmStorage.containsFilm(id)) {
            throw new NotFoundObjectException("Фильм с" + id + "не может быть найден");
        }
        if (!userService.containsUser(userId)) {
            throw new NotFoundObjectException("Пользователь " + userId + "не может быть найден");
        }
        filmStorage.increaseRating(id);
    }


    public void deleteUserLikesFilms(Integer id, Integer userId) throws NotFoundObjectException {
        Optional<Film> film = filmStorage.get(id);
        if (film.isEmpty()) {
            throw new NotFoundObjectException("Фильм с" + id + "не может быть найден");
        }
        if (!userService.containsUser(userId)) {
            throw new NotFoundObjectException("Пользователь " + userId + "не может быть найден");
        }
        filmStorage.decreaseRating(id);
    }




}