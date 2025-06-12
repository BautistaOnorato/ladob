package com.pm.ladob.genre;

import com.pm.ladob.models.Genre;
import com.pm.ladob.repository.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GenreControllerMVCTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setTup() {
        genreRepository.deleteAll();
    }

    @Test
    void itShouldGetGenres() throws Exception {
        Genre genre1 = new Genre();
        genre1.setName("Rock");
        Genre genre2 = new Genre();
        genre2.setName("Pop");
        genreRepository.save(genre1);
        genreRepository.save(genre2);

        mockMvc.perform(get("/genres/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Rock"))
                .andExpect(jsonPath("$[1].name").value("Pop"));
    }

    @Test
    void itShouldGetGenreById() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);

        mockMvc.perform(get("/genres/{id}", genre.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(genre.getId().toString()))
                .andExpect(jsonPath("$.name").value("Rock"));
    }

    @Test
    void itShouldNotGetGenreById() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/genres/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Genre not found with id: " + id));
    }

    @Test
    void itShouldCreateGenre() throws Exception {
        String validGenreJson = """
                {
                    "name": "Rock"
                }
                """;

        mockMvc.perform(post("/genres/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(matchesRegex("^[0-9a-fA-F-]{36}$")))
                .andExpect(jsonPath("$.name").value("Rock"));

        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals("Rock", genreRepository.findAll().getFirst().getName());
    }

    @Test
    void itShouldNotCreateGenreWithoutNameReturnsBadRequest() throws Exception {
        String validGenreJson = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(post("/genres/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));


        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void itShouldNotCreateGenreWithNameLengthGreaterThan50ReturnsBadRequest() throws Exception {
        String validGenreJson = "{\"name\": \"" + "a".repeat(51) + "\"}";

        mockMvc.perform(post("/genres/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.name").value("Name cannot exceed 50 characters"));


        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void itShouldNotCreateAlreadyExistingGenreReturnsBadRequest() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");

        genreRepository.save(genre);

        String validGenreJson = """
                {
                    "name": "Rock"
                }
                """;

        mockMvc.perform(post("/genres/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("A genre with this name already exists: Rock"));


        Assertions.assertEquals(1, genreRepository.count());
    }

    @Test
    void itShouldUpdateGenre() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);
        String validGenreJson = """
                {
                    "name": "Pop"
                }
                """;

        mockMvc.perform(put("/genres/{id}", genre.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validGenreJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(genre.getId().toString()))
            .andExpect(jsonPath("$.name").value("Pop"));
    }

    @Test
    void itShouldNotUpdateNotFoundGenreReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        String validGenreJson = """
                {
                    "name": "Rock"
                }
                """;

        mockMvc.perform(put("/genres/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Genre not found with id: " + id));
    }

    @Test
    void itShouldNotUpdateGenreWithoutNameReturnsBadRequest() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);
        String validGenreJson = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(put("/genres/{id}", genre.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.name").value("Name is required"));
    }

    @Test
    void itShouldNotUpdateGenreWithNameLengthGreaterThan50ReturnsBadRequest() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);
        String validGenreJson = "{\"name\": \"" + "a".repeat(51) + "\"}";

        mockMvc.perform(put("/genres/{id}", genre.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.name").value("Name cannot exceed 50 characters"));
    }

    @Test
    void itShouldNotUpdateAlreadyExistingGenreReturnsBadRequest() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);

        String validGenreJson = """
                {
                    "name": "Rock"
                }
                """;

        mockMvc.perform(put("/genres/{id}", genre.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("A genre with this name already exists: Rock"));
    }

    @Test
    void itShouldDeleteGenre() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);

        Assertions.assertEquals(1, genreRepository.count());

        mockMvc.perform(delete("/genres/{id}", genre.getId()))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void itShouldNotDeleteNotFoundGenreReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/genres/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Genre not found with id: " + id));
    }
}
