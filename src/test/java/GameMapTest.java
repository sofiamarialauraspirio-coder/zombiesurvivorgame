import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        // 1. Creiamo il nostro "Lavoratore" che sa leggere i file
        MapLoader loader = new MapLoader();
        
        // Assicurati che questo percorso sia esatto
        String percorsoFile = "src/main/resources/mappa_livello1.json";

        // 2. Facciamo caricare la mappa al loader!
        GameMap map = loader.loadMap(percorsoFile);

        // 3. Verifichiamo che la mappa esista davvero
        assertNotNull(map, "La mappa caricata non deve essere null");

        // 4. Controlliamo il tile (0,0)
        int tileInAltoASinistra = map.getTile(0, 0);
        
        // Sostituisci "1" con il numero che su Tiled rappresenta il tuo muro
        assertEquals(1, tileInAltoASinistra, "Il tile (0,0) dovrebbe essere un muro");
    }
}