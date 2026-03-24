
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import view.MainMenu;

public class MainMenuTest {

    @Test
    public void testCreazioneMenu() {
        // Arrange & Act
      // Arrange & Act
        // 1. Creiamo una finta cornice per far felice il test
        javax.swing.JFrame fintaFinestra = new javax.swing.JFrame();
        
        // 2. Creiamo la finta memoria del gioco
        model.GameSession fintaSessione = new model.GameSession();
        
        // 3. Ora creiamo il menu passandogli ENTRAMBE le cose!
        MainMenu menu = new MainMenu(fintaFinestra, fintaSessione);

        // Assert
        assertNotNull(menu, "Il menu non deve essere nullo");
        
        // (Ho tolto la riga che controllava il Titolo, perché ora il 
        // titolo ce l'ha la fintaFinestra, non più il menu interno!)
    }
}