package es.codeurjc.web.nitflex.e2e;

import es.codeurjc.web.nitflex.Application;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;


/**
 * TASK 3 : End-to-end test for FilmService
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmServiceE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    private WebDriverWait wait;


    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        User user = new User("username", "mail");
        userRepository.save(user);
    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        wait = null;
        userRepository.deleteAll();
    }

    /**
     * 1. When a new film (without image) is added, we expect the film to appear in the screen
     */
    @Test
    public void testAddFilm() throws InterruptedException {
        //Given
        driver.get("http://localhost:" + this.port + "/films/new");

        //When
        String title = "Title";
        String description = "Description";
        int releaseYear = 2024;
        String ageRating = "+18";
        driver.findElement(By.name("title")).sendKeys(title);
        WebElement element = driver.findElement(By.name("releaseYear"));
        element.clear();
        element.sendKeys(String.valueOf(releaseYear));
        driver.findElement(By.name("synopsis")).sendKeys(description);
        new Select(driver.findElement(By.name("ageRating"))).selectByValue(ageRating);
        driver.findElement(By.id("Save")).click();

        //Then
        wait.until(presenceOfElementLocated(By.id("filmDetail")));
        assertThat(driver.findElement(By.id("film-synopsis")).getText()).isEqualTo(description);
        assertThat(driver.findElement(By.id("film-title")).getText()).isEqualTo(title);
        assertThat(driver.findElement(By.id("film-releaseYear")).getText()).isEqualTo(String.valueOf(releaseYear));
        //TODO: add id to agerequirement and check, ask michel
    }


    /**
     * 3. When a new film is added and then deleted, we expect the film to disappear from the list of films
     */
    @Test
    public void testAddAndDeleteFilm() {
    }

    /**
     * 4. When a new film is added and edited to add '- part 2' to its title, we check that the change has been applied
     */
    @Test
    public void testEditFilm() {
    }

}
