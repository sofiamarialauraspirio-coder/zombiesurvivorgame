package model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.Reader;


public class GameMap {
    private int rows = 12;
    private int cols = 12;
    private int[][] logicalMatrix;
    private Key key;

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

    // Metodo per il Ticket NP-13: Legge il file JSON di Tiled e riempie la matrice
    public void loadMapFromJson(String filePath) {
        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            
            // Legge il file e va a cercare l'array "data" dentro il primo layer
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray layers = jsonObject.getAsJsonArray("layers");
            JsonObject firstLayer = layers.get(0).getAsJsonObject();
            JsonArray data = firstLayer.getAsJsonArray("data");

            // Riempie la tua matrice logica usando le tue variabili 'rows' e 'cols'
            int listIndex = 0;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    // Prende il numero dal JSON e lo mette nella tua matrice
                    logicalMatrix[r][c] = data.get(listIndex).getAsInt();
                    listIndex++;
                }
            }
            System.out.println("Mappa caricata con successo dal file: " + filePath);

        } catch (Exception e) {
            System.err.println("Errore: Impossibile leggere il file JSON!");
            e.printStackTrace();
        }
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }
}