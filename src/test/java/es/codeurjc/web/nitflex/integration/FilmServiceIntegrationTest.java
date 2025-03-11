package es.codeurjc.web.nitflex.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import es.codeurjc.web.nitflex.service.FilmService;

/**
 * TASK 2 : Integration test for FilmService
 */
@SpringBootTest
public class FilmServiceIntegrationTest {

    @Autowired
    private FilmService filmService;
    
    /**
     * 2. When the 'title' and 'synopsis' fields of a film (WITHOUT image) are updated with a valid title through FilmService, the changes are saved in the database and the list of users who have marked it as favorite is maintained
     */    
    @Test
    @Transactional
    public void testUpdateFilmWithNoImage() {
        // Given
        CreateFilmRequest film = new CreateFilmRequest("Title", "Description", 2024, "+18");
        FilmDTO filmNoImage = filmService.save(film);
        Optional<FilmDTO> optionalFilm = filmService.findOne(filmNoImage.id());
        assertTrue(optionalFilm.isPresent(), "Film should be present in the database");
        FilmDTO oldFilm = optionalFilm.get();
        String newTitle = "New Title";
        String newSynopsis = "New Synopsis";

        // When
        FilmSimpleDTO newFilm = new FilmSimpleDTO(oldFilm.id(), newTitle, newSynopsis, oldFilm.releaseYear(), oldFilm.ageRating());
        filmService.update(filmNoImage.id(), newFilm);

        // Then
        Optional<FilmDTO> updatedFilmOptional = filmService.findOne(filmNoImage.id());
        assertTrue(updatedFilmOptional.isPresent(), "Updated film should be present in the database");
        FilmDTO updatedFilm = updatedFilmOptional.get();
        assertEquals(newTitle, updatedFilm.title(), "Film title should be updated");
        assertEquals(newSynopsis, updatedFilm.synopsis(), "Film synopsis should be updated");
        assertEquals(oldFilm.usersThatLiked(), updatedFilm.usersThatLiked(), "Users who liked the film should remain the same");
    }


}
