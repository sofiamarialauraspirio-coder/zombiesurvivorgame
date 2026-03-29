import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.GameMap; 
import model.MapLoader;

public class GameMapTest {

    @Test
    public void testMapHasExactly12x12Size() {
        GameMap map = new GameMap();
        assertEquals(12, map.getRows(), "Il numero di righe deve essere 12");
        assertEquals(12, map.getCols(), "Il numero di colonne deve essere 12");
    }

    @Test
    public void testLoadMapFromJson() {
        MapLoader loader = new MapLoader();
        String percorsoFile = "src/main/resources/mappa_livello1.json";
        
        GameMap map = loader.loadMap(percorsoFile);
        assertNotNull(map, "La mappa caricata non deve essere null");

        int tileInAltoASinistra = map.getTile(0, 0);
        assertEquals(287, tileInAltoASinistra, "Il tile (0,0) dovrebbe essere un muro");
    }

    // ==========================================
    // TDD REQUIREMENT - Verifica che i muri siano ostacoli
    // ==========================================
    @Test
    public void testWallIsImpassable() {
        GameMap map = new GameMap();
        
        // Usiamo l'ID corretto del muro (ora impostato a 287 in GameMap)
        map.setTile(5, 5, GameMap.TILE_WALL);
        // Usiamo l'ID del pavimento libero
        map.setTile(5, 6, GameMap.TILE_FLOOR);

        // Assert: Il muro NON deve essere calpestabile
        assertFalse(map.isWalkable(5, 5), "La logica deve registrare il muro come ostacolo invalicabile");
        
        // Assert: Il pavimento DEVE essere calpestabile
        assertTrue(map.isWalkable(5, 6), "La logica deve registrare il pavimento come calpestabile");
    }
}