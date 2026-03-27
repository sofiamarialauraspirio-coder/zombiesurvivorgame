package model;

public abstract class Entity {
    protected int x;
    protected int y;

    protected int plannedX; // Coordinata X segreta
    protected int plannedY; // Coordinata Y segreta
    protected boolean hasPlannedMove; // Ci dice se ha già scelto la mossa
    protected boolean hasDoubleMoveBonus; // Gestisce il Bonus Handling

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
        this.hasPlannedMove = false;
        this.hasDoubleMoveBonus = false;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // 1. Salva la mossa in memoria senza muovere il personaggio
    public void planMove(int targetX, int targetY) {
        this.plannedX = targetX;
        this.plannedY = targetY;
        this.hasPlannedMove = true;
    }

    // 2. Viene chiamato solo alla fine del turno globale per eseguire lo spostamento vero
    public void executePlannedMove() {
        if (hasPlannedMove) {
            this.x = this.plannedX;
            this.y = this.plannedY;
            this.hasPlannedMove = false; // Mossa consumata
        }
    }

    public boolean hasDoubleMoveBonus() { return hasDoubleMoveBonus; }
    public void setDoubleMoveBonus(boolean hasDoubleMoveBonus) { this.hasDoubleMoveBonus = hasDoubleMoveBonus; }
    
    public boolean hasPlannedMove() { return hasPlannedMove; }
    public int getPlannedX() { return plannedX; }
    public int getPlannedY() { return plannedY; }
}