package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GlobalExceptionHandler;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class FilmorateApplicationTests {

    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController, filmController)
                .setControllerAdvice(new GlobalExceptionHandler())  // Добавить глобальный обработчик исключений
                .build();
    }

    @Test
    void testInvalidFilmData() throws Exception {
        String invalidFilmJson = "{ \"name\": \"\", \"description\": \"A description that is far too long for the allowed length...\" }";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(invalidFilmJson))
                .andExpect(status().isInternalServerError());  // Ожидаем ошибку 500
    }

    @Test
    void testInvalidUserData() throws Exception {
        String invalidUserJson = "{ \"email\": \"invalid-email\", \"login\": \"user with spaces\", \"birthday\": \"2025-12-31\" }";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(invalidUserJson))
                .andExpect(status().isInternalServerError());  // Ожидаем ошибку 500
    }

    @Test
    void testAddUserAndFilm() throws Exception {
        String userJson = "{ \"email\": \"user@example.com\", \"login\": \"user123\", \"name\": \"Test\", \"birthday\": \"1990-01-01\" }";
        String filmJson = "{ \"name\": \"Film Title\", \"description\": \"Description of the film\", \"releaseDate\": \"1967-03-25\", \"duration\": 100}";

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(filmJson))
                .andExpect(status().isOk());
    }
}



