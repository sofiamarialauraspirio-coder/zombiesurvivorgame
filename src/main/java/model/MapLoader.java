package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MapLoader {

    public GameMap loadMap(String filePath) {
        GameMap gameMap = new GameMap();
        
        try {
            // Legge il file e lo trasforma in un oggetto JSON facilissimo da navigare
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            
            // Tiled salva i tile dentro un array chiamato "layers"
            JSONArray layers = jsonObject.getJSONArray("layers");
            JSONObject firstLayer = layers.getJSONObject(0);
            
            // Prendiamo la lista dei numerini "data"
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
            // INIZIO NUOVO CODICE: LETTURA DEGLI OGGETTI (CHIAVE, ZOMBIE, ECC.)
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
                        
                        // Se l'oggetto è la nostra Chiave (Key)
                        if (objName.equals("Key")) {
                            // Tiled usa i decimali, li convertiamo in numeri interi (int)
                            int keyX = (int) obj.getDouble("x");
                            int keyY = (int) obj.getDouble("y");
                            
                            // Creiamo l'oggetto Key e lo inseriamo nella Mappa
                            Key chiave = new Key(keyX, keyY);
                            gameMap.setKey(chiave);
                            
                            System.out.println("✅ Chiave trovata e caricata in posizione X: " + keyX + ", Y: " + keyY);
                        }
                    }
                }
            }
            // =====================================================================
            // FINE NUOVO CODICE
            // =====================================================================
            
        } catch (Exception e) {
            System.err.println("Errore fatale: " + e.getMessage());
        }
        
        return gameMap;
    }
}