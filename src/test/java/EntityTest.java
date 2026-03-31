import model.Entity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    // Creiamo un'entità "finta" solo per poter testare la logica della classe padre astratta
    private class DummyEntity extends Entity {
        public DummyEntity(int x, int y) {
            super(x, y);
        }
    }

    @Test
    public void testInitialState() {
        // Arrange & Act
        Entity entity = new DummyEntity(5, 5);
        
        // Assert: verifichiamo che appena creato, non abbia mosse in canna o bonus strani
        assertFalse(entity.hasPlannedMove(), "All'inizio non deve esserci nessuna mossa programmata");
        assertFalse(entity.hasDoubleMoveBonus(), "All'inizio non deve esserci il bonus del doppio passo");
    }

    @Test
    public void testPlanMoveDoesNotChangeRealCoordinates() {
        // Arrange: Il personaggio parte dalla casella (2, 2)
        Entity entity = new DummyEntity(2, 2);
        
        // Act: Il giocatore sposta il cursore su (3, 2) e preme INVIO
        entity.planMove(3, 2);
        
        // Assert: Verifichiamo la memoria segreta (usando il nuovo sistema a Point)
        assertTrue(entity.hasPlannedMove(), "La mossa deve risultare programmata");
        assertEquals(3, entity.getPlannedMove().x, "La X programmata deve essere 3");
        assertEquals(2, entity.getPlannedMove().y, "La Y programmata deve essere 2");
        
        // CONTROLLO CRITICO PER LA NP-19 (Silent Planning): 
        // Le vere coordinate NON devono essere cambiate! Lo sprite deve rimanere fermo.
        assertEquals(2, entity.getX(), "La X reale DEVE rimanere 2 finché non si esegue la mossa");
        assertEquals(2, entity.getY(), "La Y reale DEVE rimanere 2 finché non si esegue la mossa");
    }

    @Test
    public void testExecutePlannedMove() {
        // Arrange
        Entity entity = new DummyEntity(0, 0);
        entity.planMove(1, 1); // Pianifica la mossa
        
        // Act: Il controller dichiara la fine del turno globale ed esegue la mossa!
        entity.executePlannedMove();
        
        // Assert: Ora lo sprite deve essersi "teletrasportato" fisicamente sulla nuova casella
        assertEquals(1, entity.getX(), "Ora la X reale deve essersi aggiornata a 1");
        assertEquals(1, entity.getY(), "Ora la Y reale deve essersi aggiornata a 1");
        
        // La memoria si deve essere svuotata, pronta per il turno successivo
        assertFalse(entity.hasPlannedMove(), "La mossa programmata deve essersi resettata dopo l'esecuzione");
    }
    
    @Test
    public void testDoubleMoveBonus() {
        // Arrange & Act
        Entity entity = new DummyEntity(0, 0);
        entity.setDoubleMoveBonus(true);
        
        // Assert
        assertTrue(entity.hasDoubleMoveBonus(), "Il bonus deve risultare attivo dopo averlo impostato");
    }
}