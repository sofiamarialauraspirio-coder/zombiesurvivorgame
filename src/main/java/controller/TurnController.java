package controller;

import model.Entity;
import model.Survivor; 
import model.Zombie;   
import model.GameManager;
import model.GameMap;
import model.Crate;
import model.Door;     
import view.MapPanel; 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Nuovi import per la grafica del popup
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TurnController {
    private GameManager gameManager;
    private GameMap gameMap;
    private MapPanel mapPanel;

    public enum GameState { MENU, P1_CHOICE, P2_CHOICE, RESOLUTION, SURVIVOR_VICTORY, ZOMBIE_VICTORY, END_GAME }
    private GameState currentState;

    private boolean p1HasMoved = false;
    private boolean p1HasBlocked = false; 
    private boolean p2HasMoved = false;
    private boolean p2HasBlocked = false; 
    private boolean survivorIsP1 = true; 

    private int turnsSinceLastEvent = 0; 
    private static final int SPAWN_INTERVAL = 3; 
    private Random random = new Random();

    public TurnController(GameManager gameManager, GameMap gameMap) {
        this.gameManager = gameManager;
        this.gameMap = gameMap;
        this.currentState = GameState.MENU; 
    }

    public void setMapPanel(MapPanel mapPanel) { this.mapPanel = mapPanel; }
    public void setSurvivorIsP1(boolean isP1) { this.survivorIsP1 = isP1; }

    public boolean isSurvivorTurn() {
        if (currentState == GameState.P1_CHOICE) return survivorIsP1;
        if (currentState == GameState.P2_CHOICE) return !survivorIsP1;
        return false;
    }

    public void changeState(GameState newState) {
        this.currentState = newState;
        System.out.println("🔄 State Machine: [" + this.currentState + "]");

        if (newState == GameState.P1_CHOICE) {
            boolean p1Frozen = survivorIsP1 ? !gameManager.getSurvivor().canMove() : !gameManager.getZombie().canMove();
            if (p1Frozen) {
                p1HasMoved = true;
                p1HasBlocked = true;
                checkP1Finished(); 
            }
        } 
        else if (newState == GameState.P2_CHOICE) {
            boolean p2Frozen = survivorIsP1 ? !gameManager.getZombie().canMove() : !gameManager.getSurvivor().canMove();
            if (p2Frozen) {
                p2HasMoved = true;
                p2HasBlocked = true;
                checkP2Finished(); 
            }
        }
    }

    public void startGame() {
        if (currentState == GameState.MENU) {
            changeState(GameState.P1_CHOICE); 
        }
    }

    public void confirmMove(int targetX, int targetY) {
        if (currentState != GameState.P1_CHOICE && currentState != GameState.P2_CHOICE) return;
        if (!gameMap.isWalkable(targetY, targetX)) return; 

        if (currentState == GameState.P1_CHOICE) {
            if (survivorIsP1) gameManager.getSurvivor().planMove(targetX, targetY);
            else gameManager.getZombie().planMove(targetX, targetY);
            p1HasMoved = true;
            checkP1Finished();
        } else if (currentState == GameState.P2_CHOICE) {
            if (survivorIsP1) gameManager.getZombie().planMove(targetX, targetY);
            else gameManager.getSurvivor().planMove(targetX, targetY);
            p2HasMoved = true;
            checkP2Finished();
        }
    }

    public void confirmBlock(int targetX, int targetY) {
        if (currentState == GameState.P1_CHOICE) {
            p1HasBlocked = true;
            checkP1Finished();
        } else if (currentState == GameState.P2_CHOICE) {
            p2HasBlocked = true;
            checkP2Finished();
        }
    }

    private void checkP1Finished() {
        if (p1HasMoved && p1HasBlocked) {
            changeState(GameState.P2_CHOICE); 
            if (mapPanel != null) mapPanel.clearIndicators();
        }
    }

    private void checkP2Finished() {
        if (p2HasMoved && p2HasBlocked) {
            changeState(GameState.RESOLUTION); 
            if (mapPanel != null) mapPanel.clearIndicators();
            executeResolution();
        }
    }

    private void executeResolution() {
        if (currentState == GameState.MENU || currentState == GameState.END_GAME || 
            currentState == GameState.SURVIVOR_VICTORY || currentState == GameState.ZOMBIE_VICTORY) return; 
        
        gameManager.getSurvivor().resetMoveStatus();
        gameManager.getZombie().resetMoveStatus();

        gameManager.resolveGlobalTurn();
        
        gameManager.getSurvivor().setDoubleMoveBonus(false);
        gameManager.getZombie().setDoubleMoveBonus(false);
        
        List<Crate> casseDaRimuovere = new ArrayList<>();
        boolean raccoltaAvvenuta = false;

        for (Crate cassa : gameMap.getCrates()) {
            boolean sSuC = (gameMap.getSurvivor().getX() == cassa.getX() && gameMap.getSurvivor().getY() == cassa.getY());
            boolean zSuC = (gameMap.getZombie().getX() == cassa.getX() && gameMap.getZombie().getY() == cassa.getY());
            
            if (sSuC || zSuC) {
                casseDaRimuovere.add(cassa);
                raccoltaAvvenuta = true;

                int roll = random.nextInt(2); 
                String picker = sSuC ? "Sopravvissuto" : "Zombie";
                Color coloreTema = sSuC ? new Color(255, 170, 0) : new Color(170, 0, 255); // Arancione per Survivor, Viola per Zombie

                if (roll == 0) {
                    if (sSuC) gameManager.getSurvivor().setDoubleMoveBonus(true);
                    if (zSuC) gameManager.getZombie().setDoubleMoveBonus(true);
                    
                    // USIAMO IL NUOVO POPUP CUSTOM
                    mostraPopupBonus("DOUBLE MOVEMENT!", 
                                     "Puoi muoverti fino a <b>2 caselle</b> in questo turno.", 
                                     "/speed_bonus.png", coloreTema, picker);
                } else {
                    if (sSuC) gameManager.getZombie().setCanMove(false);
                    if (zSuC) gameManager.getSurvivor().setCanMove(false);

                    String frozen = sSuC ? "Zombie" : "Sopravvissuto";
                    
                    mostraPopupBonus("STOP OPPONENT!", 
                                     "L'avversario (" + frozen + ") è <b>congelato</b> per un turno.", 
                                     "/freeze_bonus.png", new Color(0, 200, 255), picker);
                }
            }
        }
        
        for (Crate c : casseDaRimuovere) gameMap.removeCrate(c);

        if (raccoltaAvvenuta) turnsSinceLastEvent = 0; 
        else turnsSinceLastEvent++;

        if (turnsSinceLastEvent >= SPAWN_INTERVAL) {
            if (gameMap.getCrates().size() < 2) { 
                gameMap.spawnRandomCrate();
                turnsSinceLastEvent = 0; 
            }
        }
        
        if (gameMap.getKey() != null) {
            int kX = gameMap.getKey().getX() / 64;
            int kY = gameMap.getKey().getY() / 64;
            if (gameMap.getSurvivor().getX() == kX && gameMap.getSurvivor().getY() == kY) {
                gameMap.getSurvivor().collectKey();
                gameMap.setKey(null);
            }
        }
        
        if (mapPanel != null) mapPanel.repaint(); 

        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;

        checkFinalConditions();
    }

    // =========================================================================
    // 🎨 NUOVO METODO: POPUP STILE POST-APOCALITTICO
    // =========================================================================
    private void mostraPopupBonus(String titolo, String messaggio, String iconPath, Color coloreBordo, String picker) {
        if (mapPanel == null) return;
        
        Window parentWindow = SwingUtilities.getWindowAncestor(mapPanel);
        JDialog dialog = new JDialog((Frame) parentWindow, true); 
        dialog.setUndecorated(true); 
        dialog.setBackground(new Color(0, 0, 0, 0)); 

        // Pannello effetto vetro scuro
        JPanel glassPanel = new JPanel(new BorderLayout(20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30, 230)); // Grigio scurissimo
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(coloreBordo); // Bordo colorato in base al bonus
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        }; 
        glassPanel.setOpaque(false);
        glassPanel.setBorder(new EmptyBorder(20, 30, 20, 30)); 

        // Icona a sinistra
        JLabel lblIcon = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblIcon.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Icona non trovata: " + iconPath);
        }

        // Testo formattato in HTML
        String hexColor = String.format("#%02x%02x%02x", coloreBordo.getRed(), coloreBordo.getGreen(), coloreBordo.getBlue());
        String htmlText = "<html><div style='font-family: Segoe UI; color: #E0E0E0; width: 250px;'>"
                        + "<span style='font-size: 10px; color: #888888;'>Cassa raccolta da " + picker + "</span><br>"
                        + "<h2 style='color: " + hexColor + "; margin-top: 5px; margin-bottom: 5px; font-size: 22px; text-transform: uppercase; text-shadow: 2px 2px black;'>" + titolo + "</h2>"
                        + "<p style='font-size: 14px; margin-top: 0;'>" + messaggio + "</p>"
                        + "</div></html>";
        JLabel lblText = new JLabel(htmlText);

        // Bottone personalizzato
        JButton btnOk = new JButton("CONTINUA");
        btnOk.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOk.setForeground(Color.WHITE);
        btnOk.setBackground(new Color(50, 50, 50));
        btnOk.setFocusPainted(false);
        btnOk.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(coloreBordo, 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnOk.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnOk.setBackground(coloreBordo.darker()); }
            public void mouseExited(MouseEvent e) { btnOk.setBackground(new Color(50, 50, 50)); }
        });

        btnOk.addActionListener(e -> {
            dialog.dispose();
            mapPanel.requestFocusInWindow(); // Ridiamo il focus alla mappa per muoversi con le frecce!
        });

        JPanel pnlBottone = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottone.setOpaque(false);
        pnlBottone.add(btnOk);

        glassPanel.add(lblIcon, BorderLayout.WEST);
        glassPanel.add(lblText, BorderLayout.CENTER);
        glassPanel.add(pnlBottone, BorderLayout.SOUTH);

        dialog.setContentPane(glassPanel);
        dialog.pack(); 
        dialog.setLocationRelativeTo(parentWindow); 
        dialog.setVisible(true); 
    }
    // =========================================================================

    private void checkFinalConditions() {
        if (gameMap.getSurvivor().getX() == gameMap.getZombie().getX() && 
            gameMap.getSurvivor().getY() == gameMap.getZombie().getY()) {
            changeState(GameState.ZOMBIE_VICTORY);
        } else if (checkVictoryCondition()) {
            changeState(GameState.SURVIVOR_VICTORY); 
        } else {
            changeState(GameState.P1_CHOICE); 
            if (mapPanel != null) {
                Entity currentP1 = survivorIsP1 ? gameMap.getSurvivor() : gameMap.getZombie();
                int range = currentP1.hasDoubleMoveBonus() ? 2 : 1;
                mapPanel.evidenziaMossePersonaggio(currentP1.getX(), currentP1.getY(), range);
            }
        }
    }

    private boolean checkVictoryCondition() {
        if (gameMap == null || gameMap.getSurvivor() == null || gameMap.getDoor() == null) return false;
        Survivor s = gameMap.getSurvivor();
        Door d = gameMap.getDoor();
        return (s.getY() == d.getGridRow()) && 
               (s.getX() == d.getGridColLeft() || s.getX() == d.getGridColRight()) && 
               s.hasKey(); 
    }

    public GameState getCurrentState() { return currentState; }
    public void setP1HasBlocked(boolean b) { this.p1HasBlocked = b; }
    public void setP2HasBlocked(boolean b) { this.p2HasBlocked = b; }

    public void resetGame() {
        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;
        turnsSinceLastEvent = 0; 
        if (gameMap.getSurvivor() != null) {
            gameMap.getSurvivor().cancelPlannedMove();
            gameMap.getSurvivor().cancelPlannedBlock();
            gameMap.getSurvivor().dropKey();
            gameMap.getSurvivor().resetMoveStatus();
            gameMap.getSurvivor().setDoubleMoveBonus(false);
        }
        if (gameMap.getZombie() != null) {
            gameMap.getZombie().cancelPlannedMove();
            gameMap.getZombie().cancelPlannedBlock();
            gameMap.getZombie().resetMoveStatus();
            gameMap.getZombie().setDoubleMoveBonus(false);
        }
        if (gameMap.getCrates() != null) gameMap.getCrates().clear();
        changeState(GameState.MENU);
    }
}