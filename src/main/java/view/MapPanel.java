package view;

import model.GameMap;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class MapPanel extends JPanel {
    private GameMap map;
    private BufferedImage tileset;
    private BufferedImage keyImage; // Variabile per l'immagine della chiave
    // Variabile per salvare le mosse valide calcolate (caselle verdi/gialle)
    private List<Point> validMoves = new ArrayList<>();

    // Metodo per dire al pannello: "Ehi, queste sono le caselle da illuminare!"
    public void setValidMoves(List<Point> moves) {
        this.validMoves = moves;
        repaint(); // Questo comando costringe Java a ridisegnare la mappa istantaneamente!
    }
    
    // VARIABILI PER I PERSONAGGI AGGIUNTE
    private BufferedImage zombieImage;
    private BufferedImage survivorImage;

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
            keyImage = ImageIO.read(new File("src/main/resources/key.png")); 
            
            // CARICAMENTO IMMAGINI DEI PERSONAGGI
            zombieImage = ImageIO.read(new File("src/main/resources/zombie.png"));
            survivorImage = ImageIO.read(new File("src/main/resources/survivor.png"));
            
            System.out.println("Tileset, Chiave e Personaggi caricati con successo!");
        } catch (Exception e) {
            System.err.println("Errore fatale: Impossibile caricare alcune risorse! " + e.getMessage());
            System.err.println("Assicurati di aver messo zombie.png e survivor.png nella cartella resources.");
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

        // DISEGNO DELLA MAPPA DI BASE (Muri e Pavimenti)
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
        // DISEGNO DELL'EVIDENZIATORE GIALLO PER LE MOSSE VALIDE
        // =================================================================
        if (validMoves != null && !validMoves.isEmpty()) {
            // Creiamo un GIALLO con Trasparenza per farlo risaltare sul prato verde!
            Color gialloTrasparente = new Color(255, 255, 0, 150);
            Color bordoGiallo = Color.YELLOW;

            for (Point p : validMoves) {
                int drawX = p.x * DEST_TILE_SIZE;
                int drawY = p.y * DEST_TILE_SIZE;
                
                // Coloriamo l'interno della casella
                g.setColor(gialloTrasparente);
                g.fillRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
                
                // Disegniamo un bordino sottile per renderlo più stiloso
                g.setColor(bordoGiallo);
                g.drawRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
            }
        }

        // =================================================================
        // DISEGNO DEGLI OGGETTI E DEI PERSONAGGI
        // (Spostati SOTTO le caselle gialle così non vengono coperti!)
        // =================================================================
        if (map.getKey() != null && keyImage != null) {
            int scaledX = (map.getKey().getX() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE;
            int scaledY = (map.getKey().getY() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE;
            int displaySize = 32; 
            int offset = displaySize / 2;
            g.drawImage(keyImage, scaledX - offset, scaledY - offset, displaySize, displaySize, null);
        }
        
        if (map != null && map.getDoor() != null) {
            int doorScreenX = map.getDoor().getGridColLeft() * DEST_TILE_SIZE;
            int doorScreenY = map.getDoor().getGridRow() * DEST_TILE_SIZE;
            int doorWidth = DEST_TILE_SIZE * 2; 
            int doorHeight = DEST_TILE_SIZE;

            g.setColor(new Color(139, 69, 19)); 
            g.fillRect(doorScreenX, doorScreenY, doorWidth, doorHeight);
            
            g.setColor(Color.BLACK);
            g.drawRect(doorScreenX, doorScreenY, doorWidth, doorHeight);
        }

        if (map.getZombie() != null && zombieImage != null) {
            int dx = map.getZombie().getX() * DEST_TILE_SIZE;
            int dy = map.getZombie().getY() * DEST_TILE_SIZE;
            g.drawImage(zombieImage, dx, dy, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
        }

        if (map.getSurvivor() != null && survivorImage != null) {
            int dx = map.getSurvivor().getX() * DEST_TILE_SIZE;
            int dy = map.getSurvivor().getY() * DEST_TILE_SIZE;
            g.drawImage(survivorImage, dx, dy, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
        }
    }

    public BufferedImage getTileset() {
        return tileset;
    }

    public void evidenziaMossePersonaggio(int x, int y) {
    java.util.List<java.awt.Point> mosse = map.getValidMoves(x, y, 1);
    setValidMoves(mosse);
}
}