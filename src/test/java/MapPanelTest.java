import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

public class MapPanelTest {

    @Test
    public void testTilesetImageExists() {
        // Arrange
        String percorsoImmagine = "src/main/resources/tilesheet_complete.png";

        // Act
        File fileImmagine = new File(percorsoImmagine);

        // Assert: verifichiamo che il file esista davvero prima di farlo caricare al gioco!
        assertTrue(fileImmagine.exists(), "Il file del tileset DEVE esistere nella cartella resources");
    }

    @Test
    public void testMapPanelLoadsTileset() {
        // Arrange: Creiamo una mappa vuota solo per far felice il pannello
        model.GameMap dummyMap = new model.GameMap();

        // Act: Creiamo il nostro pannello (che dovrebbe caricare l'immagine nel costruttore)
        view.MapPanel panel = new view.MapPanel(dummyMap);

        // Assert: Chiediamo al pannello l'immagine. NON deve essere null!
        assertNotNull(panel.getTileset(), "Il tileset non deve essere null dopo aver creato il MapPanel");
    }

    @Test
    public void testPanelPreferredSize() {
        // Arrange
        model.GameMap map = new model.GameMap(); // Mappa 12x12
        view.MapPanel panel = new view.MapPanel(map);
        
        // Act
        java.awt.Dimension size = panel.getPreferredSize();
        
        // Assert: 12 colonne * 48 pixel = 576
        assertEquals(576, size.width, "La larghezza del pannello deve essere 576px");
        assertEquals(576, size.height, "L'altezza del pannello deve essere 576px");
    }

    // ===========================================================
    // NUOVI TEST PER LA NP-37 (Controllo esistenza immagini entità)
    // ===========================================================
    @Test
    public void testEntityImagesExist() {
        // Arrange: prepariamo i percorsi dei file appena aggiunti
        File zombieFile = new File("src/main/resources/zombie.png");
        File survivorFile = new File("src/main/resources/survivor.png");
        File keyFile = new File("src/main/resources/key.png");

        // Assert: verifichiamo che nessuno abbia cancellato o rinominato i file per sbaglio
        assertTrue(zombieFile.exists(), "Il file zombie.png DEVE esistere nella cartella resources");
        assertTrue(survivorFile.exists(), "Il file survivor.png DEVE esistere nella cartella resources");
        assertTrue(keyFile.exists(), "Il file key.png DEVE esistere nella cartella resources");
    }
}