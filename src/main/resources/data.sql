INSERT INTO mpa_ratings (name) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO genres (name) VALUES ('Комедия'), ('Драма'), ('Боевик');

INSERT INTO USERS (email, login, name, birthday) VALUES
    ('test1@example.com', 'user1', 'User One', '1990-01-01'),
    ('test2@example.com', 'user2', 'User Two', '1985-05-05');

INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES
    ('Test Film', 'This is a test film.', '2000-01-01', 120, 1);

INSERT INTO film_genres (film_id, genre_id) VALUES (1, 1);

INSERT INTO likes (film_id, user_id) VALUES (1, 1), (1, 2);

INSERT INTO friendships (user_id, friend_id, status) VALUES (1, 2, 'confirmed');