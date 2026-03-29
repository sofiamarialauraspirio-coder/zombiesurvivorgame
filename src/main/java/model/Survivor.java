package model;

import java.awt.Point;

public class Survivor {
    private int x;
    private int y;
    
    private Point plannedMove;
    private Point plannedBlock;
    
    private boolean hasKey = false; 

    public Survivor(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void planMove(int targetX, int targetY) {
        this.plannedMove = new Point(targetX, targetY);
    }

    public void planBlock(int targetX, int targetY) {
        this.plannedBlock = new Point(targetX, targetY);
    }

    public Point getPlannedMove() { return plannedMove; }
    public Point getPlannedBlock() { return plannedBlock; }

    public void cancelPlannedMove() { this.plannedMove = null; }
    public void cancelPlannedBlock() { this.plannedBlock = null; }

    public void executePlannedMove() {
        if (plannedMove != null) {
            this.x = plannedMove.x;
            this.y = plannedMove.y;
            this.plannedMove = null;
        }
        this.plannedBlock = null;
    }

    public void collectKey() {
        this.hasKey = true;
    }

    public boolean hasKey() {
        return this.hasKey;
    }
}