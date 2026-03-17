package model;

public class GameMap {
    private int rows = 12;
    private int cols = 12;
    private int[][] logicalMatrix;

    // Costanti per distinguere cosa è calpestabile e cosa no
    public static final int TILE_FLOOR = 1; 
    public static final int TILE_WALL = 2;

    public GameMap() {
        // Inizializza l'array 12x12
        logicalMatrix = new int[rows][cols];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setTile(int row, int col, int tileId) {
        logicalMatrix[row][col] = tileId;
    }

    public int getTile(int row, int col) {
        return logicalMatrix[row][col];
    }

    public boolean isWalkable(int row, int col) {
        // Se fuori dai bordi, non è calpestabile
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        // Ritorna true solo se la mattonella non è un muro
        return logicalMatrix[row][col] != TILE_WALL; 
    }
}