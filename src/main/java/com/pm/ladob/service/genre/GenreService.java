package com.pm.ladob.service.genre;

import com.pm.ladob.dto.GenreRequestDto;
import com.pm.ladob.exceptions.AlreadyExistsException;
import com.pm.ladob.exceptions.ResourceNotFoundException;
import com.pm.ladob.models.Genre;
import com.pm.ladob.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService implements IGenreService {
    private final GenreRepository genreRepository;

    @Override
    public Genre getGenreById(UUID id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if (genre.isEmpty()) {
            throw new ResourceNotFoundException("Genre not found with id: " + id);
        }
        return genre.get();
    }

    @Override
    public List<Genre> getGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Genre createGenre(GenreRequestDto genreRequestDto) {
        log.info("Creating genre...");
        if (genreRepository.existsByName(genreRequestDto.getName())) {
            throw new AlreadyExistsException("A genre with this name already exists: " + genreRequestDto.getName());
        }

        Genre genre = new Genre();
        genre.setName(genreRequestDto.getName());

        return genreRepository.save(genre);
    }

    @Override
    public Genre updateGenre(GenreRequestDto genreRequestDto, UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id));

        genre.setName(genreRequestDto.getName());

        return genre;
    }

    @Override
    public void deleteGenre(UUID id) {
        genreRepository.findById(id)
                .ifPresentOrElse(genreRepository::delete, () -> {
                    throw new ResourceNotFoundException("Genre not found with id: " + id);
                });
    }
}
