package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.GlobalExceptionHandler;
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

}



