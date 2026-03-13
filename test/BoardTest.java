package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import model.Board;

import org.junit.jupiter.api.Test;

public class BoardTest {

    @Test
    public void testCreazioneMappa12x12() {
        // 1. Preparazione (Arrange)
        Board mappa = new Board();

        // 2. Esecuzione (Act)
        int righe = mappa.getNumeroRighe();
        int colonne = mappa.getNumeroColonne();

        // 3. Verifica (Assert)
        assertEquals(12, righe, "La mappa deve avere esattamente 12 righe");
        assertEquals(12, colonne, "La mappa deve avere esattamente 12 colonne");
    }
}