package es.codeurjc.web.nitflex.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.nitflex.ImageTestUtils;
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

    @Autowired
    private UserRepository userRepository;

    //private List<User> usersThatLikeFilms;
    @Autowired
    private FilmRepository filmRepository;

    @BeforeEach
    public void setup(){

    }


    /**
     * 1. When a film with valid title is saved using FilmService, it is saved in the repository and a movie is returned
     */
    @Test
    public void testSaveFilmWithValidTitle() {
        // Given
        CreateFilmRequest film = new CreateFilmRequest("Title", "Description", 2024, "+18");

        // When
        FilmDTO savedFilm = filmService.save(film);

        // Then
        Optional<FilmDTO> optionalFilm = filmService.findOne(savedFilm.id());
        assertTrue(optionalFilm.isPresent(), "Film should be present in the database");
        FilmDTO filmFromDB = optionalFilm.get();
        assertEquals(film.title(), filmFromDB.title(), "Film title should be the same");
        assertEquals(film.synopsis(), filmFromDB.synopsis(), "Film synopsis should be the same");
        assertEquals(film.releaseYear(), filmFromDB.releaseYear(), "Film release year should be the same");
        assertEquals(film.ageRating(), filmFromDB.ageRating(), "Film age rating should be the same");
    }

    /**
     * 2. When the 'title' and 'synopsis' fields of a film (WITHOUT image) are updated with a valid title through FilmService, the changes are saved in the database and the list of users who have marked it as favorite is maintained
     */
    @Test
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


    /**
     * 3. When the 'title' and 'synopsis' fields of a film (WITH image) are updated with a valid title through FilmService, the changes are saved in the database and the image does not change
     */
    @Test
    public void testUpdateFilmWithImage() {
        // Given
        CreateFilmRequest film = new CreateFilmRequest("Title", "Description", 2024, "+18");
        MultipartFile imageField = ImageTestUtils.createSampleImage();
        FilmDTO filmWithImage = filmService.save(film, imageField);

        Optional<Film> optionalFilm = filmRepository.findById(filmWithImage.id());
        assertTrue(optionalFilm.isPresent(), "Film should be present in the database");
        Film oldFilm = optionalFilm.get();

        String newTitle = "New Title";
        String newSynopsis = "New Synopsis";

        // When
        FilmSimpleDTO newFilm = new FilmSimpleDTO(oldFilm.getId(), newTitle, newSynopsis, oldFilm.getReleaseYear(), oldFilm.getAgeRating());
        FilmDTO newFilmDTO = filmService.update(filmWithImage.id(), newFilm);

        // Then
        Optional<Film> updatedFilmOptional = filmRepository.findById(newFilmDTO.id());
        assertTrue(updatedFilmOptional.isPresent(), "Updated film should be present in the database");
        Film updatedFilm = updatedFilmOptional.get();

        assertEquals(newTitle, updatedFilm.getTitle(), "Film title should be updated");
        assertEquals(newSynopsis, updatedFilm.getSynopsis(), "Film synopsis should be updated");

        try {
            assertTrue(ImageTestUtils.areSameBlob(oldFilm.getPosterFile(), updatedFilm.getPosterFile()), "Image should not change");
        } catch(Exception e) {
            fail("Error comparing images");
        }
    }

    /**
     * 4. When a film is deleted using FilmService, it is removed from the repository and from the users' favorite films list
     */
    @Test
    public void testDeleteExistingFilm() {
        // Given
        CreateFilmRequest film = new CreateFilmRequest("Title", "Description", 2024, "+18");
        FilmDTO savedFilm = filmService.save(film);
        
        Optional<Film> filmObjectOptional = filmRepository.findById(savedFilm.id());
        assertTrue(filmObjectOptional.isPresent(), "Film should be present in the database");
        Film filmObject = filmObjectOptional.get();

        User user = new User("username", "username@mail.net");
        user.getFavoriteFilms().add(filmObject);
        userRepository.save(user);

        assertTrue(userRepository.findById(user.getId()).get().getFavoriteFilms().contains(filmObject));

        // When
        filmService.delete(filmObject.getId());

        // Then
        Optional<FilmDTO> deletedFilm = filmService.findOne(filmObject.getId());
        assertTrue(deletedFilm.isEmpty(), "Film should not be present in the database");
        Optional<User> findUser = userRepository.findById(user.getId());
        assertTrue(findUser.isPresent(), "User should be present in the database");
        user = findUser.get();
        assertFalse(user.getFavoriteFilms().contains(filmObject));

    }

}
