package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.*;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Builder(toBuilder = true)
public class Film {

    private Set<Integer> userLikes;
    private Integer id;
    private int rate;

    @NotBlank(message = "Поле name не должно быть пустым")
    private String name;

    @NotBlank(message = "Поле description не должно быть пустым.")
    @Size(max = 200, message = "Описание не должно быть больше, чем 200 символов.")
    private String description;

    @NotNull(message = "Поле releaseDate не должно быть пустым")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotNull(message = "Поле duration не должно быть пустым")
    @Positive(message = "Переменная duration должна быть положительной")
    private int duration;

    private List<Genre> genres;

    private Mpa mpa;




}