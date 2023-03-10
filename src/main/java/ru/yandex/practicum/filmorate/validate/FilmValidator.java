package ru.yandex.practicum.filmorate.validate;

import ru.yandex.practicum.filmorate.exception.instances.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {

    public static void validate(Film film) throws ValidationException {
        validateTime(film);
    }


    private static void validateTime(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 1, 28))) {
            throw new ValidationException("Дата фильма не может быть указана раньше, чем 1895-01-28");
        }
    }
}
