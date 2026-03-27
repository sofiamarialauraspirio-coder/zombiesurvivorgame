import controller.TurnController;

import model.GameManager;
import model.Survivor;
import model.Zombie;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TurnControllerTest {

    @Test
    public void testInitialTurnIsSurvivor() {
        // Arrange
        Survivor survivor = new Survivor(0, 0);
        Zombie zombie = new Zombie(10, 10);
        GameManager gm = new GameManager(survivor, zombie);
        TurnController turnController = new TurnController(gm);

        // Assert: Il gioco deve sempre iniziare con il turno del Sopravvissuto
        assertEquals(TurnController.PlayerTurn.SURVIVOR, turnController.getCurrentTurn(), "Il primo turno deve essere del Sopravvissuto");
    }

    @Test
    public void testSurvivorPlansMoveAndPassesTurn() {
        // Arrange
        Survivor survivor = new Survivor(0, 0);
        Zombie zombie = new Zombie(10, 10);
        GameManager gm = new GameManager(survivor, zombie);
        TurnController turnController = new TurnController(gm);

        // Act: Il Sopravvissuto sceglie di muoversi in (1, 0) premendo INVIO
        turnController.confirmMove(1, 0);

        // Assert per la NP-19 (Silent Planning)
        assertTrue(survivor.hasPlannedMove(), "Il Sopravvissuto deve aver memorizzato la mossa");
        assertEquals(0, survivor.getX(), "La X reale del Sopravvissuto NON deve ancora cambiare!");
        assertEquals(TurnController.PlayerTurn.ZOMBIE, turnController.getCurrentTurn(), "Il turno deve passare allo Zombie");
    }

    @Test
    public void testGlobalTurnResolution() {
        // Arrange
        Survivor survivor = new Survivor(0, 0);
        Zombie zombie = new Zombie(10, 10);
        GameManager gm = new GameManager(survivor, zombie);
        TurnController turnController = new TurnController(gm);

        // Act 1: Turno del Sopravvissuto
        turnController.confirmMove(1, 0); // Il Sopravvissuto va a destra
        
        // Act 2: Turno dello Zombie
        turnController.confirmMove(9, 10); // Lo Zombie va a sinistra

        // Assert per la NP-19 (Esecuzione a fine turno globale)
        assertEquals(1, survivor.getX(), "Ora che il turno è finito, il Sopravvissuto deve essersi mosso in X=1");
        assertEquals(9, zombie.getX(), "Ora che il turno è finito, lo Zombie deve essersi mosso in X=9");
        assertFalse(survivor.hasPlannedMove(), "La memoria del Sopravvissuto deve essersi svuotata");
        assertEquals(TurnController.PlayerTurn.SURVIVOR, turnController.getCurrentTurn(), "Il nuovo giro deve riniziare dal Sopravvissuto");
    }
}