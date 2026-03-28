package view;

import model.GameMap;
import controller.TurnController;
import controller.TurnController.GameState;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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

    public void setTurnController(TurnController turnController) {
        this.turnController = turnController;
    }

    private void caricaTileset() {
        try {
            tileset = ImageIO.read(new File("src/main/resources/tilesheet_complete.png"));
            keyImage = ImageIO.read(new File("src/main/resources/key.png")); 
            zombieImage = ImageIO.read(new File("src/main/resources/zombie.png"));
            survivorImage = ImageIO.read(new File("src/main/resources/survivor.png"));
        } catch (Exception e) {
            System.err.println("Errore fatale: Impossibile caricare alcune risorse! " + e.getMessage());
        }
    }

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

        // 1. Sfondo Verde
        g.setColor(new Color(46, 204, 113)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        if (map == null || tileset == null) return;

        // 2. Disegno Mappa
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

        // 3. Indicatori Mosse (Solo nelle fasi di scelta)
        boolean canShowIndicators = true;
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

        // 4. Oggetti e Personaggi
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
            g.setColor(new Color(139, 69, 19)); 
            g.fillRect(doorScreenX, doorScreenY, DEST_TILE_SIZE * 2, DEST_TILE_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(doorScreenX, doorScreenY, DEST_TILE_SIZE * 2, DEST_TILE_SIZE);
        }

        if (map.getZombie() != null && zombieImage != null) {
            g.drawImage(zombieImage, map.getZombie().getX() * DEST_TILE_SIZE, map.getZombie().getY() * DEST_TILE_SIZE, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
        }

        if (map.getSurvivor() != null && survivorImage != null) {
            g.drawImage(survivorImage, map.getSurvivor().getX() * DEST_TILE_SIZE, map.getSurvivor().getY() * DEST_TILE_SIZE, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
        }

        // =================================================================
        // USER STORY 31: ACTIVE TURN INDICATOR (Dynamic UI Element)
        // =================================================================
        if (turnController != null) {
            GameState state = turnController.getCurrentState();
            String statusText = "";
            Color textColor = Color.WHITE;

            // State-Driven Content & Transition Feedback
            switch (state) {
                case MENU:
                    statusText = "Premi INVIO per Iniziare!";
                    break;
                case P1_CHOICE:
                    statusText = "Turno P1 (Sopravvissuto)...";
                    textColor = Color.CYAN; // Colore per distinguere il P1
                    break;
                case P2_CHOICE:
                    statusText = "Turno P2 (Zombie)...";
                    textColor = Color.RED; // Colore per distinguere il P2
                    break;
                case RESOLUTION:
                    statusText = "Risoluzione Mosse...";
                    textColor = Color.YELLOW;
                    break;
                case END_GAME:
                    statusText = "🏆 PARTITA FINITA!";
                    textColor = Color.GREEN;
                    break;
            }

            // Impostiamo il Font (Grassetto, grandezza 20)
            g.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(statusText);
            int textHeight = fm.getHeight();

            // Calcoliamo la posizione per centrarlo in alto (Visibility rule)
            int paddingX = 20;
            int paddingY = 10;
            int bannerWidth = textWidth + (paddingX * 2);
            int bannerHeight = textHeight + (paddingY * 2);
            int bannerX = (getWidth() - bannerWidth) / 2;
            int bannerY = 15; // Distanza dal bordo superiore

            // Disegniamo il rettangolo di sfondo semi-trasparente
            g.setColor(new Color(0, 0, 0, 180)); 
            g.fillRoundRect(bannerX, bannerY, bannerWidth, bannerHeight, 15, 15);

            // Disegniamo il testo centrato nel rettangolo
            g.setColor(textColor);
            g.drawString(statusText, bannerX + paddingX, bannerY + textHeight + (paddingY / 2) - 2);
        }
    }

    public BufferedImage getTileset() { return tileset; }

    public void evidenziaMossePersonaggio(int x, int y) {
        java.util.List<java.awt.Point> mosse = map.getValidMoves(x, y, 1);
        setValidMoves(mosse);
    }
}