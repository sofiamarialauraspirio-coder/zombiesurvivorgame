package model;

import java.awt.Point;

public class GameManager {
    private Survivor survivor;
    private Zombie zombie;

    public GameManager(Survivor survivor, Zombie zombie) {
        this.survivor = survivor;
        this.zombie = zombie;
    }

    // ==========================================
    // STORY 15: RESOLUTION AND UPDATE
    // ==========================================
    public void resolveGlobalTurn() {
        // 1. Leggiamo le intenzioni segrete di entrambi
        Point sMove = survivor.getPlannedMove();
        Point zMove = zombie.getPlannedMove();
        Point sBlock = survivor.getPlannedBlock();
        Point zBlock = zombie.getPlannedBlock();

        boolean cancelSurvivorMove = false;
        boolean cancelZombieMove = false;

        // 2. CONFLICT RESOLUTION (Collision)
        // Se entrambi hanno provato a muoversi sulla STESSA casella
        if (sMove != null && zMove != null && sMove.equals(zMove)) {
            System.out.println("⚔️ COLLISIONE! Entrambi hanno mirato alla stessa casella. Mosse annullate.");
            cancelSurvivorMove = true;
            cancelZombieMove = true;
        }

        // 3. CONFLICT RESOLUTION (Blocking)
        // Se A si muove sul blocco piazzato da B
        if (sMove != null && zBlock != null && sMove.equals(zBlock)) {
            System.out.println("🧱 BLOCCO! Il Sopravvissuto ha sbattuto sul blocco dello Zombie.");
            cancelSurvivorMove = true;
        }
        if (zMove != null && sBlock != null && zMove.equals(sBlock)) {
            System.out.println("🧱 BLOCCO! Lo Zombie ha sbattuto sul blocco del Sopravvissuto.");
            cancelZombieMove = true;
        }

        // Applichiamo le cancellazioni dei movimenti (restano al punto di partenza)
        if (cancelSurvivorMove) survivor.cancelPlannedMove();
        if (cancelZombieMove) zombie.cancelPlannedMove();

        // 4. OCCUPIED CELL RULE
        // "Un blocco pianificato viene ignorato/annullato se la cella è occupata dalla posizione 
        // di partenza di un giocatore che non è riuscito a muoversi."
        Point sStart = new Point(survivor.getX(), survivor.getY());
        Point zStart = new Point(zombie.getX(), zombie.getY());

        boolean sFailedToMove = cancelSurvivorMove || sMove == null;
        boolean zFailedToMove = cancelZombieMove || zMove == null;

        if (sFailedToMove && zBlock != null && zBlock.equals(sStart)) {
            System.out.println("❌ CELLA OCCUPATA: Il blocco dello Zombie fallisce, il Sopravvissuto è rimasto lì!");
            zombie.cancelPlannedBlock();
        }
        if (zFailedToMove && sBlock != null && sBlock.equals(zStart)) {
            System.out.println("❌ CELLA OCCUPATA: Il blocco del Sopravvissuto fallisce, lo Zombie è rimasto lì!");
            survivor.cancelPlannedBlock();
        }

        // 5. ATOMIC UPDATE
        // Ora che tutti i conflitti logici sono risolti, aggiorniamo le coordinate vere IN UN SOLO COLPO
        survivor.executePlannedMove();
        zombie.executePlannedMove();
        
        System.out.println("Turno globale terminato: Aggiornamento Atomico eseguito! 🚀");
    }

    public Survivor getSurvivor() { return survivor; }
    public Zombie getZombie() { return zombie; }
}