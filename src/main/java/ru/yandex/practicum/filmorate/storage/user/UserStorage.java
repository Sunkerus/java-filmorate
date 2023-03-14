package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> get(Integer id);

    User add(User user);

    User update(User user);

    Collection<User> getAll();

    boolean containsUser(Integer id);

    void createSubscriber(Integer authorId, Integer subscriberId);

    boolean checkIsSubscriber(Integer authorId, Integer subscriberId);

    void deleteSubscriber(Integer authorId, Integer subscriberId);

    List<User> getSubscribers(Integer id);
}