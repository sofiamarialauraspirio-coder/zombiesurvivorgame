package controller;

import model.GameManager;
import model.GameMap;

public class TurnController {
    private GameManager gameManager;
    private GameMap gameMap; // Aggiungiamo la Mappa!

    public enum PlayerTurn {
        SURVIVOR,
        ZOMBIE
    }

    private PlayerTurn currentTurn;

    // Aggiorniamo il costruttore per ricevere anche la GameMap
    public TurnController(GameManager gameManager, GameMap gameMap) {
        this.gameManager = gameManager;
        this.gameMap = gameMap;
        this.currentTurn = PlayerTurn.SURVIVOR;
    }

    public void confirmMove(int targetX, int targetY) {
        if (!gameMap.isWalkable(targetY, targetX)) {
            System.out.println("❌ Mossa Rifiutata: Hai colpito un muro o il bordo mappa!");
            // Usciamo dal metodo (return).
            // Niente viene salvato e il turno NON passa (State Persistence).
            return; 
        }

        // --- Logica della NP-19 (invariata) ---
        if (currentTurn == PlayerTurn.SURVIVOR) {
            gameManager.getSurvivor().planMove(targetX, targetY);
            currentTurn = PlayerTurn.ZOMBIE;
            System.out.println("Sopravvissuto ha confermato. Tocca allo Zombie.");
            
        } else if (currentTurn == PlayerTurn.ZOMBIE) {
            gameManager.getZombie().planMove(targetX, targetY);
            System.out.println("Zombie ha confermato. Risoluzione turno globale!");
            
            gameManager.resolveGlobalTurn();
            currentTurn = PlayerTurn.SURVIVOR;
        }
    }

    public PlayerTurn getCurrentTurn() { return currentTurn; }
}