package model;
import java.awt.Point;

public abstract class Entity {
    protected int x;
    protected int y;

    protected int plannedX; // Coordinata X segreta
    protected int plannedY; // Coordinata Y segreta
    protected boolean hasPlannedMove; // Ci dice se ha già scelto la mossa
    protected boolean hasDoubleMoveBonus; // Gestisce il Bonus Handling
    
    protected Point plannedBlock; // Variabile per il blocco (NP-??)

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
        this.hasPlannedMove = false;
        this.hasDoubleMoveBonus = false;
        this.plannedBlock = null; // All'inizio non c'è nessun blocco
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // ==========================================
    // GESTIONE MOVIMENTO SEGRETO
    // ==========================================
    public void planMove(int targetX, int targetY) {
        this.plannedX = targetX;
        this.plannedY = targetY;
        this.hasPlannedMove = true;
    }

    // Metodo comodità che restituisce la mossa come Point (serve per i Test!)
    public Point getPlannedMove() {
        if (hasPlannedMove) {
            return new Point(plannedX, plannedY);
        }
        return null;
    }

    // ==========================================
    // GESTIONE BLOCCO SEGRETO
    // ==========================================
    public void planBlock(int targetX, int targetY) {
        this.plannedBlock = new Point(targetX, targetY);
    }

    public Point getPlannedBlock() { return plannedBlock; }
    public boolean hasPlannedBlock() { return plannedBlock != null; }

    // ==========================================
    // ESECUZIONE FINE TURNO
    // ==========================================
    // Viene chiamato solo alla fine del turno globale per eseguire lo spostamento vero
    public void executePlannedMove() {
        if (hasPlannedMove) {
            this.x = this.plannedX;
            this.y = this.plannedY;
            this.hasPlannedMove = false; // Mossa consumata
        }
        this.plannedBlock = null; // A fine turno resettiamo anche il blocco!
    }

    // ==========================================
    // GETTER & SETTER VARI
    // ==========================================
    public boolean hasDoubleMoveBonus() { return hasDoubleMoveBonus; }
    public void setDoubleMoveBonus(boolean hasDoubleMoveBonus) { this.hasDoubleMoveBonus = hasDoubleMoveBonus; }
    
    public boolean hasPlannedMove() { return hasPlannedMove; }
    public int getPlannedX() { return plannedX; }
    public int getPlannedY() { return plannedY; }
}