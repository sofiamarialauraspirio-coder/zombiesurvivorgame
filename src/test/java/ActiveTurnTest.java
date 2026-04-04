import controller.TurnController;
import controller.TurnController.GameState;
import model.GameManager;
import model.GameMap;
import model.MapLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ActiveTurnTest {

    private TurnController turnController;
    private GameManager gameManager;
    private GameMap map;

    @BeforeEach
    public void setUp() {
        MapLoader loader = new MapLoader();
        map = loader.loadMap("src/main/resources/mappa_livello1.json");

        gameManager = new GameManager(map.getSurvivor(), map.getZombie());
        turnController = new TurnController(gameManager, map);
    }

    @Test
    public void testInitialStateIsMenu() {
        // All'inizio, prima di premere invio, deve esserci lo stato MENU
        assertEquals(GameState.MENU, turnController.getCurrentState(), 
            "Il gioco deve partire in stato MENU per mostrare 'Premi INVIO per Iniziare!'");
    }

    @Test
    public void testStartGameTransitionsToP1Choice() {
        // Simuliamo la pressione del tasto Invio (che chiamerà startGame)
        turnController.startGame();

        // Lo stato deve cambiare al turno del Sopravvissuto
        assertEquals(GameState.P1_CHOICE, turnController.getCurrentState(), 
            "Dopo l'avvio, il testo deve indicare che è il Turno di P1 (Sopravvissuto)");
    }

    @Test
    public void testP1ConfirmTransitionsToP2Choice() {
        // Avviamo la partita
        turnController.startGame();

        // Simuliamo P1 che sceglie una mossa (sopra di lui) e conferma il blocco
        int startX = map.getSurvivor().getX();
        int startY = map.getSurvivor().getY();
        
        turnController.confirmMove(startX, startY - 1); // Mossa simulata
        turnController.confirmBlock(0, 0); // Blocco confermato

        // Lo stato deve cambiare al turno dello Zombie
        assertEquals(GameState.P2_CHOICE, turnController.getCurrentState(), 
            "Quando P1 finisce, il testo deve aggiornarsi mostrando il Turno di P2 (Zombie)");
    }
}