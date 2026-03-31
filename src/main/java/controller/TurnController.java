package controller;

import model.GameManager;
import model.GameMap;
import model.Crate;
import view.MapPanel; 
import java.util.ArrayList;
import java.util.List;

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

    // --- STORY 17: TIMER DINAMICO ---
    private int turnsSinceLastEvent = 0; 
    private static final int SPAWN_INTERVAL = 3; 

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

    // --- STORY 32: ROBUSTEZZA ---
    public void changeState(GameState newState) {
        if (this.currentState == GameState.MENU && (newState == GameState.RESOLUTION || newState == GameState.P2_CHOICE)) {
            System.err.println("❌ ERRORE: Transizione illegale da " + this.currentState + " a " + newState);
            return;
        }
        System.out.println("🔄 State Machine: [" + this.currentState + "] ➡️ [" + newState + "]");
        this.currentState = newState;
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
        
        gameManager.resolveGlobalTurn();
        
        // --- 1. RIMOZIONE CASSE E RESET TIMER ---
        List<Crate> casseDaRimuovere = new ArrayList<>();
        boolean raccoltaAvvenuta = false;

        for (Crate cassa : gameMap.getCrates()) {
            boolean sSuC = (gameMap.getSurvivor().getX() == cassa.getX() && gameMap.getSurvivor().getY() == cassa.getY());
            boolean zSuC = (gameMap.getZombie().getX() == cassa.getX() && gameMap.getZombie().getY() == cassa.getY());
            
            if (sSuC || zSuC) {
                casseDaRimuovere.add(cassa);
                raccoltaAvvenuta = true;
            }
        }
        
        for (Crate c : casseDaRimuovere) {
            gameMap.removeCrate(c);
            System.out.println("📦 CASSA RACCOLTA! Timer resettato.");
        }

        // --- 2. LOGICA TIMER (Reset se raccolta) ---
        if (raccoltaAvvenuta) {
            turnsSinceLastEvent = 0; 
        } else {
            turnsSinceLastEvent++;
        }

        System.out.println("⏳ Timer Cassa: " + turnsSinceLastEvent + "/3");

        if (turnsSinceLastEvent >= SPAWN_INTERVAL) {
            if (gameMap.getCrates().size() < 2) { 
                gameMap.spawnRandomCrate();
                turnsSinceLastEvent = 0; 
            }
        }
        
        // --- 3. LOGICA CHIAVE ---
        if (gameMap.getKey() != null) {
            int kX = gameMap.getKey().getX() / 64;
            int kY = gameMap.getKey().getY() / 64;
            if (gameMap.getSurvivor().getX() == kX && gameMap.getSurvivor().getY() == kY) {
                gameMap.getSurvivor().collectKey();
                gameMap.setKey(null);
                System.out.println("🔑 CHIAVE RACCOLTA!");
            }
        }
        
        if (mapPanel != null) mapPanel.repaint(); 

        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;

        checkFinalConditions();
    }

    private void checkFinalConditions() {
        if (gameMap.getSurvivor().getX() == gameMap.getZombie().getX() && 
            gameMap.getSurvivor().getY() == gameMap.getZombie().getY()) {
            
            changeState(GameState.ZOMBIE_VICTORY);
            System.out.println("🧟‍♂️ LO ZOMBIE HA VINTO!");
            
        } else if (checkVictoryCondition()) {
            
            changeState(GameState.SURVIVOR_VICTORY); 
            System.out.println("🏆 IL SOPRAVVISSUTO HA VINTO!");
            
        } else {
            changeState(GameState.P1_CHOICE); 
            if (mapPanel != null) {
                if (survivorIsP1) mapPanel.evidenziaMossePersonaggio(gameMap.getSurvivor().getX(), gameMap.getSurvivor().getY());
                else mapPanel.evidenziaMossePersonaggio(gameMap.getZombie().getX(), gameMap.getZombie().getY());
            }
        }
    }

    private boolean checkVictoryCondition() {
        if (gameMap == null || gameMap.getSurvivor() == null || gameMap.getDoor() == null) return false;
        model.Survivor s = gameMap.getSurvivor();
        model.Door d = gameMap.getDoor();
        boolean isOnDoor = (s.getY() == d.getGridRow()) && 
                           (s.getX() == d.getGridColLeft() || s.getX() == d.getGridColRight());
        return isOnDoor && s.hasKey(); 
    }

    public GameState getCurrentState() { return currentState; }
    public void setP1HasBlocked(boolean b) { this.p1HasBlocked = b; }
    public void setP2HasBlocked(boolean b) { this.p2HasBlocked = b; }

    // --- STORY 27: RESET TOTALE (PULITO) ---
    public void resetGame() {
        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;
        turnsSinceLastEvent = 0; 

        if (gameMap.getSurvivor() != null) {
            gameMap.getSurvivor().cancelPlannedMove();
            gameMap.getSurvivor().cancelPlannedBlock();
            gameMap.getSurvivor().dropKey();
        }
        if (gameMap.getZombie() != null) {
            gameMap.getZombie().cancelPlannedMove();
            gameMap.getZombie().cancelPlannedBlock();
        }

        if (gameMap.getCrates() != null) {
            gameMap.getCrates().clear();
        }

        changeState(GameState.MENU);
        System.out.println("🧹 RESET COMPLETO EFFETTUATO.");
    }
}