import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import view.MainMenu;

public class MainMenuTest {

    @Test
    public void testCreazioneMenu() {
        // Arrange
        javax.swing.JFrame fintaFinestra = new javax.swing.JFrame();
        model.GameSession fintaSessione = new model.GameSession();
        
        // Act
        MainMenu menu = new MainMenu(fintaFinestra, fintaSessione);

        // Assert
        assertNotNull(menu, "Il menu non deve essere nullo");
        assertNotNull(menu.getBtnGioca(), "Il bottone Gioca deve essere presente");
    }

    @Test
    public void testMenuHasRulesButton() {
        // Verifica dei Criteri di Accettazione della STORY 2 (NP-9)
        
        // Arrange
        javax.swing.JFrame fintaFinestra = new javax.swing.JFrame();
        model.GameSession fintaSessione = new model.GameSession();
        
        // Act
        MainMenu menu = new MainMenu(fintaFinestra, fintaSessione);

        // Assert: Dobbiamo essere sicuri che ci sia un modo per il giocatore di leggere le regole!
        assertNotNull(menu.getBtnRegole(), "Il menu DEVE contenere il pulsante per le Regole come richiesto dalla NP-9!");
        org.junit.jupiter.api.Assertions.assertEquals("Regole", menu.getBtnRegole().getText(), "Il testo del pulsante deve essere 'Regole'");
    }
}