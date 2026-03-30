package controller;

import model.GameManager;
import model.GameMap;
import view.MapPanel; 

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

    // ==========================================================
    // STORY 32: TRANSITION LOGIC E ROBUSTNESS
    // ==========================================================
    private void changeState(GameState newState) {
        // ROBUSTNESS: Impediamo transizioni illegali (es. da MENU direttamente a RESOLUTION)
        if (this.currentState == GameState.MENU && (newState == GameState.RESOLUTION || newState == GameState.P2_CHOICE)) {
            System.err.println("❌ ERRORE: Transizione illegale da " + this.currentState + " a " + newState);
            return;
        }

        System.out.println("🔄 State Machine: Cambio stato da [" + this.currentState + "] ➡️ [" + newState + "]");
        this.currentState = newState;
    }

    public void startGame() {
        if (currentState == GameState.MENU) {
            changeState(GameState.P1_CHOICE); // Usiamo il nuovo metodo
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
            changeState(GameState.P2_CHOICE); // Usiamo il nuovo metodo
            if (mapPanel != null) mapPanel.clearIndicators();
        }
    }

    private void checkP2Finished() {
        if (p2HasMoved && p2HasBlocked) {
            changeState(GameState.RESOLUTION); // Usiamo il nuovo metodo
            if (mapPanel != null) mapPanel.clearIndicators();
            executeResolution();
        }
    }

    private void executeResolution() {
        // 1. Aggiunto ZOMBIE_VICTORY qui per bloccare il gioco quando finisce
        if (currentState == GameState.MENU || currentState == GameState.END_GAME || currentState == GameState.SURVIVOR_VICTORY || currentState == GameState.ZOMBIE_VICTORY) return; 
        
        gameManager.resolveGlobalTurn();
        
        if (gameMap.getKey() != null) {
            int keyGridX = gameMap.getKey().getX() / 64;
            int keyGridY = gameMap.getKey().getY() / 64;
            
            if (gameMap.getSurvivor().getX() == keyGridX && gameMap.getSurvivor().getY() == keyGridY) {
                gameMap.getSurvivor().collectKey();
                gameMap.setKey(null);
                System.out.println("🔑 IL SOPRAVVISSUTO HA RACCOLTO LA CHIAVE!");
            }
        }
        
        if (mapPanel != null) mapPanel.repaint(); 

        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;

        System.out.println("📍 FINE TURNO -> Sopravvissuto è a: (" + gameMap.getSurvivor().getX() + ", " + gameMap.getSurvivor().getY() + ") | Zombie è a: (" + gameMap.getZombie().getX() + ", " + gameMap.getZombie().getY() + ")");

        // --- 🟢 INIZIO CONTROLLO VITTORIE ---
        
        // PRIORITÀ 1: Lo Zombie mangia il Sopravvissuto
        if (gameMap.getSurvivor().getX() == gameMap.getZombie().getX() && 
            gameMap.getSurvivor().getY() == gameMap.getZombie().getY()) {
            
            changeState(GameState.ZOMBIE_VICTORY);
            System.out.println("🧟‍♂️ LO ZOMBIE HA MANGIATO IL SOPRAVVISSUTO! HA VINTO!");
            
            if (mapPanel != null) {
                mapPanel.repaint();
            }
        } 
        // PRIORITÀ 2: Il Sopravvissuto scappa (viene controllato SOLO se lo Zombie non l'ha mangiato)
        else if (checkVictoryCondition()) {
            changeState(GameState.SURVIVOR_VICTORY); 
            System.out.println("🏆 IL SOPRAVVISSUTO HA VINTO!");
            
            if (mapPanel != null) {
                mapPanel.repaint();
            }
        } 
        // NESSUNA VITTORIA: Il gioco continua al prossimo turno
        else {
            changeState(GameState.P1_CHOICE); 

            if (mapPanel != null) {
                if (survivorIsP1) {
                    mapPanel.evidenziaMossePersonaggio(gameMap.getSurvivor().getX(), gameMap.getSurvivor().getY());
                } else {
                    mapPanel.evidenziaMossePersonaggio(gameMap.getZombie().getX(), gameMap.getZombie().getY());
                }
            }
            
            System.out.println("🔄 Nuovo turno iniziato! Tocca di nuovo a P1.");
        }
        // --- FINE CONTROLLO VITTORIE ---
    }

    private boolean checkVictoryCondition() {
        if (gameMap == null || gameMap.getSurvivor() == null || gameMap.getDoor() == null) {
            return false;
        }

        model.Survivor s = gameMap.getSurvivor();
        model.Door d = gameMap.getDoor();

        boolean isOnDoor = (s.getY() == d.getGridRow()) && 
                           (s.getX() == d.getGridColLeft() || s.getX() == d.getGridColRight());

        boolean hasKey = s.hasKey();

        return isOnDoor && hasKey; 
    }

    public GameState getCurrentState() { return currentState; }
    public void setP1HasBlocked(boolean b) { this.p1HasBlocked = b; }
    public void setP2HasBlocked(boolean b) { this.p2HasBlocked = b; }
}