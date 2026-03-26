package view;

import model.GameMap;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class MapPanel extends JPanel {
    private GameMap map;
    private BufferedImage tileset;
    private BufferedImage keyImage; // Variabile per l'immagine della chiave

    // 1. La grandezza ESATTA dei quadratini nel file PNG (NON CAMBIARE MAI, resta 64)
    private final int SOURCE_TILE_SIZE = 64;
    
    // 2. La grandezza che vuoi TU sullo schermo (48 per farla stare nel PC)
    private final int DEST_TILE_SIZE = 48;

    public MapPanel(GameMap map) {
        this.map = map;
        caricaTileset();
        // Usiamo la grandezza dello schermo per calcolare la finestra
        setPreferredSize(new Dimension(map.getCols() * DEST_TILE_SIZE, map.getRows() * DEST_TILE_SIZE));
    }

    private void caricaTileset() {
        try {
            tileset = ImageIO.read(new File("src/main/resources/tilesheet_complete.png"));
            // AGGIUNTA: Carichiamo anche l'immagine della chiave!
            keyImage = ImageIO.read(new File("src/main/resources/key.png")); 
            System.out.println("Tileset e Chiave caricati con successo!");
        } catch (Exception e) {
            System.err.println("Errore fatale: Impossibile caricare le risorse! " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Colora il "vuoto" di verde
        g.setColor(new Color(46, 204, 113)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        if (map == null || tileset == null) return;

        // Calcoliamo le colonne usando la dimensione ORIGINALE dell'immagine (64)
        int colsInTileset = tileset.getWidth() / SOURCE_TILE_SIZE;

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                
                int tileId = map.getTile(row, col);
                
                if (tileId > 0) {
                    int actualId = tileId - 1;
                    
                    // RITAGLIO DAL PNG (Usiamo SOURCE_TILE_SIZE = 64)
                    int sx = (actualId % colsInTileset) * SOURCE_TILE_SIZE; 
                    int sy = (actualId / colsInTileset) * SOURCE_TILE_SIZE;

                    // DISEGNO SULLO SCHERMO (Usiamo DEST_TILE_SIZE = 48 per rimpicciolire)
                    int dx = col * DEST_TILE_SIZE;
                    int dy = row * DEST_TILE_SIZE;

                    g.drawImage(tileset, 
                        dx, dy, dx + DEST_TILE_SIZE, dy + DEST_TILE_SIZE,           // Spazio sullo schermo
                        sx, sy, sx + SOURCE_TILE_SIZE, sy + SOURCE_TILE_SIZE,       // Ritaglio dal PNG
                        null);
                }
            }
        }

        // =================================================================
        // INIZIO NUOVO CODICE: DISEGNO DELLA CHIAVE (Immagine PNG)
        // =================================================================
        if (map.getKey() != null && keyImage != null) {
            // Calcoliamo la proporzione per le coordinate rimpicciolite (da 64 a 48)
            int scaledX = (map.getKey().getX() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE;
            int scaledY = (map.getKey().getY() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE;
            
            // Scegliamo la grandezza in pixel della chiave (32x32)
            int displaySize = 32; 
            
            // Calcoliamo l'offset (la metà) per centrarla perfettamente sulle coordinate
            int offset = displaySize / 2;

            // Disegniamo l'immagine magica!
            g.drawImage(keyImage, scaledX - offset, scaledY - offset, displaySize, displaySize, null);
        }
        // =================================================================
        // FINE NUOVO CODICE
        // =================================================================
    }

    public BufferedImage getTileset() {
        return tileset;
    }
}