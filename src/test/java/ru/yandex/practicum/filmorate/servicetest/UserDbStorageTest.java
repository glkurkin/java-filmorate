package ru.yandex.practicum.filmorate.servicetest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;


import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    void testCreateAndFindUser() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(java.time.LocalDate.of(1990, 1, 1));
        userDbStorage.create(user);

        User userFromDb = userDbStorage.getById(user.getId());
        assertThat(userFromDb).isNotNull();
        assertThat(userFromDb.getEmail()).isEqualTo("test@test.ru");
    }
}
