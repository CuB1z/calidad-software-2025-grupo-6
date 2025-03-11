package es.codeurjc.web.nitflex.unit;

import java.sql.Blob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;
import es.codeurjc.web.nitflex.utils.ImageUtils;

/**
 * TASK 1 : Unit test for FilmService
 */
public class FilmServiceUnitTest {

    private FilmRepository filmRepository;

    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        // Initialize the necessary components for the test
        filmRepository = mock(FilmRepository.class); // TODO: Mock the FilmRepository or use a real one
        UserRepository userRepository = mock(UserRepository.class); // TODO: Mock the UserRepository or use a real one
        ImageUtils imageUtils = mock(ImageUtils.class); // TODO: Mock the ImageUtils or use a real one
        FilmMapper filmMapper = Mappers.getMapper(FilmMapper.class);

        // Create an instance of FilmService with the mocked dependencies
        filmService = new FilmService(filmRepository, userRepository, imageUtils, filmMapper);
    }


    /**
     * 2. When a film (without image) and an empty title are saved using FilmService, it is NOT saved in the repository and an exception is thrown
     */
    @Test
    public void testSaveFilmWithEmptyImageAndTitle() {
        // Given
        CreateFilmRequest film = new CreateFilmRequest("", "Description", 2024, "+18");

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            filmService.save(film);
        });

        // Then
        assertEquals("The title is empty", exception.getMessage());
        verify(filmRepository, never()).save(any(Film.class));
    }

    /**
     * 4. When a non-existent film is deleted using FilmService, it is not removed from the repository and an exception is thrown
     */
    @Test
    public void testDeleteNonExistentFilm() {
        // Given
        long nonExistentFilmId = 999L;

        // When
        Exception exception = assertThrows(FilmNotFoundException.class, () -> {
            filmService.delete(nonExistentFilmId);
        });

        // Then
        assertEquals("Film not found with id: " + nonExistentFilmId, exception.getMessage());
        verify(filmRepository, never()).deleteById(nonExistentFilmId);
    }
}
