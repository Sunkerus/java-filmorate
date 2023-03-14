package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.instances.InternalServerException;
import ru.yandex.practicum.filmorate.exception.instances.NotFoundObjectException;
import ru.yandex.practicum.filmorate.exception.instances.ValidationException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validate.UserValidator.validateCorrect;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addUser(User user) throws ValidationException {
        validateCorrect(user);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User tempUser = userStorage.add(user);
        log.debug("Был добавлен новый пользователь {}, {}", tempUser.getId(), tempUser.getName());
        return tempUser;
    }

    public User updateUser(User user) {
        validateCorrect(user);
        if (!userStorage.containsUser(user.getId())) {
            throw new NotFoundObjectException("Объект не найден");
        }
        User updateUser = userStorage.update(user);
        log.debug("Пользователь {}, {}, обновлен", updateUser.getId(), user.getName());
        return updateUser;
    }

    public List<User> getFriendsById(Integer id) {
            if (!userStorage.containsUser(id)) {
                throw new NotFoundObjectException("Пользователь с id:" + id + "не может быть найден");
            }
            return userStorage.getSubscribers(id);
    }

    public List<User> getMutualFriendsById(Integer firstUserId, Integer secondUserId) {
        if (!(userStorage.containsUser(firstUserId) && userStorage.containsUser(secondUserId))) {
            throw new NotFoundObjectException("Пользователь с id:" + firstUserId + "не может быть найден");
        }
        Set<Integer> userFriendsIds = userStorage.getSubscribers(firstUserId).stream().map(User::getId).collect(Collectors.toSet());
        Set<Integer> otherUserFriendsIds = userStorage.getSubscribers(secondUserId).stream().map(User::getId).collect(Collectors.toSet());
        userFriendsIds.retainAll(otherUserFriendsIds);
        List<User> commonFriends = new ArrayList<>();
        userFriendsIds.forEach(friendId -> userStorage.get(friendId).ifPresent(commonFriends::add));
        return commonFriends;
    }

    public Collection<User> getAll() {
        Collection<User> users = userStorage.getAll();
        log.debug("Были получены все пользователи");
        return users;
    }

    public User getUserById(Integer id) {
         if (userStorage.get(id).isEmpty()) {
             throw new NotFoundObjectException("Пользователь с id" + id + "не найден");
         }
             User user = userStorage.get(id).get();
             log.debug("Был получен пользователь {}, {}", id, user.getName());
             return user;

    }

    public void makeSubscribe(Integer authorId, Integer subscriberId) {
        if (!userStorage.containsUser(authorId) || !userStorage.containsUser(subscriberId)) {
            throw new NotFoundObjectException("Пользователя с id" + subscriberId + " не существует.");
        }
        if (isSubscribe(authorId, subscriberId)) {
            throw new InternalServerException("Вы уже подписаны на этого автора");
        }
        userStorage.createSubscriber(authorId, subscriberId);
    }

    public void removeSubscribe(Integer authorId, Integer subscriberId) {
        if (!userStorage.containsUser(authorId) || !userStorage.containsUser(subscriberId)) {
            throw new NotFoundObjectException(String.format("Юзера с id %d не существует", subscriberId));
        }
        if (!isSubscribe(authorId, subscriberId)) {
            throw new InternalServerException("Вы не подписаны на этого автора");
        }
        userStorage.deleteSubscriber(authorId, subscriberId);
    }

    public boolean isSubscribe(Integer authorId, Integer subscriberId) {
        return userStorage.checkIsSubscriber(authorId, subscriberId);
    }

    public boolean containsUser(Integer id) {
        return userStorage.containsUser(id);
    }

}