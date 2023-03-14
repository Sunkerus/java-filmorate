package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable @NotNull Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(name = "count", required = false, defaultValue = "10")
            @NotNull @Positive Integer count) {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film postFilm(@NotNull @Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@NotNull @Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void putlikesFilm(@NotNull @Positive @PathVariable Integer id,
                             @NotNull @Positive @PathVariable Integer userId) {
          filmService.updateUserLikesFilms(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeFilm(@NotNull @Positive @PathVariable Integer id,
                               @NotNull @Positive @PathVariable Integer userId) {
         filmService.deleteUserLikesFilms(id, userId);
    }



}

