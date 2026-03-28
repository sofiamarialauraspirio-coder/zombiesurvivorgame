package controller;

import model.GameManager;
import model.GameMap;
import view.MapPanel; // NP-21: Importiamo lo Schermo!

public class TurnController {
    private GameManager gameManager;
    private GameMap gameMap;
    
    // ==========================================
    // NP-21: Riferimento al MapPanel (Lo Schermo)
    // ==========================================
    private MapPanel mapPanel;

    public enum GameState {
        MENU,
        P1_CHOICE,    
        P2_CHOICE,    
        RESOLUTION,   
        END_GAME      
    }

    private GameState currentState;

    private boolean p1HasMoved = false;
    private boolean p1HasBlocked = false; 
    private boolean p2HasMoved = false;
    private boolean p2HasBlocked = false; 

    public TurnController(GameManager gameManager, GameMap gameMap) {
        this.gameManager = gameManager;
        this.gameMap = gameMap;
        this.currentState = GameState.MENU; 
    }

    // NP-21: Metodo per collegare la TV alla Console
    public void setMapPanel(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
    }

    public void startGame() {
        if (currentState == GameState.MENU) {
            currentState = GameState.P1_CHOICE;
            System.out.println("Partita Iniziata! Turno P1 (Sopravvissuto).");
        }
    }

    public void confirmMove(int targetX, int targetY) {
        if (currentState != GameState.P1_CHOICE && currentState != GameState.P2_CHOICE) {
            System.out.println("❌ Input bloccato: Non è il momento di scegliere le mosse!");
            return;
        }

        if (!gameMap.isWalkable(targetY, targetX)) {
            System.out.println("❌ Mossa Rifiutata: Hai colpito un muro o il bordo mappa!");
            return; 
        }

        if (currentState == GameState.P1_CHOICE) {
            gameManager.getSurvivor().planMove(targetX, targetY);
            p1HasMoved = true;
            System.out.println("P1 (Sopravvissuto) ha confermato la mossa.");
            checkP1Finished();

        } else if (currentState == GameState.P2_CHOICE) {
            gameManager.getZombie().planMove(targetX, targetY);
            p2HasMoved = true;
            System.out.println("P2 (Zombie) ha confermato la mossa.");
            checkP2Finished();
        }
    }

    public void confirmBlock() {
        if (currentState == GameState.P1_CHOICE) {
            p1HasBlocked = true;
            System.out.println("P1 ha confermato il Block.");
            checkP1Finished();
        } else if (currentState == GameState.P2_CHOICE) {
            p2HasBlocked = true;
            System.out.println("P2 ha confermato il Block.");
            checkP2Finished();
        }
    }

    private void checkP1Finished() {
        if (p1HasMoved && p1HasBlocked) {
            currentState = GameState.P2_CHOICE;
            System.out.println("P1 ha finito. Turno P2 (Zombie).");
            
            // ==========================================
            // NP-21: VISUAL RESET (Nascondiamo a P2 le scelte di P1)
            // ==========================================
            if (mapPanel != null) {
                mapPanel.clearIndicators();
            }
        }
    }

    private void checkP2Finished() {
        if (p2HasMoved && p2HasBlocked) {
            currentState = GameState.RESOLUTION;
            System.out.println("Entrambi hanno scelto. Fase di RESOLUTION!");
            
            // NP-21: VISUAL RESET (Puliamo tutto prima dell'esecuzione)
            if (mapPanel != null) {
                mapPanel.clearIndicators();
            }
            
            executeResolution();
        }
    }

    private void executeResolution() {
        if (currentState == GameState.MENU || currentState == GameState.END_GAME) {
            return; 
        }

        // 1. Risoluzione di scontri e calcolo mosse
        gameManager.resolveGlobalTurn();

        // ==========================================
        // STORY 15: SIMULTANEOUS VIEW REFRESH
        // Diciamo allo schermo di ridisegnare i personaggi nelle nuove posizioni!
        // ==========================================
        if (mapPanel != null) {
            mapPanel.repaint(); 
        }

        // 2. Resettiamo le variabili per il prossimo turno
        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;

        // 3. End Condition Check
        if (checkVictoryCondition()) {
            currentState = GameState.END_GAME;
            System.out.println("🏆 PARTITA FINITA!");
        } else {
            currentState = GameState.P1_CHOICE;
            System.out.println("Nuovo turno globale. Tocca a P1.");
        }
    }

    private boolean checkVictoryCondition() {
        return false; 
    }

    public GameState getCurrentState() { return currentState; }
    
    public void setP1HasBlocked(boolean b) { this.p1HasBlocked = b; }
    public void setP2HasBlocked(boolean b) { this.p2HasBlocked = b; }
}