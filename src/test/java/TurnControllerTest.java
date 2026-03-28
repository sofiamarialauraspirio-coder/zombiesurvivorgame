import controller.TurnController;
import model.GameManager;
import model.GameMap;
import model.Survivor;
import model.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Point;
import static org.junit.jupiter.api.Assertions.*;

public class TurnControllerTest {
    private GameMap gameMap;
    private GameManager gm;
    private TurnController turnController;
    private Survivor survivor;
    private Zombie zombie;

    @BeforeEach
    public void setUp() {
        gameMap = new GameMap();
        survivor = new Survivor(2, 2); // Lo mettiamo al centro (2,2) per testare le adiacenze
        zombie = new Zombie(10, 10);
        gm = new GameManager(survivor, zombie);
        turnController = new TurnController(gm, gameMap);
        
        // Rendiamo tutte le caselle adiacenti pavimenti calpestabili
        gameMap.setTile(1, 2, GameMap.TILE_FLOOR); // Su
        gameMap.setTile(3, 2, GameMap.TILE_FLOOR); // Giù
        gameMap.setTile(2, 1, GameMap.TILE_FLOOR); // Sinistra
        gameMap.setTile(2, 3, GameMap.TILE_FLOOR); // Destra
        
        turnController.startGame(); // Entriamo in P1_CHOICE
    }

    @Test
    public void test1_PositivePlanning() {
        // Arrange
        Point validMove = new Point(3, 2); // Si muove a Destra
        Point validBlock = new Point(1, 2); // Blocca a Sinistra

        // Act
        boolean result = turnController.setPlannedActions(validMove, validBlock);

        // Assert
        assertTrue(result, "Il metodo deve restituire true per scelte valide");
        assertTrue(survivor.hasPlannedMove(), "La mossa deve essere salvata");
        assertTrue(survivor.hasPlannedBlock(), "Il blocco deve essere salvato");
        assertEquals(TurnController.GameState.P2_CHOICE, turnController.getCurrentState(), "Il turno deve passare a P2");
    }

    @Test
    public void test2_NegativeWall() {
        // Arrange
        gameMap.setTile(2, 3, GameMap.TILE_WALL); // Mettiamo un muro a destra
        Point wallMove = new Point(3, 2); // Prova ad andare sul muro
        Point validBlock = new Point(1, 2);

        // Act
        boolean result = turnController.setPlannedActions(wallMove, validBlock);

        // Assert
        assertFalse(result, "Il sistema deve rifiutare la pianificazione se il target è un muro");
        assertFalse(survivor.hasPlannedMove());
        assertEquals(TurnController.GameState.P1_CHOICE, turnController.getCurrentState());
    }

    @Test
    public void test3_NegativeOverlap() {
        // Arrange
        Point move = new Point(3, 2); 
        Point block = new Point(3, 2); // Stessa identica cella!

        // Act
        boolean result = turnController.setPlannedActions(move, block);

        // Assert
        assertFalse(result, "Il sistema deve rifiutare se Move e Block coincidono");
    }

    @Test
    public void test4_StatePersistence() {
        // Arrange
        Point validMove = new Point(3, 2); 
        Point validBlock = new Point(1, 2);

        // Act
        turnController.setPlannedActions(validMove, validBlock);

        // Assert
        assertEquals(2, survivor.getX(), "La X reale non deve cambiare");
        assertEquals(2, survivor.getY(), "La Y reale non deve cambiare");
        assertEquals(new Point(3, 2), survivor.getPlannedMove(), "Le coordinate future sono salvate separatamente");
    }
}