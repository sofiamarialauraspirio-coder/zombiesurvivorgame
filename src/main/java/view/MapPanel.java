package view;

import model.GameMap;
import controller.TurnController;
import controller.TurnController.GameState;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MapPanel extends JPanel {
    private GameMap map;
    private BufferedImage tileset;
    private BufferedImage keyImage; 
    private BufferedImage crateImage; 
    private BufferedImage doorImage;
    
    private List<Point> validMoves = new ArrayList<>();
    private List<Point> validBlocks = new ArrayList<>(); 
    
    private TurnController turnController;
    private BufferedImage zombieImage;
    private BufferedImage survivorImage;

    private Point cursorPosition = null; 
    private boolean isChoosingBlock = false; 

    private final int SOURCE_TILE_SIZE = 64;
    private final int DEST_TILE_SIZE = 48;

    private Runnable onStateChanged;

    public MapPanel(GameMap map) {
        this.map = map;
        this.setLayout(null); 
        caricaTileset();
        setPreferredSize(new Dimension(map.getCols() * DEST_TILE_SIZE, map.getRows() * DEST_TILE_SIZE));

        this.setFocusable(true);
        this.requestFocusInWindow();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (turnController == null) return;
                GameState state = turnController.getCurrentState();
                if (state != GameState.P1_CHOICE && state != GameState.P2_CHOICE) return;

                int keyCode = e.getKeyCode();
                boolean survivorTurn = turnController.isSurvivorTurn();
                model.Entity activeEntity = survivorTurn ? map.getSurvivor() : map.getZombie();
                model.Entity oppEntity = survivorTurn ? map.getZombie() : map.getSurvivor();

                Point activePos = new Point(activeEntity.getX(), activeEntity.getY());
                Point oppPos = new Point(oppEntity.getX(), oppEntity.getY());

                Point centerRef = isChoosingBlock ? oppPos : activePos;
                Point basePos = (cursorPosition != null) ? cursorPosition : centerRef;
                Point target = new Point(basePos);

                if (keyCode == KeyEvent.VK_UP) { target.y = basePos.y - 1; target.x = centerRef.x; if (target.y == centerRef.y) target.y -= 1; }
                else if (keyCode == KeyEvent.VK_DOWN) { target.y = basePos.y + 1; target.x = centerRef.x; if (target.y == centerRef.y) target.y += 1; }
                else if (keyCode == KeyEvent.VK_LEFT) { target.x = basePos.x - 1; target.y = centerRef.y; if (target.x == centerRef.x) target.x -= 1; }
                else if (keyCode == KeyEvent.VK_RIGHT) { target.x = basePos.x + 1; target.y = centerRef.y; if (target.x == centerRef.x) target.x += 1; }

                if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT) {
                    boolean isCenter = target.equals(centerRef);
                    boolean isValidMove = !isChoosingBlock && validMoves != null && validMoves.contains(target);
                    boolean isValidBlock = isChoosingBlock && validBlocks != null && validBlocks.contains(target);

                    if (isCenter || isValidMove || isValidBlock) {
                        cursorPosition = target;
                    }
                } 
                else if (keyCode == KeyEvent.VK_ENTER) {
                    if (!isChoosingBlock && cursorPosition != null && validMoves != null && validMoves.contains(cursorPosition)) {
                        int targetX = cursorPosition.x;
                        int targetY = cursorPosition.y;
                        int blocchiDisponibili = activeEntity.getNumeroBlocchiPossibili();
                        boolean oppIsFrozen = !oppEntity.canMove();
                        
                        validMoves.clear(); 
                        cursorPosition = null; 
                        turnController.confirmMove(targetX, targetY);

                       if (blocchiDisponibili > 0 && !oppIsFrozen) {
                            isChoosingBlock = true;
                            calcolaCaselleBlocco(); 
                            
                            if (validBlocks.isEmpty()) {
                                isChoosingBlock = false;
                                turnController.forceFinishBlock();
                            } else {
                                // 🎯 FIX UX: Snappiamo il cursore al primo blocco rosso valido!
                                // Così se il giocatore preme ripetutamente Invio, piazza la trappola senza bloccarsi.
                                cursorPosition = new Point(validBlocks.get(0));
                            }
                        }
                    } 
                    else if (isChoosingBlock && cursorPosition != null && validBlocks != null && validBlocks.contains(cursorPosition)) {
                        Point bloccoPiazzato = new Point(cursorPosition.x, cursorPosition.y);
                        boolean finished = turnController.confirmBlock(bloccoPiazzato.x, bloccoPiazzato.y);
                        validBlocks.remove(bloccoPiazzato); 
                        
                        if (finished || validBlocks.isEmpty()) {
                            if (!finished) turnController.forceFinishBlock();
                            isChoosingBlock = false;
                            validBlocks.clear();
                            cursorPosition = null; 
                        } else {
                            cursorPosition = oppPos; 
                        }
                    }
                }
                repaint();
                if (onStateChanged != null) onStateChanged.run(); 
            }
        });
    } 

    public void setOnStateChanged(Runnable callback) { this.onStateChanged = callback; }
    public boolean isChoosingBlock() { return isChoosingBlock; }

    private void calcolaCaselleBlocco() {
        validBlocks.clear();
        boolean survivorTurn = turnController.isSurvivorTurn();
        Point bersaglio = survivorTurn ? new Point(map.getZombie().getX(), map.getZombie().getY()) : new Point(map.getSurvivor().getX(), map.getSurvivor().getY());
        if (map.isBlockable(bersaglio.y - 1, bersaglio.x)) validBlocks.add(new Point(bersaglio.x, bersaglio.y - 1));
        if (map.isBlockable(bersaglio.y + 1, bersaglio.x)) validBlocks.add(new Point(bersaglio.x, bersaglio.y + 1));
        if (map.isBlockable(bersaglio.y, bersaglio.x - 1)) validBlocks.add(new Point(bersaglio.x - 1, bersaglio.y));
        if (map.isBlockable(bersaglio.y, bersaglio.x + 1)) validBlocks.add(new Point(bersaglio.x + 1, bersaglio.y));
    }

    public void setTurnController(TurnController turnController) { this.turnController = turnController; }

    private void caricaTileset() {
        try {
            tileset = ImageIO.read(new File("src/main/resources/tilesheet_complete.png"));
            keyImage = ImageIO.read(new File("src/main/resources/key.png")); 
            zombieImage = ImageIO.read(new File("src/main/resources/zombie.png"));
            survivorImage = ImageIO.read(new File("src/main/resources/survivor.png"));
            crateImage = ImageIO.read(new File("src/main/resources/crate.png")); 
            // 🚪 Caricamento immagine porta
            File filePorta = new File("src/main/resources/door.png");
            if (filePorta.exists()) {
                doorImage = ImageIO.read(filePorta);
            }
        } catch (Exception e) { System.err.println("Errore: " + e.getMessage()); }
    }

    public void clearIndicators() {
        if (validMoves != null) validMoves.clear();
        if (validBlocks != null) validBlocks.clear();
        isChoosingBlock = false; 
        cursorPosition = null;
        repaint();
        if (onStateChanged != null) onStateChanged.run();
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
                    g.drawImage(tileset, dx, dy, dx + DEST_TILE_SIZE, dy + DEST_TILE_SIZE, sx, sy, sx + SOURCE_TILE_SIZE, sy + SOURCE_TILE_SIZE, null);
                }
            }
        }

        if (map.getCrates() != null) {
            for (model.Crate cassa : map.getCrates()) {
                int drawX = cassa.getX() * DEST_TILE_SIZE;
                int drawY = cassa.getY() * DEST_TILE_SIZE;
                if (crateImage != null) g.drawImage(crateImage, drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
            }
        }

        boolean canShowIndicators = true; 
        if (turnController != null) {
            GameState state = turnController.getCurrentState();
            if (state == GameState.MENU || state == GameState.RESOLUTION || state == GameState.END_GAME || state == GameState.ZOMBIE_VICTORY) canShowIndicators = false;
        }

        // =========================================================
        // ✨ GRAFICA: QUADRATI ARROTONDATI (Più spessi ed eleganti)
        // =========================================================
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
        
        // 1. Un pizzico più spessi! (3.5 invece di 2.5)
        g2.setStroke(new BasicStroke(3.5f)); 

        // 2. Impostazioni del quadrato
        int offset = 6; // Margine interno: staccato dal bordo ma non troppo piccolo
        int size = DEST_TILE_SIZE - (offset * 2); 
        int arc = 16; // Curvatura degli angoli (più è alto, più è rotondo)

        if (canShowIndicators && !isChoosingBlock && validMoves != null) {
            for (Point p : validMoves) {
                int dx = p.x * DEST_TILE_SIZE + offset;
                int dy = p.y * DEST_TILE_SIZE + offset;
                g2.setColor(new Color(255, 255, 0, 50)); 
                g2.fillRoundRect(dx, dy, size, size, arc, arc); // Riempe il quadrato arrotondato
                g2.setColor(new Color(255, 215, 0, 220)); 
                g2.drawRoundRect(dx, dy, size, size, arc, arc); // Disegna il bordo
            }
        }

        if (canShowIndicators && isChoosingBlock && validBlocks != null) {
            for (Point p : validBlocks) {
                int dx = p.x * DEST_TILE_SIZE + offset;
                int dy = p.y * DEST_TILE_SIZE + offset;
                g2.setColor(new Color(255, 0, 0, 50)); 
                g2.fillRoundRect(dx, dy, size, size, arc, arc);
                g2.setColor(new Color(255, 50, 50, 220)); 
                g2.drawRoundRect(dx, dy, size, size, arc, arc);
            }
        }

        // Muri Bonus (Double Block) con lo stesso stile
        if (map.getSurvivor() != null && map.getSurvivor().getPlannedBlocks() != null && map.getSurvivor().getNumeroBlocchiPossibili() == 2) {
            for (Point p : map.getSurvivor().getPlannedBlocks()) {
                int dx = p.x * DEST_TILE_SIZE + offset;
                int dy = p.y * DEST_TILE_SIZE + offset;
                g2.setColor(new Color(100, 100, 100, 70)); 
                g2.fillRoundRect(dx, dy, size, size, arc, arc);
                g2.setColor(new Color(200, 200, 200, 220)); 
                g2.drawRoundRect(dx, dy, size, size, arc, arc);
            }
        }
        if (map.getZombie() != null && map.getZombie().getPlannedBlocks() != null && map.getZombie().getNumeroBlocchiPossibili() == 2) {
            for (Point p : map.getZombie().getPlannedBlocks()) {
                int dx = p.x * DEST_TILE_SIZE + offset;
                int dy = p.y * DEST_TILE_SIZE + offset;
                g2.setColor(new Color(100, 100, 100, 70)); 
                g2.fillRoundRect(dx, dy, size, size, arc, arc);
                g2.setColor(new Color(200, 200, 200, 220)); 
                g2.drawRoundRect(dx, dy, size, size, arc, arc);
            }
        }
        g2.dispose();

        // CHIAVE E PORTA
        if (map.getKey() != null && keyImage != null) g.drawImage(keyImage, ((map.getKey().getX() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE) - 16, ((map.getKey().getY() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE) - 16, 32, 32, null);
        
        if (map != null && map.getDoor() != null) { 
            int doorX = map.getDoor().getGridColLeft() * DEST_TILE_SIZE;
            int doorY = map.getDoor().getGridRow() * DEST_TILE_SIZE;
            if (doorImage != null) {
                g.drawImage(doorImage, doorX, doorY, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
                g.drawImage(doorImage, doorX + DEST_TILE_SIZE, doorY, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
            } else {
                g.setColor(new Color(139, 69, 19)); 
                g.fillRect(doorX, doorY, DEST_TILE_SIZE * 2, DEST_TILE_SIZE); 
            }
        }

        // PERSONAGGI
        if (map.getZombie() != null && zombieImage != null) g.drawImage(zombieImage, map.getZombie().getX() * DEST_TILE_SIZE, map.getZombie().getY() * DEST_TILE_SIZE, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
        if (map.getSurvivor() != null && survivorImage != null) g.drawImage(survivorImage, map.getSurvivor().getX() * DEST_TILE_SIZE, map.getSurvivor().getY() * DEST_TILE_SIZE, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
    }

    public void evidenziaMossePersonaggio(int x, int y, int range) { setValidMoves(map.getValidMoves(x, y, range)); }
    public void evidenziaMossePersonaggio(int x, int y) { evidenziaMossePersonaggio(x, y, 1); }
    public BufferedImage getTileset() { return this.tileset; }
}