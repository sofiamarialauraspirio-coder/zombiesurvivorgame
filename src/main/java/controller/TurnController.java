package controller;

import model.GameManager;

public class TurnController {
    private GameManager gameManager;

    // Un Enum è perfetto per sapere di chi è il turno!
    public enum PlayerTurn {
        SURVIVOR,
        ZOMBIE
    }

    private PlayerTurn currentTurn;

    public TurnController(GameManager gameManager) {
        this.gameManager = gameManager;
        this.currentTurn = PlayerTurn.SURVIVOR; // Inizia sempre il sopravvissuto
    }

    public void confirmMove(int targetX, int targetY) {
        if (currentTurn == PlayerTurn.SURVIVOR) {
            // 1. Il Sopravvissuto salva la mossa
            gameManager.getSurvivor().planMove(targetX, targetY);
            
            // 2. Passiamo il turno allo Zombie (nessuno si muove ancora visivamente!)
            currentTurn = PlayerTurn.ZOMBIE;
            System.out.println("Sopravvissuto ha pianificato la mossa. Ora tocca allo Zombie.");
            
        } else if (currentTurn == PlayerTurn.ZOMBIE) {
            // 1. Lo Zombie salva la mossa
            gameManager.getZombie().planMove(targetX, targetY);
            System.out.println("Zombie ha pianificato la mossa.");
            
            // 2. ENTRAMBI HANNO SCELTO! Fischiamo la fine del turno globale
            gameManager.resolveGlobalTurn();
            
            // 3. Ricomincia il giro dal Sopravvissuto per il turno successivo
            currentTurn = PlayerTurn.SURVIVOR;
        }
    }

    public PlayerTurn getCurrentTurn() {
        return currentTurn;
    }
}