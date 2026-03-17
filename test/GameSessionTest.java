package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.GameSession;

public class GameSessionTest {

    @Test
    public void testSalvataggioSceltaGiocatore1() {
        // Creiamo la classe che gestirà la sessione (il "cervello" della partita)
        GameSession session = new GameSession();
        
        // Simuliamo che il giocatore 1 clicchi il bottone "Zombie"
        session.setPlayer1Choice("ZOMBIE");
        
        // Verifichiamo che la memoria del gioco abbia salvato correttamente la scelta
        assertEquals("ZOMBIE", session.getPlayer1Choice());
    }
}