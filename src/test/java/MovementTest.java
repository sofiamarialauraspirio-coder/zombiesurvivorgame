import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MovementTest {

    // Test 1: Verificare che il movimento sia limitato a croce (su, giù, destra, sinistra)
    @Test
    public void testValidOrthogonalMoves() {
        // Arrange
        model.GameMap map = new model.GameMap(); 
        int startX = 5;
        int startY = 5;
        int distanza = 1;

        // Act
        java.util.List<java.awt.Point> mosseValide = map.getValidMoves(startX, startY, distanza);

        // Assert
        assertNotNull(mosseValide, "La lista delle mosse valide non deve essere null");
        assertEquals(4, mosseValide.size(), "Dovrebbero esserci esattamente 4 mosse valide (solo a croce)");

        boolean contieneDiagonali = false;
        for (java.awt.Point mossa : mosseValide) {
            if (mossa.x != startX && mossa.y != startY) {
                contieneDiagonali = true;
                break;
            }
        }
        assertFalse(contieneDiagonali, "ERRORE: La lista contiene movimenti in diagonale!");
    }

    // Test 2: Verificare che i muri (Wall) vengano esclusi dalle mosse valide
    @Test
    public void testWallTilesAreExcludedFromMoves() {
        // Arrange
        model.GameMap map = new model.GameMap();
        int startX = 5;
        int startY = 5;

        // Simuliamo un muro esattamente a NORD del sopravvissuto (Y - 1)
        // FIX: Invece di usare "2", chiediamo alla mappa qual è il vero ID del muro (287)
        int wallId = model.GameMap.TILE_WALL; 
        map.setTile(startY - 1, startX, wallId); 

        // Act
        java.util.List<java.awt.Point> mosseValide = map.getValidMoves(startX, startY, 1);

        // Assert
        java.awt.Point muro = new java.awt.Point(startX, startY - 1);
        
        assertFalse(mosseValide.contains(muro), "ERRORE: Il muro a Nord è stato inserito nelle mosse valide!");
        assertEquals(3, mosseValide.size(), "Le mosse valide devono essere 3 perché una direzione è bloccata dal muro");
    }
}