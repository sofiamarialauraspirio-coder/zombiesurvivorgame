package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameMap {
    private int rows = 12;
    private int cols = 12;
    private int[][] logicalMatrix;
    
    // I nostri oggetti speciali!
    private Key key;
    private Door door;

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
        // 1. Se fuori dai bordi, non è calpestabile
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        
        // 2. Se è un muro di base, non è calpestabile
        if (logicalMatrix[row][col] == TILE_WALL) {
            return false;
        }

        // =========================================================
        // 3. NUOVO CONTROLLO: LA PORTA!
        // Se c'è una porta, ed è CHIUSA, controlliamo se stiamo sbattendo contro i suoi 2 blocchi
        // =========================================================
        if (door != null && !door.isOpen()) {
            if (row == door.getGridRow() && (col == door.getGridColLeft() || col == door.getGridColRight())) {
                return false; // BOOM! Sbatti contro la porta chiusa!
            }
        }

        // Se passiamo tutti i controlli, possiamo camminare!
        return true; 
    }

    // Metodo aggiornato usando org.json (che hai già installato e funzionante!)
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

    // --- METODI PER LA CHIAVE E LA PORTA ---

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Door getDoor() {
        return door;
    }

    public void setDoor(Door door) {
        this.door = door;
    }
}