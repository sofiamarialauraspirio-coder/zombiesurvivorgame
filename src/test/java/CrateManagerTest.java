import model.CrateManager;
import model.Crate; // Supponendo che avremo un oggetto Cassa
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CrateManagerTest {

    private CrateManager crateManager;

    @BeforeEach
    public void setUp() {
        // Inizializziamo il manager prima di ogni test
        crateManager = new CrateManager();
    }

    @Test
    public void testMaxCratesLimit() {
        // Forziamo lo spawn di 2 casse
        crateManager.spawnCrate(0, 0); // Cassa 1
        crateManager.spawnCrate(1, 1); // Cassa 2

        // Tentiamo di spawnare una TERZA cassa
        boolean isThirdSpawned = crateManager.spawnCrate(2, 2);

        // Verifichiamo che la terza NON sia spawnata e il limite sia rimasto 2
        assertFalse(isThirdSpawned, "La terza cassa NON dovrebbe spawnare!");
        assertEquals(2, crateManager.getActiveCrates().size(), "Ci devono essere esattamente 2 casse attive!");
    }
    
    @Test
    public void testDynamicReset() {
        // Riempiamo la mappa con 2 casse
        crateManager.spawnCrate(0, 0);
        crateManager.spawnCrate(1, 1);
        
        // Simuliamo che un giocatore raccolga la prima cassa
        Crate collectedCrate = crateManager.getActiveCrates().get(0);
        crateManager.removeCrate(collectedCrate);
        
        // Ora tentiamo di spawnare di nuovo
        boolean isRespawned = crateManager.spawnCrate(3, 3);
        
        // Questa volta deve funzionare perché abbiamo liberato uno slot!
        assertTrue(isRespawned, "La cassa DEVE spawnare perché uno slot si è liberato!");
        assertEquals(2, crateManager.getActiveCrates().size(), "Il numero totale deve essere tornato a 2.");
    }
}