import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import model.GameMap;
import model.MapLoader; 
import model.Survivor; 

public class MapLoaderTest {

    @Test
    public void testMapDimensions() {
        MapLoader loader = new MapLoader();

        // Facciamo finta di caricare il file che abbiamo creato
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json");

        // Verifichiamo le dimensioni!
        assertNotNull(map, "La mappa non dovrebbe essere null");
        assertEquals(12, map.getRows(), "La mappa deve avere esattamente 12 righe");
        assertEquals(12, map.getCols(), "La mappa deve avere esattamente 12 colonne");
    }

    @Test
    public void testKeyGenerationDaTiledObjectLayer() {
        MapLoader loader = new MapLoader();
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json"); 
        
        // Controlliamo che la chiave sia stata caricata con successo dal JSON!
        assertNotNull(map.getKey(), "Errore: La chiave non è stata caricata dal file!");
        
        // Verifichiamo le coordinate esatte (i numeri interi che abbiamo preso dal JSON)
        assertEquals(355, map.getKey().getX(), "La coordinata X della chiave non coincide");
        assertEquals(352, map.getKey().getY(), "La coordinata Y della chiave non coincide");
    }

    @Test
    public void testKeyCollection() {
        MapLoader loader = new MapLoader();
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json"); 
        Survivor survivor = new Survivor(0, 0); 

        // Verifichiamo la situazione iniziale
        assertNotNull(map.getKey(), "All'inizio la chiave deve esserci sulla mappa");
        assertFalse(survivor.hasKey(), "All'inizio il sopravvissuto non deve avere la chiave");

        // SIMULIAMO LA RACCOLTA: Il sopravvissuto prende la chiave e sparisce dalla mappa
        if (map.getKey() != null) {
            survivor.collectKey();
            map.setKey(null);
        }

        // Verifichiamo il risultato finale
        assertNull(map.getKey(), "Dopo la raccolta, la chiave non deve più essere sulla mappa");
        assertTrue(survivor.hasKey(), "Dopo la raccolta, il sopravvissuto deve avere la chiave in tasca");
        
        System.out.println("✅ Test Raccolta Chiave superato con successo!");
    }
}