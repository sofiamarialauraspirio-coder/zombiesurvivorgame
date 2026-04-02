package controller;

import model.Entity;
import model.Survivor;   
import model.GameManager;
import model.GameMap;
import model.Crate;
import model.Door;     
import view.MapPanel; 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
                System.out.println("❄️ P1 CONGELATO! Salto il turno automaticamente.");
                p1HasMoved = true;
                p1HasBlocked = true;
                checkP1Finished(); 
                return; 
            } else {
                evidenziaTurnoCorrente();
            }
        } 
        else if (newState == GameState.P2_CHOICE) {
            boolean p2Frozen = survivorIsP1 ? !gameManager.getZombie().canMove() : !gameManager.getSurvivor().canMove();
            if (p2Frozen) {
                System.out.println("❄️ P2 CONGELATO! Salto il turno automaticamente.");
                p2HasMoved = true;
                p2HasBlocked = true;
                checkP2Finished(); 
                return;
            } else {
                evidenziaTurnoCorrente();
            }
        }
    }

    private void evidenziaTurnoCorrente() {
        if (mapPanel != null) {
            Entity current = isSurvivorTurn() ? gameMap.getSurvivor() : gameMap.getZombie();
            int range = current.hasDoubleMoveBonus() ? 2 : 1;
            mapPanel.evidenziaMossePersonaggio(current.getX(), current.getY(), range);
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

        Entity current = (currentState == GameState.P1_CHOICE) ? 
            (survivorIsP1 ? gameManager.getSurvivor() : gameManager.getZombie()) : 
            (survivorIsP1 ? gameManager.getZombie() : gameManager.getSurvivor());

        current.planMove(targetX, targetY);

        if (currentState == GameState.P1_CHOICE) {
            p1HasMoved = true;
            checkP1Finished();
        } else if (currentState == GameState.P2_CHOICE) {
            p2HasMoved = true;
            checkP2Finished();
        }
    }

    public boolean confirmBlock(int targetX, int targetY) {
        Entity current = (currentState == GameState.P1_CHOICE) ? 
            (survivorIsP1 ? gameManager.getSurvivor() : gameManager.getZombie()) : 
            (survivorIsP1 ? gameManager.getZombie() : gameManager.getSurvivor());

        current.planBlock(targetX, targetY);

        if (current.getPlannedBlocks().size() >= current.getNumeroBlocchiPossibili()) {
            forceFinishBlock();
            return true; 
        }
        return false; 
    }

    public void forceFinishBlock() {
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
            if (mapPanel != null) mapPanel.clearIndicators();
            changeState(GameState.P2_CHOICE); 
        }
    }

    private void checkP2Finished() {
        if (p2HasMoved && p2HasBlocked) {
            if (mapPanel != null) mapPanel.clearIndicators();
            changeState(GameState.RESOLUTION); 
            executeResolution();
        }
    }

    private void executeResolution() {
        if (currentState == GameState.MENU || currentState == GameState.END_GAME || 
            currentState == GameState.SURVIVOR_VICTORY || currentState == GameState.ZOMBIE_VICTORY) return; 
        
        gameManager.getSurvivor().resetMoveStatus();
        gameManager.getZombie().resetMoveStatus();
        gameMap.getSurvivor().resetMoveStatus(); 
        gameMap.getZombie().resetMoveStatus();   

        gameManager.resolveGlobalTurn();
        
        gameManager.getSurvivor().setDoubleMoveBonus(false);
        gameManager.getZombie().setDoubleMoveBonus(false);
        gameMap.getSurvivor().setDoubleMoveBonus(false); 
        gameMap.getZombie().setDoubleMoveBonus(false);   
        
        // 🧱 TUTTO A UNO A FINE TURNO! (Trappola Invisibile)
        gameManager.getSurvivor().setNumeroBlocchiPossibili(1);
        gameManager.getZombie().setNumeroBlocchiPossibili(1);
        gameMap.getSurvivor().setNumeroBlocchiPossibili(1);
        gameMap.getZombie().setNumeroBlocchiPossibili(1);
        
        List<Crate> casseDaRimuovere = new ArrayList<>();
        boolean raccoltaAvvenuta = false;

        for (Crate cassa : gameMap.getCrates()) {
            boolean sSuC = (gameMap.getSurvivor().getX() == cassa.getX() && gameMap.getSurvivor().getY() == cassa.getY());
            boolean zSuC = (gameMap.getZombie().getX() == cassa.getX() && gameMap.getZombie().getY() == cassa.getY());
            
            if (sSuC || zSuC) {
                casseDaRimuovere.add(cassa);
                raccoltaAvvenuta = true;

                int roll = random.nextInt(3) + 1; 
                String picker = sSuC ? "Sopravvissuto" : "Zombie";
                Color coloreTema = sSuC ? new Color(255, 170, 0) : new Color(170, 0, 255); 

                if (roll == 1) {
                    if (sSuC) { gameManager.getSurvivor().setDoubleMoveBonus(true); gameMap.getSurvivor().setDoubleMoveBonus(true); }
                    if (zSuC) { gameManager.getZombie().setDoubleMoveBonus(true); gameMap.getZombie().setDoubleMoveBonus(true); }
                    mostraPopupBonus("DOUBLE MOVEMENT!", "Puoi muoverti fino a <b>2 caselle</b> in questo turno.", "/speed_bonus.png", coloreTema, picker);
                } 
                else if (roll == 2) {
                    if (sSuC) { gameManager.getZombie().setCanMove(false); gameMap.getZombie().setCanMove(false); }
                    if (zSuC) { gameManager.getSurvivor().setCanMove(false); gameMap.getSurvivor().setCanMove(false); }
                    String frozen = sSuC ? "Zombie" : "Sopravvissuto";
                    mostraPopupBonus("STOP OPPONENT!", "L'avversario (" + frozen + ") è <b>congelato</b> per un turno.", "/freeze_bonus.png", new Color(0, 200, 255), picker); 
                }
                else if (roll == 3) {
                    // 🎉 Assegna 2 blocchi. Il MapPanel leggerà questo "2" e disegnerà i muri veri!
                    if (sSuC) { gameManager.getSurvivor().setNumeroBlocchiPossibili(2); gameMap.getSurvivor().setNumeroBlocchiPossibili(2); }
                    if (zSuC) { gameManager.getZombie().setNumeroBlocchiPossibili(2); gameMap.getZombie().setNumeroBlocchiPossibili(2); }
                    mostraPopupBonus("DOUBLE BLOCK!", "Puoi posizionare <b>2 blocchi</b> in questo turno.", "/speed_bonus.png", new Color(100, 255, 100), picker); 
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

    private void mostraPopupBonus(String titolo, String messaggio, String iconPath, Color coloreBordo, String picker) {
        if (mapPanel == null) return;
        
        Window parentWindow = SwingUtilities.getWindowAncestor(mapPanel);
        JDialog dialog = new JDialog((Frame) parentWindow, true); 
        dialog.setUndecorated(true); 
        dialog.setBackground(new Color(0, 0, 0, 0)); 

        JPanel glassPanel = new JPanel(new BorderLayout(20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 30, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(coloreBordo); 
                g2.setStroke(new BasicStroke(3.0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        }; 
        glassPanel.setOpaque(false);
        glassPanel.setBorder(new EmptyBorder(20, 30, 20, 30)); 

        JLabel lblIcon = new JLabel();
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = originalIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblIcon.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Icona non trovata: " + iconPath);
        }

        String hexColor = String.format("#%02x%02x%02x", coloreBordo.getRed(), coloreBordo.getGreen(), coloreBordo.getBlue());
        String htmlText = "<html><div style='font-family: Segoe UI; color: #E0E0E0; width: 250px;'>"
                        + "<span style='font-size: 10px; color: #888888;'>Cassa raccolta da " + picker + "</span><br>"
                        + "<h2 style='color: " + hexColor + "; margin-top: 5px; margin-bottom: 5px; font-size: 22px; text-transform: uppercase; text-shadow: 2px 2px black;'>" + titolo + "</h2>"
                        + "<p style='font-size: 14px; margin-top: 0;'>" + messaggio + "</p>"
                        + "</div></html>";
        JLabel lblText = new JLabel(htmlText);

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
            mapPanel.requestFocusInWindow(); 
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

    private void checkFinalConditions() {
        if (gameMap.getSurvivor().getX() == gameMap.getZombie().getX() && 
            gameMap.getSurvivor().getY() == gameMap.getZombie().getY()) {
            changeState(GameState.ZOMBIE_VICTORY);
        } else if (checkVictoryCondition()) {
            changeState(GameState.SURVIVOR_VICTORY); 
        } else {
            changeState(GameState.P1_CHOICE); 
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
            gameManager.getSurvivor().resetMoveStatus();
            gameManager.getSurvivor().setDoubleMoveBonus(false);
            gameManager.getSurvivor().setNumeroBlocchiPossibili(1);
        }
        if (gameMap.getZombie() != null) {
            gameMap.getZombie().cancelPlannedMove();
            gameMap.getZombie().cancelPlannedBlock();
            gameManager.getZombie().resetMoveStatus();
            gameManager.getZombie().setDoubleMoveBonus(false);
            gameManager.getZombie().setNumeroBlocchiPossibili(1);
        }
        if (gameMap.getCrates() != null) gameMap.getCrates().clear();
        changeState(GameState.MENU);
        if(mapPanel != null) mapPanel.clearIndicators();
    }
}