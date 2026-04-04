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
        gameMap = new GameMap();
        survivor = new Survivor(2, 2); 
        zombie = new Zombie(10, 10);

        gameMap.setSurvivor(survivor);
        gameMap.setZombie(zombie);

        gm = new GameManager(survivor, zombie);
        turnController = new TurnController(gm, gameMap);
    }

    @Test
    public void testInitialStateIsMenu() {
        assertEquals(TurnController.GameState.MENU, turnController.getCurrentState(), "All'avvio lo stato deve essere MENU");
    }

    @Test
    public void testInputProtectionInMenu() {
        gameMap.setTile(0, 1, 0);
        turnController.confirmMove(1, 0);
        assertNull(survivor.getPlannedMove(), "Non deve essere possibile muoversi dal MENU");
        assertEquals(TurnController.GameState.MENU, turnController.getCurrentState(), "Lo stato non deve cambiare");
    }

    @Test
    public void testWallRejection_NP23_InGame() {
        turnController.startGame();
        gameMap.setTile(0, 1, 287); 

        turnController.confirmMove(1, 0);

        assertNull(survivor.getPlannedMove(), "La mossa NON deve essere salvata");
        assertEquals(TurnController.GameState.P1_CHOICE, turnController.getCurrentState(), "Dobbiamo rimanere in P1_CHOICE");
    }

    @Test
    public void testNormalTurnSkipsBlockPhase() {
        // Avviamo il gioco
        turnController.startGame();
        gameMap.setTile(0, 1, 0); 

        // P1 conferma SOLO la mossa
        turnController.confirmMove(1, 0);

        // Il turno DEVE passare a P2 immediatamente perché non ci sono blocchi di base!
        assertNotNull(survivor.getPlannedMove(), "La mossa deve essere salvata");
        assertEquals(TurnController.GameState.P2_CHOICE, turnController.getCurrentState(), "Il turno deve passare automaticamente a P2!");
    }

    @Test
    public void testZombieVictoryOnCollision() {
        turnController.startGame();
        zombie.setX(3);
        zombie.setY(2);

        // Sopravvissuto scappa in 2,1 (salta il blocco in automatico)
        turnController.confirmMove(2, 1);

        // Lo Zombie salta addosso in 2,1 
        turnController.confirmMove(2, 1);

        assertEquals(TurnController.GameState.ZOMBIE_VICTORY, turnController.getCurrentState(), "Lo Zombie deve vincere!");
    }

    @Test
    public void testCrateCollectionBySurvivor() {
        turnController.startGame();
        model.Crate crate = new model.Crate(3, 2);
        gameMap.addCrate(crate);

        assertTrue(gameMap.getCrates().contains(crate));

        // Mossa S e Mossa Z (senza blocchi)
        turnController.confirmMove(3, 2);
        turnController.confirmMove(10, 9);

        assertFalse(gameMap.getCrates().contains(crate), "La cassa deve essere rimossa dalla mappa!");
    }

    @Test
    public void testCleanStateReset() {
        turnController.startGame(); 
        turnController.confirmMove(5, 5);  
        turnController.confirmMove(6, 6);  
        
        survivor.collectKey();
        gameMap.addCrate(new model.Crate(1, 1));
        turnController.changeState(TurnController.GameState.SURVIVOR_VICTORY);

        turnController.resetGame();

        assertNull(survivor.getPlannedMove());
        assertTrue(survivor.getPlannedBlocks().isEmpty()); 
        assertNull(zombie.getPlannedMove());
        assertFalse(survivor.hasKey());
        assertTrue(gameMap.getCrates().isEmpty());
        assertEquals(0, survivor.getNumeroBlocchiPossibili(), "I blocchi possibili devono resettarsi a 0!"); 
        assertEquals(TurnController.GameState.MENU, turnController.getCurrentState());
    }

    @Test
    public void testBonusRandomAssignment_IncludesAll4Outcomes() {
        int stopOpponentCount = 0;
        int doubleMovementCount = 0;
        int doubleBlockCount = 0;
        int selfFreezeCount = 0; // Aggiungiamo il contatore per la Tagliola
        int numTrials = 400;     // Alziamo a 400 i tentativi

        for (int i = 0; i < numTrials; i++) {
            gameMap.getCrates().clear();
            model.Crate crate = new model.Crate(2, 2); 
            gameMap.addCrate(crate);
            survivor.setDoubleMoveBonus(false);
            survivor.setNumeroBlocchiPossibili(1); // Mettiamo 1 come concordato per la trappola invisibile
            zombie.setCanMove(true);
            survivor.setCanMove(true); // Ci assicuriamo che non sia già congelato
            
            turnController.changeState(TurnController.GameState.P2_CHOICE); 
            turnController.confirmMove(10, 10);
            turnController.confirmBlock(10, 10); 

            if (survivor.hasDoubleMoveBonus()) doubleMovementCount++;
            else if (!zombie.canMove()) stopOpponentCount++;
            else if (survivor.getNumeroBlocchiPossibili() == 2) doubleBlockCount++;
            else if (!survivor.canMove()) selfFreezeCount++; // Se il Sopravvissuto è congelato, è uscita la Tagliola!
        }

        assertTrue(doubleMovementCount > 0, "Double Movement mancante");
        assertTrue(stopOpponentCount > 0, "Stop Opponent mancante");
        assertTrue(doubleBlockCount > 0, "Double Block mancante");
        assertTrue(selfFreezeCount > 0, "Tagliola (Self-Freeze) mancante"); // Verifica finale
    }

    @Test
    public void testDoubleMovementReset_AfterTurn() {
        survivor.setDoubleMoveBonus(true);
        turnController.startGame(); 
        
        // Il Sopravvissuto si muove e poi DEVE confermare il blocco (o finire la sua fase)
        turnController.confirmMove(3, 2); 
        turnController.forceFinishBlock(); // Chiude la fase del P1
        
        // Lo Zombie si muove e chiude la sua fase
        turnController.confirmMove(10, 9);
        turnController.forceFinishBlock(); // Chiude la fase del P2 e innesca la RESOLUTION
        
        assertFalse(survivor.hasDoubleMoveBonus(), "Il bonus di Double Movement deve svanire alla fine del turno!");
    }
}