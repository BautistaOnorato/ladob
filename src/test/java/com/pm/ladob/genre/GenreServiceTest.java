package com.pm.ladob.genre;

import com.pm.ladob.dto.genre.GenreRequestDto;
import com.pm.ladob.exceptions.AlreadyExistsException;
import com.pm.ladob.exceptions.ResourceNotFoundException;
import com.pm.ladob.models.Genre;
import com.pm.ladob.repository.GenreRepository;
import com.pm.ladob.service.genre.GenreService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    @Test
    void itShouldGetGenreById() {
        UUID id = UUID.randomUUID();
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName("Rock");
        Mockito.when(genreRepository.findById(id)).thenReturn(Optional.of(genre));

        Genre result = genreService.getGenreById(id);

        Assertions.assertEquals("Rock", result.getName());
        Assertions.assertEquals(id, result.getId());
        Mockito.verify(genreRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void itShouldNotGetGenreById() {
        UUID id = UUID.randomUUID();
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName("Rock");
        Mockito.when(genreRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> genreService.getGenreById(id)
        );

        Assertions.assertTrue(exception.getMessage().contains("Genre not found with id: " + id));
        Mockito.verify(genreRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void itShouldGetGenres() {
        Genre rock = new Genre();
        rock.setName("Rock");
        rock.setId(UUID.randomUUID());
        Genre pop = new Genre();
        pop.setName("Pop");
        pop.setId(UUID.randomUUID());

        List<Genre> genres = List.of(
                rock,
                pop
        );
        Mockito.when(genreRepository.findAll()).thenReturn(genres);

        List<Genre> result = genreService.getGenres();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Rock", result.get(0).getName());
        Assertions.assertEquals("Pop", result.get(1).getName());

        Mockito.verify(genreRepository, Mockito.times(1)).findAll();
    }

    @Test
    void itShouldCreateGenre() {
        GenreRequestDto dto = GenreRequestDto.builder().name("Rock").build();
        Mockito.when(genreRepository.existsByName("Rock")).thenReturn(false);

        Genre savedGenre = new Genre();
        savedGenre.setId(UUID.randomUUID());
        savedGenre.setName("Rock");
        Mockito.when(genreRepository.save(Mockito.any(Genre.class))).thenReturn(savedGenre);

        Genre result = genreService.createGenre(dto);

        Assertions.assertEquals("Rock", result.getName());
        Mockito.verify(genreRepository, Mockito.times(1)).existsByName("Rock");
        Mockito.verify(genreRepository, Mockito.times(1)).save(Mockito.any(Genre.class));
    }

    @Test
    void itShouldNotCreateGenre() {
        GenreRequestDto dto = GenreRequestDto.builder().name("Rock").build();
        Mockito.when(genreRepository.existsByName("Rock")).thenReturn(true);

        AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> genreService.createGenre(dto)
        );

        Assertions.assertTrue(exception.getMessage().contains("A genre with this name already exists: " + dto.getName()));
        Mockito.verify(genreRepository, Mockito.times(1)).existsByName("Rock");
        Mockito.verify(genreRepository, Mockito.never()).save(Mockito.any(Genre.class));
    }

    @Test
    void itShouldUpdateGenre() {
        UUID id = UUID.randomUUID();
        Genre existing = new Genre();
        existing.setId(id);
        existing.setName("Rock");

        Genre update = new Genre();
        update.setName("Pop");

        Mockito.when(genreRepository.existsByName(update.getName())).thenReturn(false);
        Mockito.when(genreRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(genreRepository.save(Mockito.any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        genreService.updateGenre(GenreRequestDto.builder().name(update.getName()).build(), id);

        Mockito.verify(genreRepository, Mockito.times(1)).existsByName(update.getName());
        Mockito.verify(genreRepository, Mockito.times(1)).findById(id);

        ArgumentCaptor<Genre> genreCaptor = ArgumentCaptor.forClass(Genre.class);
        Mockito.verify(genreRepository, Mockito.times(1)).save(genreCaptor.capture());
        Genre updatedGenre = genreCaptor.getValue();

        Assertions.assertEquals("Pop", updatedGenre.getName());
        Assertions.assertEquals(id, updatedGenre.getId());
    }

    @Test
    void itShouldNotUpdateExistingGenre() {
        UUID id = UUID.randomUUID();
        GenreRequestDto existing = GenreRequestDto.builder().name("Rock").build();

        Mockito.when(genreRepository.existsByName(existing.getName())).thenReturn(true);

        Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> genreService.updateGenre(existing, id)
        );

        Mockito.verify(genreRepository, Mockito.times(1)).existsByName(existing.getName());
        Mockito.verify(genreRepository, Mockito.never()).findById(id);
        Mockito.verify(genreRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void itShouldNotUpdateNonExistingGenre() {
        UUID id = UUID.randomUUID();
        GenreRequestDto existing = GenreRequestDto.builder().name("Rock").build();

        Mockito.when(genreRepository.existsByName(existing.getName())).thenReturn(false);
        Mockito.when(genreRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> genreService.updateGenre(existing, id)
        );

        Mockito.verify(genreRepository, Mockito.times(1)).existsByName(existing.getName());
        Mockito.verify(genreRepository, Mockito.times(1)).findById(id);
        Mockito.verify(genreRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void itShouldDeleteGenre() {
        UUID id = UUID.randomUUID();
        Genre genre = new Genre();
        genre.setId(id);
        genre.setName("Rock");

        Mockito.when(genreRepository.findById(id)).thenReturn(Optional.of(genre));

        genreService.deleteGenre(id);

        Mockito.verify(genreRepository, Mockito.times(1)).findById(id);
        Mockito.verify(genreRepository, Mockito.times(1)).delete(genre);
    }

    @Test
    void itShouldNotDeleteGenre() {
        UUID id = UUID.randomUUID();
        Genre genre = new Genre();
        genre.setId(id);

        Mockito.when(genreRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> genreService.deleteGenre(id)
        );

        Mockito.verify(genreRepository, Mockito.times(1)).findById(id);
        Mockito.verify(genreRepository, Mockito.never()).delete(genre);
    }
}
