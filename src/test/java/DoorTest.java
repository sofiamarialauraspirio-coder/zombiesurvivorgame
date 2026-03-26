import model.Door;
import model.GameMap;
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

        // 2. Verifichiamo che nasca chiusa
        assertFalse(door.isOpen(), "La porta deve nascere chiusa di default!");

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

        // 2. Simuliamo il giocatore che usa la chiave e APRE la porta (Act)
        door.setOpen(true);

        // 3. Verifichiamo che ora la porta sia attraversabile (Assert)
        assertTrue(door.isOpen(), "La porta ora dovrebbe essere aperta!");
        assertTrue(map.isWalkable(5, 5), "Una volta aperta, il blocco sinistro deve essere calpestabile!");
        assertTrue(map.isWalkable(5, 6), "Una volta aperta, il blocco destro deve essere calpestabile!");
    }
}