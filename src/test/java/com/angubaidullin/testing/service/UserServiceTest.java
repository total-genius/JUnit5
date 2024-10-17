package com.angubaidullin.testing.service;

import com.angubaidullin.testing.dto.User;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class UserServiceTest {

    private UserService userService;
    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "456");

    @BeforeAll
    void init() {
        System.out.println("Before All");
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each test: " + this);
        userService = new UserService();
    }

    @Test
    @Order(1)
    void usersEmptyIfNoUserAdded() {
        System.out.println("usersEmptyIfNoUserAdded test: " + this);
        List<User> users = userService.getAll();
        assertAll("Testing multiple assertions",
                () -> assertTrue(users.isEmpty(), () -> "user list should be empty"),
                () -> assertFalse(!users.isEmpty(), () -> "user list should be empty"),
                () -> assertEquals(0, users.size(), () -> "user list should be empty"));
    }

    @Test
    @Order(2)
    void usersSizeIfUserAdded() {
        System.out.println("usersSizeIfUserAdded test: " + this);
        userService.add(User.of(1, "Ivan", "123"));
        userService.add(User.of(2, "Petr", "456"));

        List<User> users = userService.getAll();

        assertEquals(users.size(), 2, () -> "user list should have 2 elements");
    }

    //Используем библиотеку AssertJ
    @Test
    @Order(3)
    void usersSizeIfUserAdded_2() {

        System.out.println("usersSizeIfUserAdded test: " + this);
        userService.add(User.of(1, "Ivan", "123"));
        userService.add(User.of(2, "Petr", "456"));

        List<User> users = userService.getAll();

        //Проверка размера коллекции с помощью библиотеки AssertJ
        assertThat(users).hasSize(2);

    }

    @Test
    void usersConvertedToMapByID() {
        userService.add(IVAN);
        userService.add(PETR);

        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );

    }




    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each test: " + this);
    }

    @AfterAll
    void cleanUp() {
        System.out.println("After All");
    }

    @Nested
    @DisplayName("login method tests")
    @Tag("login")
    class LoginTest {
        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    ()->assertThrows(IllegalArgumentException.class, () -> userService.login(null, "pass")),
                    ()->assertThrows(IllegalArgumentException.class, () -> userService.login("user", null))
            );
        }

        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<User> user = userService.login(IVAN.getUsername(), IVAN.getPassword());
            assertThat(user).isPresent();
            user.ifPresent(u -> assertThat(u).isEqualTo(IVAN));
        }

        @Test
        void loginFailIfUserDoesNotExist() {
            userService.add(PETR);
            Optional<User> user = userService.login("user", IVAN.getPassword());
            assertTrue(user.isEmpty());
        }
    }
}
