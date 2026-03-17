public class GameMap {
<<<<<<< HEAD
    private int rows = 12;
    private int cols = 12;
    private int[][] logicalMatrix;

    // Costanti per distinguere cosa è calpestabile e cosa no (Criterio 3)
    // NOTA: I numeri dipendono dal tuo file Tiled. Di solito 0 o 1 è il pavimento.
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

    // Criterio di Accettazione 3: Distinguere walkable e ostacoli
    public boolean isWalkable(int row, int col) {
        // Se fuori dai bordi, non è calpestabile
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        // Ritorna true solo se la mattonella non è un muro
        return logicalMatrix[row][col] != TILE_WALL; 
    }
=======
    private int[][] logicalMatrix; // 12x12
    private Point exitLocation;

    // Costante per codificare i tipi di tile
    public static final int FLOOR = 0;
    public static final int WALL = 1;
    public static final int EXIT = 2;

    // ... getter, setter ...
>>>>>>> 0bee1b3f9f7571411accdd212ce661021de8cc77
}