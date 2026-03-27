package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private int rows = 12;
    private int cols = 12;
    private int[][] logicalMatrix;
    
    // I nostri oggetti speciali!
    private Key key;
    private Door door;
    private Zombie zombie;     
    private Survivor survivor;

    // Costanti fondamentali per la calpestabilità
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

    /**
     * LOGICA DI CALPESTABILITÀ AGGIORNATA
     * Ora permette il movimento SOLO se la casella è un pavimento (ID 1).
     * Questo esclude muri, bordi mappa e porte chiuse.
     */
    /**
     * LOGICA DI CALPESTABILITÀ RIPRISTINATA (E CORRETTA)
     * Blocca solo i Muri (ID 2) e le Porte Chiuse.
    */
    public boolean isWalkable(int row, int col) {
        // 1. Controllo bordi logici dell'array
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        
        // 2. Controllo Muri: Blocchiamo solo se è esplicitamente un muro (TILE_WALL = 2)
        // Questo permetterà al giallo di apparire sui bordi grigi (ID diverso da 2),
        // ma è meglio che non vedere nulla!
        if (logicalMatrix[row][col] == TILE_WALL) {
            return false;
        }

        // 3. Controllo Porta Chiusa
        if (door != null && !door.isOpen()) {
            if (row == door.getGridRow() && (col == door.getGridColLeft() || col == door.getGridColRight())) {
                return false; 
            }
        }

        // Se è arrivato qui, consideriamo la casella calpestabile
        return true; 
    }

    public void loadMapFromJson(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            
            JSONArray layers = jsonObject.getJSONArray("layers");
            JSONObject firstLayer = layers.getJSONObject(0);
            JSONArray data = firstLayer.getJSONArray("data");

            int listIndex = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    logicalMatrix[r][c] = data.getInt(listIndex);
                    listIndex++;
                }
            }
            System.out.println("Mappa caricata con successo dal file: " + filePath);

        } catch (Exception e) {
            System.err.println("Errore: Impossibile leggere il file JSON!");
            e.printStackTrace();
        }
    }

    // --- GETTERS E SETTERS ---

    public Key getKey() { return key; }
    public void setKey(Key key) { this.key = key; }

    public Door getDoor() { return door; }
    public void setDoor(Door door) { this.door = door; }

    public Zombie getZombie() { return zombie; }
    public void setZombie(Zombie zombie) { this.zombie = zombie; }

    public Survivor getSurvivor() { return survivor; }
    public void setSurvivor(Survivor survivor) { this.survivor = survivor; }

    // ===========================================================
    // LOGICA DI MOVIMENTO (NP-35 / NP-19)
    // ===========================================================
    public List<Point> getValidMoves(int startX, int startY, int maxDistance) {
        List<Point> validMoves = new ArrayList<>();

        // Calcolo delle 4 direzioni (Croce)
        // isWalkable richiede (Riga, Colonna) -> (Y, X)
        
        if (isWalkable(startY - 1, startX)) {
            validMoves.add(new Point(startX, startY - 1)); // SU
        }
        if (isWalkable(startY + 1, startX)) {
            validMoves.add(new Point(startX, startY + 1)); // GIÙ
        }
        if (isWalkable(startY, startX - 1)) {
            validMoves.add(new Point(startX - 1, startY)); // SINISTRA
        }
        if (isWalkable(startY, startX + 1)) {
            validMoves.add(new Point(startX + 1, startY)); // DESTRA
        }

        return validMoves;
    }
}