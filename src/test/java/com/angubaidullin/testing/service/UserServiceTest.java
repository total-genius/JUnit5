package com.angubaidullin.testing.service;

import com.angubaidullin.testing.dao.UserDao;
import com.angubaidullin.testing.dto.User;
import com.angubaidullin.testing.extetion.beforeafterallextention.GlobalExtension;
import com.angubaidullin.testing.extetion.conditional.ConditionalExtension;
import com.angubaidullin.testing.extetion.paramresolver.UserServiceParamResolver;
import com.angubaidullin.testing.postprocessing.PostProcessingExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith({
        UserServiceParamResolver.class,
        GlobalExtension.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
//        ThrowableExtension.class
})
class UserServiceTest {

    private UserDao userDao;
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
        this.userDao = Mockito.spy(new UserDao());
        this.userService = new UserService(this.userDao);
    }

    @Test
    @Order(1)
    void usersEmptyIfNoUserAdded() throws IOException {
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
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "pass")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("user", null))
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

        @ParameterizedTest(name = "{arguments} test")
        @MethodSource("com.angubaidullin.testing.service.UserServiceTest#getArgsForLoginMethod")
        void loginParamThroughMethodSourceTest(String username, String password, Optional<User> user) {
            userService.addAll(IVAN, PETR);

            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }

        @Test
        void loginMethodFunctionalityPerformance() {
            Optional<User> ivan = assertTimeout(Duration.ofMillis(200L), () -> {
//                Thread.sleep(300);
                return userService.login("Ivan", "123");
            });
        }

        @Test
        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void loginMethodFunctionalityPerformance2() throws InterruptedException {
            userService.add(IVAN);
            Optional<User> user = userService.login(IVAN.getUsername(), IVAN.getPassword());
            //Thread.sleep(300);
            assertThat(user).isPresent();
            user.ifPresent(u -> assertThat(u).isEqualTo(IVAN));
        }

    }


    public static Stream<Arguments> getArgsForLoginMethod() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "456", Optional.of(PETR)),
                Arguments.of("Petr", "djhs", Optional.empty()),
                Arguments.of("gjf", "123", Optional.empty())
        );
    }

    @Test
    void shouldDeleteExisted() {
        userService.add(IVAN);
//        Mockito.doReturn(true).when(userDao).delete(IVAN.getId());
        Mockito.doReturn(true).when(userDao).delete(Mockito.anyInt());
//        Mockito.when(userDao.delete(IVAN.getId())).thenReturn(true);

        boolean result = userService.deleteUserById(IVAN.getId());
        assertThat(result).isTrue();
    }

}
