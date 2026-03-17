package test.java;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MapLoaderTest {

    @Test
    public void testMapDimensions() {
        // 1. Prepariamo il "Lettore" della mappa
        MapLoader loader = new MapLoader();
        
        // 2. Facciamo finta di caricare il file che hai creato prima
        // (Assicurati che il percorso del file sia corretto)
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json");

        // 3. Verifichiamo le dimensioni! (Criterio 1 e 4)
        assertNotNull(map, "La mappa non dovrebbe essere null");
        assertEquals(12, map.getRows(), "La mappa deve avere esattamente 12 righe");
        assertEquals(12, map.getCols(), "La mappa deve avere esattamente 12 colonne");
    }
}