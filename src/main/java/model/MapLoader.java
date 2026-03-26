package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MapLoader {

    public GameMap loadMap(String filePath) {
        GameMap gameMap = new GameMap();
        
        try {
            // Legge il file e lo trasforma in un oggetto JSON
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            
            // Tiled salva i tile dentro un array chiamato "layers"
            JSONArray layers = jsonObject.getJSONArray("layers");
            JSONObject firstLayer = layers.getJSONObject(0);
            
            // Prendiamo la lista dei numerini "data" (i muri e i pavimenti)
            JSONArray data = firstLayer.getJSONArray("data");
            
            // Riempiamo la nostra matrice 12x12
            int index = 0;
            for (int r = 0; r < 12; r++) {
                for (int c = 0; c < 12; c++) {
                    int tileId = data.getInt(index);
                    gameMap.setTile(r, c, tileId);
                    index++;
                }
            }
            System.out.println("Mappa 12x12 caricata con successo da JSON!");

            // =====================================================================
            // LETTURA DEGLI OGGETTI (CHIAVE E PORTA)
            // =====================================================================
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                
                // Cerchiamo il livello che si chiama "objectgroup"
                if (layer.has("type") && layer.getString("type").equals("objectgroup")) {
                    JSONArray objects = layer.getJSONArray("objects");
                    
                    // Scorriamo tutti gli oggetti trovati
                    for (int j = 0; j < objects.length(); j++) {
                        JSONObject obj = objects.getJSONObject(j);
                        String objName = obj.getString("name");
                        
                        // 1. LETTURA DELLA CHIAVE
                        if (objName.equals("Key")) {
                            int keyX = (int) obj.getDouble("x");
                            int keyY = (int) obj.getDouble("y");
                            
                            Key chiave = new Key(keyX, keyY);
                            gameMap.setKey(chiave);
                            
                            System.out.println("✅ Chiave caricata in posizione X: " + keyX + ", Y: " + keyY);
                        }
                        
                        // 2. LETTURA DELLA PORTA (2-Blocks)
                        if (objName.equals("Porta")) {
                            int doorX = (int) obj.getDouble("x");
                            int doorY = (int) obj.getDouble("y");
                            
                            // Calcoliamo la cella della griglia (pixel diviso 64)
                            int gridRow = doorY / 64;
                            int gridColLeft = doorX / 64;
                            // La porta occupa 2 blocchi, quindi prendiamo anche quello a destra (+1)
                            int gridColRight = gridColLeft + 1; 
                            
                            Door porta = new Door(doorX, doorY, gridColLeft, gridColRight, gridRow);
                            gameMap.setDoor(porta);
                            
                            System.out.println("🚪 Porta caricata! Occupa la riga " + gridRow + ", colonne " + gridColLeft + " e " + gridColRight);
                        }
                    }
                }
            }
            // =====================================================================
            
        } catch (Exception e) {
            System.err.println("Errore fatale in MapLoader: " + e.getMessage());
        }
        
        return gameMap;
    }
}