package es.codeurjc.web.nitflex.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
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

    private UserRepository userRepository;

    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        // Initialize the necessary components for the test
        filmRepository = mock(FilmRepository.class);
        userRepository = mock(UserRepository.class);
        ImageUtils imageUtils = new ImageUtils();
        FilmMapper filmMapper = Mappers.getMapper(FilmMapper.class);

        // Create an instance of FilmService with the mocked dependencies
        filmService = new FilmService(filmRepository, userRepository, imageUtils, filmMapper);
    }

    /**
     * 1. When a film (without image) and a valid title is saved using FilmService, it is saved in the repository
     */
    @Test
    public void testSaveFilmWithValidTitle() {
        // Given
        CreateFilmRequest film = new CreateFilmRequest("Title", "Description", 2024, "+18");

        // When
        filmService.save(film);

        // Then
        verify(filmRepository).save(any(Film.class));
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
     * 3. When an existing film is deleted using FilmService, it is removed from the repository and from the users' favorite films list
     */
    @Test
    public void testDeleteExistingFilm() {
        // Given
        long existingFilmId = 1L;
        Film existingFilm = mock(Film.class);
        User user = mock(User.class);
        List<Film> favoriteFilms = new ArrayList<>();
        favoriteFilms.add(existingFilm);

        // When
        when(existingFilm.getUsersThatLiked()).thenReturn(List.of(user));
        when(filmRepository.findById(existingFilmId)).thenReturn(Optional.of(existingFilm));
        when(user.getFavoriteFilms()).thenReturn(favoriteFilms);
        
        filmService.delete(existingFilmId);

        // Then
        verify(filmRepository).deleteById(existingFilmId);
        verify(user).getFavoriteFilms();
        verify(userRepository).save(user);
        
        assertFalse(favoriteFilms.contains(existingFilm), "The film should not be in the user's favorite films list");
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

    /**
     * 5. When a film from before 1895 is added, an exception is raised
     */
    @Test
    public void testSaveFilmWithYearBefore1895() {
        // Given
        CreateFilmRequest film = new CreateFilmRequest("Title", "Description", 1894, "+18");

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            filmService.save(film);
        });

        // Then
        assertEquals("No film was made before 1895", exception.getMessage());
        verify(filmRepository, never()).save(any(Film.class));
    }
}
