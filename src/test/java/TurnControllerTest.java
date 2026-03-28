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
        survivor = new Survivor(0, 0);
        zombie = new Zombie(10, 10);
        gm = new GameManager(survivor, zombie);
        turnController = new TurnController(gm, gameMap);
    }

    @Test
    public void testWallRejection_NP23() {
        // Arrange: Mettiamo un muro (ID 2) alle coordinate (X=1, Y=0)
        gameMap.setTile(0, 1, GameMap.TILE_WALL);

        // Act: Il Sopravvissuto prova ad andare sul muro
        turnController.confirmMove(1, 0);

        // Assert: Verifica la NP-23
        assertFalse(survivor.hasPlannedMove(), "La mossa NON deve essere salvata (Input Rejection)");
        assertEquals(TurnController.PlayerTurn.SURVIVOR, turnController.getCurrentTurn(), "Il turno NON deve passare allo Zombie (State Persistence)");
    }

    @Test
    public void testValidMovePassesTurn() {
        // Arrange: Assicuriamoci che la casella sia pavimento (ID 1)
        gameMap.setTile(0, 1, GameMap.TILE_FLOOR);

        // Act: Il Sopravvissuto prenota una casella valida
        turnController.confirmMove(1, 0);

        // Assert
        assertTrue(survivor.hasPlannedMove(), "La mossa deve essere salvata");
        assertEquals(TurnController.PlayerTurn.ZOMBIE, turnController.getCurrentTurn(), "Il turno deve passare allo Zombie");
    }
}