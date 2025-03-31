package es.codeurjc.web.nitflex.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import es.codeurjc.web.nitflex.Application;
import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;


/**
 * TASK 4 : REST test for FilmService
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmServiceRestTest {

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = BASE_URL + port;
    }

    /**
     * 2. When a new film is added without a title, we expect that an appropriate error message is displayed
     */
    @Test
    public void testAddFilmWithoutTitle() {

        // Create a new film
        JSONObject newFilm = new JSONObject();
        newFilm.put("title", "");
        newFilm.put("synopsis", "This is a synopsis of the new film.");
        newFilm.put("releaseYear", 2025);
        newFilm.put("ageRating", "+18");

        // Verify the film was created
        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(newFilm.toJSONString())
            .when()
                .post("/api/films/")
            .then()
                .log().all()
                .statusCode(400).assertThat()
                .body(Matchers.equalTo("The title is empty"));
    }
    
    /**
     * 3. When a new film is added and then edited to append "- parte 2" to its title, we verify that the change has been applied
     */
    @Test
    public void testAddAndEditFilm() {

        // Create a new film
        JSONObject newFilm = new JSONObject();
        newFilm.put("title", "New Film Title");
        newFilm.put("synopsis", "This is a synopsis of the new film.");
        newFilm.put("releaseYear", 2025);
        newFilm.put("ageRating", "+18");

        // Verify the film was created
        Integer filmId = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(newFilm.toJSONString())
            .when()
                .post("/api/films/")
            .then()
                .statusCode(201)
                .body("title", Matchers.equalTo(newFilm.get("title")))
                .body("synopsis", Matchers.equalTo(newFilm.get("synopsis")))
                .body("releaseYear", Matchers.equalTo(newFilm.get("releaseYear")))
                .body("ageRating", Matchers.equalTo(newFilm.get("ageRating")))
                .extract()
                .path("id");

        // Edit the film
        FilmSimpleDTO updatedFilm = new FilmSimpleDTO(
            (long) filmId,
            "New Film Title - parte 2",
            "This is a synopsis of the new film.",
            2025,
            "PG-13"
        );
            
        // Verify the film was updated
        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(updatedFilm)
            .when()
                .put("/api/films/" + filmId)
            .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(filmId))
                .body("title", Matchers.equalTo(updatedFilm.title()));        
    }
}
