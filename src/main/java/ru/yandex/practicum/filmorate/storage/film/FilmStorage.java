package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAll();

    Optional<Film> get(Integer id);

    Film update(Film film);

    Film add(Film film);

    boolean containsFilm(Integer id);

    void decreaseRating(Integer filmId);

    void increaseRating(Integer filmId);
}