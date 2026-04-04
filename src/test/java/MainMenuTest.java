import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import view.MainMenu;

public class MainMenuTest {

    @Test
    public void testCreazioneMenu() {
        javax.swing.JFrame fintaFinestra = new javax.swing.JFrame();
        model.GameSession fintaSessione = new model.GameSession();
        
        MainMenu menu = new MainMenu(fintaFinestra, fintaSessione);

        assertNotNull(menu, "Il menu non deve essere nullo");
        assertNotNull(menu.getBtnGioca(), "Il bottone Gioca deve essere presente");
    }

    @Test
    public void testMenuHasRulesButton() {
        
        javax.swing.JFrame fintaFinestra = new javax.swing.JFrame();
        model.GameSession fintaSessione = new model.GameSession();
        
        MainMenu menu = new MainMenu(fintaFinestra, fintaSessione);

        // Dobbiamo essere sicuri che ci sia un modo per il giocatore di leggere le regole!
        assertNotNull(menu.getBtnRegole(), "Il menu DEVE contenere il pulsante per le Regole come richiesto dalla NP-9!");
        org.junit.jupiter.api.Assertions.assertEquals("Regole", menu.getBtnRegole().getText(), "Il testo del pulsante deve essere 'Regole'");
    }
}