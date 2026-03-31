import model.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameSessionTest {

    private GameSession session;

    @BeforeEach
    public void setUp() {
        session = new GameSession();
    }

    @Test
    public void testRandomCoinToss() {
        // Act: Lanciamo la moneta
        int startingPlayer = session.tossCoin();

        // Assert: Il risultato deve essere per forza il Giocatore 1 o il Giocatore 2
        assertTrue(startingPlayer == 1 || startingPlayer == 2, 
            "ERRORE: La moneta deve restituire 1 o 2, ha restituito: " + startingPlayer);
    }

    @Test
    public void testAutomaticRoleCoupling_P1ChoosesSurvivor() {
        // Arrange: Simuliamo che la moneta l'abbia vinta il P1
        session.setChoosingPlayer(1);

        // Act: Il P1 sceglie il Sopravvissuto
        session.assignRole("SURVIVOR");

        // Assert: Il sistema deve assegnare i ruoli in automatico (One-Click & Coupling)
        assertEquals("SURVIVOR", session.getPlayer1Role(), "Il P1 deve essere il Sopravvissuto");
        assertEquals("ZOMBIE", session.getPlayer2Role(), "Il P2 deve essere automaticamente lo Zombie");
    }

    @Test
    public void testAutomaticRoleCoupling_P2ChoosesZombie() {
        // Arrange: Simuliamo che la moneta l'abbia vinta il P2
        session.setChoosingPlayer(2);

        // Act: Il P2 sceglie lo Zombie
        session.assignRole("ZOMBIE");

        // Assert: Il sistema deve assegnare i ruoli in automatico
        assertEquals("ZOMBIE", session.getPlayer2Role(), "Il P2 deve essere lo Zombie");
        assertEquals("SURVIVOR", session.getPlayer1Role(), "Il P1 deve essere automaticamente il Sopravvissuto");
    }
}