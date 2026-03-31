import model.Survivor;
import model.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BonusStopTest {

    private Survivor survivor;
    private Zombie zombie;

    @BeforeEach
    public void setUp() {
        survivor = new Survivor(0, 0);
        zombie = new Zombie(5, 5);
    }

    @Test
    public void testInitialCanMoveState() {
        // Di base, i personaggi devono potersi muovere
        assertTrue(survivor.canMove(), "Il Sopravvissuto dovrebbe potersi muovere all'inizio.");
        assertTrue(zombie.canMove(), "Lo Zombie dovrebbe potersi muovere all'inizio.");
    }

    @Test
    public void testApplyFreezeBonus() {
        // Simuliamo che lo Zombie venga congelato
        zombie.setCanMove(false);
        assertFalse(zombie.canMove(), "Lo Zombie dovrebbe essere bloccato (canMove = false).");

        // Simuliamo la fine di un turno, che dovrebbe resettare la variabile a true
        zombie.resetMoveStatus();
        assertTrue(zombie.canMove(), "Lo Zombie dovrebbe potersi muovere di nuovo dopo il reset del turno.");
    }
}