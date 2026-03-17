package test.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import main.java.view.MainMenu;

public class MainMenuTest {

    @Test
    public void testCreazioneMenu() {
        // Arrange & Act
        MainMenu menu = new MainMenu();

        // Assert
        assertNotNull(menu, "Il menu non deve essere nullo");
        assertEquals("Zombie Survivor", menu.getTitle(), "Il titolo della finestra deve essere corretto");
    }
}