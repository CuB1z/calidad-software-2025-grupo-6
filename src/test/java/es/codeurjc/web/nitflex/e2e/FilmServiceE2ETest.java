package es.codeurjc.web.nitflex.e2e;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import es.codeurjc.web.nitflex.Application;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;


/**
 * TASK 3 : End-to-end test for FilmService
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmServiceE2ETest {

    private static final String BASE_URL = "http://localhost:";

    @LocalServerPort
    private int port;

    private WebDriver driver;

    private WebDriverWait wait;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        User user = new User("username", "mail");
        userRepository.save(user);
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) driver.quit();
        wait = null;
        userRepository.deleteAll();
    }

    /**
     * 1. When a new film (without image) is added, we expect the film to appear in the screen
     */
    @Test
    public void testAddFilm() throws InterruptedException {
        //Given
        driver.get(BASE_URL + this.port + "/films/new");

        //When
        String title = "Title";
        String description = "Description";
        int releaseYear = 2024;
        String ageRating = "+18";
        addFilmToForm(title, description, releaseYear, ageRating);

        //Then
        wait.until(presenceOfElementLocated(By.id("filmDetail")));
        assertThat(driver.findElement(By.id("film-synopsis")).getText()).isEqualTo(description);
        assertThat(driver.findElement(By.id("film-title")).getText()).isEqualTo(title);
        assertThat(driver.findElement(By.id("film-releaseYear")).getText()).isEqualTo(String.valueOf(releaseYear));
        assertThat(driver.findElement(By.id("age-requirement")).getText()).isEqualTo(ageRating);
    }

    /**
     * 2. When adding a new film without a title, we expect an error message to be displayed and that the film does not appear on the main page.
     */
    @Test
    public void testAddFilmWithoutTitle() {
        //Given
        driver.get(BASE_URL + this.port + "/films/new");

        //When
        addFilmToForm("", "Description", 2024, "+18");

        //Then
        wait.until(presenceOfElementLocated(By.id("new-film")));
        assertNotNull(driver.findElement(By.id("error-list")), "There is no error message");
        
        // Go to the main page
        driver.get(BASE_URL + this.port + "/");
        wait.until(presenceOfElementLocated(By.id("film-list")));
        assertTrue(getFilms().stream().allMatch(film -> !film.getText().isEmpty()),
                   "Film with empty title should not be present in the list");
    }


    /**
     * 3. When a new film is added and then deleted, we expect the film to disappear from the list of films
     */
    @Test
    public void testAddAndDeleteFilm() {
        //Given
        driver.get(BASE_URL + this.port + "/films/new");

        //When
        addFilmToForm("NewTitle", "Description", 2024, "+18");
        wait.until(presenceOfElementLocated(By.id("remove-film")));
        driver.findElement(By.id("remove-film")).click();

        //Then
        wait.until(presenceOfElementLocated(By.id("all-films")));
        driver.findElement(By.id("all-films")).click();

        wait.until(presenceOfElementLocated(By.id("film-list")));
        assertTrue(getFilms().stream().noneMatch(film -> film.getText().equals("NewTitle")),
                   "Film should not be present in the list");
    }

    /**
     *  4. When a new film is added and edited to add '- part 2' to its title, we check that the change has been applied
     */
    @Test
    public void testEditFilm() {
        // Given
        String title = "Title";
        String addToTittle = " - part 2";
        driver.get(BASE_URL + this.port + "/films/new");

        // When
        addFilmToForm(title, "Description", 2024, "+18");
        wait.until(presenceOfElementLocated(By.id("edit-film")));
        driver.findElement(By.id("edit-film")).click();
        wait.until(presenceOfElementLocated(By.id("new-film")));
        driver.findElement(By.name("title")).sendKeys(addToTittle);
        driver.findElement(By.id("Save")).click();

        // Then
        wait.until(presenceOfElementLocated(By.id("all-films")));
        driver.findElement(By.id("all-films")).click();

        wait.until(presenceOfElementLocated(By.id("film-list")));
        assertTrue(getFilms().stream().anyMatch(film -> film.getText().equals(title + addToTittle)), 
                "Film title should be updated");
    }

    /**
     * 5. When "Cancel" is clicked in the film creation form, we expect to be redirected to the main page (Films list)
     */
    @Test
    public void testCancelFilmCreation() {
        // Given
        driver.get(BASE_URL + this.port + "/films/new");

        // When
        driver.findElement(By.id("cancel-btn")).click();

        // Then
        wait.until(presenceOfElementLocated(By.id("film-list")));
        assertTrue(driver.getCurrentUrl().equals(BASE_URL + this.port + "/"), 
                   "Should be redirected to the main page");
    }

    /**
     * 5. When a film from before 1895 is added, its not added
     */
    @Test
    public void testAddFilmBefore1895() {
        // Given
        driver.get(BASE_URL + this.port + "/films/new");

        // When
        addFilmToForm("Title", "Description", 1894, "+18");

        // Then
        wait.until(presenceOfElementLocated(By.id("new-film")));
        assertNotNull(driver.findElement(By.id("error-list")), "There is no error message");

        // Go to the main page
        driver.get(BASE_URL + this.port + "/");
        wait.until(presenceOfElementLocated(By.id("film-list")));
        assertTrue(getDates().stream().allMatch(date -> !date.getText().equals("Released in 1894")),
                   "Film with release year before 1895 should not be present in the list");
    }

    /**
     * Helper method to add a film to the form
     * 
     * @param title
     * @param description
     * @param releaseYear
     * @param ageRating
     */
    private void addFilmToForm(String title, String description, int releaseYear, String ageRating) {
        driver.findElement(By.name("title")).sendKeys(title);
        WebElement element = driver.findElement(By.name("releaseYear"));
        element.clear();
        element.sendKeys(String.valueOf(releaseYear));
        driver.findElement(By.name("synopsis")).sendKeys(description);
        new Select(driver.findElement(By.name("ageRating"))).selectByValue(ageRating);
        driver.findElement(By.id("Save")).click();
    }

    /**
     * Helper method to get the list of films
     * 
     * @return List of films
     */
    private List<WebElement> getFilms() {
        return driver.findElements(By.className("film-title"));
    }

    private List<WebElement> getDates() {
        return driver.findElements(By.className("date"));
    }
}
