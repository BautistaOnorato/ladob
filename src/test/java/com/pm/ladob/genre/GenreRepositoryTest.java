package com.pm.ladob.genre;


import com.pm.ladob.models.Genre;
import com.pm.ladob.repository.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @Test
    void itShouldExistByName() {
        Genre genre = new Genre();
        genre.setName("Rock");

        genreRepository.save(genre);
        Assertions.assertTrue(genreRepository.existsByName("Rock"));
    }
}
