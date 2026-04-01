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
    // STORY 17: SPAWN INTELLIGENTE (Distanza Minima 5 blocchi)
    // ==========================================================
    public void spawnRandomCrate() {
        // 1. Controllo limite massimo
        if (this.crates.size() >= CrateManager.MAX_CRATES) {
            System.out.println("🛑 GameMap: Limite massimo raggiunto. Niente spawn.");
            return;
        }

        Random rand = new Random();
        boolean spawned = false;
        int tentativi = 0;
        int maxTentativi = 200; // Aumentato a 200 perché ora la regola è molto severa

        while (!spawned && tentativi < maxTentativi) {
            int x = rand.nextInt(cols);
            int y = rand.nextInt(rows);

            // --- REQUISITI DI VALIDITÀ ---
            // A. La cella deve essere calpestabile (no muri)
            boolean walkable = isWalkable(y, x);
            
            // B. Non deve esserci il Sopravvissuto (Se è nullo va bene, se c'è deve essere altrove)
            boolean noSurvivor = (survivor == null || (survivor.getX() != x || survivor.getY() != y));
            
            // C. Non deve esserci lo Zombie (Se è nullo va bene, se c'è deve essere altrove)
            boolean noZombie = (zombie == null || (zombie.getX() != x || zombie.getY() != y));
            
            // D. DISTANZA MINIMA: Almeno 5 blocchi dalle altre casse
            boolean distanceValid = true;
            for (Crate c : crates) {
                // Calcolo della Distanza di Manhattan
                int manhattanDist = Math.abs(c.getX() - x) + Math.abs(c.getY() - y);
                if (manhattanDist < 5) {
                    distanceValid = false;
                    break; // Troppo vicina! Scartiamo questa cella
                }
            }

            // Se tutti i controlli passano, confermiamo lo spawn
            if (walkable && noSurvivor && noZombie && distanceValid) {
                crates.add(new Crate(x, y));
                spawned = true;
                System.out.println("🎁 GameMap: Nuova cassa spawnata in (" + x + ", " + y + ") - Distanza rispettata!");
            }
            tentativi++;
        }
        
        // Se non trova posto dopo 200 tentativi, lo segnaliamo
        if (!spawned) {
            System.out.println("⚠️ GameMap: Impossibile trovare uno spot valido a distanza 5 dopo " + maxTentativi + " tentativi.");
        }
    }

    public boolean isWalkable(int row, int col) {
        // 1. Controllo dei confini (Evita crash se clicchi fuori dalla mappa)
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;

        // 2. Gestione Speciale: La Porta
        if (door != null) {
            if (row == door.getGridRow() && (col == door.getGridColLeft() || col == door.getGridColRight())) {
                // Si passa dalla porta SOLO se il sopravvissuto ha la chiave
                return (survivor != null && survivor.hasKey());
            }
        }

        // 3. 🧱 LA REGOLA DEI MURI DEFINITIVA
        // In Tiled hai lasciato vuoto (0) dove c'è l'erba verde.
        // Se il numero è diverso da 0 (es. 9, 287, 365, ecc.), ALLORA È UN MURO!
        if (logicalMatrix[row][col] != 0) {
            return false;
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

   public List<Point> getValidMoves(int startX, int startY, int range) {
        List<Point> valid = new ArrayList<>();
        
        for (int x = startX - range; x <= startX + range; x++) {
            for (int y = startY - range; y <= startY + range; y++) {
                // 1. Escludiamo il centro (il personaggio)
                if (x == startX && y == startY) continue;
                
                // 2. FORMA A CROCE: Permettiamo solo caselle sulla stessa riga o colonna
                if (x == startX || y == startY) {
                    
                    int distance = Math.abs(x - startX) + Math.abs(y - startY);
                    
                    if (distance <= range) {
                        if (isWalkable(y, x)) { // y è la riga, x è la colonna
                            valid.add(new Point(x, y));
                        }
                    }
                }
            }
        }
        return valid;
    }

    public void addCrate(Crate crate) { this.crates.add(crate); }
    public void removeCrate(Crate crate) { this.crates.remove(crate); }
}