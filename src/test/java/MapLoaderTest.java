import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.GameMap;
import model.MapLoader;
import model.Key; // <-- Nuova riga aggiunta per far riconoscere la Chiave!

public class MapLoaderTest {

    @Test
    public void testMapDimensions() {
        // 1. Prepariamo il "Lettore" della mappa
        MapLoader loader = new MapLoader();

        // 2. Facciamo finta di caricare il file che hai creato prima
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json");

        // 3. Verifichiamo le dimensioni!
        assertNotNull(map, "La mappa non dovrebbe essere null");
        assertEquals(12, map.getRows(), "La mappa deve avere esattamente 12 righe");
        assertEquals(12, map.getCols(), "La mappa deve avere esattamente 12 colonne");
    }

    // =========================================================
    // NUOVO TEST: Verifica che la chiave venga letta dal JSON
    // =========================================================
    @Test
    public void testKeyGenerationDaTiledObjectLayer() {
        MapLoader loader = new MapLoader();
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json"); 
        
        // 1. Controlliamo che la chiave sia stata caricata con successo dal JSON!
        assertNotNull(map.getKey(), "Errore: La chiave non è stata caricata dal file!");
        
        // 2. Verifichiamo le coordinate esatte (i numeri interi che abbiamo preso dal JSON)
        assertEquals(355, map.getKey().getX(), "La coordinata X della chiave non coincide");
        assertEquals(352, map.getKey().getY(), "La coordinata Y della chiave non coincide");
    }
}