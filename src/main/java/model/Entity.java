package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity {
    protected int x;
    protected int y;
    
    protected Point plannedMove;
    // Ora usiamo una LISTA per supportare i blocchi multipli!
    protected List<Point> plannedBlocks = new ArrayList<>();
    
    protected boolean canMove = true; 
    protected boolean doubleMoveBonus = false;
    protected int numeroBlocchiPossibili = 1; // Variabile della Story 22!

    public Entity(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public void planMove(int targetX, int targetY) {
        this.plannedMove = new Point(targetX, targetY);
    }
    
    public Point getPlannedMove() { return plannedMove; }
    public boolean hasPlannedMove() { return plannedMove != null; } 
    public void cancelPlannedMove() { this.plannedMove = null; }

    public void planBlock(int targetX, int targetY) {
        this.plannedBlocks.add(new Point(targetX, targetY));
    }
    
    public List<Point> getPlannedBlocks() { return plannedBlocks; }
    
    public void cancelPlannedBlock() {
        this.plannedBlocks.clear();
    }

    public void executePlannedMove() {
        if (plannedMove != null) {
            this.x = plannedMove.x;
            this.y = plannedMove.y;
            this.plannedMove = null;
        }
        this.plannedBlocks.clear(); // Resetta i blocchi a fine turno
    }

    public boolean canMove() { return canMove; }
    public void setCanMove(boolean canMove) { this.canMove = canMove; }
    
    public void resetMoveStatus() { 
        this.canMove = true; 
        this.numeroBlocchiPossibili = 1; // Si resetta a 1 blocco normale
        this.doubleMoveBonus = false;
    }

    public boolean hasDoubleMoveBonus() { return doubleMoveBonus; }
    public void setDoubleMoveBonus(boolean bonus) { this.doubleMoveBonus = bonus; }
    
    public int getNumeroBlocchiPossibili() { return numeroBlocchiPossibili; }
    public void setNumeroBlocchiPossibili(int num) { this.numeroBlocchiPossibili = num; }
}