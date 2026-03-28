package view;

import model.GameMap;
import controller.TurnController; // Importiamo il nostro Regista
import controller.TurnController.GameState; // Importiamo gli stati

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
    private BufferedImage keyImage; 
    private List<Point> validMoves = new ArrayList<>();
    
    // ==========================================
    // NP-21: Riferimento al Regista (TurnController)
    // ==========================================
    private TurnController turnController;

    private BufferedImage zombieImage;
    private BufferedImage survivorImage;

    private final int SOURCE_TILE_SIZE = 64;
    private final int DEST_TILE_SIZE = 48;

    public MapPanel(GameMap map) {
        this.map = map;
        caricaTileset();
        setPreferredSize(new Dimension(map.getCols() * DEST_TILE_SIZE, map.getRows() * DEST_TILE_SIZE));
    }

    // NP-21: Metodo per collegare il pannello al Regista
    public void setTurnController(TurnController turnController) {
        this.turnController = turnController;
    }

    private void caricaTileset() {
        try {
            tileset = ImageIO.read(new File("src/main/resources/tilesheet_complete.png"));
            keyImage = ImageIO.read(new File("src/main/resources/key.png")); 
            zombieImage = ImageIO.read(new File("src/main/resources/zombie.png"));
            survivorImage = ImageIO.read(new File("src/main/resources/survivor.png"));
            System.out.println("Tileset, Chiave e Personaggi caricati con successo!");
        } catch (Exception e) {
            System.err.println("Errore fatale: Impossibile caricare alcune risorse! " + e.getMessage());
        }
    }

    // ==========================================
    // NP-21: VISUAL RESET
    // Metodo per cancellare le mosse visibili dal turno precedente
    // ==========================================
    public void clearIndicators() {
        if (validMoves != null) {
            validMoves.clear();
        }
        repaint();
    }

    public void setValidMoves(List<Point> moves) {
        this.validMoves = moves;
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(46, 204, 113)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        if (map == null || tileset == null) return;

        int colsInTileset = tileset.getWidth() / SOURCE_TILE_SIZE;

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                int tileId = map.getTile(row, col);
                
                if (tileId > 0) {
                    int actualId = tileId - 1;
                    int sx = (actualId % colsInTileset) * SOURCE_TILE_SIZE; 
                    int sy = (actualId / colsInTileset) * SOURCE_TILE_SIZE;

                    int dx = col * DEST_TILE_SIZE;
                    int dy = row * DEST_TILE_SIZE;

                    g.drawImage(tileset, dx, dy, dx + DEST_TILE_SIZE, dy + DEST_TILE_SIZE, 
                                         sx, sy, sx + SOURCE_TILE_SIZE, sy + SOURCE_TILE_SIZE, null);
                }
            }
        }

        // =================================================================
        // NP-21: STATE-GATED RENDERING & PRIVACY OF DATA
        // =================================================================
        // Controlliamo in che stato siamo. Disegniamo le mosse SOLO se 
        // siamo nella fase di scelta (P1_CHOICE o P2_CHOICE). 
        // In MENU o RESOLUTION nascondiamo i segreti!
        boolean canShowIndicators = true; // Fallback di sicurezza
        if (turnController != null) {
            GameState state = turnController.getCurrentState();
            if (state == GameState.MENU || state == GameState.RESOLUTION || state == GameState.END_GAME) {
                canShowIndicators = false;
            }
        }

        if (canShowIndicators && validMoves != null && !validMoves.isEmpty()) {
            Color gialloTrasparente = new Color(255, 255, 0, 150);
            Color bordoGiallo = Color.YELLOW;

            for (Point p : validMoves) {
                int drawX = p.x * DEST_TILE_SIZE;
                int drawY = p.y * DEST_TILE_SIZE;
                
                g.setColor(gialloTrasparente);
                g.fillRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
                g.setColor(bordoGiallo);
                g.drawRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
            }
        }

        // --- Disegno degli oggetti (Invariato) ---
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

    public BufferedImage getTileset() { return tileset; }

    public void evidenziaMossePersonaggio(int x, int y) {
        java.util.List<java.awt.Point> mosse = map.getValidMoves(x, y, 1);
        setValidMoves(mosse);
    }
}