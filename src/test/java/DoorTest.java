import model.Door;
import model.GameMap;
import model.Survivor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DoorTest {

    @Test
    public void testDoorBlocksPassageWhenClosed() {
        // 1. Prepariamo l'ambiente (Arrange)
        GameMap map = new GameMap();
        
        // Creiamo una porta finta per il test (riga 5, colonne 5 e 6)
        Door door = new Door(320, 320, 5, 6, 5);
        map.setDoor(door);

        // 3. Verifichiamo i 2 blocchi della collisione (Assert)
        assertFalse(map.isWalkable(5, 5), "Il blocco sinistro della porta deve bloccare il passaggio!");
        assertFalse(map.isWalkable(5, 6), "Il blocco destro della porta deve bloccare il passaggio!");
        
        // Verifichiamo che invece un punto a caso del pavimento sia libero
        assertTrue(map.isWalkable(1, 1), "Il pavimento normale deve essere calpestabile!");
    }

    @Test
    public void testDoorAllowsPassageWhenOpened() {
        // 1. Prepariamo l'ambiente
        GameMap map = new GameMap();
        Door door = new Door(320, 320, 5, 6, 5);
        map.setDoor(door);

        // 2. NUOVA LOGICA: Creiamo un sopravvissuto e gli diamo la chiave!
        Survivor survivor = new Survivor(0, 0);
        survivor.collectKey(); 
        map.setSurvivor(survivor); // Lo inseriamo nella mappa

        // 3. Verifichiamo che ora la porta sia attraversabile (Assert)
        assertTrue(map.isWalkable(5, 5), "Con la chiave, il blocco sinistro deve essere calpestabile!");
        assertTrue(map.isWalkable(5, 6), "Con la chiave, il blocco destro deve essere calpestabile!");
    }
}