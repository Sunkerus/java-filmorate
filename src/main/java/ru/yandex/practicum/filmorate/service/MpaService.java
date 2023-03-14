package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.instances.NotFoundObjectException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage storage;
    public List<Mpa> getAll() {
        return storage.getAll();
    }

    public Mpa getById(Integer id) throws NotFoundObjectException {
        return storage.getMpa(id)
                .orElseThrow(
                        () -> new NotFoundObjectException("Объект не найден")
                );
    }

}
