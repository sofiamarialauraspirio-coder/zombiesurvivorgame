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

        gameMap.setSurvivor(survivor);
        gameMap.setZombie(zombie);

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

    @Test
    public void testZombieVictoryOnCollision() {
        // Arrange: Avviamo il gioco
        turnController.startGame();
        
        // Spostiamo lo zombie artificialmente a un passo dal Sopravvissuto (che in setUp è a 2,2)
        zombie.setX(3);
        zombie.setY(2);

        // Act 1: Il Sopravvissuto passa il turno stando fermo
        turnController.confirmMove(2, 2);
        turnController.confirmBlock(2, 2);

        // Act 2: Lo Zombie si muove esattamente sulle coordinate (2, 2) del Sopravvissuto e si blocca
        turnController.confirmMove(2, 2);
        turnController.confirmBlock(2, 2);

        // Assert: Dopo la risoluzione del turno, l'arbitro deve dichiarare la vittoria dello Zombie!
        assertEquals(TurnController.GameState.ZOMBIE_VICTORY, turnController.getCurrentState(), "Lo Zombie deve vincere se finisce sulla stessa casella del Sopravvissuto!");
    }

    @Test
    public void testCrateCollectionBySurvivor() {
        // Arrange: Avviamo il gioco
        turnController.startGame();
        
        // Creiamo una cassa e la mettiamo alle coordinate (3, 2)
        model.Crate crate = new model.Crate(3, 2);
        gameMap.addCrate(crate);

        // Verifichiamo che la cassa sia effettivamente nella mappa all'inizio
        assertTrue(gameMap.getCrates().contains(crate), "La mappa deve contenere la cassa all'inizio");

        // Act: Il Sopravvissuto (che parte da 2,2) si muove sulla cassa (3,2) e si blocca
        turnController.confirmMove(3, 2);
        turnController.confirmBlock(2, 2);

        // Lo Zombie (lontano) fa mosse a caso per far finire il turno
        turnController.confirmMove(10, 9);
        turnController.confirmBlock(10, 10);

        // Assert: Dopo la risoluzione (executeResolution), la cassa NON deve più essere nella mappa!
        assertFalse(gameMap.getCrates().contains(crate), "La cassa deve essere rimossa dalla mappa dopo essere stata calpestata!");
    }

    @Test
    public void testRandomCrateSpawningLimit() {
        // Arrange: Avviamo il gioco e svuotiamo la mappa da eventuali casse di prova
        turnController.startGame();
        gameMap.getCrates().clear(); 

        // Riempiamo la mappa fino al limite massimo (2 casse)
        gameMap.addCrate(new model.Crate(1, 1));
        gameMap.addCrate(new model.Crate(1, 2));

        // Act 1: Diciamo alla mappa di provare a generare una nuova cassa casuale
        gameMap.spawnRandomCrate();

        // Assert 1: Il sistema deve BLOCCARE lo spawn perché ci sono già 2 casse!
        assertEquals(2, gameMap.getCrates().size(), "ERRORE: Non possono esserci più di 2 casse contemporaneamente!");

        // Act 2: Togliamo una cassa (simulando che un giocatore l'abbia raccolta)
        gameMap.getCrates().remove(0);
        
        // Ora proviamo di nuovo a generare una cassa casuale
        gameMap.spawnRandomCrate();

        // Assert 2: Ora la mappa deve aver accettato lo spawn, tornando a 2 casse in totale
        assertEquals(2, gameMap.getCrates().size(), "ERRORE: Deve creare una nuova cassa se siamo sotto il limite di 2!");
        
        // Assert 3: Controlliamo che la nuova cassa sia "atterrata" su una mattonella calpestabile (non dentro un muro)
        model.Crate nuovaCassa = gameMap.getCrates().get(1);
        assertTrue(gameMap.isWalkable(nuovaCassa.getY(), nuovaCassa.getX()), "ERRORE: La cassa deve spawnare sul pavimento, non sui muri!");
    }
}