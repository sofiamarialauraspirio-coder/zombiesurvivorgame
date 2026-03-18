package view;

import model.GameMap;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Dimension;

public class MapPanel extends JPanel {
    private GameMap map;
    private BufferedImage tileset;

    // Dimensione di ogni "quadratino" sullo schermo (es. 64x64 pixel)
    private final int TILE_SIZE = 64;

    public MapPanel(GameMap map) {
        this.map = map;
        caricaTileset();
        setPreferredSize(new java.awt.Dimension(map.getCols() * TILE_SIZE, map.getRows() * TILE_SIZE));
    }

    private void caricaTileset() {
        try {
            // Usa il percorso esatto del tuo file immagine!
            tileset = ImageIO.read(new File("src/main/resources/tilesheet_complete.png"));
            System.out.println("Tileset caricato con successo!");
        } catch (Exception e) {
            System.err.println("Errore fatale: Impossibile caricare il tileset! " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (map == null || tileset == null) return;

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                int tileId = map.getTile(row, col);
                
                // Calcoliamo dove si trova la mattonella nel tileset (PNG)
                // Assumendo che il tileset abbia 10 colonne di mattonelle da 64x64
                int sx = (tileId % 10) * 64; 
                int sy = (tileId / 10) * 64;

                // Disegniamo il pezzetto di immagine sulla griglia
                g.drawImage(tileset, 
                    col * TILE_SIZE, row * TILE_SIZE,           // Destinazione (schermo)
                    (col + 1) * TILE_SIZE, (row + 1) * TILE_SIZE, 
                    sx, sy, sx + 64, sy + 64,                   // Sorgente (file PNG)
                    null);
            }
        }
    }

    // Metodo aggiunto per permettere ai Test di verificare il caricamento
    public BufferedImage getTileset() {
        return tileset;
    }
}