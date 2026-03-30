import controller.TurnController;
import model.GameManager;
import model.GameMap;
import model.Survivor;
import model.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TurnControllerTest {
    private GameMap gameMap;
    private GameManager gm;
    private TurnController turnController;
    private Survivor survivor;
    private Zombie zombie;

    @BeforeEach
    public void setUp() {
        // Prepariamo il campo prima di ogni test
        gameMap = new GameMap();
        survivor = new Survivor(2, 2); // Lo mettiamo al centro (2,2)
        zombie = new Zombie(10, 10);
        gm = new GameManager(survivor, zombie);
        turnController = new TurnController(gm, gameMap);
    }

    @Test
    public void testInitialStateIsMenu() {
        // Verifica la "State Integrity": il gioco deve partire dal MENU
        assertEquals(TurnController.GameState.MENU, turnController.getCurrentState(), "All'avvio lo stato deve essere MENU");
    }

    @Test
    public void testInputProtectionInMenu() {
        // Arrange: Assicuriamoci che la casella sia libera
        gameMap.setTile(0, 1, GameMap.TILE_FLOOR);

        // Act: Il giocatore prova a muoversi mentre è nel MENU
        turnController.confirmMove(1, 0);

        // Assert: Input Protection! La mossa deve essere scartata (usiamo assertNull!)
        assertNull(survivor.getPlannedMove(), "Non deve essere possibile muoversi dal MENU");
        assertEquals(TurnController.GameState.MENU, turnController.getCurrentState(), "Lo stato non deve cambiare");
    }

    @Test
    public void testWallRejection_NP23_InGame() {
        // Arrange: Avviamo il gioco per passare a P1_CHOICE
        turnController.startGame();
        gameMap.setTile(0, 1, GameMap.TILE_WALL); // Piazziamo il muro

        // Act: P1 prova ad andare sul muro
        turnController.confirmMove(1, 0);

        // Assert: La mossa fallisce e lo stato NON avanza
        assertNull(survivor.getPlannedMove(), "La mossa NON deve essere salvata");
        assertEquals(TurnController.GameState.P1_CHOICE, turnController.getCurrentState(), "Dobbiamo rimanere in P1_CHOICE");
    }

    @Test
    public void testStrictSequentialFlow_RequiresMoveAndBlock() {
        // Arrange: Avviamo il gioco
        turnController.startGame();
        gameMap.setTile(0, 1, GameMap.TILE_FLOOR);

        // Act 1: P1 conferma SOLO la mossa
        turnController.confirmMove(1, 0);

        // Assert 1: Il turno NON deve ancora passare a P2 (usiamo assertNotNull!)
        assertNotNull(survivor.getPlannedMove(), "La mossa deve essere salvata");
        assertEquals(TurnController.GameState.P1_CHOICE, turnController.getCurrentState(), "Serve anche il Block per passare il turno!");

        // Act 2: P1 conferma anche l'azione (Block)
        turnController.confirmBlock(0, 0);

        // Assert 2: Solo ora si passa al turno dello Zombie!
        assertEquals(TurnController.GameState.P2_CHOICE, turnController.getCurrentState(), "Ora tocca a P2 (Zombie)");
    }
}