import model.GameManager;
import model.Survivor;
import model.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {
    private GameManager gm;
    private Survivor survivor;
    private Zombie zombie;

    @BeforeEach
    public void setUp() {
        // Partono lontani: Sopravvissuto in (0,0) e Zombie in (5,5)
        survivor = new Survivor(0, 0);
        zombie = new Zombie(5, 5);
        gm = new GameManager(survivor, zombie);
    }

    @Test
    public void testNormalMovement() {
        // Mosse valide e separate
        survivor.planMove(0, 1);
        zombie.planMove(5, 4);

        // Risoluzione turno
        gm.resolveGlobalTurn();

        // Entrambi si sono mossi con successo (Atomic Update)
        assertEquals(0, survivor.getX());
        assertEquals(1, survivor.getY());
        assertEquals(5, zombie.getX());
        assertEquals(4, zombie.getY());
    }

    @Test
    public void testCollisionResolution() {
        // Entrambi mirano alla STESSA casella (2,2)
        survivor.planMove(2, 2);
        zombie.planMove(2, 2);

        gm.resolveGlobalTurn();

        // Collisione! Entrambi devono essere rimasti al punto di partenza
        assertEquals(0, survivor.getX(), "Il Sopravvissuto NON doveva muoversi");
        assertEquals(0, survivor.getY());
        assertEquals(5, zombie.getX(), "Lo Zombie NON doveva muoversi");
        assertEquals(5, zombie.getY());
    }

    @Test
    public void testBlockingResolution() {
        // Il Sopravvissuto vuole andare in (0,1)...
        survivor.planMove(0, 1);
        // ...ma lo Zombie gli piazza un Blocco proprio lì! (Ora finisce nella Lista dei blocchi)
        zombie.planBlock(0, 1); 
        zombie.planMove(5, 4); // E nel frattempo lo zombie si muove per i fatti suoi

        gm.resolveGlobalTurn();

        // Il Sopravvissuto sbatte contro il blocco e resta fermo. Lo Zombie si muove.
        assertEquals(0, survivor.getX(), "Il Sopravvissuto è bloccato, deve restare in 0,0");
        assertEquals(0, survivor.getY());
        assertEquals(5, zombie.getX(), "Lo Zombie si muove regolarmente");
        assertEquals(4, zombie.getY());
    }

    @Test
    public void testOccupiedCellRule() {
        // Creiamo uno scontro. Entrambi provano ad andare in (2,2)
        survivor.planMove(2, 2);
        zombie.planMove(2, 2);

        // Il Sopravvissuto prova astutamente a piazzare un blocco sulla casella di PARTENZA dello Zombie (5,5)
        survivor.planBlock(5, 5);

        gm.resolveGlobalTurn();

        // A causa della collisione in (2,2), lo Zombie è rimasto fermo in (5,5)
        assertEquals(5, zombie.getX());
        assertEquals(5, zombie.getY());
        
        // Poiché lo Zombie è ancora in (5,5), il blocco del Sopravvissuto in (5,5) deve sparire!
        assertTrue(survivor.getPlannedBlocks().isEmpty(), "Il blocco del Sopravvissuto doveva essere annullato perché lo Zombie occupa la cella!");
    }
}