package controller;

import model.GameManager;
import model.GameMap;

public class TurnController {
    private GameManager gameManager;
    private GameMap gameMap;

    // ==========================================
    // STATE INTEGRITY (La nuova Macchina a Stati)
    // ==========================================
    public enum GameState {
        MENU,
        P1_CHOICE,    // Turno del Sopravvissuto
        P2_CHOICE,    // Turno dello Zombie
        RESOLUTION,   // Esecuzione simultanea
        END_GAME      // Partita finita
    }

    private GameState currentState;

    // Variabili per il "Resolution Trigger" (Mossa + Azione)
    private boolean p1HasMoved = false;
    private boolean p1HasBlocked = false; // Da collegare alla logica del "Block"
    private boolean p2HasMoved = false;
    private boolean p2HasBlocked = false; // Da collegare alla logica del "Block"

    public TurnController(GameManager gameManager, GameMap gameMap) {
        this.gameManager = gameManager;
        this.gameMap = gameMap;
        this.currentState = GameState.MENU; // Il gioco parte sempre dal Menu
    }

    // Passaggio da MENU a Gioco (da chiamare quando si clicca "Start")
    public void startGame() {
        if (currentState == GameState.MENU) {
            currentState = GameState.P1_CHOICE;
            System.out.println("Partita Iniziata! Turno P1 (Sopravvissuto).");
        }
    }

    // ==========================================
    // INPUT PROTECTION & RESOLUTION TRIGGER
    // ==========================================
    public void confirmMove(int targetX, int targetY) {
        // INPUT PROTECTION: Ignoriamo i click se non siamo in fase di scelta
        if (currentState != GameState.P1_CHOICE && currentState != GameState.P2_CHOICE) {
            System.out.println("❌ Input bloccato: Non è il momento di scegliere le mosse!");
            return;
        }

        // Controllo Muri (NP-23)
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

    // Metodo fittizio per il "Block" (da implementare quando farete la logica delle barricate/azioni)
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

    // ==========================================
    // STRICT SEQUENTIAL FLOW
    // ==========================================
    private void checkP1Finished() {
        // Passiamo a P2 solo se P1 ha fatto ENTRAMBE le cose (Move + Block)
        // NOTA: Se per ora volete testare solo il movimento, cambia i boolean in (p1HasMoved)
        if (p1HasMoved && p1HasBlocked) {
            currentState = GameState.P2_CHOICE;
            System.out.println("P1 ha finito. Turno P2 (Zombie).");
        }
    }

    private void checkP2Finished() {
        if (p2HasMoved && p2HasBlocked) {
            currentState = GameState.RESOLUTION;
            System.out.println("Entrambi hanno scelto. Fase di RESOLUTION!");
            executeResolution();
        }
    }

    // ==========================================
    // ACTION GATING & END CONDITION CHECK
    // ==========================================
    private void executeResolution() {
        if (currentState == GameState.MENU || currentState == GameState.END_GAME) {
            return; // Action Gating di sicurezza
        }

        // 1. Eseguiamo le mosse (Story 15 / NP-19)
        gameManager.resolveGlobalTurn();

        // 2. Resettiamo le variabili per il prossimo turno
        p1HasMoved = false; p1HasBlocked = false;
        p2HasMoved = false; p2HasBlocked = false;

        // 3. End Condition Check: Il gioco è finito?
        if (checkVictoryCondition()) {
            currentState = GameState.END_GAME;
            System.out.println("🏆 PARTITA FINITA!");
        } else {
            // Se nessuno ha vinto, si ricomincia da P1
            currentState = GameState.P1_CHOICE;
            System.out.println("Nuovo turno globale. Tocca a P1.");
        }
    }

    // Da espandere con la vera logica di vittoria (es. P1 tocca l'uscita, P2 mangia P1)
    private boolean checkVictoryCondition() {
        return false; // Per ora la partita continua all'infinito
    }

    public GameState getCurrentState() { return currentState; }
    
    // Per i test
    public void setP1HasBlocked(boolean b) { this.p1HasBlocked = b; }
    public void setP2HasBlocked(boolean b) { this.p2HasBlocked = b; }
}