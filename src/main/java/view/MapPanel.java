package view;

import model.GameMap;
import controller.TurnController;
import controller.TurnController.GameState;

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
    
    private List<Point> validMoves = new ArrayList<>();
    private List<Point> validBlocks = new ArrayList<>(); 
    
    private TurnController turnController;
    private BufferedImage zombieImage;
    private BufferedImage survivorImage;

    private Point cursorPosition = null; // Il cursore parte INVISIBILE!
    private boolean isChoosingBlock = false; 

    private final int SOURCE_TILE_SIZE = 64;
    private final int DEST_TILE_SIZE = 48;

    private javax.swing.JButton btnMenu;
    private javax.swing.JButton btnQuit;

    public MapPanel(GameMap map) {
        this.map = map;

        // Permette di muovere i bottoni liberamente sulla mappa
        this.setLayout(null); 

        // Creiamo i bottoni veri
        btnMenu = new javax.swing.JButton("Main Menu");
        btnQuit = new javax.swing.JButton("Quit");

        // Stile dei bottoni (scuro con testo bianco, senza bordini strani)
        java.awt.Font fontBottoni = new java.awt.Font("Arial", java.awt.Font.BOLD, 18);
        btnMenu.setFont(fontBottoni);
        btnMenu.setBackground(new Color(50, 50, 50));
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);

        btnQuit.setFont(fontBottoni);
        btnQuit.setBackground(new Color(50, 50, 50));
        btnQuit.setForeground(Color.WHITE);
        btnQuit.setFocusPainted(false);

        // All'inizio della partita devono essere invisibili
        btnMenu.setVisible(false);
        btnQuit.setVisible(false);

        // Cosa succede se clicco "Quit"?
        btnQuit.addActionListener(e -> System.exit(0));

        // Cosa succede se clicco "Main Menu"?
        btnMenu.addActionListener(e -> {
            if (turnController != null) turnController.resetGame();
            java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(MapPanel.this);
            if (window instanceof javax.swing.JFrame) {
                javax.swing.JFrame frame = (javax.swing.JFrame) window;
                frame.setContentPane(new view.MainMenu(frame, new model.GameSession()));

                frame.setSize(800, 500); // Riporta la finestra alla grandezza originale del menu
                frame.setLocationRelativeTo(null); // La ricentra perfettamente sullo schermo

                frame.revalidate();
                frame.repaint();
            }
        });

        // Aggiungiamo fisicamente i bottoni allo schermo
        this.add(btnMenu);
        this.add(btnQuit);

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

                // Capiamo chi si sta muovendo (Active) e chi è l'avversario (Opponent)
                boolean survivorTurn = turnController.isSurvivorTurn();
                Point activePos = survivorTurn ? new Point(map.getSurvivor().getX(), map.getSurvivor().getY()) 
                                               : new Point(map.getZombie().getX(), map.getZombie().getY());
                Point oppPos = survivorTurn ? new Point(map.getZombie().getX(), map.getZombie().getY()) 
                                            : new Point(map.getSurvivor().getX(), map.getSurvivor().getY());

                // Se scegliamo il movimento partiamo dal personaggio attivo, se scegliamo il blocco partiamo dall'avversario
                Point centerRef = isChoosingBlock ? oppPos : activePos;
                Point target = null;

                // Calcoliamo la cella che il giocatore vuole selezionare premendo la freccetta
                if (keyCode == KeyEvent.VK_UP) target = new Point(centerRef.x, centerRef.y - 1);
                else if (keyCode == KeyEvent.VK_DOWN) target = new Point(centerRef.x, centerRef.y + 1);
                else if (keyCode == KeyEvent.VK_LEFT) target = new Point(centerRef.x - 1, centerRef.y);
                else if (keyCode == KeyEvent.VK_RIGHT) target = new Point(centerRef.x + 1, centerRef.y);

                if (target != null) {
                    // Accendi il cursore SOLO se la cella scelta è una mossa valida!
                    if (!isChoosingBlock && validMoves != null && validMoves.contains(target)) {
                        cursorPosition = target;
                    } else if (isChoosingBlock && validBlocks != null && validBlocks.contains(target)) {
                        cursorPosition = target;
                    }
                } 
                else if (keyCode == KeyEvent.VK_ENTER) {
                    
                    if (!isChoosingBlock && cursorPosition != null && validMoves.contains(cursorPosition)) {
                        turnController.confirmMove(cursorPosition.x, cursorPosition.y);
                        isChoosingBlock = true;
                        validMoves.clear(); 
                        calcolaCaselleBlocco(); 
                        cursorPosition = null; // Nascondiamo la selezione
                    } 
                    else if (isChoosingBlock && cursorPosition != null && validBlocks.contains(cursorPosition)) {
                        turnController.confirmBlock(cursorPosition.x, cursorPosition.y);
                        isChoosingBlock = false;
                        validBlocks.clear();
                        cursorPosition = null; // Nascondiamo la selezione
                        
                        // Passaggio di turno: mostriamo le mosse per il P2!
                        if (turnController.getCurrentState() == GameState.P2_CHOICE) {
                            boolean p2Survivor = turnController.isSurvivorTurn();
                            Point p2Pos = p2Survivor ? new Point(map.getSurvivor().getX(), map.getSurvivor().getY()) 
                                                     : new Point(map.getZombie().getX(), map.getZombie().getY());
                            evidenziaMossePersonaggio(p2Pos.x, p2Pos.y);
                        }
                    }
                }
                repaint();
            }
        });

    } // <--- QUESTA È LA PARENTESI CHE CHIUDE IL COSTRUTTORE MapPanel(...)

    private void calcolaCaselleBlocco() {
        validBlocks.clear();
        boolean survivorTurn = turnController.isSurvivorTurn();
        Point bersaglio = survivorTurn ? new Point(map.getZombie().getX(), map.getZombie().getY()) 
                                       : new Point(map.getSurvivor().getX(), map.getSurvivor().getY());

        if (map.isWalkable(bersaglio.y - 1, bersaglio.x)) validBlocks.add(new Point(bersaglio.x, bersaglio.y - 1));
        if (map.isWalkable(bersaglio.y + 1, bersaglio.x)) validBlocks.add(new Point(bersaglio.x, bersaglio.y + 1));
        if (map.isWalkable(bersaglio.y, bersaglio.x - 1)) validBlocks.add(new Point(bersaglio.x - 1, bersaglio.y));
        if (map.isWalkable(bersaglio.y, bersaglio.x + 1)) validBlocks.add(new Point(bersaglio.x + 1, bersaglio.y));
    }

    public void setTurnController(TurnController turnController) { this.turnController = turnController; }

    private void caricaTileset() {
        try {
            tileset = ImageIO.read(new File("src/main/resources/tilesheet_complete.png"));
            keyImage = ImageIO.read(new File("src/main/resources/key.png")); 
            zombieImage = ImageIO.read(new File("src/main/resources/zombie.png"));
            survivorImage = ImageIO.read(new File("src/main/resources/survivor.png"));
        } catch (Exception e) { System.err.println("Errore: " + e.getMessage()); }
    }

    public void clearIndicators() {
        if (validMoves != null) validMoves.clear();
        if (validBlocks != null) validBlocks.clear();
        isChoosingBlock = false; 
        cursorPosition = null;
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

        if (map.getCrates() != null) {
            for (model.Crate cassa : map.getCrates()) {
                int drawX = cassa.getX() * DEST_TILE_SIZE;
                int drawY = cassa.getY() * DEST_TILE_SIZE;
                
                // Disegniamo una bella cassa di legno (Marrone)
                g.setColor(new Color(205, 133, 63)); // Colore marrone chiaro
                g.fillRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
                
                // Bordo scuro e una "X" sopra per farla sembrare una vera cassa
                g.setColor(new Color(139, 69, 19)); // Colore marrone scuro
                g.drawRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
                g.drawLine(drawX, drawY, drawX + DEST_TILE_SIZE, drawY + DEST_TILE_SIZE);
                g.drawLine(drawX + DEST_TILE_SIZE, drawY, drawX, drawY + DEST_TILE_SIZE);
            }
        }

        boolean canShowIndicators = true; 
        if (turnController != null) {
            GameState state = turnController.getCurrentState();
            if (state == GameState.MENU || state == GameState.RESOLUTION || state == GameState.END_GAME || state == GameState.ZOMBIE_VICTORY) {
                canShowIndicators = false;
            }
        }

        if (canShowIndicators && !isChoosingBlock && validMoves != null) {
            Color gialloTrasparente = new Color(255, 255, 0, 150);
            for (Point p : validMoves) {
                int drawX = p.x * DEST_TILE_SIZE;
                int drawY = p.y * DEST_TILE_SIZE;
                g.setColor(gialloTrasparente);
                g.fillRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
                g.setColor(Color.YELLOW);
                g.drawRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
            }
        }

        if (canShowIndicators && isChoosingBlock && validBlocks != null) {
            Color rossoTrasparente = new Color(255, 0, 0, 150);
            for (Point p : validBlocks) {
                int drawX = p.x * DEST_TILE_SIZE;
                int drawY = p.y * DEST_TILE_SIZE;
                g.setColor(rossoTrasparente);
                g.fillRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
                g.setColor(Color.RED);
                g.drawRect(drawX, drawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
            }
        }

        if (map.getKey() != null && keyImage != null) {
            int scaledX = (map.getKey().getX() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE;
            int scaledY = (map.getKey().getY() * DEST_TILE_SIZE) / SOURCE_TILE_SIZE;
            g.drawImage(keyImage, scaledX - 16, scaledY - 16, 32, 32, null);
        }
        if (map != null && map.getDoor() != null) {
            int dx = map.getDoor().getGridColLeft() * DEST_TILE_SIZE;
            int dy = map.getDoor().getGridRow() * DEST_TILE_SIZE;
            g.setColor(new Color(139, 69, 19)); 
            g.fillRect(dx, dy, DEST_TILE_SIZE * 2, DEST_TILE_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(dx, dy, DEST_TILE_SIZE * 2, DEST_TILE_SIZE);
        }
        
        // --- HIGHLIGHT COLLISIONE ZOMBIE (Se lo Zombie ha vinto, disegna un cerchio/quadrato di sangue sotto di loro) ---
        if (turnController != null && turnController.getCurrentState() == GameState.ZOMBIE_VICTORY) {
            int collisionX = map.getZombie().getX() * DEST_TILE_SIZE;
            int collisionY = map.getZombie().getY() * DEST_TILE_SIZE;
            g.setColor(new Color(150, 0, 0, 200)); // Rosso scuro sangue
            g.fillRect(collisionX, collisionY, DEST_TILE_SIZE, DEST_TILE_SIZE);
        }

        if (map.getZombie() != null && zombieImage != null) {
            g.drawImage(zombieImage, map.getZombie().getX() * DEST_TILE_SIZE, map.getZombie().getY() * DEST_TILE_SIZE, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
        }
        if (map.getSurvivor() != null && survivorImage != null) {
            g.drawImage(survivorImage, map.getSurvivor().getX() * DEST_TILE_SIZE, map.getSurvivor().getY() * DEST_TILE_SIZE, DEST_TILE_SIZE, DEST_TILE_SIZE, null);
        }

        // IL CURSORE APPARE SOLO SE HAI SELEZIONATO UNA MOSSA!
        if (canShowIndicators && cursorPosition != null) {
            int cursorDrawX = cursorPosition.x * DEST_TILE_SIZE;
            int cursorDrawY = cursorPosition.y * DEST_TILE_SIZE;
            g.setColor(Color.CYAN);
            g.drawRect(cursorDrawX, cursorDrawY, DEST_TILE_SIZE, DEST_TILE_SIZE);
            g.drawRect(cursorDrawX + 1, cursorDrawY + 1, DEST_TILE_SIZE - 2, DEST_TILE_SIZE - 2); 
        }
        
        // ==========================================
        // NOTIFICHE VISIVE DI VITTORIA 
        // ==========================================
        
        // 🏆 VITTORIA SOPRAVVISSUTO
        if (turnController != null && turnController.getCurrentState() == GameState.SURVIVOR_VICTORY) {
            
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            String messaggio = "IL SOPRAVVISSUTO HA VINTO!";
            java.awt.Font fontVittoria = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 36);
            g.setFont(fontVittoria);
            
            java.awt.FontMetrics metrics = g.getFontMetrics(fontVittoria);
            int xTesto = (getWidth() - metrics.stringWidth(messaggio)) / 2;
            int yTesto = (getHeight() / 2);

            g.setColor(Color.BLACK);
            g.drawString(messaggio, xTesto + 3, yTesto + 3);
            g.setColor(new Color(255, 215, 0)); // Oro
            g.drawString(messaggio, xTesto, yTesto);
        }
        
        // 🧟‍♂️ VITTORIA ZOMBIE
        else if (turnController != null && turnController.getCurrentState() == GameState.ZOMBIE_VICTORY) {
            
            // Schermo rosso sangue
            g.setColor(new Color(100, 0, 0, 190));
            g.fillRect(0, 0, getWidth(), getHeight());

            String messaggio = "LO ZOMBIE HA VINTO!";
            java.awt.Font fontVittoria = new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 42);
            g.setFont(fontVittoria);
            
            java.awt.FontMetrics metrics = g.getFontMetrics(fontVittoria);
            int xTesto = (getWidth() - metrics.stringWidth(messaggio)) / 2;
            int yTesto = (getHeight() / 2);

            // Ombra per far spiccare il testo
            g.setColor(Color.BLACK);
            g.drawString(messaggio, xTesto + 3, yTesto + 3);
            // Scritta in bianco
            g.setColor(Color.WHITE);
            g.drawString(messaggio, xTesto, yTesto);
        }

        // ==========================================
        // MOSTRA I BOTTONI VERI A FINE PARTITA
        // ==========================================
        if (turnController != null && 
           (turnController.getCurrentState() == GameState.SURVIVOR_VICTORY || 
            turnController.getCurrentState() == GameState.ZOMBIE_VICTORY)) {
            
            // Accendiamo i bottoni e li posizioniamo al centro
            if (!btnMenu.isVisible()) {
                int centerX = getWidth() / 2;
                int btnY = getHeight() / 2 + 50; 
                
                btnMenu.setBounds(centerX - 160, btnY, 140, 40);
                btnQuit.setBounds(centerX + 20, btnY, 140, 40);
                
                btnMenu.setVisible(true);
                btnQuit.setVisible(true);
            }
        } else {
            // Se il gioco si resetta e riparte, rinascondiamo i bottoni!
            if (btnMenu != null && btnMenu.isVisible()) {
                btnMenu.setVisible(false);
                btnQuit.setVisible(false);
            }
        }

    } // <--- QUESTA È LA PARENTESI CHE CHIUDE IL METODO paintComponent(...)

    public BufferedImage getTileset() { return tileset; }
    public void evidenziaMossePersonaggio(int x, int y) {
        java.util.List<java.awt.Point> mosse = map.getValidMoves(x, y, 1);
        setValidMoves(mosse);
    }
}