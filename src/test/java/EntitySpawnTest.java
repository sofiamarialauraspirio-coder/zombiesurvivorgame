import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.MapLoader;
import model.GameMap;

public class EntitySpawnTest {

    @Test
    public void testCharacterSpawnsCorrectlyFromMap() {
        // 1. Carichiamo la mappa
        MapLoader loader = new MapLoader();
        GameMap map = loader.loadMap("src/main/resources/mappa_livello1.json");

        // 2. Testiamo lo ZOMBIE
        // Verifichiamo prima che lo zombie esista (che non sia null)
        assertNotNull(map.getZombie(), "Errore: Lo Zombie non è stato creato dal MapLoader!");
        
        // Verifichiamo le coordinate sulla griglia (Pixel 609/64 = 9, Pixel 548/64 = 8)
        assertEquals(9, map.getZombie().getX(), "La X dello Zombie non coincide con Tiled!");
        assertEquals(8, map.getZombie().getY(), "La Y dello Zombie non coincide con Tiled!");

        // 3. Testiamo il SOPRAVVISSUTO
        // Verifichiamo prima che il sopravvissuto esista
        assertNotNull(map.getSurvivor(), "Errore: Il Sopravvissuto non è stato creato dal MapLoader!");
        
        // Verifichiamo le coordinate sulla griglia (Pixel 224/64 = 3, Pixel 669/64 = 10)
        assertEquals(3, map.getSurvivor().getX(), "La X del Sopravvissuto è sbagliata!");
        assertEquals(10, map.getSurvivor().getY(), "La Y del Sopravvissuto è sbagliata!");
    }
}