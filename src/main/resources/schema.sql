DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS subscribes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS mpa;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS rating;

CREATE TABLE IF NOT EXISTS mpa
(
    id   int PRIMARY KEY AUTO_INCREMENT,
    name varchar(100),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS genre
(
    id   int PRIMARY KEY AUTO_INCREMENT,
    name varchar(100),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS films
(
    id           int PRIMARY KEY AUTO_INCREMENT,
    name         varchar(100),
    mpa          int
        CONSTRAINT films_mpa_id_fk REFERENCES mpa (id),
    rate         int DEFAULT 0,
    description  varchar(100),
    release_date date,
    duration     int,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id       int PRIMARY KEY AUTO_INCREMENT,
    email    varchar(100),
    login    varchar(100),
    name     varchar(100),
    birthday date,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS subscribes
(
    author     int
        CONSTRAINT author_id_fk REFERENCES users (id),
    subscriber int
        CONSTRAINT subscriber_id_fk REFERENCES users (id),
    CONSTRAINT subscribe_id_fk UNIQUE (author, subscriber)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  int
        CONSTRAINT films_id_fk REFERENCES films (id),
    genre_id int
        CONSTRAINT genre_id_fk REFERENCES genre (id),
    CONSTRAINT unique_id_fk UNIQUE (film_id, genre_id)
);
