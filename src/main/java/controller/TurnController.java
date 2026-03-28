package controller;

import java.awt.Point;
import model.Entity;
import model.GameManager;
import model.GameMap;

public class TurnController {
    private GameManager gameManager;
    private GameMap gameMap;

    // ==========================================
    // STATE INTEGRITY (Macchina a Stati)
    // ==========================================
    public enum GameState {
        MENU,
        P1_CHOICE,    // Turno del Sopravvissuto
        P2_CHOICE,    // Turno dello Zombie
        RESOLUTION,   // Esecuzione simultanea
        END_GAME      // Partita finita
    }

    private GameState currentState;

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
    // USER STORY: PIANIFICAZIONE COMPLETA (Move + Block)
    // ==========================================
    public boolean setPlannedActions(Point move, Point block) {
        // 1. Input Protection: Siamo nel turno di qualcuno?
        if (currentState != GameState.P1_CHOICE && currentState != GameState.P2_CHOICE) {
            System.out.println("❌ Errore: Non è la fase di pianificazione.");
            return false; 
        }

        // Identifichiamo chi sta giocando
        Entity currentPlayer = (currentState == GameState.P1_CHOICE) ? gameManager.getSurvivor() : gameManager.getZombie();

        // 2. Integrità della Scelta (Non sovrapponibili)
        if (move.equals(block)) {
            System.out.println("❌ Errore: Mossa e Blocco non possono coincidere.");
            return false;
        }

        // 3. Validazione Destinazione (Muro e Adiacenza)
        // Attenzione: isWalkable usa (riga, colonna) -> (y, x)
        if (!gameMap.isWalkable(move.y, move.x)) {
            System.out.println("❌ Errore: Non puoi muoverti su un muro o fuori mappa.");
            return false;
        }
        
        // Calcolo adiacenza (Distanza di Manhattan)
        int distance = Math.abs(move.x - currentPlayer.getX()) + Math.abs(move.y - currentPlayer.getY());
        int maxDist = currentPlayer.hasDoubleMoveBonus() ? 2 : 1; // Bonus gestione NP-19
        if (distance > maxDist || distance == 0) {
            System.out.println("❌ Errore: Casella di destinazione troppo lontana.");
            return false;
        }

        // 4. Validazione Blocco (Muro e Occupata)
        if (!gameMap.isWalkable(block.y, block.x)) {
            System.out.println("❌ Errore: Non puoi bloccare una casella già ostruita.");
            return false;
        }
        // Controlliamo che non ci sia sopra un giocatore
        if ((block.x == gameManager.getSurvivor().getX() && block.y == gameManager.getSurvivor().getY()) ||
            (block.x == gameManager.getZombie().getX() && block.y == gameManager.getZombie().getY())) {
            System.out.println("❌ Errore: Non puoi bloccare una casella con un giocatore sopra.");
            return false;
        }

        // SE ARRIVIAMO QUI: TUTTO VALIDO! Memorizziamo le scelte 💾
        currentPlayer.planMove(move.x, move.y);
        currentPlayer.planBlock(block.x, block.y);

        // 5. Conferma e Passaggio Turno
        if (currentState == GameState.P1_CHOICE) {
            currentState = GameState.P2_CHOICE;
            System.out.println("P1 ha confermato le azioni. Tocca a P2 (Zombie).");
        } else {
            currentState = GameState.RESOLUTION;
            System.out.println("P2 ha confermato. Entriamo in fase di RESOLUTION!");
            // Inneschiamo la risoluzione finale automaticamente
            executeResolution(); 
        }

        return true; 
    }

    // ==========================================
    // ACTION GATING & END CONDITION CHECK
    // ==========================================
    private void executeResolution() {
        if (currentState == GameState.MENU || currentState == GameState.END_GAME) {
            return; // Action Gating di sicurezza
        }

        // 1. Eseguiamo le mosse vere e proprie sui personaggi
        gameManager.resolveGlobalTurn();

        // 2. End Condition Check: Il gioco è finito?
        if (checkVictoryCondition()) {
            currentState = GameState.END_GAME;
            System.out.println("🏆 PARTITA FINITA!");
        } else {
            // Se nessuno ha vinto, si ricomincia da P1
            currentState = GameState.P1_CHOICE;
            System.out.println("Nuovo turno globale. Tocca a P1 (Sopravvissuto).");
        }
    }

    // Da espandere con la vera logica di vittoria 
    private boolean checkVictoryCondition() {
        return false; // Per ora la partita continua all'infinito
    }

    // GETTER
    public GameState getCurrentState() { return currentState; }
}