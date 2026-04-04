import model.GameMap;
import model.MapLoader;
import model.Crate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrateSpawnTest {

    private GameMap map;

    @BeforeEach
    public void setUp() {
        // Carichiamo la mappa reale per fare un test accurato sui muri e sugli spazi
        MapLoader loader = new MapLoader();
        map = loader.loadMap("src/main/resources/mappa_livello1.json");
        
        // Puliamo eventuali casse già presenti per partire da zero
        if (map.getCrates() != null) {
            map.getCrates().clear();
        }
    }

    @Test
    public void testCrateSpawnsOnWalkableCell() {
        // Forziamo la generazione di una cassa
        map.spawnRandomCrate();

        // Verifichiamo che sia apparsa e che sia su una cella valida
        assertFalse(map.getCrates().isEmpty(), "Dovrebbe essere spawnata una cassa.");
        Crate spawnedCrate = map.getCrates().get(0);

        assertTrue(map.isWalkable(spawnedCrate.getY(), spawnedCrate.getX()), 
            "ERRORE: La cassa è spawnata su un muro o fuori mappa in (" + spawnedCrate.getX() + ", " + spawnedCrate.getY() + ")!");
    }

    @Test
    public void testCollisionAvoidance() {
        // Prendiamo le coordinate attuali del Sopravvissuto
        int sx = map.getSurvivor().getX();
        int sy = map.getSurvivor().getY();

        // Generiamo 100 casse (ignorando temporaneamente il limite di 2 per testare la statistica)
        for (int i = 0; i < 100; i++) {
            map.spawnRandomCrate();
        }

        // Nessuna di queste 100 casse deve essere finita addosso al Sopravvissuto
        for (Crate c : map.getCrates()) {
            boolean isOnSurvivor = (c.getX() == sx && c.getY() == sy);
            assertFalse(isOnSurvivor, "CRITICO: Una cassa è spawnata esattamente sopra il Sopravvissuto!");
        }
    }
}