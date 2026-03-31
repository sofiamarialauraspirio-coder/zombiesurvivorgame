import model.GameMap;
import model.Crate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrateSpawnRulesTest {

    private GameMap map;

    @BeforeEach
    public void setUp() {
        map = new GameMap();
        // Svuotiamo eventuali casse iniziali per sicurezza
        map.getCrates().clear();
        
        // Per questo test, simuliamo una griglia 12x12 completamente vuota e camminabile
        for(int r = 0; r < 12; r++) {
            for(int c = 0; c < 12; c++) {
                map.setTile(r, c, GameMap.TILE_FLOOR);
            }
        }
    }

    @Test
    public void testMinimumSpawnDistance() {
        // Act: Forziamo la creazione di due casse consecutive
        map.spawnRandomCrate();
        map.spawnRandomCrate();

        // Assert 1: Verifichiamo che ci siano esattamente 2 casse
        assertEquals(2, map.getCrates().size(), "La mappa deve contenere esattamente 2 casse.");

        Crate c1 = map.getCrates().get(0);
        Crate c2 = map.getCrates().get(1);

        // Calcoliamo la Distanza di Manhattan: |x1 - x2| + |y1 - y2|
        int manhattanDistance = Math.abs(c1.getX() - c2.getX()) + Math.abs(c1.getY() - c2.getY());

        // Assert 2: La distanza deve essere >= 5
        assertTrue(manhattanDistance >= 5, 
            "ERRORE SPAZIALE: Le casse sono troppo vicine! Distanza rilevata: " + manhattanDistance + " (minimo richiesto: 5). " +
            "Cassa 1 in (" + c1.getX() + "," + c1.getY() + "), Cassa 2 in (" + c2.getX() + "," + c2.getY() + ")");
    }
}