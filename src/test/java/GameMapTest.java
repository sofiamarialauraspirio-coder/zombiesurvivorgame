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
        assertEquals(287, tileInAltoASinistra, "Il tile (0,0) dovrebbe essere un muro 287");
    }

    // ==========================================
    // TDD REQUIREMENT - Verifica che i muri siano ostacoli
    // ==========================================
    @Test
    public void testWallIsImpassable() {
        GameMap map = new GameMap();
        
        // 🧱 FIX: Testiamo il muro con l'ID reale (287) e il pavimento con lo ZERO (0)
        map.setTile(5, 5, 287); // Muro
        map.setTile(5, 6, 0);   // Pavimento libero

        // Assert: Il muro (287) NON deve essere calpestabile
        assertFalse(map.isWalkable(5, 5), "La logica deve registrare il muro 287 come ostacolo invalicabile");
        
        // Assert: Il pavimento vuoto (0) DEVE essere calpestabile
        assertTrue(map.isWalkable(5, 6), "La logica deve registrare lo zero come calpestabile");
    }
}