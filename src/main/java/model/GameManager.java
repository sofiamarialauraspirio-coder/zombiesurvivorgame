package model;

import java.awt.Point;
import java.util.List;

public class GameManager {
    private Survivor survivor;
    private Zombie zombie;

    public GameManager(Survivor survivor, Zombie zombie) {
        this.survivor = survivor;
        this.zombie = zombie;
    }

    public void resolveGlobalTurn() {
        Point sMove = survivor.getPlannedMove();
        Point zMove = zombie.getPlannedMove();
        
        List<Point> sBlocks = survivor.getPlannedBlocks();
        List<Point> zBlocks = zombie.getPlannedBlocks();

        boolean cancelSurvivorMove = false;
        boolean cancelZombieMove = false;

        // Salviamo subito le posizioni di partenza (così non ci sono duplicati!)
        Point sStart = new Point(survivor.getX(), survivor.getY());
        Point zStart = new Point(zombie.getX(), zombie.getY());

        // 1. ⚔️ SCONTRO FRONTALE (Saltano sulla stessa casella)
        if (sMove != null && zMove != null && sMove.equals(zMove)) {
            System.out.println("⚔️ SCONTRO FRONTALE! Lo Zombie ha afferrato il Sopravvissuto!");
            // Nessuno viene cancellato, atterrano insieme e lo Zombie vince!
        }

        // 2. 🔄 INCROCIO FATALE (Provano a scambiarsi di posto)
        if (sMove != null && zMove != null && sMove.equals(zStart) && zMove.equals(sStart)) {
            System.out.println("🔄 INCROCIO FATALE! Si sono venuti addosso nei corridoi!");
            zombie.planMove(zStart.x, zStart.y); // Lo Zombie lo aspetta al varco
            zMove = zombie.getPlannedMove();     
        }

        // 3. 🧱 CONFLICT RESOLUTION (Blocking)
        if (sMove != null && zBlocks != null && zBlocks.contains(sMove)) {
            System.out.println("🧱 BLOCCO! Il Sopravvissuto ha sbattuto su un blocco dello Zombie.");
            cancelSurvivorMove = true;
        }
        if (zMove != null && sBlocks != null && sBlocks.contains(zMove)) {
            System.out.println("🧱 BLOCCO! Lo Zombie ha sbattuto su un blocco del Sopravvissuto.");
            cancelZombieMove = true;
        }

        if (cancelSurvivorMove) survivor.cancelPlannedMove();
        if (cancelZombieMove) zombie.cancelPlannedMove();

        // 4. ❌ OCCUPIED CELL RULE (Regola della cella occupata per i muri)
        boolean sFailedToMove = cancelSurvivorMove || sMove == null;
        boolean zFailedToMove = cancelZombieMove || zMove == null;

        if (sFailedToMove && zBlocks != null && zBlocks.contains(sStart)) {
            System.out.println("❌ CELLA OCCUPATA: Un blocco dello Zombie fallisce!");
            zombie.getPlannedBlocks().remove(sStart);
        }
        if (zFailedToMove && sBlocks != null && sBlocks.contains(zStart)) {
            System.out.println("❌ CELLA OCCUPATA: Un blocco del Sopravvissuto fallisce!");
            survivor.getPlannedBlocks().remove(zStart);
        }

        // 5. 🚀 ATOMIC UPDATE (Esecuzione finale)
        survivor.executePlannedMove();
        zombie.executePlannedMove();
        
        System.out.println("Turno globale terminato: Aggiornamento Atomico eseguito! 🚀");
    }

    public Survivor getSurvivor() { return survivor; }
    public Zombie getZombie() { return zombie; }
}