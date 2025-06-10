package com.pm.ladob.service.genre;

import com.pm.ladob.dto.GenreRequestDto;
import com.pm.ladob.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IGenreService {
    Genre getGenreById(UUID id);
    List<Genre> getGenres();
    Genre createGenre(GenreRequestDto genreRequestDto);
    Genre updateGenre(GenreRequestDto genreRequestDto, UUID id);
    void deleteGenre(UUID id);
}
