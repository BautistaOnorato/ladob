package com.pm.ladob.controller;

import com.pm.ladob.dto.ApiErrorDto;
import com.pm.ladob.dto.genre.GenreRequestDto;
import com.pm.ladob.models.Genre;
import com.pm.ladob.service.genre.IGenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/genres")
@Tag(name = "Genres", description = "API endpoint to manage genres")
public class GenreController {
    private final IGenreService genreService;

    @Operation(summary = "Get all genres", description = "Retrieve a list of all genres in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genres retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Genre.class))),
            @ApiResponse(responseCode = "500", description = "System failed while retrieving genres",
                    content = @Content(schema = @Schema()))
    })
    @GetMapping("/")
    public ResponseEntity<List<Genre>> getGenres() {
        try {
            List<Genre> genres = genreService.getGenres();
            return ResponseEntity.ok().body(genres);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Get genre by id", description = "Retrieve a genre using the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Genre.class))),
            @ApiResponse(responseCode = "400", description = "Genre not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable UUID id) {
        Genre genre = genreService.getGenreById(id);
        return ResponseEntity.ok().body(genre);
    }


    @Operation(summary = "Create a new genre", description = "Add a new genre to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Genre created successfully",
                    content = @Content(schema = @Schema(implementation = Genre.class))),
            @ApiResponse(responseCode = "400", description = "Body did not pass validation filters",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this resource",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<Genre> createGenre(
            @Valid @RequestBody GenreRequestDto genreRequestDto
    ) {
        Genre genre = genreService.createGenre(genreRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(genre);
    }

    @Operation(summary = "Update a genre", description = "Update an existing genre using the id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Genre updated successfully",
                    content = @Content(schema = @Schema(implementation = Genre.class))),
            @ApiResponse(responseCode = "400", description = "Unable to find genre in the database or body did not pass validation filters",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this resource",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(
            @PathVariable UUID id,
            @Valid @RequestBody GenreRequestDto genreRequestDto
    ) {
        Genre genre = genreService.updateGenre(genreRequestDto, id);
        return ResponseEntity.ok().body(genre);
    }

    @Operation(summary = "Delete genre by id", description = "Remove genre from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Genre removed successfully",
                    content = @Content(schema = @Schema(implementation = Genre.class))),
            @ApiResponse(responseCode = "400", description = "Unable to find genre in the database",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "User cannot access this resource",
                    content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Genre> deleteGenre(@PathVariable UUID id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
