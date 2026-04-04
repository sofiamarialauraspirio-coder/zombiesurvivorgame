import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import model.Board;


public class BoardTest {

    @Test
    public void testCreazioneMappa12x12() {
        Board mappa = new Board();

        int righe = mappa.getNumeroRighe();
        int colonne = mappa.getNumeroColonne();

        assertEquals(12, righe, "La mappa deve avere esattamente 12 righe");
        assertEquals(12, colonne, "La mappa deve avere esattamente 12 colonne");
    }
}