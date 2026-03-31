package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMap {
    private int rows = 12;
    private int cols = 12;
    private int[][] logicalMatrix;
    
    private Key key;
    private List<Crate> crates = new ArrayList<>();
    private Door door;
    private Zombie zombie;     
    private Survivor survivor;

    // ID del muro basato sul tuo tileset Tiled
    public static final int TILE_FLOOR = 1; 
    public static final int TILE_WALL = 287; 

    public GameMap() {
        logicalMatrix = new int[rows][cols];
        // NOTA: Il costruttore è vuoto. 
        // NON chiamare spawnRandomCrate qui, altrimenti le casse appaiono subito!
    }

    // ==========================================================
    // STORY 17: SPAWN INTELLIGENTE (Anti-Sovrapposizione e Collisione)
    // ==========================================================
    public void spawnRandomCrate() {
        // 1. Controllo limite massimo (Story precedente)
        if (this.crates.size() >= CrateManager.MAX_CRATES) {
            System.out.println("🛑 GameMap: Limite massimo raggiunto. Niente spawn.");
            return;
        }

        Random rand = new Random();
        boolean spawned = false;
        int tentativi = 0;
        int maxTentativi = 100;

        while (!spawned && tentativi < maxTentativi) {
            int x = rand.nextInt(cols);
            int y = rand.nextInt(rows);

            // --- REQUISITI DI VALIDITÀ ---
            // A. La cella deve essere calpestabile (no muri)
            boolean walkable = isWalkable(y, x);
            
            // B. Non deve esserci il Sopravvissuto
            boolean noSurvivor = (survivor != null && (survivor.getX() != x || survivor.getY() != y));
            
            // C. Non deve esserci lo Zombie
            boolean noZombie = (zombie != null && (zombie.getX() != x || zombie.getY() != y));
            
            // D. ANTI-SOVRAPPOSIZIONE: Non deve esserci già un'altra cassa
            boolean noOtherCrate = true;
            for (Crate c : crates) {
                if (c.getX() == x && c.getY() == y) {
                    noOtherCrate = false;
                    break;
                }
            }

            // Se tutti i controlli passano, confermiamo lo spawn
            if (walkable && noSurvivor && noZombie && noOtherCrate) {
                crates.add(new Crate(x, y));
                spawned = true;
                System.out.println("🎁 GameMap: Nuova cassa spawnata in (" + x + ", " + y + ")");
            }
            tentativi++;
        }
    }

    public boolean isWalkable(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        if (logicalMatrix[row][col] == TILE_WALL) return false;

        // Gestione Porta (NP-32)
        if (door != null) {
            if (row == door.getGridRow() && (col == door.getGridColLeft() || col == door.getGridColRight())) {
                return (survivor != null && survivor.hasKey());
            }
        }
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
            System.out.println("Mappa caricata: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- GETTERS E SETTERS ---
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Key getKey() { return key; }
    public void setKey(Key key) { this.key = key; }
    public Door getDoor() { return door; }
    public void setDoor(Door door) { this.door = door; }
    public Zombie getZombie() { return zombie; }
    public void setZombie(Zombie zombie) { this.zombie = zombie; }
    public Survivor getSurvivor() { return survivor; }
    public void setSurvivor(Survivor survivor) { this.survivor = survivor; }
    public List<Crate> getCrates() { return crates; }

    public void setTile(int row, int col, int tileId) { logicalMatrix[row][col] = tileId; }
    public int getTile(int row, int col) { return logicalMatrix[row][col]; }

    public List<Point> getValidMoves(int startX, int startY, int maxDistance) {
        List<Point> validMoves = new ArrayList<>();
        if (isWalkable(startY - 1, startX)) validMoves.add(new Point(startX, startY - 1));
        if (isWalkable(startY + 1, startX)) validMoves.add(new Point(startX, startY + 1));
        if (isWalkable(startY, startX - 1)) validMoves.add(new Point(startX - 1, startY));
        if (isWalkable(startY, startX + 1)) validMoves.add(new Point(startX + 1, startY));
        return validMoves;
    }

    public void addCrate(Crate crate) { this.crates.add(crate); }
    public void removeCrate(Crate crate) { this.crates.remove(crate); }
}