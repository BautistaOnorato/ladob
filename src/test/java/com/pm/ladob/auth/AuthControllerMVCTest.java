package com.pm.ladob.auth;

import com.pm.ladob.AbstractIntegrationTest;
import com.pm.ladob.config.TestUserDataLoader;
import com.pm.ladob.enums.UserRole;
import com.pm.ladob.models.Genre;
import com.pm.ladob.repository.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestUserDataLoader.class)
public class AuthControllerMVCTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GenreRepository genreRepository;

    private String token;

    @BeforeEach
    void setUp() {
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
                        .with(authToken(UserRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(matchesRegex("^[0-9a-fA-F-]{36}$")))
                .andExpect(jsonPath("$.name").value("Rock"));

        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals("Rock", genreRepository.findAll().getFirst().getName());
    }

    @Test
    void itShouldNotCreateGenreWithRoleUser() throws Exception {
        String validGenreJson = """
                {
                    "name": "Rock"
                }
                """;

        mockMvc.perform(post("/genres/")
                        .with(authToken(UserRole.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Access Denied"));

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void itShouldNotCreateGenreWithoutToken() throws Exception {
        String validGenreJson = """
                {
                    "name": "Rock"
                }
                """;

        mockMvc.perform(post("/genres/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Full authentication is required to access this resource"));

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void itShouldNotCreateGenreWithoutNameReturnsBadRequest() throws Exception {
        String validGenreJson = """
                {
                    "name": ""
                }
                """;

        mockMvc.perform(post("/genres/")
                        .with(authToken(UserRole.ADMIN))
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
                        .with(authToken(UserRole.ADMIN))
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
                        .with(authToken(UserRole.ADMIN))
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
                        .with(authToken(UserRole.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(genre.getId().toString()))
            .andExpect(jsonPath("$.name").value("Pop"));
    }

    @Test
    void itShouldNotUpdateGenreWithRoleUser() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);
        String validGenreJson = """
                {
                    "name": "Pop"
                }
                """;

        mockMvc.perform(put("/genres/{id}", genre.getId())
                        .with(authToken(UserRole.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validGenreJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Access Denied"));

        Assertions.assertTrue(genreRepository.findById(genre.getId()).isPresent());
        Assertions.assertEquals("Rock", genreRepository.findById(genre.getId()).get().getName());
    }

    @Test
    void itShouldNotUpdateGenreWithoutToken() throws Exception {
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
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Full authentication is required to access this resource"));

        Assertions.assertTrue(genreRepository.findById(genre.getId()).isPresent());
        Assertions.assertEquals("Rock", genreRepository.findById(genre.getId()).get().getName());
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
                        .with(authToken(UserRole.ADMIN))
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
                        .with(authToken(UserRole.ADMIN))
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
                        .with(authToken(UserRole.ADMIN))
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
                        .with(authToken(UserRole.ADMIN))
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

        mockMvc.perform(delete("/genres/{id}", genre.getId()).with(authToken(UserRole.ADMIN)))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    void itShouldNotDeleteGenreWithRoleUser() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);

        mockMvc.perform(delete("/genres/{id}", genre.getId()).with(authToken(UserRole.USER)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode").value(403))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Access Denied"));

        Assertions.assertEquals(1, genreRepository.count());
    }

    @Test
    void itShouldNotDeleteGenreWithoutToken() throws Exception {
        Genre genre = new Genre();
        genre.setName("Rock");
        genre = genreRepository.save(genre);

        mockMvc.perform(post("/genres/{id}", genre.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Full authentication is required to access this resource"));

        Assertions.assertEquals(1, genreRepository.count());
    }

    @Test
    void itShouldNotDeleteNotFoundGenreReturnsNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/genres/{id}", id).with(authToken(UserRole.ADMIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.message").value("Genre not found with id: " + id));
    }
}
