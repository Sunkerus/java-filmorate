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
public class User {


    private Set<Integer> friends;

    private Integer id;

    @NotBlank(message = "Поле email не должно быть пустым.")
    @Email
    private String email;

    @NotBlank(message = "Поле login не может быть пустым")
    private String login;

    private String name;


    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Поле birthday не может быть пустым")
    @Past(message = "Дата дня рождения не должна быть указана в будущем.")
    private LocalDate birthday;

    public boolean addFriend(int id) {

        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends.add(id);
    }
    public boolean deleteFriend(int id) {
        return friends.remove(id);
    }
    private List<Integer> subscribers;


}
