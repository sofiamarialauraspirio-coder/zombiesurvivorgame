package model;

import java.awt.Point;

public abstract class Entity {
    protected int x;
    protected int y;
    
    // Piani di mossa usati dal GameManager
    protected Point plannedMove;
    protected Point plannedBlock;
    
    // --- STORY 20: VARIABILE CONGELAMENTO ---
    protected boolean canMove = true; 
    
    // --- STORY PRECEDENTE: DOPPIO PASSO ---
    protected boolean doubleMoveBonus = false;

    public Entity(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    // Getter e Setter base
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    // ==========================================
    // LOGICA MOVIMENTO E BLOCCO
    // ==========================================
    public void planMove(int targetX, int targetY) {
        this.plannedMove = new Point(targetX, targetY);
    }
    
    public Point getPlannedMove() { return plannedMove; }
    public boolean hasPlannedMove() { return plannedMove != null; } // Aiuto per il Test
    
    public void cancelPlannedMove() {
        this.plannedMove = null;
    }

    public void planBlock(int targetX, int targetY) {
        this.plannedBlock = new Point(targetX, targetY);
    }
    
    public Point getPlannedBlock() { return plannedBlock; }
    
    public void cancelPlannedBlock() {
        this.plannedBlock = null;
    }

    public void executePlannedMove() {
        if (plannedMove != null) {
            this.x = plannedMove.x;
            this.y = plannedMove.y;
            this.plannedMove = null;
        }
        this.plannedBlock = null; 
    }

    // ==========================================
    // STORY 20: METODI BONUS STOP OPPONENT
    // ==========================================
    public boolean canMove() { return canMove; }
    public void setCanMove(boolean canMove) { this.canMove = canMove; }
    public void resetMoveStatus() { this.canMove = true; }

    // ==========================================
    // METODI BONUS DOPPIO PASSO
    // ==========================================
    public boolean hasDoubleMoveBonus() { return doubleMoveBonus; }
    public void setDoubleMoveBonus(boolean bonus) { this.doubleMoveBonus = bonus; }
}