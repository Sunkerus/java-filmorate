package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriend(@NotNull @Positive @PathVariable Integer id) {
        return userService.getFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable(name = "id") @NotNull @Positive Integer firstUserId,
                                       @PathVariable(name = "otherId") @NotNull @Positive Integer secondUserId) {
        return userService.getMutualFriendsById(firstUserId, secondUserId);
    }

    @GetMapping("/{id}")
    public User getUserById(@NotNull @Positive @PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User addUser(@NotNull @Valid @RequestBody User user) {
        return userService.addUser(user);
    }



    @PutMapping
    public User updateUser(@RequestBody @NotNull @Valid User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteSubscribe(@NotNull @Positive @PathVariable Integer id,
                             @NotNull @Positive @PathVariable Integer friendId) {
        userService.removeSubscribe(id, friendId);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addSubscribe(@NotNull @Positive @PathVariable Integer id,
                          @NotNull @Positive @PathVariable Integer friendId) {
        userService.makeSubscribe(id, friendId);
    }
}
