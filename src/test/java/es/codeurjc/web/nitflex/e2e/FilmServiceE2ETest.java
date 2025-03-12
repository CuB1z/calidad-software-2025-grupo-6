package es.codeurjc.web.nitflex.e2e;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * TASK 3 : End-to-end test for FilmService
 */
@SpringBootTest
public class FilmServiceE2ETest {
    
    /**
     * 3. When a new film is added and then deleted, we expect the film to disappear from the list of films
     */
    @Test
    public void testAddAndDeleteFilm() {}

    /**
     * 4. When a new film is added and edited to add '- part 2' to its title, we check that the change has been applied
     */
    @Test
    public void testEditFilm() {}
        
}
