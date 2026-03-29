package controller;

import model.GameManager;
import model.GameMap;
import view.MapPanel; 

public class TurnController {
    private GameManager gameManager;
    private GameMap gameMap;
    private MapPanel mapPanel;

    public enum GameState { MENU, P1_CHOICE, P2_CHOICE, RESOLUTION, END_GAME }
    private GameState currentState;

    private boolean p1HasMoved = false;
    private boolean p1HasBlocked = false; 
    private boolean p2HasMoved = false;
    private boolean p2HasBlocked = false; 
    
    // NUOVO: L'arbitro ricorda chi hai scelto come P1!
    private boolean survivorIsP1 = true; 

    public TurnController(GameManager gameManager, GameMap gameMap) {
        this.gameManager = gameManager;
        this.gameMap = gameMap;
        this.currentState = GameState.MENU; 
    }

    public void setMapPanel(MapPanel mapPanel) { this.mapPanel = mapPanel; }
    public void setSurvivorIsP1(boolean isP1) { this.survivorIsP1 = isP1; }

    // NUOVO: Capisce di chi è il turno in modo dinamico
    public boolean isSurvivorTurn() {
        if (currentState == GameState.P1_CHOICE) return survivorIsP1;
        if (currentState == GameState.P2_CHOICE) return !survivorIsP1;
        return false;
    }

    public void startGame() {
        if (currentState == GameState.MENU) {
            currentState = GameState.P1_CHOICE;
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
            currentState = GameState.P2_CHOICE;
            if (mapPanel != null) mapPanel.clearIndicators();
        }
    }

    private void checkP2Finished() {
        if (p2HasMoved && p2HasBlocked) {
            currentState = GameState.RESOLUTION;
            if (mapPanel != null) mapPanel.clearIndicators();
            executeResolution();
        }
    }

    private void executeResolution() {
        if (currentState == GameState.MENU || currentState == GameState.END_GAME) return; 
        
        // 1. Risoluzione di scontri e calcolo mosse
        gameManager.resolveGlobalTurn();
        
        // 2. Ridisegniamo la mappa con le nuove posizioni
        if (mapPanel != null) mapPanel.repaint(); 

        // 3. Resettiamo le variabili per il nuovo turno
        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;

        // 4. Passiamo la palla di nuovo al Giocatore 1
        currentState = GameState.P1_CHOICE;

        // ==========================================================
        // IL PEZZO MANCANTE: Generiamo le mosse gialle per il NUOVO turno!
        // ==========================================================
        if (mapPanel != null) {
            if (survivorIsP1) {
                mapPanel.evidenziaMossePersonaggio(gameMap.getSurvivor().getX(), gameMap.getSurvivor().getY());
            } else {
                mapPanel.evidenziaMossePersonaggio(gameMap.getZombie().getX(), gameMap.getZombie().getY());
            }
        }
        
        System.out.println("🔄 Nuovo turno iniziato! Tocca di nuovo a P1.");
    }

    public GameState getCurrentState() { return currentState; }
    public void setP1HasBlocked(boolean b) { this.p1HasBlocked = b; }
    public void setP2HasBlocked(boolean b) { this.p2HasBlocked = b; }
}